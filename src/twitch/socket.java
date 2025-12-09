package twitch;

import command.CommandTable;
import enumerate.Action;
import io.socket.client.IO;
import io.socket.client.Socket;
import manager.InputManager;
import struct.Key;

import java.net.URISyntaxException;

public class socket {
    private static final int SOCKET_PORT = 3001;
    private static final String SERVER_CONNECTION = "Connected to Server!";

    static {
        try {
            Socket socket = IO.socket("http://localhost:" + SOCKET_PORT);

            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println(SERVER_CONNECTION);
            });

            socket.on("jump", args ->{
                CommandTable.performOneTimeAction(Action.JUMP, true);
            });

            socket.on("attack", args ->{
                CommandTable.performOneTimeAction(Action.STAND_FA, true);
            });

            socket.on("defence", args ->{
                CommandTable.performOneTimeAction(Action.STAND_GUARD, true);
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
