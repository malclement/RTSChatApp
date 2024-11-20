import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * The UDPServer class listens for incoming UDP packets on a specified port.
 * It receives messages from clients and displays the content along with the client's address and port.
 * The server runs continuously, processing messages until interrupted.
 *
 * The server binds to a specified port and listens for UDP packets, printing out the received message and
 * client details (IP address and port). The default port is 8000 if no port is provided.
 */
public class UDPServer {
    private final int port;
    private static final int BUFFER_SIZE = 1024;
    private static final int DEFAULT_PORT = 8000;

    /**
     * Constructor to initialize the UDP server with a custom port.
     *
     * @param port the port to bind the server to
     */
    public UDPServer(int port) {
        this.port = port;
    }

    /**
     * Default constructor which sets the port to 8000.
     */
    public UDPServer() {
        this.port = DEFAULT_PORT;
    }

    /**
     * Launches the UDP server, binding it to the specified port. The server listens indefinitely for UDP packets.
     * When a packet is received, the server prints the message along with the client's address and port.
     *
     * @throws IOException if there is an error receiving the packet or binding to the port
     */
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

    /**
     * Returns a string representation of the UDP server with the configured port.
     *
     * @return a string representing the UDP server's configuration
     */
    @Override
    public String toString() {
        return "UDPServer{" +
                "port=" + port +
                '}';
    }

    /**
     * The main method that starts the UDP server.
     * The server listens on the provided port or on the default port if no port is provided.
     *
     * @param args command-line arguments: the first argument is the port number (optional)
     * @throws IOException if there is an error while launching or receiving packets
     */
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
