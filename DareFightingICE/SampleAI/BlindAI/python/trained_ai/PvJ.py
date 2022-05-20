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
        # FFT GRU
        for i in range(3):
            gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
            manager = gateway.entry_point
            ai_name = 'FFTGRU'
            manager.registerAI(ai_name, SoundAgent(gateway,logger=logger, encoder='fft', path='trained_model', rnn=True))
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