# game agent (implements aiinterface.AIInterface)
# TODO check the actions from RHEA_PPO bot (currently 56 by default)
# TODO collect data
import time

import numpy as np

import torch

GATHER_DEVICE = 'cpu'

import logging
class SoundAgent:
    def __init__(self, gateway, **kwargs):
        self.gateway = gateway
        self.actor = kwargs.get('actor')
        self.critic = kwargs.get('critic')
        self.device = kwargs.get('device')
        self.logger = kwargs.get('logger')
        self.collect_data_helper = kwargs.get('collect_data_helper')
        self.rnn = kwargs.get('rnn')
        self.trajectories_data = None
        # original actions
        # self.actions = ['AIR', 'AIR_A', 'AIR_B', 'AIR_D_DB_BA', 'AIR_D_DB_BB', 'AIR_D_DF_FA', 'AIR_D_DF_FB', 'AIR_DA',
        #                 'AIR_DB', 'AIR_F_D_DFA', 'AIR_F_D_DFB', 'AIR_FA', 'AIR_FB', 'AIR_GUARD', 'AIR_GUARD_RECOV',
        #                 'AIR_RECOV', 'AIR_UA', 'AIR_UB', 'BACK_JUMP', 'BACK_STEP', 'CHANGE_DOWN', 'CROUCH', 'CROUCH_A',
        #                 'CROUCH_B', 'CROUCH_FA', 'CROUCH_FB', 'CROUCH_GUARD', 'CROUCH_GUARD_RECOV', 'CROUCH_RECOV',
        #                 'DASH', 'DOWN', 'FOR_JUMP', 'FORWARD_WALK', 'JUMP', 'LANDING', 'NEUTRAL', 'RISE', 'STAND',
        #                 'STAND_A', 'STAND_B', 'STAND_D_DB_BA', 'STAND_D_DB_BB', 'STAND_D_DF_FA', 'STAND_D_DF_FB',
        #                 'STAND_D_DF_FC', 'STAND_F_D_DFA', 'STAND_F_D_DFB', 'STAND_FA', 'STAND_FB', 'STAND_GUARD',
        #                 'STAND_GUARD_RECOV', 'STAND_RECOV', 'THROW_A', 'THROW_B', 'THROW_HIT', 'THROW_SUFFER']
        # from RHEA_PPO
        self.actions = "AIR_A", "AIR_B", "AIR_D_DB_BA", "AIR_D_DB_BB", "AIR_D_DF_FA", "AIR_D_DF_FB", "AIR_DA", "AIR_DB", \
                       "AIR_F_D_DFA", "AIR_F_D_DFB", "AIR_FA", "AIR_FB", "AIR_UA", "AIR_UB", "BACK_JUMP", "BACK_STEP", \
                       "CROUCH_A", "CROUCH_B", "CROUCH_FA", "CROUCH_FB", "CROUCH_GUARD", "DASH", "FOR_JUMP", "FORWARD_WALK", \
                       "JUMP", "NEUTRAL", "STAND_A", "STAND_B", "STAND_D_DB_BA", "STAND_D_DB_BB", "STAND_D_DF_FA", \
                       "STAND_D_DF_FB", "STAND_D_DF_FC", "STAND_F_D_DFA", "STAND_F_D_DFB", "STAND_FA", "STAND_FB", \
                       "STAND_GUARD", "THROW_A", "THROW_B"
        self.audio_data = None
        self.raw_audio_memory = None
        self.just_inited = True
        self.pre_framedata = None
        self.nonDelay = None

        # data of 1 rounds
        # self.round_state_list = []
        # self.round_value_list = []
        # self.round_action_list = []
        # self.round_action_probs_list = []
        # self.round_true_reward_list = []
        # self.round_reward_list = []
        # self.round_terminal_list = []
        # self.round_actor_hidden_state_list = []
        # self.round_actor_cell_state_list = []
        # self.round_critic_hidden_state_list = []
        # self.round_critic_cell_state_list = []

        # data over all rounds
        # self.state_list = []
        # self.value_list = []
        # self.action_list = []
        # self.action_probs_list = []
        # self.true_reward_list = []
        # self.reward_list = []
        # self.terminal_list = []
        # self.actor_hidden_state_list = []
        # self.actor_cell_state_list = []
        # self.critic_hidden_state_list = []
        # self.critic_cell_state_list = []
        # self.episode_lengths = []
        if self.rnn:
            self.actor.get_init_state(GATHER_DEVICE)
            self.critic.get_init_state(GATHER_DEVICE)
        self.round_count = 0
        self.n_frame = kwargs.get('n_frame')
        # self.collect_data_helper = CollectDataHelper()

    def initialize(self, gameData, player):
        # Initializng the command center, the simulator and some other things
        self.inputKey = self.gateway.jvm.struct.Key()
        self.frameData = self.gateway.jvm.struct.FrameData()
        self.cc = self.gateway.jvm.aiinterface.CommandCenter()
        self.player = player  # p1 == True, p2 == False
        self.gameData = gameData
        self.simulator = self.gameData.getSimulator()
        self.isGameJustStarted = True
        return 0

    def close(self):
        pass

    def getInformation(self, frameData, inControl, nonDelay):
        # Load the frame data every time getInformation gets called
        self.frameData = frameData
        self.cc.setFrameData(self.frameData, self.player)
        # nonDelay = self.frameData
        self.pre_framedata = self.nonDelay if self.nonDelay is not None else nonDelay
        self.nonDelay = nonDelay
        self.isControl = inControl
        self.currentFrameNum = nonDelay.getFramesNumber()  # first frame is 14

    def roundEnd(self, x, y, z):
        self.logger.info(x)
        self.logger.info(y)
        self.logger.info(z)
        self.just_inited = True
        obs = self.raw_audio_memory
        if obs is not None:
            self.collect_data_helper.put([obs, 0, True, None])
        # final step
        # state = torch.tensor(obs, dtype=torch.float32)
        terminal = 1
        # with torch.no_grad():
        #     value = self.critic(state.unsqueeze(0).to(self.device), terminal=terminal)
            # future value for terminal episodes is 0
            # self.round_value_list.append(value.squeeze().to(self.device) * 0)
        # reward
        true_reward = self.get_reward()
        # previous state
        # self.round_true_reward_list.append(torch.tensor(true_reward).float())
        # self.round_reward_list.append(torch.tensor(self.normalize_reward(true_reward)).float())
        # self.round_terminal_list.append(torch.tensor(terminal).float())
        # # final state
        # self.round_true_reward_list.append(torch.tensor(0).float())
        # self.round_reward_list.append(torch.tensor(self.normalize_reward(0)).float())
        # self.round_terminal_list.append(torch.tensor(1).float())
        # print('reward', len(self.round_reward_list), 'value', len(self.round_value_list), 'state',
        #       len(self.round_state_list))
        # append 1 round's data
        # self.state_list.append(self.round_state_list)
        # self.value_list.append(self.round_value_list)
        # self.action_list.append(self.round_action_list)
        # self.action_probs_list.append(self.round_action_probs_list)
        # self.true_reward_list.append(self.round_true_reward_list)
        # self.reward_list.append(self.round_reward_list)
        # self.terminal_list.append(self.round_terminal_list)
        # self.actor_hidden_state_list.append(self.round_actor_hidden_state_list)
        # self.actor_cell_state_list.append(self.round_actor_cell_state_list)
        # self.critic_hidden_state_list.append(self.round_critic_hidden_state_list)
        # self.critic_cell_state_list.append(self.round_critic_cell_state_list)
        # self.episode_lengths.append(len(self.round_state_list))

        # reset round data
        # self.round_state_list = []
        # self.round_value_list = []
        # self.round_action_list = []
        # self.round_action_probs_list = []
        # self.round_true_reward_list = []
        # self.round_reward_list = []
        # self.round_terminal_list = []
        # self.round_actor_hidden_state_list = []
        # self.round_actor_cell_state_list = []
        # self.round_critic_hidden_state_list = []
        # self.round_critic_cell_state_list = []

        self.collect_data_helper.finish_round()
        self.raw_audio_memory = None
        self.round_count += 1 
        self.logger.info('Finished {} round'.format(self.round_count))

    # please define this method when you use FightingICE version 4.00 or later
    def getScreenData(self, sd):
        pass
        # start_time = time.time()
        # data = sd.getDisplayByteBufferAsBytes()
        # # tmp = np.frombuffer(data)
        # end_time = time.time()
        # print('screen', (end_time - start_time) * 1000)

    def input(self):
        return self.inputKey

    @torch.no_grad()
    def processing(self):
        # start_time = time.time()
        # First we check whether we are at the end of the round
        start_time = time.time() * 1000
        if self.frameData.getEmptyFlag() or self.frameData.getRemainingFramesNumber() <= 0:
            self.isGameJustStarted = True
            return
        # if self.cc.getSkillFlag():
        #     self.inputKey = self.cc.getSkillKey()
        #     return
        self.inputKey.empty()
        self.cc.skillCancel()
        # if not self.isGameJustStarted:
        #     pass
        # else:
        #     # initialize the argment at 1st frame of round
        #     self.isGameJustStarted = False
        #
        # if self.cc.getSkillFlag():
        #     self.inputKey = self.cc.getSkillKey()
        #     return
        # self.inputKey.empty()
        # self.cc.skillCancel()
        # same as env.reset()
        # if self.just_inited:

        # for lstm
        # self.actor_hidden_state_list.append(self.actor.hidden_cell[0].squeeze(0).cpu())
        # self.actor_cell_state_list.append(self.actor.hidden_cell[1].squeeze(0).cpu())
        # self.critic_hidden_state_list.append(self.critic.hidden_cell[0].squeeze(0).cpu())
        # self.critic_cell_state_list.append(self.critic.hidden_cell[1].squeeze(0).cpu())
        # if self.currentFrameNum == 14:
        # self.set_last_hp()
        # for gru
        obs = self.raw_audio_memory
        if self.just_inited:
            self.just_inited = False
            if obs is None:
                obs = np.zeros((800 * self.n_frame, 2))
            self.collect_data_helper.put([obs])
            terminal = 1
        elif obs is None:
            obs = np.zeros((800 * self.n_frame, 2))
            self.collect_data_helper.put([obs])
        else:
            terminal = 0
            reward = self.get_reward()
            self.collect_data_helper.put([obs, reward, False, None])

        # get action
        # self.round_actor_hidden_state_list.append(self.actor.hidden_cell.squeeze(0).to(self.device))
        state = torch.tensor(obs, dtype=torch.float32)
        action_dist = self.actor(state.unsqueeze(0).to(self.device), terminal=torch.tensor(terminal).float())
        action = action_dist.sample()
        # put to helper
        self.collect_data_helper.put_action(action)
        if self.rnn:
            self.collect_data_helper.put_actor_hidden_data(self.actor.hidden_cell.squeeze(0).to(self.device))
        #
        self.cc.commandCall(self.actions[action])
        self.inputKey = self.cc.getSkillKey()

            ## old
            # self.round_actor_hidden_state_list.append(self.actor.hidden_cell.squeeze(0).to(self.device))
            # self.round_critic_hidden_state_list.append(self.critic.hidden_cell.squeeze(0).to(self.device))
            #
            # state = torch.tensor(self.raw_audio_memory, dtype=torch.float32)
            # self.round_state_list.append(state)
            # value = self.critic(state.unsqueeze(0).to(self.device))  # terminal?
            # self.round_value_list.append(value.squeeze().to(self.device))
            # action_dist = self.actor(state.unsqueeze(0).to(self.device))
            # action = action_dist.sample()
            # self.round_action_list.append(action.squeeze().to(self.device))
            # self.round_action_probs_list.append(action_dist.log_prob(action).to(self.device))
            # if not self.just_inited:
            #     done = 0
            #     done = torch.tensor(done).float()
            #     true_reward = self.get_reward()
            #     true_reward = torch.tensor(true_reward).float()
            #     reward = self.normalize_reward(true_reward)
            #     reward = torch.tensor(reward).float()
            #     self.round_terminal_list.append(done)
            #     self.round_true_reward_list.append(true_reward)
            #     self.round_reward_list.append(reward)
            #     # self.just_inited = False
            #
            # self.just_inited = False
            # self.cc.commandCall(self.actions[action])
            # self.inputKey = self.cc.getSkillKey()
            # with open('log.txt', 'a') as f:
            #     data = 'reward {} value {} state {}\n'.format(len(self.round_reward_list), len(self.round_value_list),
            #                                                   len(self.round_state_list))
            #     f.write(data)
        # end_time = time.time()
        # print('processing time', end_time - start_time)
        # if self.currentFrameNum == 14:
        #     pass
        # elif self.isControl:
        #     pass
        # process audio hidden state
        end_time = time.time() * 1000
        # print('processing time', end_time - start_time)
        # print(np.sum(obs))
    # maximin normalization
    def normalize_reward(self, reward):
        return (reward + 400) / 800

    def get_reward(self):
        # offence_reward = self.last_opp_hp - self.nonDelay.getCharacter(not self.player).getHp()
        # defence_reward = self.nonDelay.getCharacter(self.player).getHp() - self.last_my_hp
        offence_reward = self.pre_framedata.getCharacter(not self.player).getHp() - self.nonDelay.getCharacter(
            not self.player).getHp()
        defence_reward = self.nonDelay.getCharacter(self.player).getHp() - self.pre_framedata.getCharacter(
            self.player).getHp()
        return offence_reward + defence_reward

    def set_last_hp(self):
        self.last_my_hp = self.nonDelay.getCharacter(self.player).getHp()
        self.last_opp_hp = self.nonDelay.getCharacter(not self.player).getHp()

    @torch.no_grad()
    def give_action(self):
        state = self.raw_audio_memory
        action_dist = self.actor

    def getAudioData(self, audio_data):
        self.audio_data = audio_data
        # process audio
        try:
            start_time = time.time() * 1000
            byte_data = self.audio_data.getRawDataAsBytes()
            np_array = np.frombuffer(byte_data, dtype=np.float32)
            raw_audio = np_array.reshape((2, 1024))
            raw_audio = raw_audio.T
            # raw_audio = np_array.reshape((1024, 2))
            raw_audio = raw_audio[:800, :]
            end_time = time.time() * 1000
            # windows 1-2 ms
            # linux 40 ms
            # print('total time', end_time - start_time)
        except Exception as ex:
            # print('no audio', self.currentFrameNum)
            raw_audio = np.zeros((800, 2))
            # print(ex)
            # raise ex # test
        if self.raw_audio_memory is None:
            self.logger.info('raw_audio_memory none {}'.format(raw_audio.shape))
            # self.raw_audio_memory = np.expand_dims(raw_audio, axis=0)
            self.raw_audio_memory = raw_audio
        else:
            # self.raw_audio_memory = np.vstack((np.expand_dims(raw_audio, axis=0), self.raw_audio_memory))
            self.raw_audio_memory = np.vstack((raw_audio, self.raw_audio_memory))
            # self.raw_audio_memory = self.raw_audio_memory[:4, :, :]
            self.raw_audio_memory = self.raw_audio_memory[:800 * self.n_frame, :]

        # append so that audio memory has the first shape of 4
        increase = (800 * self.n_frame - self.raw_audio_memory.shape[0]) // 800
        for _ in range(increase):
            # self.raw_audio_memory = np.vstack((np.zeros((1, 1024, 2)), self.raw_audio_memory))
            self.raw_audio_memory = np.vstack((np.zeros((800, 2)), self.raw_audio_memory))

    # def get_trajectories_data(self):
    #     trajectories_data = {
    #         "actions": self.action_list,
    #         "action_probabilities": self.action_probs_list,
    #         "states": self.state_list,
    #         "rewards": self.reward_list,
    #         "values": self.value_list,
    #         "true_rewards": self.true_reward_list,
    #         "terminals": self.terminal_list,
    #         # add hidden state list
    #         "actor_hidden_states": self.actor_hidden_state_list,
    #         # "actor_cell_states": self.actor_cell_state_list,
    #         "critic_hidden_states": self.critic_hidden_state_list,
    #         # "critic_cell_states": self.critic_cell_state_list
    #     }
    #     # return tensor
    #     return {key: [torch.stack(v) for v in value] for key, value in trajectories_data.items()}, self.episode_lengths

    class Java:
        implements = ["aiinterface.AIInterface"]

    def reset(self):
        self.collect_data_helper = CollectDataHelper(self.logger)

class CollectDataHelper:
    total_round_data = []
    total_round_action_data = []
    total_round_action_dist_data = []
    total_round_actor_hidden_data = []

    current_round_data = []
    current_round_action = []
    current_round_action_dist_data = []
    current_round_actor_hidden_data = []
    # curr_idx = 0

    def __init__(self, logger) -> None:
        self.total_round_data = []
        self.total_round_action_data = []
        self.total_round_action_dist_data = []
        self.total_round_actor_hidden_data = []

        self.current_round_data = []
        self.current_round_action = []
        self.current_round_action_dist_data = []
        self.current_round_actor_hidden_data = []
        self.logger = logger
        self.logger.info('create new data helper')
    def put(self, data):
        if(len(data) == 1):
            self.logger.info('put data at game reset')
            if len(self.current_round_data) > 0 and len(self.current_round_data[-1]) == 1:
                self.logger.info('game reset data exists')
        self.current_round_data.append(data)

    def put_action(self, action):
        self.current_round_action.append(action)

    def put_action_dist(self, action_dist):
        self.current_round_action_dist_data.append(action_dist)

    def put_actor_hidden_data(self, hidden_data):
        self.current_round_actor_hidden_data.append(hidden_data)

    # def get(self):
    #     data = self.current_round_data[self.curr_idx]
    #     self.curr_idx += 1
    #     return data

    def finish_round(self):
        self.total_round_data.append(self.current_round_data)
        self.current_round_data = []

        self.total_round_action_data.append(self.current_round_action)
        self.current_round_action = []

        self.total_round_action_dist_data.append(self.current_round_action_dist_data)
        self.current_round_action_dist_data = []

        self.total_round_actor_hidden_data.append(self.current_round_actor_hidden_data)
        self.current_round_actor_hidden_data = []

    def reset(self):
        self.total_round_data = []
        self.current_round_data = []

        self.total_round_action_data = []
        self.current_round_action = []

        self.total_round_action_dist_data = []
        self.current_round_action_dist_data = []

        self.total_round_actor_hidden_data = []
        self.current_round_actor_hidden_data = []


class SandboxAgent:
    def __init__(self, gateway, **kwargs):
        self.gateway = gateway
        self.nonDelay = None
        # self.logger = kwargs.get('logger')

    def initialize(self, gameData, player):
        # Initializng the command center, the simulator and some other things
        self.inputKey = self.gateway.jvm.struct.Key()
        self.frameData = self.gateway.jvm.struct.FrameData()
        self.cc = self.gateway.jvm.aiinterface.CommandCenter()
        self.player = player  # p1 == True, p2 == False
        self.gameData = gameData
        self.simulator = self.gameData.getSimulator()
        self.isGameJustStarted = True
        return 0

    def close(self):
        pass

    def getInformation(self, frameData, inControl, nonDelay):
        # Load the frame data every time getInformation gets called
        self.frameData = frameData
        self.cc.setFrameData(self.frameData, self.player)
        # nonDelay = self.frameData
        self.pre_framedata = self.nonDelay if self.nonDelay is not None else nonDelay
        self.nonDelay = nonDelay
        self.isControl = inControl
        self.currentFrameNum = nonDelay.getFramesNumber()  # first frame is 14

    def roundEnd(self, x, y, z):
        print(x)
        print(y)
        print(z)
    
    def getScreenData(self, sd):
        pass
        # start_time = time.time()
        # data = sd.getDisplayByteBufferAsBytes()
        # # tmp = np.frombuffer(data)
        # end_time = time.time()
        # print('screen', (end_time - start_time) * 1000)

    def input(self):
        return self.inputKey
    def processing(self):
        self.inputKey.empty()
        self.cc.skillCancel()

    def getAudioData(self, audio_data):
        pass

    class Java:
        implements = ["aiinterface.AIInterface"]
