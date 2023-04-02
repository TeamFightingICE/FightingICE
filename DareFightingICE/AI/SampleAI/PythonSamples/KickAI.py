from pyftg.ai_interface import AIInterface
from pyftg.struct import *

class KickAI(AIInterface):
    def __init__(self):
        super().__init__()
        self.blind_flag = True

    def name(self) -> str:
        return self.__class__.__name__

    def is_blind(self) -> bool:
        return self.blind_flag

    def initialize(self, game_data: GameData, player_number: int):
        self.cc = CommandCenter()
        self.key = Key()
        self.player = player_number

    def input(self) -> Key:
        return self.key

    def get_information(self, frame_data: FrameData, is_control: bool, non_delay_frame_data: FrameData):
        self.frame_data = frame_data
        self.cc.set_frame_data(self.frame_data, self.player)

    def get_screen_data(self, screen_data: ScreenData):
        self.screen_data = screen_data

    def get_audio_data(self, audio_data: AudioData):
        self.audio_data = audio_data

    def processing(self):
        if self.frame_data.empty_flag or self.frame_data.current_frame_number <= 0:
            return

        if self.cc.get_skill_flag():
            self.key = self.cc.get_skill_key()
        else:
            self.key.empty()
            self.cc.skill_cancel()

            self.cc.command_call("B")
    
    def round_end(self, round_result: RoundResult):
        print(round_result.remaining_hps[0])
        print(round_result.remaining_hps[1])
        print(round_result.elapsed_frame)

    def game_end(self):
        pass
