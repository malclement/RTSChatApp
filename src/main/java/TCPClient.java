import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * TCPClient is a command-line application that implements a TCP client capable of connecting to a server,
 * sending messages, and receiving responses. It uses the JCommander library for parsing command-line arguments.
 */
public class TCPClient {

    public int getServerPort() {
        return serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    private final String serverAddress;
    private final int serverPort;

    /**
     * Constructs a TCPClient instance with the specified server address and port.
     *
     * @param serverAddress The address of the server to connect to.
     * @param serverPort    The port of the server to connect to.
     * @throws IllegalArgumentException If the server address is invalid or the port is out of range.
     */
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

    /**
     * Converts a string to its hexadecimal representation.
     *
     * @param input The string to convert.
     * @return The hexadecimal representation of the input string.
     * @throws IllegalArgumentException If the input string is null.
     */
    public static String toHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char c : input.toCharArray()) {
            hexString.append(String.format("%02x", (int) c));
        }
        return hexString.toString();
    }

    /**
     * Starts the TCP client, allowing it to connect to the server, send messages, and receive responses.
     * The user can input messages via the console.
     */
    public void start() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Enter your nickname (leave blank for a random one): ");
            String nickname = userInput.readLine();
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = "User-" + UUID.randomUUID().toString().substring(0, 8);
            }
            out.println(nickname);

            System.out.println("Connected as " + nickname + ". Type your message and press Enter to send. Press CTRL+D to exit.");

            String inputLine;
            while ((inputLine = userInput.readLine()) != null) {
                System.out.println("Hexadecimal representation: " + toHex(inputLine));
                out.println(inputLine);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }

    /**
     * Represents the command-line arguments for the TCP client.
     * It uses JCommander annotations to define options.
     */
    public static class Args {
        @Parameter(names = {"-a", "--address"}, description = "Server address (e.g., localhost)", required = true)
        private String serverAddress;

        @Parameter(names = {"-p", "--port"}, description = "Server port (e.g., 8000)", required = true)
        private int serverPort;

        @Parameter(names = {"-h", "--help"}, help = true, description = "Display help message")
        private boolean help = false;
    }

    /**
     * The main entry point of the TCP client application. It parses command-line arguments
     * using JCommander and starts the client.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Args jArgs = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(jArgs)
                .programName("TCPClient")
                .build();

        try {
            commander.parse(args);

            if (jArgs.help) {
                commander.usage();
                return;
            }

            TCPClient client = new TCPClient(jArgs.serverAddress, jArgs.serverPort);
            client.start();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            commander.usage();
        }
    }
}
