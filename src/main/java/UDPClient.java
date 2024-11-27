import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * The UDPClient class sends messages to a UDP server using DatagramPackets.
 * It connects to a server specified by its IP address and port, sends the user input to the server,
 * and exits when the user types "exit".
 *
 * The client reads input from the console, validates the server address and port, and sends the messages
 * in UTF-8 encoding to the server.
 */
public class UDPClient {
    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    private final String serverAddress;
    private final int serverPort;

    /**
     * Constructor that initializes the UDPClient with the server address and port.
     *
     * @param serverAddress the IP address of the server to connect to
     * @param serverPort the port number to connect to on the server
     * @throws IllegalArgumentException if the server address is invalid or the port is out of the valid range
     */
    public UDPClient(String serverAddress, int serverPort) {
        if (serverAddress == null || serverAddress.isEmpty()) {
            throw new IllegalArgumentException("Server address cannot be null or empty.");
        }
        if (!isValidIPAddress(serverAddress)) {
            throw new IllegalArgumentException("Invalid IP address format: " + serverAddress);
        }
        if (serverPort < 1 || serverPort > 65535) {
            throw new IllegalArgumentException("Port number must be between 1 and 65535.");
        }

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * Helper method to validate the format of an IP address.
     * This checks if the provided string is a valid IP address using InetAddress.
     *
     * @param ip the IP address to validate
     * @return true if the IP address is valid, false otherwise
     */
    private boolean isValidIPAddress(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * Launches the client, allowing the user to input messages to send to the server.
     * The client sends messages as DatagramPackets over a DatagramSocket to the server.
     * The loop continues until the user types "exit".
     *
     * @throws IOException if there is an error while sending the DatagramPacket
     */
    public void launch() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverInetAddress = InetAddress.getByName(serverAddress);
        Console console = System.console();

        System.out.println("Server address: " + serverAddress);

        if (console == null) {
            System.err.println("No console available");
            return;
        }

        String userInput;
        while (true) {
            userInput = console.readLine();

            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("Exiting client.");
                break;
            }

            byte[] buffer = userInput.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverInetAddress, serverPort);
            socket.send(packet);

            System.out.println("Message sent to server.");
        }
    }

    /**
     * Main method to run the UDP client. This expects the server address and port as command-line arguments.
     * If the arguments are invalid, it displays the usage and exits.
     *
     * @param args command-line arguments: server address and port number
     * @throws IOException if there is an issue creating or sending the DatagramPacket
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java UDPClient <serverAddress> <serverPort>");
            return;
        }

        String serverAddress = args[0];
        int serverPort = Integer.valueOf(args[1]);

        UDPClient client = new UDPClient(serverAddress, serverPort);
        client.launch();
    }
}
