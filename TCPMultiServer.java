import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiServer {
    private final int port;

    public TCPMultiServer(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port);
        }
        this.port = port;
    }

    public void launch() {
        System.out.println("Starting the multi-threaded server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.printf("Connection accepted from %s:%d%n",
                        clientSocket.getInetAddress().getHostAddress(),
                        clientSocket.getPort()
                );

                ConnectionThread connectionThread = new ConnectionThread(clientSocket);
                connectionThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;
        TCPMultiServer server = new TCPMultiServer(port);
        server.launch();
    }
}
