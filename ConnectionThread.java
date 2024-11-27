import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * The {@code ConnectionThread} class is responsible for managing the communication between the server and a client.
 * Each client connection is handled by a separate thread to allow multiple clients to interact simultaneously.
 * This thread reads messages from the client, broadcasts them to other clients, and handles client disconnections.
 */
public class ConnectionThread extends Thread {
    private final Socket clientSocket;
    private final TCPMultiServer server;
    private String nickname;

    /**
     * Constructs a new {@code ConnectionThread} with the specified client socket and server reference.
     *
     * @param clientSocket The socket through which the client is connected to the server.
     * @param server The server instance responsible for broadcasting messages and managing clients.
     */
    public ConnectionThread(Socket clientSocket, TCPMultiServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    /**
     * Runs the thread to handle communication between the server and the client.
     * This includes:
     * <ul>
     *   <li>Requesting and setting the client's nickname.</li>
     *   <li>Adding the client to the server's list of active clients.</li>
     *   <li>Listening for messages from the client and broadcasting them to other clients.</li>
     *   <li>Handling client disconnection and notifying other clients.</li>
     * </ul>
     */
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            out.println("Enter your nickname (leave blank for a random one): ");
            out.flush();
            nickname = in.readLine();

            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = "User-" + clientSocket.getInetAddress().getHostAddress();
            }

            if (server.isNicknameTaken(nickname)) {
                Random rand = new Random();
                final int rndVal = rand.nextInt(100);
                nickname = nickname + "-" + rndVal;
            }

            server.addClient(nickname, clientSocket);
            server.broadcast(nickname + " joined the chat.", nickname);

            out.println("Connected as " + nickname + ". Type your message and press Enter to send. Press CTRL+D to exit.");
            out.flush();

            System.out.println(nickname + " has connected.");

            String message;
            while ((message = in.readLine()) != null) {
                server.broadcast(nickname + ": " + message, nickname);
            }
        } catch (IOException e) {
            System.err.println("Connection with " + (nickname != null ? nickname : "unknown user") + " lost: " + e.getMessage());
        } finally {
            if (nickname != null) {
                server.removeClient(nickname);
                server.broadcast(nickname + " has left the chat.", nickname);
                System.out.println(nickname + " has disconnected.");
            }

            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket for " + (nickname != null ? nickname : "unknown user") + ": " + e.getMessage());
            }
        }
    }


    /**
     * Returns the nickname of the client.
     *
     * @return The client's nickname.
     */
    public String getNickname() {
        return nickname;
    }
}
