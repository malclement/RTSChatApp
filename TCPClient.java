import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient {
    private String serverAddress;
    private int serverPort;

    public TCPClient(String serverAddress, int serverPort) {
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
}
