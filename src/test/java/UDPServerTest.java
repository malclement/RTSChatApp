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
    private static ExecutorService executorService;
    private static DatagramSocket clientSocket;
    private static ByteArrayOutputStream serverOutput;

    @BeforeAll
    static void setupServer() throws Exception {
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
        TimeUnit.SECONDS.sleep(1); // Ensure server is up
    }

    @AfterAll
    static void tearDownServer() throws Exception {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
        System.setOut(System.out); // Restore System.out
    }

    @Test
    void testServerReceivesSingleMessage() throws Exception {
        sendMessage(TEST_MESSAGE);
        TimeUnit.SECONDS.sleep(1);
        String output = serverOutput.toString();
        assertTrue(output.contains(TEST_MESSAGE), "Server should receive the message: " + TEST_MESSAGE);
    }

    @Test
    void testServerReceivesMultipleMessages() throws Exception {
        String[] messages = {"Hello", "World", "Test"};
        for (String message : messages) {
            sendMessage(message);
        }
        TimeUnit.SECONDS.sleep(1);
        String output = serverOutput.toString();
        for (String message : messages) {
            assertTrue(output.contains(message), "Server should receive the message: " + message);
        }
    }

    @Test
    void testServerHandlesEmptyMessage() throws Exception {
        sendMessage("");
        TimeUnit.SECONDS.sleep(1);
        String output = serverOutput.toString();
        assertTrue(output.contains(""), "Server should handle empty messages gracefully.");
    }

    @Test
    void testServerHandlesInvalidIPAddress() {
        assertThrows(UnknownHostException.class, () -> {
            new DatagramSocket().send(new DatagramPacket(
                    new byte[0], 0, InetAddress.getByName("999.999.999.999"), TEST_SERVER_PORT
            ));
        }, "Server should throw exception for invalid IP address.");
    }

    @Test
    void testServerToStringMethod() {
        UDPServer server = new UDPServer(TEST_SERVER_PORT);
        assertEquals("UDPServer{port=" + TEST_SERVER_PORT + "}", server.toString(),
                "Server's toString method should return the correct representation.");
    }

    @Test
    void testServerConstructorDefaultPort() {
        UDPServer server = new UDPServer();
        assertEquals(8000, server.getPort(), "Default port should be 8000.");
    }

    @Test
    void testServerHandlesInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UDPServer(-1); // Invalid port
        }, "Constructor should throw IllegalArgumentException for negative port numbers.");

        assertThrows(IllegalArgumentException.class, () -> {
            new UDPServer(70000); // Invalid port
        }, "Constructor should throw IllegalArgumentException for ports above 65535.");
    }

    @Test
    void testServerHandlesContinuousTraffic() throws Exception {
        for (int i = 0; i < 10; i++) {
            sendMessage("Message " + i);
        }
        TimeUnit.SECONDS.sleep(1);
        String output = serverOutput.toString();
        for (int i = 0; i < 10; i++) {
            assertTrue(output.contains("Message " + i), "Server should handle continuous messages: Message " + i);
        }
    }

    /**
     * Helper method to send a UDP message to the server.
     *
     * @param message the message to send
     * @throws Exception if an error occurs while sending
     */
    private void sendMessage(String message) throws Exception {
        clientSocket = new DatagramSocket();
        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), TEST_SERVER_PORT);
        clientSocket.send(packet);
        clientSocket.close();
    }
}