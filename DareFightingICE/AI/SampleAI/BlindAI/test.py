import argparse
import logging
import os
import numpy as np
from common import BASE_CHECKPOINT_PATH

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
ch.setFormatter(formatter)
logger.addHandler(ch)

def analyze_fight_result(results: 'np.ndarray'):
    win_ratio = np.sum(results[:, 0] > results[:, 1]) / results.shape[0]
    avg_hp_diff = np.mean(results[:, 0] - results[:, 1])
    logger.info('Win ratio: {}'.format(win_ratio))
    logger.info('Avg. HP difference: {}'.format(avg_hp_diff))

def start_game(characters: 'list[str]', args: 'argparse.Namespace'):
    encoder, rnn, port, p2, game_num = args.encoder, args.recurrent, args.port, args.p2, args.game_num

    if args.model_path:
        model_path = args.model_path
        ai_name = 'SoundAgent'
    elif args.id and args.checkpoint:
        experiment_id, checkpoint = args.id, args.checkpoint
        ai_name = f'SoundAgent-{experiment_id}-{checkpoint}'
        model_path = os.path.join(BASE_CHECKPOINT_PATH, encoder, f'rnn\{experiment_id}' if rnn else experiment_id, str(checkpoint))
    else:
        print('Please specify either --model-path or --id and --checkpoint')
        return

    if os.path.exists(model_path):
        from agent.fight_agent import SoundAgent
        from pyftg.gateway import Gateway

        for character in characters:
            results = list()
            for i in range(game_num):
                gateway = Gateway(port=port)
                agent = SoundAgent(logger=logger, encoder=encoder, path=model_path, rnn=rnn, results=results)
                gateway.register_ai(ai_name, agent)
                logger.info('Start game {}'.format(i+1))
                gateway.run_game([character, character], [ai_name, p2], 1)
                logger.info('Game {} finished'.format(i+1))
                gateway.close()
            analyze_fight_result(np.array(results))
    else:
        print(f'No model found at {model_path}')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--encoder', type=str, choices=['conv1d', 'fft', 'mel'], default='conv1d', help='Choose an encoder for the Blind AI')
    parser.add_argument('--recurrent', action='store_true', help='Use GRU')
    parser.add_argument('--model-path', type=str, default=None, help='Path to the trained model')
    parser.add_argument('--port', type=int, default=50051, help='Port used by DareFightingICE')
    parser.add_argument('--id', type=str, default=None, help='Experiment id')
    parser.add_argument('--checkpoint', type=int, default=None, help='Checkpoint to load')
    parser.add_argument('--p2', choices=['Sandbox', 'MctsAi23i'], type=str, required=True, help='The opponent AI')
    parser.add_argument('--game-num', type=int, default=30, help='Number of games to play')
    args = parser.parse_args()
    characters = ['ZEN']
    logger.info('Input parameters:')
    logger.info(' '.join(f'{k}={v}' for k, v in vars(args).items()))
    start_game(characters, args)
