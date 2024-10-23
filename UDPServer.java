import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
        DatagramSocket socket = new DatagramSocket(this.port);
        byte[] buffer = new byte[BUFFER_SIZE];
        System.out.println("UDP Server started on port " + socket.getLocalPort());

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println("UDP" + packet.getData());

            String receivedMessage = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

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
