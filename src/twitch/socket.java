package twitch;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

static int SOCKET_PORT = 3001;
static String SERVER_CONNECTION = "Connected to Server!";

public class socket {
    static {
        try {
            Socket socket = IO.socket("http://localhost:" + SOCKET_PORT);

            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println(SERVER_CONNECTION);
            });

            socket.on("Jump", args -> {
                System.out.println("Jump!");
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
