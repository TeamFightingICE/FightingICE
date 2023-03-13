import sys
import argparse
from fight_agent import SoundAgent
from pyftg.gateway import Gateway
import logging

sys.path.append('../')
logger = logging.getLogger(__name__)

def start_game(encoder: str, characters: 'list[str]', p2: str, game_num: int):
    for character in characters:
        # FFT GRU
        for _ in range(game_num):
            gateway = Gateway(port=50051)
            ai_name = 'FFTGRU'
            agent = SoundAgent(logger=logger, encoder=encoder, path='trained_model', rnn=True)
            gateway.register_ai(ai_name, agent)
            print("Start game")
            gateway.run_game([character, character], [ai_name, p2], 1)
            print("After game")
            sys.stdout.flush()
            gateway.close()

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--encoder', type=str, choices=['conv1d', 'fft', 'mel'], default='conv1d', help='Choose an encoder for the Blind AI')
    parser.add_argument('--port', type=int, default=50051, help='Port used by DareFightingICE')
    parser.add_argument('--p2', choices=['Sandbox', 'MctsAi10is'], type=str, required=True, help='The opponent AI')
    parser.add_argument('--game_num', type=int, default=30, help='Number of games to play')
    args = parser.parse_args()
    characters = ['ZEN']
    logger.info('Input parameters:')
    logger.info(' '.join(f'{k}={v}' for k, v in vars(args).items()))
    start_game(args.encoder, characters, args.p2, args.game_num)
