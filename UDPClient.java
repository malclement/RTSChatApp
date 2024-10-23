import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    private String serverAddress;
    private int serverPort;

    public UDPClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void launch() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverInetAddress = InetAddress.getByName(serverAddress);
        Console console = System.console();

        if (console == null) {
            System.err.println("No console available");
            return;
        }

        String userInput;
        while (true) {
            userInput = console.readLine();

            byte[] buffer = userInput.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverInetAddress, serverPort);
            socket.send(packet);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java UDPClient <serverAddress> <serverPort>");
            return;
        }

        String serverAddress = args[0];
        int serverPort = Integer.valueOf(args[1]);

        UDPClient client = new UDPClient(serverAddress, serverPort);
        client.launch();
    }
}
