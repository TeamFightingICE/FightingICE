import logging
import argparse
from pyftg.gateway import Gateway
from KickAI import KickAI
from DisplayInfo import DisplayInfo

def start_game(port: int):
    gateway = Gateway(port=port)
    character = 'ZEN'
    game_num = 1
    agent1 = KickAI()
    agent2 = DisplayInfo()
    gateway.register_ai("KickAI", agent1)
    gateway.register_ai("DisplayInfo", agent2)
    gateway.run_game([character, character], ["KickAI", "DisplayInfo"], game_num)
    gateway.close()

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--log', default='INFO', type=str, choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'])
    parser.add_argument('--port', default=50051, type=int, help='Port used by DareFightingICE')
    args = parser.parse_args()
    logging.basicConfig(level=args.log)
    start_game(args.port)
