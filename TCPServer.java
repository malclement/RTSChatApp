import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

public class TCPServer {
    private int port;

    public TCPServer(int port) {
        this.port = port;
    }

    public TCPServer() {
        this(8080);
    }

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
        }
    }

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

    @Override
    public String toString() {
        return "TCPServer listening on port " + port;
    }

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;
        TCPServer server = new TCPServer(port);
        server.launch();
    }
}
