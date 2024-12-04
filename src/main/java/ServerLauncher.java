import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code ServerLauncher} class serves as an entry point for launching either
 * a TCP or UDP server. It prompts the user to select the desired server mode (TCP/UDP)
 * and a port number, validates the input, and then starts the appropriate server.
 */
public class ServerLauncher {

    /**
     * Main method to launch the server based on user input.
     * Users can select between TCP and UDP modes and provide a custom port number.
     * The default port is 8080 if no valid port is provided.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Set<String> validModes = new HashSet<>();
        validModes.add("TCP");
        validModes.add("UDP");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select Server Mode (TCP/UDP):");
        String mode = scanner.nextLine().trim().toUpperCase();

        if (!validModes.contains(mode)) {
            System.err.println("Invalid mode selected. Please choose either TCP or UDP.");
            return;
        }

        int port = 8080;
        System.out.println("Enter port number (default 8080):");
        String portInput = scanner.nextLine().trim();

        if (!portInput.isEmpty()) {
            try {
                port = Integer.parseInt(portInput);
                if (port <= 0 || port > 65535) {
                    throw new IllegalArgumentException("Port must be between 1 and 65535.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 8080.");
                port = 8080;
            }
        }

        try {
            switch (mode) {
                case "TCP":
                    System.out.println("Launching TCP Multi-Server on port " + port + "...");
                    TCPMultiServer tcpServer = new TCPMultiServer(port);
                    tcpServer.launch();
                    break;

                case "UDP":
                    System.out.println("Launching UDP Server on port " + port + "...");
                    UDPServer udpServer = new UDPServer(port);
                    udpServer.launch();
                    break;

                default:
                    System.err.println("Unexpected error: Invalid mode detected.");
            }
        } catch (Exception e) {
            System.err.println("Critical error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("ServerLauncher has stopped.");
        }
    }
}
