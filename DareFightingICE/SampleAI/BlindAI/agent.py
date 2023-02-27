# game agent (implements aiinterface.AIInterface)
# TODO check the actions from RHEA_PPO bot (currently 56 by default)
# TODO collect data
import time
import numpy as np
import torch
from pyftg.ai_interface import AIInterface
from pyftg.struct import *

GATHER_DEVICE = 'cpu'

import logging
class SoundAgent(AIInterface):
    def __init__(self, **kwargs):
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
        self.pre_framedata: FrameData = None
        self.nonDelay: FrameData = None

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
    
    def name(self) -> str:
        return self.__class__.__name__
    
    def is_blind(self) -> bool:
        return False

    def initialize(self, gameData, player):
        # Initializng the command center, the simulator and some other things
        self.inputKey = Key()
        self.frameData = FrameData()
        self.cc = CommandCenter()
        self.player = player  # p1 == True, p2 == False
        self.gameData = gameData
        self.isGameJustStarted = True
        return 0

    def close(self):
        pass

    def get_information(self, frame_data: FrameData, is_control: bool, non_delay: FrameData):
        # Load the frame data every time getInformation gets called
        self.frameData = frame_data
        self.cc.set_frame_data(self.frameData, self.player)
        # nonDelay = self.frameData
        self.pre_framedata = self.nonDelay if self.nonDelay is not None else non_delay
        self.nonDelay = non_delay
        self.isControl = is_control
        self.currentFrameNum = self.frameData.current_frame_number  # first frame is 14


    def round_end(self, round_result: RoundResult):
        self.logger.info(round_result.remaining_hps[0])
        self.logger.info(round_result.remaining_hps[1])
        self.logger.info(round_result.elapsed_frame)
        self.just_inited = True
        obs = self.raw_audio_memory
        if obs is not None:
            self.collect_data_helper.put([obs, 0, True, None])
        # final step
        # state = torch.tensor(obs, dtype=torch.float32)
        terminal = 1
        true_reward = self.get_reward()
        self.collect_data_helper.finish_round()
        self.raw_audio_memory = None
        self.round_count += 1 
        self.logger.info('Finished {} round'.format(self.round_count))

    def input(self):
        return self.inputKey

    @torch.no_grad()
    def processing(self):
        # start_time = time.time()
        # First we check whether we are at the end of the round
        start_time = time.time() * 1000
        if self.frameData.empty_flag or self.frameData.current_frame_number <= 0:
            self.isGameJustStarted = True
            return
        # if self.cc.getSkillFlag():
        #     self.inputKey = self.cc.getSkillKey()
        #     return
        self.inputKey.empty()
        self.cc.skill_cancel()
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
        self.cc.command_call(self.actions[action])
        self.inputKey = self.cc.get_skill_key()
        # end_time = time.time() * 1000

    def get_reward(self):
        offence_reward = self.pre_framedata.get_character(not self.player).hp - self.nonDelay.get_character(not self.player).hp
        defence_reward = self.nonDelay.get_character(self.player).hp - self.pre_framedata.get_character(self.player).hp
        return offence_reward + defence_reward

    def set_last_hp(self):
        self.last_my_hp = self.nonDelay.get_character(self.player).hp
        self.last_opp_hp = self.nonDelay.get_character(not self.player).hp

    def get_audio_data(self, audio_data: AudioData):
        self.audio_data = audio_data
        # process audio
        try:
            byte_data = self.audio_data.raw_data_as_bytes
            np_array = np.frombuffer(byte_data, dtype=np.float32)
            raw_audio = np_array.reshape((2, 1024))
            raw_audio = raw_audio.T
            raw_audio = raw_audio[:800, :]
        except Exception as ex:
            raw_audio = np.zeros((800, 2))
        if self.raw_audio_memory is None:
            # self.logger.info('raw_audio_memory none {}'.format(raw_audio.shape))
            self.raw_audio_memory = raw_audio
        else:
            self.raw_audio_memory = np.vstack((raw_audio, self.raw_audio_memory))
            # self.raw_audio_memory = self.raw_audio_memory[:4, :, :]
            self.raw_audio_memory = self.raw_audio_memory[:800 * self.n_frame, :]

        # append so that audio memory has the first shape of n_frame
        increase = (800 * self.n_frame - self.raw_audio_memory.shape[0]) // 800
        for _ in range(increase):
            self.raw_audio_memory = np.vstack((np.zeros((800, 2)), self.raw_audio_memory))

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
