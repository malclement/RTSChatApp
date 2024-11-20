import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiServer {
    private final int port;

    public TCPMultiServer(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port + ". Must be between 1 and 65535.");
        }
        this.port = port;
    }

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
