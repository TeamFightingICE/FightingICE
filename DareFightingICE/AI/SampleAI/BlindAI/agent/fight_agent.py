import numpy as np
import os
import torch
from model import FeedForwardActor, RecurrentActor
from encoder import RawEncoder, FFTEncoder, MelSpecEncoder, SampleEncoder
from pyftg.ai_interface import AIInterface
from pyftg.struct import *
from common import STATE_DIM

class SoundAgent(AIInterface):
    def __init__(self, **kwargs):
        self.encoder = kwargs.get('encoder')
        self.logger = kwargs.get('logger')
        self.results = kwargs.get('results')
        self.path = kwargs.get('path')
        self.rnn = kwargs.get('rnn', False)

        self.actions = "AIR_A", "AIR_B", "AIR_D_DB_BA", "AIR_D_DB_BB", "AIR_D_DF_FA", "AIR_D_DF_FB", "AIR_DA", "AIR_DB", \
                       "AIR_F_D_DFA", "AIR_F_D_DFB", "AIR_FA", "AIR_FB", "AIR_UA", "AIR_UB", "BACK_JUMP", "BACK_STEP", \
                       "CROUCH_A", "CROUCH_B", "CROUCH_FA", "CROUCH_FB", "CROUCH_GUARD", "DASH", "FOR_JUMP", "FORWARD_WALK", \
                       "JUMP", "NEUTRAL", "STAND_A", "STAND_B", "STAND_D_DB_BA", "STAND_D_DB_BB", "STAND_D_DF_FA", \
                       "STAND_D_DF_FB", "STAND_D_DF_FC", "STAND_F_D_DFA", "STAND_F_D_DFB", "STAND_FA", "STAND_FB", \
                       "STAND_GUARD", "THROW_A", "THROW_B"
        self.audio_data = None
        self.raw_audio_memory = None
        self.just_inited = True
        self.device = 'cpu'
        self.n_frame = 1
        if not self.rnn:
            self.actor = FeedForwardActor(STATE_DIM[self.n_frame][self.encoder], 512, 1, get_sound_encoder(self.encoder))
        else:
            self.actor = RecurrentActor(STATE_DIM[self.n_frame][self.encoder], 512, 1, get_sound_encoder(self.encoder))
        self.load_actor()
        if self.rnn:
            self.actor.get_init_state(self.device)
        self.round_count = 0
    
    def name(self) -> str:
        return self.__class__.__name__

    def is_blind(self) -> bool:
        return True

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
        self.isControl = is_control

    def round_end(self, round_result: RoundResult):
        self.logger.info(round_result.remaining_hps[0])
        self.logger.info(round_result.remaining_hps[1])
        self.logger.info(round_result.elapsed_frame)
        if self.results is not None:
            self.results.append([round_result.remaining_hps[0], round_result.remaining_hps[1]])
        self.just_inited = True
        self.raw_audio_memory = None
        self.round_count += 1
        self.logger.info('Finished {} round'.format(self.round_count))

    def input(self):
        return self.inputKey

    @torch.no_grad()
    def processing(self):
#         if self.frameData.getEmptyFlag() or self.frameData.getRemainingFramesNumber() <= 0:
#             self.isGameJustStarted = True
#             return
        self.inputKey.empty()
        self.cc.skill_cancel()
        obs = self.raw_audio_memory
        if self.just_inited:
            self.just_inited = False
            action_idx = np.random.choice(40, 1, replace=False)[0]
        else:
            if obs is None:
                obs = np.zeros((800 * self.n_frame, 2))
            state = torch.tensor(obs, dtype=torch.float32)
            action_idx = self.actor.act(state.unsqueeze(0).to(self.device)).float()
            action_idx = torch.argmax(action_idx)
        self.cc.command_call(self.actions[int(action_idx)])
        self.inputKey = self.cc.get_skill_key()

    def get_audio_data(self, audio_data: AudioData):
        self.audio_data = audio_data
        # process audio
        try:
            byte_data = self.audio_data.raw_data_as_bytes
            np_array = np.frombuffer(byte_data, dtype=np.float32)
            np_array = np_array.reshape((2, 1024))
            np_array = np_array.T
            raw_audio = np_array[:800, :]
            if not self.player:
                raw_audio = raw_audio[:,::-1].copy()
        except Exception as ex:
            raw_audio = np.zeros((800, 2))
        if self.raw_audio_memory is None:
            # self.raw_audio_memory = np.expand_dims(raw_audio, axis=0)
            self.raw_audio_memory = raw_audio
        else:
            self.raw_audio_memory = np.vstack((raw_audio, self.raw_audio_memory))
            self.raw_audio_memory = self.raw_audio_memory[:800 * self.n_frame, :]

        # append so that audio memory has the first shape of n_frame
        increase = (self.n_frame * 800 - self.raw_audio_memory.shape[0]) // 800
        for _ in range(increase):
            self.raw_audio_memory = np.vstack((np.zeros((800, 2)), self.raw_audio_memory))

    def load_actor(self):
        actor_state_dict = torch.load(os.path.join(self.path, "actor.pt"), map_location=torch.device(self.device))
        self.actor.load_state_dict(actor_state_dict, strict=True)

def get_sound_encoder(encoder_name, n_frame=1):
    encoder = None
    if encoder_name == 'conv1d':
        encoder = RawEncoder(frame_skip=n_frame)
    elif encoder_name == 'fft':
        encoder = FFTEncoder(frame_skip=n_frame)
    elif encoder_name == 'mel':
        encoder = MelSpecEncoder(frame_skip=n_frame)
    else:
        encoder = SampleEncoder(frame_skip=n_frame)
    return encoder
