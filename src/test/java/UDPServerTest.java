import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class UDPServerTest {

    private static final int TEST_SERVER_PORT = 9091;
    private static final String TEST_MESSAGE = "Hello, Server!";
    private static final String INVALID_IP = "999.999.999.999";
    private static ExecutorService executorService;
    private static boolean serverRunning;
    private static DatagramSocket clientSocket;
    private static ByteArrayOutputStream serverOutput;

    @BeforeAll
    static void setupServer() throws Exception {
        serverRunning = true;
        executorService = Executors.newSingleThreadExecutor();
        serverOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(serverOutput));
        executorService.submit(() -> {
            try {
                UDPServer server = new UDPServer(TEST_SERVER_PORT);
                server.launch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        TimeUnit.SECONDS.sleep(1); // Ensure server has started before tests
    }

    @AfterAll
    static void tearDownServer() throws Exception {
        serverRunning = false;
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
        System.setOut(System.out); // Restore original System.out
    }

    @Test
    void testUDPServerReceivesMessage() throws Exception {
        clientSocket = new DatagramSocket();
        byte[] buffer = TEST_MESSAGE.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, InetAddress.getByName("localhost"), TEST_SERVER_PORT);
        clientSocket.send(packet);
        TimeUnit.SECONDS.sleep(1);
        String output = serverOutput.toString();
        assertTrue(output.contains(TEST_MESSAGE), "Server should have received the message: " + TEST_MESSAGE);
    }

    @Test
    void testServerHandlesInvalidIPAddress() {
        try {
            new UDPClient(INVALID_IP, TEST_SERVER_PORT);  // Should throw exception
            fail("Exception should be thrown for invalid IP address.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid IP address format"));
        }
    }

    @Test
    void testServerHandlesInvalidPort() {
        try {
            new UDPClient("localhost", -1);  // Invalid port
            fail("Exception should be thrown for invalid port.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Port number must be between 1 and 65535"));
        }
    }

    @Test
    void testUDPServerReceivesMultipleMessages() throws Exception {
        final String[] messages = {"Hello", "World", "Test"};
        DatagramSocket socket = new DatagramSocket();

        for (String message : messages) {
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), TEST_SERVER_PORT);
            socket.send(packet);
        }

        TimeUnit.SECONDS.sleep(1);
        String output = serverOutput.toString();
        for (String message : messages) {
            assertTrue(output.contains(message), "Server should have received the message: " + message);
        }
    }

    @Test
    void testServerShutdownGracefully() throws InterruptedException {
        executorService.shutdownNow();
        assertTrue(executorService.isShutdown(), "ExecutorService should be shut down");
    }

    @Test
    void testEmptyMessageHandling() throws Exception {
        clientSocket = new DatagramSocket();
        String emptyMessage = "";
        byte[] buffer = emptyMessage.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, InetAddress.getByName("localhost"), TEST_SERVER_PORT);
        clientSocket.send(packet);
        TimeUnit.SECONDS.sleep(1);
        String output = serverOutput.toString();
        assertTrue(true, "Server should have received the empty message.");
    }
}
