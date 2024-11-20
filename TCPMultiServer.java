import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCPMultiServer is a multi-threaded TCP server that listens for client connections on a specified port.
 * For each incoming client connection, the server spawns a new thread (ConnectionThread) to handle the communication
 * with the client independently. This allows the server to manage multiple clients simultaneously.
 */
public class TCPMultiServer {
    private final int port;

    /**
     * Constructor to initialize the server with a specified port.
     *
     * @param port The port number the server will listen on.
     * @throws IllegalArgumentException If the provided port number is not in the valid range (1-65535).
     */
    public TCPMultiServer(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port + ". Must be between 1 and 65535.");
        }
        this.port = port;
    }

    /**
     * Launches the server and begins accepting client connections.
     * For each incoming connection, a new thread is created to handle communication with the client.
     */
    public void launch() {
        System.out.println("Starting the multi-threaded server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.printf("Connection accepted from %s:%d%n",
                            clientSocket.getInetAddress().getHostAddress(),
                            clientSocket.getPort()
                    );

                    ConnectionThread connectionThread = new ConnectionThread(clientSocket);
                    connectionThread.start();

                } catch (IOException e) {
                    System.err.println("Error accepting a client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start the server on port " + port + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Server has stopped.");
        }
    }

    /**
     * The main entry point of the server program. It reads the port number from the command-line arguments,
     * validates it, and starts the server.
     *
     * @param args Command-line arguments where the first argument is the port number.
     */
    public static void main(String[] args) {
        int port = 8080;

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
                if (port <= 0 || port > 65535) {
                    throw new IllegalArgumentException("Port must be between 1 and 65535. Provided: " + port);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer between 1 and 65535.");
            return;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            TCPMultiServer server = new TCPMultiServer(port);
            server.launch();
        } catch (Exception e) {
            System.err.println("Critical error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
