import java.io.IOException;
import java.net.*;

public class UDPServer {
    private final int port;
    private static final int BUFFER_SIZE = 1024;
    private static final int DEFAULT_PORT = 8000;

    public UDPServer(int port) {
        this.port = port;
    }
    public UDPServer() {
        this.port = DEFAULT_PORT;
    }

    public void launch() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String receivedMessage = new String(packet.getData());

            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();

            System.out.println("Received from " + clientAddress + ":" + clientPort + " - " + receivedMessage);
        }
    }

    @Override
    public String toString() {
        return "UDPServer{" +
                "port=" + port +
                '}';
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            UDPServer server = new UDPServer();
            System.out.println(server);
            server.launch();
        }
        else {
            UDPServer server = new UDPServer(Integer.valueOf(args[0]));
            System.out.println(server);
            server.launch();
        }
    }
}
