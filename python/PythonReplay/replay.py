import sys, os
from time import sleep
sys.path .append('../')
from py4j.java_gateway import JavaGateway, GatewayParameters, CallbackServerParameters, get_field

gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
manager = gateway.entry_point

print("Replay: Loading")
replay = manager.loadReplay("HPMode_KickAIPython_RandomAI_2017.12.07-15.51.44") # Load replay data

print("Replay: Init")
replay.init()

# Main process
for i in range(1000): # Simulate 100 frames
    print("Replay: Run frame", i)

    if i % 10 == 0 and replay.getState().name() == "UPDATE":
        framedata = replay.getFrameData()

        print("Replay: Infos")
        print("Replay:     Round:", framedata.getRound())
        print("Replay:     Frame:", framedata.getFramesNumber())
        print("Replay:     P1 HP:", framedata.getCharacter(True).getHp())
        print("Replay:     P2 HP:", framedata.getCharacter(False).getHp())

    sys.stdout.flush()

    replay.updateState()

print("Replay: Close")
replay.close()

sys.stdout.flush()

gateway.close_callback_server()
gateway.close()