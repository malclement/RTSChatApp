import java.util.Scanner;

public class ServerLauncher {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select Server Mode (TCP/UDP):");
        String mode = scanner.nextLine().trim().toUpperCase();

        int port = 8080; // Default port
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
                    System.out.println("Launching TCP Multi-Server...");
                    TCPMultiServer tcpServer = new TCPMultiServer(port);
                    tcpServer.launch();
                    break;
                case "UDP":
                    System.out.println("Launching UDP Server...");
                    UDPServer udpServer = new UDPServer(port);
                    udpServer.launch();
                    break;

                default:
                    System.err.println("Invalid mode selected. Please choose either TCP or UDP.");
            }
        } catch (Exception e) {
            System.err.println("Critical error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
