import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TCPMultiServer is a multi-threaded server that listens on a specified port and accepts client connections.
 * Each client connection is handled by a separate thread, allowing multiple clients to interact simultaneously.
 * This server broadcasts messages to all clients except the one sending the message and manages client connections.
 */
public class TCPMultiServer {
    private final int port;
    private final ConcurrentHashMap<String, Socket> clients = new ConcurrentHashMap<>();
    private volatile boolean isRunning = true;

    /**
     * Constructor to initialize the server with a specified port number.
     *
     * @param port The port number on which the server will listen for incoming connections.
     * @throws IllegalArgumentException If the port number is not in the valid range (1-65535).
     */
    public TCPMultiServer(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port + ". Must be between 1 and 65535.");
        }
        this.port = port;
    }

    /**
     * Starts the server and begins accepting client connections. Each client connection is handled by a new
     * {@link ConnectionThread}. The server runs continuously until it is stopped.
     */
    public void launch() {
        System.out.println("Starting the multi-threaded server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new ConnectionThread(clientSocket, this).start();
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start the server: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * Broadcasts a message to all connected clients except the specified one.
     * The message is sent to the output stream of each client socket.
     *
     * @param message    The message to be broadcast to clients.
     * @param excludeUser The nickname of the user whose message should not be broadcasted.
     */
    public synchronized void broadcast(String message, String excludeUser) {
        System.out.println(message);

        clients.forEach((nickname, socket) -> {
            if (!nickname.equals(excludeUser)) {
                try {
                    new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8).println(message);
                } catch (IOException e) {
                    System.err.println("Failed to send message to " + nickname + ": " + e.getMessage());
                }
            }
        });
    }

    /**
     * Adds a new client to the server's list of active clients.
     * The client's nickname and associated socket are added to the clients map.
     *
     * @param nickname The nickname of the client.
     * @param socket   The client's socket connection.
     */
    public synchronized void addClient(String nickname, Socket socket) {
        clients.put(nickname, socket);
    }

    /**
     * Removes a client from the server's list of active clients.
     * The client's socket is closed, and the client is removed from the clients map.
     *
     * @param nickname The nickname of the client to be removed.
     */
    public synchronized void removeClient(String nickname) {
        Socket socket = clients.remove(nickname);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket for " + nickname + ": " + e.getMessage());
            }
        }
    }

    /**
     * Checks if a given nickname is already taken by another client.
     *
     * @param nickname The nickname to check.
     * @return {@code true} if the nickname is already taken, {@code false} otherwise.
     */
    public synchronized boolean isNicknameTaken(String nickname) {
        return clients.containsKey(nickname);
    }

    /**
     * Shuts down the server, stopping it from accepting new connections and closing all active client connections.
     * This method is invoked during server shutdown to cleanly terminate all resources.
     */
    public synchronized void shutdown() {
        System.out.println("Shutting down the server...");
        isRunning = false;

        clients.forEach((nickname, socket) -> {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close client socket for " + nickname + ": " + e.getMessage());
            }
        });

        clients.clear();
        System.out.println("Server shutdown complete.");
    }

    /**
     * The main entry point for the server program.
     * It reads the port number from the command-line arguments, validates it, and starts the server.
     *
     * @param args Command-line arguments where the first argument is the port number (optional).
     */
    public static void main(String[] args) {
        int port = 8080;

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer between 1 and 65535.");
            return;
        }

        TCPMultiServer server = new TCPMultiServer(port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.launch();
    }
}
