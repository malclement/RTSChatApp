import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    private final String serverAddress;
    private final int serverPort;

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

    // Helper method to validate IP address format
    private boolean isValidIPAddress(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

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
