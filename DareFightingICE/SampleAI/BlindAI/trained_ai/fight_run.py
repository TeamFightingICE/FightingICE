import logging
import time
import sys
from py4j.java_gateway import JavaGateway, GatewayParameters, CallbackServerParameters, get_field
from fight_agent import SoundAgent
logger = logging.getLogger(__name__)
gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242),
                            callback_server_parameters=CallbackServerParameters())
manager = gateway.entry_point
current_time = int(time.time() * 1000)
encoder1 = 'conv1d'
encoder2 = 'mel'
encoder3 = 'fft'
encoders = ['conv1d']
# register AIs
# collect_data_helper = CollectDataHelper(logger)
agent1 = SoundAgent(gateway, encoder=encoder1, logger=logger)
agent2 = SoundAgent(gateway, encoder=encoder2, logger=logger)
agent3 = SoundAgent(gateway, encoder=encoder3, logger=logger)
manager.registerAI(f'SoundAgent_{encoder1}', agent1)
manager.registerAI(f'SoundAgent_{encoder2}', agent2)
manager.registerAI(f'SoundAgent_{encoder3}', agent3)
# game = manager.createGame('ZEN', 'ZEN', f'SoundAgent_{encoder1}', 'MctsAi65', 1)
# game = manager.createGame('ZEN', 'ZEN', f'SoundAgent_{encoder2}', f'SoundAgent_{encoder3}', 3)
game = manager.createGame('ZEN', 'ZEN', f'SoundAgent_{encoder1}', 'MctsAi65', 3)

# start game
manager.runGame(game)

# finish game
logger.info('Finish game')
sys.stdout.flush()

# close gateway
gateway.close_callback_server()
gateway.close()
error = False