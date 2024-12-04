import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code ServerLauncher} class serves as an entry point for launching either
 * a TCP or UDP server. It prompts the user to select the desired server mode (TCP/UDP)
 * and a port number, validates the input, and then starts the appropriate server.
 */
public class ServerLauncher {
    private final ServerFactory serverFactory;

    public ServerLauncher(ServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    public void run() {
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
                    System.err.println("Port must be between 1 and 65535. Using default port 8080.");
                    port = 8080;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number format. Using default port 8080.");
                port = 8080;
            }
        }

        try {
            if ("TCP".equals(mode)) {
                System.out.println("Launching TCP Multi-Server on port " + port + "...");
                serverFactory.createTCPServer(port).launch();
            } else if ("UDP".equals(mode)) {
                System.out.println("Launching UDP Server on port " + port + "...");
                serverFactory.createUDPServer(port).launch();
            }
        } catch (Exception e) {
            System.err.println("Critical error: " + e.getMessage());
        } finally {
            System.out.println("ServerLauncher has stopped.");
        }
    }

    public static void main(String[] args) {
        ServerLauncher launcher = new ServerLauncher(new ServerFactory());
        launcher.run();
    }
}