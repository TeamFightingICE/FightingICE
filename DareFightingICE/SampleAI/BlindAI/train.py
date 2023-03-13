import argparse
import math
import time
import os
import re
import psutil
import numpy as np
import sys
import torch
import torch.nn.functional as F
from dataclasses import dataclass
from dotmap import DotMap
from torch import optim
from torch.utils.tensorboard import SummaryWriter
from agent import SoundAgent, CollectDataHelper
from model import RecurrentActor, RecurrentCritic, FeedForwardActor, FeedForwardCritic
from encoder import SampleEncoder, RawEncoder, FFTEncoder, MelSpecEncoder
import pickle
import tqdm
import pathlib
import logging
from pyftg.gateway import Gateway

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
# create console handler and set level to debug
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)

# create formatter
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')

# add formatter to ch
ch.setFormatter(formatter)

# add ch to logger
logger.addHandler(ch)
# logging.basicConfig(format='%(asctime)s %(message)s')
# torch.set_num_threads(2)
# TODO use rnn from others/rnnppo.py and check vf loss and entropy losses from others/ppo.py
# TODO add train part
HIDDEN_SIZE = 512
RECURRENT_LAYERS = 1
LEARNING_RATE = 0.0003
GAMMA = 0.99
C1 = 0.95
LAMBDA = 0.95
# EXPERIMENT_NAME = 'experiment_{}'.format(args.encoder)
BASE_CHECKPOINT_PATH = f'ppo_pytorch/checkpoints'
ROLL_OUT = 3600
# BATCH_SIZE = 512
BATCH_SIZE = 64
STATE_DIM = {
    1: {
        'conv1d': 160,
        'fft': 512,
        'mel': 2560
    },
    4: {
        'conv1d': 64,
        'fft': 512,
        'mel': 1280
    }
}
GATHER_DEVICE = 'cpu'
# GATHER_DEVICE = 'cuda' if torch.cuda.is_available() else 'cpu'
TRAIN_DEVICE = 'cuda' if torch.cuda.is_available() else 'cpu'
BATCH_LEN = 32
PPO_CLIP = 0.2
ENTROPY_FACTOR = 0.01
VF_FACTOR = 1
MAX_GRAD_NORM = 1.0
ACTION_NUM = 40

def kill_proc_tree(pid, including_parent=True):    
    parent = psutil.Process(pid)
    children = parent.children(recursive=True)
    for child in children:
        child.kill()
    gone, still_alive = psutil.wait_procs(children, timeout=5)
    if including_parent:
        parent.kill()
        parent.wait(5)

@torch.no_grad()
def process_game_agent_data(actor_, critic_, data_collect_helper, recurrent):
    logger.info('process game agent data')
    number_round = len(data_collect_helper.total_round_data)
    episode_lengths = []
    state_list = []
    value_list = []
    action_list = []
    action_probs_list = []
    true_reward_list = []
    reward_list = []
    terminal_list = []
    actor_hidden_state_list = []
    critic_hidden_state_list = []
    for r in range(number_round):
        logger.info(f'Round {r+1}')
        round_data = data_collect_helper.total_round_data[r]
        round_actions = data_collect_helper.total_round_action_data[r]
        round_action_dists = data_collect_helper.total_round_action_dist_data[r]
        # round_actor_hidden_state_list = data_collect_helper.total_round_actor_hidden_data[r]

        round_state_list = []
        round_value_list = []
        round_action_list = []
        round_action_probs_list = []
        round_true_reward_list = []
        round_reward_list = []
        round_terminal_list = []
        round_actor_hidden_state_list = []
        round_critic_hidden_state_list = []

        obsv = round_data[0][0]
        if recurrent:
            actor_.get_init_state(GATHER_DEVICE)
            critic_.get_init_state(GATHER_DEVICE)
        terminal = torch.tensor(0).float()
        for i in tqdm.trange(1, min(len(round_data) - 1, len(round_actions))):
            if len(round_data[i]) == 1:
                break
            if recurrent:
                round_actor_hidden_state_list.append(actor_.hidden_cell.squeeze(0).to(GATHER_DEVICE))
                round_critic_hidden_state_list.append(critic_.hidden_cell.squeeze(0).to(GATHER_DEVICE))

            state = torch.tensor(obsv, dtype=torch.float32)
            round_state_list.append(state)
            value = critic_(state.unsqueeze(0).to(GATHER_DEVICE), terminal.to(GATHER_DEVICE))
            round_value_list.append(value.squeeze())
            action_dist = actor_(state.unsqueeze(0).to(GATHER_DEVICE), terminal.to(GATHER_DEVICE))
            action = round_actions[i - 1]
            round_action_list.append(action.squeeze().to(GATHER_DEVICE))
            round_action_probs_list.append(action_dist.log_prob(action).to(GATHER_DEVICE))

            # get obsv, reward, done data
            obsv, reward, done, _ = round_data[i]

            terminal = torch.tensor(done).float()
            # transformed_reward = normalize_reward(reward)
            transformed_reward = reward
            round_true_reward_list.append(torch.tensor(reward).float())
            round_reward_list.append(torch.tensor(transformed_reward).float())
            round_terminal_list.append(terminal)

        # compute for final state
        state = torch.tensor(obsv, dtype=torch.float32)
        value = critic(state.unsqueeze(0).to(GATHER_DEVICE))
        round_value_list.append(value.squeeze())

        episode_lengths.append(len(round_state_list))
        state_list.append(round_state_list)
        value_list.append(round_value_list)
        action_list.append(round_action_list)
        action_probs_list.append(round_action_probs_list)
        true_reward_list.append(round_true_reward_list)
        reward_list.append(round_reward_list)
        terminal_list.append(round_terminal_list)
        actor_hidden_state_list.append(round_actor_hidden_state_list)
        critic_hidden_state_list.append(round_critic_hidden_state_list)

    if recurrent:
        trajectories_data = {
            "actions": action_list,
            "action_probabilities": action_probs_list,
            "states": state_list,
            "rewards": reward_list,
            "values": value_list,
            "true_rewards": true_reward_list,
            "terminals": terminal_list,
            # add hidden state list
            "actor_hidden_states": actor_hidden_state_list,
            "critic_hidden_states": critic_hidden_state_list,
        }
    else:
        trajectories_data = {
            "actions": action_list,
            "action_probabilities": action_probs_list,
            "states": state_list,
            "rewards": reward_list,
            "values": value_list,
            "true_rewards": true_reward_list,
            "terminals": terminal_list,
            # add hidden state list
        }
    # print({key: {type(v[0]) for v in value} for key, value in trajectories_data.items()})
    # return tensor
    for (key, value)  in trajectories_data.items():
        for i in range(len(value)):
            if len(value[i]) == 0:
                logger.info(f'empty tensorlist: {key} {i}')
    return {key: [torch.stack(v) for v in value] for key, value in trajectories_data.items()}, episode_lengths

## maximin normalization
def normalize_reward(reward):
    return (reward + 400) / 800

# collect trajectories
def collect_trajectories(actor, critic, port, game_num, p2, rnn, n_frame):
    logger.info(f'start fight with {p2}')
    logger.info(f'game_num value {game_num}')
    error = True
    while error:
        gateway = Gateway(port=port)
        try:
            current_time = int(time.time() * 1000)
            # register AIs
            collect_data_helper = CollectDataHelper(logger)
            agent = SoundAgent(actor=actor, critic=critic, collect_data_helper=collect_data_helper, logger=logger, n_frame=n_frame, rnn=rnn)
            gateway.register_ai('SoundAgent', agent)
            gateway.run_game(['ZEN', 'ZEN'], ['SoundAgent', p2], game_num)
            # finish game
            logger.info('Finish game')
            sys.stdout.flush()
            # close gateway
            gateway.close()
            error = False
        except Exception as ex:
            print(ex)
            logger.info('There is an error with the gateway, restarting')
            gateway.close()
            error = True


    # return agent.get_trajectories_data()
    agent_data = process_game_agent_data(actor, critic, agent.collect_data_helper, rnn)
    # try:
    #     kill_proc_tree(java_env.pid, False)
    # except:
    #     print('kill process')
    # agent.reset()
    return agent_data


def compute_advantages(rewards, values, discount, gae_lambda):
    """
    Compute General Advantage.
    """
    deltas = rewards + discount * values[1:] - values[:-1]
    seq_len = len(rewards)
    advs = torch.zeros(seq_len + 1)
    multiplier = discount * gae_lambda
    for i in range(seq_len - 1, -1, -1):
        advs[i] = advs[i + 1] * multiplier + deltas[i]
    return advs[:-1]


def calc_discounted_return(rewards, discount, final_value):
    """
    Calculate discounted returns based on rewards and discount factor.
    """
    seq_len = len(rewards)
    discounted_returns = torch.zeros(seq_len)
    discounted_returns[-1] = rewards[-1] + discount * final_value
    for i in range(seq_len - 2, -1, -1):
        discounted_returns[i] = rewards[i] + discount * discounted_returns[i + 1]
    return discounted_returns


def pad_and_compute_returns(trajectory_episodes, len_episodes):
    """
    Pad the trajectories up to hp.rollout_steps so they can be combined in a
    single tensor.
    Add advantages and discounted_returns to trajectories.
    """

    episode_count = len(len_episodes)
    advantages_episodes, discounted_returns_episodes = [], []
    padded_trajectories = {key: [] for key in trajectory_episodes.keys()}
    padded_trajectories["advantages"] = []
    padded_trajectories["discounted_returns"] = []
    episode_max_length = ROLL_OUT

    for i in range(episode_count):
        single_padding = torch.zeros(episode_max_length - len_episodes[i])
        for key, value in trajectory_episodes.items():
            if value[i].ndim > 2:
                padding = torch.zeros(episode_max_length - len_episodes[i], value[0].shape[1], value[0].shape[2],
                                      dtype=value[i].dtype)
            elif value[i].ndim > 1:
                padding = torch.zeros(episode_max_length - len_episodes[i], value[0].shape[1], dtype=value[i].dtype)
            else:
                padding = torch.zeros(episode_max_length - len_episodes[i], dtype=value[i].dtype)
            padded_trajectories[key].append(torch.cat((value[i], padding)))
        padded_trajectories["advantages"].append(
            torch.cat((compute_advantages(rewards=trajectory_episodes["rewards"][i],
                                          values=trajectory_episodes["values"][i],
                                          discount=GAMMA,
                                          gae_lambda=LAMBDA), single_padding)))
        padded_trajectories["discounted_returns"].append(
            torch.cat((calc_discounted_return(rewards=trajectory_episodes["rewards"][i],
                                              discount=GAMMA,
                                              final_value=trajectory_episodes["values"][i][-1]), single_padding)))
    return_val = {k: torch.stack(v) for k, v in padded_trajectories.items()}
    return_val["seq_len"] = torch.tensor(len_episodes)

    return return_val


@dataclass
class TrajectorBatchRNN():
    """
    Dataclass for storing data batch.
    """
    states: torch.tensor
    actions: torch.tensor
    action_probabilities: torch.tensor
    advantages: torch.tensor
    discounted_returns: torch.tensor
    batch_size: torch.tensor
    actor_hidden_states: torch.tensor
    # actor_cell_states: torch.tensor
    critic_hidden_states: torch.tensor
    # critic_cell_states: torch.tensor
    
@dataclass
class TrajectorBatch():
    """
    Dataclass for storing data batch.
    """
    states: torch.tensor
    actions: torch.tensor
    action_probabilities: torch.tensor
    advantages: torch.tensor
    discounted_returns: torch.tensor
    batch_size: torch.tensor
    # actor_hidden_states: torch.tensor
    # actor_cell_states: torch.tensor
    # critic_hidden_states: torch.tensor
    # critic_cell_states: torch.tensor


class TrajectoryDataset():
    """
    Fast dataset for producing training batches from trajectories.
    """
    def __init__(self, trajectories, batch_size, device, sequence_len, recurrent=True):
        # Combine multiple trajectories into
        self.trajectories = {key: value.to(device) for key, value in trajectories.items()}
        self.sequence_len = sequence_len
        truncated_seq_len = torch.clamp(trajectories["seq_len"] - sequence_len + 1, 0, ROLL_OUT)
        self.cumsum_seq_len = np.cumsum(np.concatenate((np.array([0]), truncated_seq_len.numpy())))
        self.batch_size = batch_size
        self.recurrent = recurrent

    def __iter__(self):
        self.valid_idx = np.arange(self.cumsum_seq_len[-1])
        self.batch_count = 0
        return self

    def __next__(self):
        if self.batch_count * self.batch_size >= math.ceil(self.cumsum_seq_len[-1] / self.sequence_len):
            raise StopIteration
        else:
            actual_batch_size = min(len(self.valid_idx), self.batch_size)
            start_idx = np.random.choice(self.valid_idx, size=actual_batch_size, replace=False)
            self.valid_idx = np.setdiff1d(self.valid_idx, start_idx)
            eps_idx = np.digitize(start_idx, bins=self.cumsum_seq_len, right=False) - 1
            seq_idx = start_idx - self.cumsum_seq_len[eps_idx]
            series_idx = np.linspace(seq_idx, seq_idx + self.sequence_len - 1, num=self.sequence_len, dtype=np.int64)
            self.batch_count += 1
            if self.recurrent:
                return TrajectorBatchRNN(**{key: magic_combine(value[eps_idx, series_idx], 0, 2) for key, value
                                     in self.trajectories.items() if key in TrajectorBatchRNN.__dataclass_fields__.keys()},
                                  batch_size=actual_batch_size)
            return TrajectorBatch(**{key: magic_combine(value[eps_idx, series_idx], 0, 2) for key, value
                                     in self.trajectories.items() if key in TrajectorBatch.__dataclass_fields__.keys()},
                                  batch_size=actual_batch_size)
    
    def __len__(self):
        return math.ceil(math.ceil(self.cumsum_seq_len[-1] / self.sequence_len) / self.batch_size)


def train_model(actor, critic, actor_optimizer, critic_optimizer, iteration, port, encoder_name, experiment_id, p2, recurrent, n_frame, epoch, training_iteration, game_num):
    loop_count = 0
    if not recurrent:
        writer = SummaryWriter(log_dir=f'ppo_pytorch/logs/{encoder_name}/{experiment_id}')
    else:
        writer = SummaryWriter(log_dir=f'ppo_pytorch/logs/{encoder_name}/rnn/{experiment_id}')
    iteration += 1
    while iteration < training_iteration:
        logger.info(f"Training iteration {iteration}")
        actor = actor.to(GATHER_DEVICE)
        critic = critic.to(GATHER_DEVICE)
        start_gather_time = time.time()
        loop_count += 1
        # gather trajectories
        logger.info(f'Gathering trajectories data for {game_num} games')
        total_trajectories_data = []
        total_episode_lengths = []
        
        trajectories_data = {}
        # run 1 game in GAME_NUM times and concatenate game data together
        for i in range(game_num):
            logger.info('Start game {}'.format(i+1))
            game_trajectories_data, game_episode_lengths = collect_trajectories(actor, critic, port, 1, p2, recurrent, n_frame)
            total_trajectories_data.append(game_trajectories_data)
            
            for k, v in game_trajectories_data.items():
                if trajectories_data.get(k, None) is None:
                    trajectories_data[k] = v
                else:
                    trajectories_data[k] = trajectories_data[k] + v
            total_episode_lengths += game_episode_lengths

        # for game_trajectories_data in 
        episode_lengths = total_episode_lengths
        # trajectories_data = process_trajectories(trajectories_data)
        # episode_lengths = trajectories_data['episode_lengths']
        logger.info('Calculate returns')
        trajectories = pad_and_compute_returns(trajectories_data, episode_lengths)
        logger.info('Calculate mean reward')

        # sum of rewards over all steps is actually HP_self - HP_opp at the end of round.
        complete_episode_count = len(trajectories_data['states'])
        terminal_episodes_rewards = trajectories["true_rewards"].sum(axis=1).sum()
        mean_reward = terminal_episodes_rewards / complete_episode_count
        stddev_reward = np.std(trajectories["true_rewards"].sum(axis=1).numpy())

        #  normalized rewards
        normalized_terminal_episodes_rewards = trajectories["rewards"].sum(axis=1).sum()
        normalized_mean_reward = normalized_terminal_episodes_rewards / complete_episode_count
        normalized_stddev_reward = np.std(trajectories["rewards"].sum(axis=1).numpy())

        # log mean_reward
        trajectory_dataset = TrajectoryDataset(trajectories, batch_size=BATCH_SIZE, device=TRAIN_DEVICE, sequence_len=BATCH_LEN, recurrent=recurrent)
        end_gather_time = time.time()
        start_train_time = time.time()
        actor = actor.to(TRAIN_DEVICE)
        critic = critic.to(TRAIN_DEVICE)
        logger.info('Start policy gradient')
        # Train actor and critic
        for i in range(epoch):
            logger.info(f'Epoch {i+1}')
            for batch in tqdm.tqdm(trajectory_dataset):
                # Get batch
                # actor.hidden_cell = (batch.actor_hidden_states[:1], batch.actor_cell_states[:1])
                if recurrent:
                    actor.hidden_cell = batch.actor_hidden_states[:1]

                # Update actor
                actor_optimizer.zero_grad()
                action_dist = actor(batch.states)
                # Action dist runs on cpu as a workaround to CUDA illegal memory access.
                # action_probabilities = action_dist.log_prob(batch.actions.to("cpu")).to(TRAIN_DEVICE)  # batch.actions[-1, :]
                action_probabilities = action_dist.log_prob(batch.actions).to(TRAIN_DEVICE)
                # Compute probability ratio from probabilities in logspace.
                probabilities_ratio = torch.exp(action_probabilities - batch.action_probabilities)  # [-1, :])
                surrogate_loss_0 = probabilities_ratio * batch.advantages  # [-1, :]
                surrogate_loss_1 = torch.clamp(probabilities_ratio, 1. - PPO_CLIP,
                                               1. + PPO_CLIP) * batch.advantages  # [-1, :]
                actor_loss = -torch.mean(torch.min(surrogate_loss_0, surrogate_loss_1))
                surrogate_loss_2 = action_dist.entropy().to(TRAIN_DEVICE)
                # actor_loss = -torch.mean(torch.min(surrogate_loss_0, surrogate_loss_1)) - torch.mean(
                #     ENTROPY_FACTOR * surrogate_loss_2)
                actor_loss.backward()
                torch.nn.utils.clip_grad.clip_grad_norm_(actor.parameters(), MAX_GRAD_NORM)
                actor_optimizer.step()

                # Update critic
                critic_optimizer.zero_grad()
                if recurrent:
                    critic.hidden_cell = batch.critic_hidden_states[:1]  # , batch.critic_cell_states[:1])
                values = critic(batch.states)
                critic_loss = F.mse_loss(batch.discounted_returns, values.squeeze())  # batch.discounted_returns[-1, :]
                torch.nn.utils.clip_grad.clip_grad_norm_(critic.parameters(), MAX_GRAD_NORM)
                critic_loss.backward()
                critic_optimizer.step()
        end_train_time = time.time()

        # save mean reward to text file
        logger.info("Save mean reward to file")
        save_reward_file(encoder_name, experiment_id, mean_reward.float(), recurrent=recurrent, filename='result')
        # logger.info("Save stddev reward to file")
        # save_reward_file(encoder_name, experiment_id, stddev_reward, filename="std", recurrent=recurrent)

        # # save normalized_rewards to text file
        # logger.info('Save mean normalized reward to file')
        # save_reward_file(encoder_name, experiment_id, normalized_mean_reward.float(), filename='reward_normalized', recurrent=recurrent)
        # logger.info('Save std normalized reward to file')
        # save_reward_file(encoder_name, experiment_id, normalized_stddev_reward, filename="std_normalized", recurrent=recurrent)

        logger.info(f"Iteration: {iteration}, Reward std: {stddev_reward},  Mean reward: {mean_reward}, Mean Entropy: {torch.mean(surrogate_loss_2)}, " +
              f"complete_episode_count: {complete_episode_count}, Gather time: {end_gather_time - start_gather_time:.2f}s, " +
              f"Train time: {end_train_time - start_train_time:.2f}s")
        save_checkpoint(actor, critic, actor_optimizer, critic_optimizer, iteration, encoder_name, experiment_id, recurrent)

        # write tensorboard log
        writer.add_scalar("complete_episode_count", complete_episode_count, iteration)
        writer.add_scalar("total_reward", mean_reward, iteration)
        writer.add_scalar("actor_loss", actor_loss, iteration)
        writer.add_scalar("critic_loss", critic_loss, iteration)
        writer.add_scalar("policy_entropy", torch.mean(surrogate_loss_2), iteration)
        writer.add_scalar("total_normalized_reward", normalized_mean_reward, iteration)

        # write actor, critic parameters
        save_parameters(writer, "actor", actor, iteration, encoder_name, experiment_id)
        save_parameters(writer, "value", critic, iteration, encoder_name, experiment_id)
        iteration += 1
                
        # del loss
        del surrogate_loss_0
        del surrogate_loss_1
        del probabilities_ratio
        del critic_loss
        del actor_loss
        torch.cuda.empty_cache()


def save_parameters(writer, tag, model, batch_idx, encoder, experiment_id):
    """
    Save model parameters for tensorboard.
    """
    _INVALID_TAG_CHARACTERS = re.compile(r"[^-/\w\.]")
    for k, v in model.state_dict().items():
        shape = v.shape
        # Fix shape definition for tensorboard.
        shape_formatted = _INVALID_TAG_CHARACTERS.sub("_", str(shape))
        # Don't do this for single weights or biases
        if np.any(np.array(shape) > 1):
            mean = torch.mean(v)
            std_dev = torch.std(v)
            maximum = torch.max(v)
            minimum = torch.min(v)
            writer.add_scalars(
                "{}_{}_{}_weights/{}{}".format(encoder, experiment_id, tag, k, shape_formatted),
                {"mean": mean, "std_dev": std_dev, "max": maximum, "min": minimum},
                batch_idx
            )
        else:
            writer.add_scalar("{}_{}{}".format(tag, k, shape_formatted), v.data, batch_idx)


def save_reward_file(encoder, experiment_id, reward, filename="reward", recurrent=True):
    if not recurrent:
        file_name = f'{filename}_{encoder}_{experiment_id}.txt'
    else:
        file_name = f'{filename}_{encoder}_{experiment_id}_rnn.txt'
    with open(file_name, 'a') as f:
        f.write(str(reward))
        f.write('\n')


def init(encoder_name, experiment_id, n_frame, rnn=True):
    # TODO load data from checkpoint
    if rnn:
        print('init recurrent network')
    else:
        print('init feedforward network')
    # initialize new data
    if rnn:
        actor_model = RecurrentActor(STATE_DIM[n_frame][encoder_name], HIDDEN_SIZE, RECURRENT_LAYERS, get_sound_encoder(encoder_name, n_frame),
                        action_num=ACTION_NUM)
    else:
        actor_model = FeedForwardActor(STATE_DIM[n_frame][encoder_name], HIDDEN_SIZE, RECURRENT_LAYERS, get_sound_encoder(encoder_name, n_frame),
                        action_num=ACTION_NUM)
    actor_opt = optim.Adam(actor_model.parameters(), lr=LEARNING_RATE)
    if rnn:
        critic_model = RecurrentCritic(STATE_DIM[n_frame][encoder_name], HIDDEN_SIZE, RECURRENT_LAYERS, get_sound_encoder(encoder_name, n_frame))
    else:
        critic_model = FeedForwardCritic(STATE_DIM[n_frame][encoder_name], HIDDEN_SIZE, RECURRENT_LAYERS, get_sound_encoder(encoder_name, n_frame))
    critic_opt = optim.Adam(critic_model.parameters(), lr=LEARNING_RATE)

    max_checkpoint_iteration = get_last_checkpoint_iteration(encoder_name, experiment_id, rnn)
    if max_checkpoint_iteration > -1:
        actor_state_dict, critic_state_dict, actor_optimizer_state_dict, critic_optimizer_state_dict = load_checkpoint(
            encoder_name, experiment_id, max_checkpoint_iteration, rnn)
        actor_model.load_state_dict(actor_state_dict, strict=True)
        critic_model.load_state_dict(critic_state_dict, strict=True)
        actor_opt.load_state_dict(actor_optimizer_state_dict)
        critic_opt.load_state_dict(critic_optimizer_state_dict)
        # We have to move manually move optimizer states to TRAIN_DEVICE manually since optimizer doesn't yet have a "to" method.
        for state in actor_opt.state.values():
            for k, v in state.items():
                if isinstance(v, torch.Tensor):
                    state[k] = v.to(TRAIN_DEVICE)
        for state in critic_opt.state.values():
            for k, v in state.items():
                if isinstance(v, torch.Tensor):
                    state[k] = v.to(TRAIN_DEVICE)
    return actor_model, critic_model, actor_opt, critic_opt, max_checkpoint_iteration


def get_last_checkpoint_iteration(encoder_name, experiment_id, rnn):
    """
    Determine latest checkpoint iteration.
    """
    if not rnn:
        CHECKPOINT_PATH = f'{BASE_CHECKPOINT_PATH}/{encoder_name}/{experiment_id}/'
    else:
         CHECKPOINT_PATH = f'{BASE_CHECKPOINT_PATH}/{encoder_name}/rnn/{experiment_id}/'
    if os.path.isdir(CHECKPOINT_PATH):
        max_checkpoint_iteration = max([int(dirname) for dirname in os.listdir(CHECKPOINT_PATH)])
    else:
        max_checkpoint_iteration = -1
    return max_checkpoint_iteration


def get_sound_encoder(encoder_name, n_frame):
    encoder = None
    if encoder_name == 'conv1d':
        encoder = RawEncoder(frame_skip=n_frame)
    elif encoder_name == 'fft':
        encoder = FFTEncoder(frame_skip=n_frame)
    elif encoder_name == 'mel':
        encoder = MelSpecEncoder(frame_skip=n_frame)
    else:
        encoder = SampleEncoder()
    return encoder


# combine dimensions of a tensor
def magic_combine(x, dim_begin, dim_end):
    combined_shape = list(x.shape[:dim_begin]) + [-1] + list(x.shape[dim_end:])
    return x.view(combined_shape)


def save_checkpoint(actor, critic, actor_optimizer, critic_optimizer, iteration, encoder_name, experiment_id, rnn):
    """
    Save training checkpoint.
    """
    checkpoint = DotMap()
    # checkpoint.env = ENV
    checkpoint.iteration = iteration
    # checkpoint.stop_conditions = stop_conditions
    # checkpoint.hp = hp
    # CHECKPOINT_PATH = BASE_CHECKPOINT_PATH + f"{iteration}/"
    if not rnn:
        CHECKPOINT_PATH = f'{BASE_CHECKPOINT_PATH}/{encoder_name}/{experiment_id}/{iteration}/'
    else:
        CHECKPOINT_PATH = f'{BASE_CHECKPOINT_PATH}/{encoder_name}/rnn/{experiment_id}/{iteration}/'

    pathlib.Path(CHECKPOINT_PATH).mkdir(parents=True, exist_ok=True)
    with open(CHECKPOINT_PATH + "parameters.pt", "wb") as f:
        pickle.dump(checkpoint, f)
    with open(CHECKPOINT_PATH + "actor_class.pt", "wb") as f:
        pickle.dump(type(actor), f)
    with open(CHECKPOINT_PATH + "critic_class.pt", "wb") as f:
        pickle.dump(type(actor), f)
    torch.save(actor.state_dict(), CHECKPOINT_PATH + "actor.pt")
    torch.save(critic.state_dict(), CHECKPOINT_PATH + "critic.pt")
    torch.save(actor_optimizer.state_dict(), CHECKPOINT_PATH + "actor_optimizer.pt")
    torch.save(critic_optimizer.state_dict(), CHECKPOINT_PATH + "critic_optimizer.pt")


def load_checkpoint(encoder_name, experiment_id, iteration, rnn):
    if not rnn:
        CHECKPOINT_PATH = f'{BASE_CHECKPOINT_PATH}/{encoder_name}/{experiment_id}/{iteration}/'
    else:
        CHECKPOINT_PATH = f'{BASE_CHECKPOINT_PATH}/{encoder_name}/rnn/{experiment_id}/{iteration}/'
    with open(CHECKPOINT_PATH + 'parameters.pt', 'rb') as f:
        checkpoint = pickle.load(f)

    actor_state_dict = torch.load(CHECKPOINT_PATH + "actor.pt", map_location=torch.device(TRAIN_DEVICE))
    critic_state_dict = torch.load(CHECKPOINT_PATH + "critic.pt", map_location=torch.device(TRAIN_DEVICE))
    actor_optimizer_state_dict = torch.load(CHECKPOINT_PATH + "actor_optimizer.pt",
                                            map_location=torch.device(TRAIN_DEVICE))
    critic_optimizer_state_dict = torch.load(CHECKPOINT_PATH + "critic_optimizer.pt",
                                             map_location=torch.device(TRAIN_DEVICE))
    return actor_state_dict, critic_state_dict, \
           actor_optimizer_state_dict, critic_optimizer_state_dict,


if __name__ == '__main__':
    # EXPERIMENT_NAME
    parser = argparse.ArgumentParser()
    parser.add_argument('--encoder', type=str, choices=['conv1d', 'fft', 'mel'], default='conv1d', help='Choose an encoder for the Blind AI')
    parser.add_argument('--port', type=int, default=50051, help='Port used by DareFightingICE')
    parser.add_argument('--id', type=str, required=True, help='Experiment id')
    parser.add_argument('--p2', choices=['Sandbox', 'MctsAi10is'], type=str, required=True, help='The opponent AI')
    parser.add_argument('--recurrent', action='store_true', help='Use GRU')
    parser.add_argument('--n_frame', type=int, default=1, help='Number of frame to sample data')
    parser.add_argument('--epoch', type=int, default=10, help='Number of epochs to train')
    parser.add_argument('--training_iteration', type=int, default=60, help='Number of training iterations')
    parser.add_argument('--game_num', type=int, default=5, help='Number of games to play per iteration')
    args = parser.parse_args()
    logger.info('Input parameters:')
    logger.info(' '.join(f'{k}={v}' for k, v in vars(args).items()))
    actor, critic, actor_optim, critic_optim, iteration = init(args.encoder, args.id, args.n_frame, args.recurrent)
    logger.info(f'iteration {iteration}')
    while iteration < args.training_iteration - 1:
        logger.info(f'Start training at epoch: {iteration}')
        try:
            actor, critic, actor_optim, critic_optim, iteration = init(args.encoder, args.id, args.n_frame, args.recurrent)
            train_model(actor, critic, actor_optim, critic_optim, iteration, args.port, args.encoder, args.id, args.p2, args.recurrent, 
                        args.n_frame, args.epoch, args.training_iteration, args.game_num)
        except Exception as ex:
            print(ex)
            logger.error("Error occurred while collecting trajectories data, restarting")

