import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class TCPClient {
    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    private final String serverAddress;
    private final int serverPort;

    public TCPClient(String serverAddress, int serverPort) {
        if (serverAddress == null || serverAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Server address cannot be null or empty.");
        }
        if (serverPort <= 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Port must be in the range 1 to 65535.");
        }
        try {
            InetAddress.getByName(serverAddress);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid server address: " + serverAddress, e);
        }

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Connected to the server. Type your message and press Enter to send. Press CTRL+D to exit.");

            String inputLine;
            while ((inputLine = userInput.readLine()) != null) {
                out.println(inputLine);
                String response = in.readLine();

                if (response != null) {
                    System.out.println("Received from server (hex): " + toHex(response));
                }
            }

        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }

    private String toHex(String text) {
        StringBuilder hexString = new StringBuilder();
        for (char c : text.toCharArray()) {
            hexString.append(String.format("%02x", (int) c));
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TCPClient <server_address> <port>");
            return;
        }

        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);

        TCPClient client = new TCPClient(serverAddress, port);
        client.start();
    }

    public String sendAndReceiveMessage(String message) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println(message);
            String response = in.readLine();
            return response != null ? toHex(response) : null;

        } catch (IOException e) {
            System.err.println("Error in client communication: " + e.getMessage());
            return null;
        }
    }
}
