import org.junit.jupiter.api.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class TCPServerTest {

    private static final int TEST_PORT = 8080;
    private static TCPServer server;

    @BeforeAll
    static void setUpServer() {
        server = new TCPServer(TEST_PORT);
        Thread serverThread = new Thread(() -> {
            try {
                server.launch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {}

    @Test
    void testToString() {
        TCPServer server = new TCPServer(8000);
        String expected = "TCPServer listening on port 8000";
        assertEquals(expected, server.toString());
    }

    @Test
    void testToStringDefaultConstructor() {
        TCPServer server = new TCPServer();
        String expected = "TCPServer listening on port 8080";
        assertEquals(expected, server.toString());
    }

    @Test
    void testParameterizedConstructor() {
        TCPServer server = new TCPServer(8080);
        assertEquals(8080, server.getPort(), "Parameterized constructor should correctly set the port.");
        TCPServer serverMin = new TCPServer(1);
        assertEquals(1, serverMin.getPort(), "Port should be 1.");
        TCPServer serverMax = new TCPServer(65535);
        assertEquals(65535, serverMax.getPort(), "Port should be 65535.");
    }

    @Test
    void testInvalidPortConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new TCPServer(0), "Port 0 should throw IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> new TCPServer(-1), "Negative ports should throw IllegalArgumentException.");
    }

    @Test
    void testLaunchServer() throws InterruptedException {
        try (Socket socket = new Socket("localhost", TEST_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("Hello Server");
            String response = in.readLine();
            assertNotNull(response, "The server should send a response.");
            assertTrue(response.contains("Hello Server"), "The server response should include the sent message.");
        } catch (IOException e) {
            fail("Failed to connect to server or read the response: " + e.getMessage());
        }
    }

    @Test
    void testLaunchServerPortInUse() {
        try (ServerSocket ignored = new ServerSocket(TEST_PORT)) {
            TCPServer server = new TCPServer(TEST_PORT);
            Thread serverThread = new Thread(() -> {
                try {
                    server.launch();
                } catch (Exception e) {
                    assertTrue(e.getMessage().contains("Address already in use"), "Expected 'Address already in use' error");
                }
            });
            serverThread.start();
            Thread.sleep(500);
            serverThread.interrupt();
            serverThread.join();
        } catch (IOException e) {
            fail("Port 8080 is already in use before the test started. Try using a different port.");
        } catch (InterruptedException e) {
            fail("Test interrupted unexpectedly");
        }
    }

    @Test
    void testLaunchServerIOExceptionHandling() throws InterruptedException {
        Thread serverThread = new Thread(() -> {
            TCPServer server = new TCPServer(TEST_PORT);
            server.launch();
        });
        serverThread.start();
        Thread.sleep(1000);
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Test message");
            socket.close();
            Thread.sleep(500);
        } catch (IOException ignored) {
        }
        serverThread.join();
    }

    @AfterAll
    static void tearDownServer() {
        System.out.println("Shutting down the server.");
        if (server != null) {
            try {
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
