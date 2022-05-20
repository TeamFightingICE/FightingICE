import sys
sys.path.append('../')
from time import sleep
from py4j.java_gateway import JavaGateway, GatewayParameters, CallbackServerParameters, get_field
from fight_agent import SoundAgent
import logging
logger = logging.getLogger(__name__)
def check_args(args):
	for i in range(argc):
		if args[i] == "-n" or args[i] == "--n" or args[i] == "--number":
			global GAME_NUM
			GAME_NUM = int(args[i+1])

def start_game(Character):

    for Chara in Character:
        # # Raw 120
        for i in range(1):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'Conv1D'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='raw', path='SampleAI\\BlindAI\\python\\trained_model'))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)
        
        # # FFT 120
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'FFT'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='fft', path='D:\\weights\\fft\\no_rnn_1_frame_256_mctsai65'))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)
        
        # # Mel 120
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'Mel'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='mel', path='D:\\weights\\mel\\no_rnn_1_frame_256_mctsai65'))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)

        # # FFT GRU
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'FFTGRU'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='fft', path='D:\\weights\\fft\\rnn_1_frame_256_mctsai65', rnn=True))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)
        
        # # Mel GRU
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'MelGRU'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='mel', path='D:\\weights\\mel\\rnn_1_frame_256_mctsai65', rnn=True))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)

        # Conv1D 4.5
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'Conv1D4.5'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='raw', path='D:\\weights\\raw\\no_rnn_1_frame_256_mctsai65_worse'))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)
        # FFT 4.5
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'FFT4.5'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='fft', path='D:\\weights\\fft\\no_rnn_1_frame_256_mctsai65_worse'))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)

        # Mel 4.5
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'Mel4.5'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='mel', path='D:\\weights\\mel\\no_rnn_1_frame_256_mctsai65_worse'))
            print("Start game")
            game = manager.createGame(Chara, Chara, ai_name, "MctsAi65", GAME_NUM)
            manager.runGame(game)
            print("After game")
            sys.stdout.flush()
            close_gateway(gateway)

        


def close_gateway(g):
	g.close_callback_server()
	g.close()
	
def main_process(Chara):
	check_args(args)
	start_game(Chara)

args = sys.argv
argc = len(args)
GAME_NUM = 1
Character = ["ZEN"]

main_process(Character)