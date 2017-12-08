import sys
from time import sleep

from py4j.java_gateway import JavaGateway, GatewayParameters, CallbackServerParameters, get_field

gateway = JavaGateway(gateway_parameters=GatewayParameters(port=4242), callback_server_parameters=CallbackServerParameters());
manager = gateway.entry_point

print("Replay: Loading")
replay = manager.loadReplay("Replay_File_Name") # Load replay data

print("Replay: Init")
replay.init()

# Main process
for i in range(100): # Simulate 100 frames
    print("Replay: Run frame", i)

    if i % 10 == 0:
        framedata = replay.getReplayFrameData()

        print("Replay: Infos")
        print("Replay:     Round:", framedata.getRound())
        print("Replay:     Frame:", framedata.getFrameNumber())
        print("Replay:     P1 HP:", framedata.getP1().getHp())
        print("Replay:     P2 HP:", framedata.getP2().getHp())

    sys.stdout.flush()

    replay.update()

print("Replay: Close")
replay.close()

sys.stdout.flush()

gateway.close_callback_server()
gateway.close()