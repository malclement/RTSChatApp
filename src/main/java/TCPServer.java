import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

/**
 * The TCPServer class represents a simple TCP server that listens for incoming client connections on a specified port.
 * When a client connects, the server reads messages from the client, prints them to the console,
 * and sends an echo message back to the client with the client's IP address.
 */
public class TCPServer {
    public int getPort() {
        return port;
    }

    private final int port;
    private ServerSocket serverSocket;

    /**
     * Constructs a TCPServer instance with a specified port number.
     *
     * @param port the port number to bind the server to
     * @throws IllegalArgumentException if the port is not within the valid range (1-65535)
     */
    public TCPServer(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port);
        }
        this.port = port;
    }

    /**
     * Constructs a TCPServer instance with a default port number (8080).
     */
    public TCPServer() {
        this(8080);
    }

    /**
     * Starts the server and begins listening for client connections.
     * Once a client connects, the server accepts the connection,
     * prints client details, and handles the communication in a loop.
     */
    public void launch() {
        System.out.println("Starting the server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            Socket clientSocket = serverSocket.accept();
            System.out.printf("Connection accepted from %s:%d%n",
                    clientSocket.getInetAddress().getHostAddress(),
                    clientSocket.getPort()
            );

            handleClient(clientSocket);

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            throw new RuntimeException("Server failed to start due to port conflict or other issue: " + e.getMessage());
        }
    }

    /**
     * Handles communication with the connected client.
     * It reads the messages from the client and sends an echo message back
     * containing the client's IP address.
     *
     * @param clientSocket the socket representing the client connection
     */
    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.printf("Received from %s: %s%n", clientAddress, inputLine);
                out.println(clientAddress + ": " + inputLine);
            }

        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connection with client closed.");
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    /**
     * Stops the server by closing the ServerSocket, effectively shutting it down.
     *
     * @throws IOException if an I/O error occurs while closing the server socket
     */
    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            System.out.println("Stopping the server...");
            serverSocket.close();
            System.out.println("Server stopped.");
        }
    }

    /**
     * Provides a string representation of the server, indicating the port it is listening on.
     *
     * @return a string representing the server
     */
    @Override
    public String toString() {
        return "TCPServer listening on port " + port;
    }

    /**
     * The main method that starts the server. It accepts an optional command-line argument for the port number.
     * If no argument is provided, the default port (8080) is used.
     *
     * @param args command-line arguments (optional: port number)
     */
    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;
        TCPServer server = new TCPServer(port);
        server.launch();
    }

}
