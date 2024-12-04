import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class TCPServerTest {

    @Test
    void testServerInitializationWithInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> new TCPServer(0), "Port 0 should be invalid.");
        assertThrows(IllegalArgumentException.class, () -> new TCPServer(70000), "Port 70000 should be invalid.");
    }

    @Test
    void testServerLaunchWithNoClient() throws IOException, InterruptedException {
        int testPort = 9093;
        TCPServer server = new TCPServer(testPort);

        Thread serverThread = new Thread(server::launch);
        serverThread.start();

        Thread.sleep(1000); // Give the server some time to start
        try (Socket socket = new Socket("localhost", testPort)) {
            assertFalse(socket.isClosed(), "Server should accept connections even if no client sends data.");
        } finally {
            server.stop();
            serverThread.interrupt();
        }
    }

    @Test
    void testServerResponseToEmptyMessage() throws IOException {
        int testPort = 9094;
        TCPServer server = new TCPServer(testPort);

        Thread serverThread = new Thread(server::launch);
        serverThread.start();

        try (Socket clientSocket = new Socket("localhost", testPort);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            out.println(""); // Send an empty message
            String response = in.readLine();
            assertNotNull(response, "Server should respond even to an empty message.");
        } finally {
            server.stop();
            serverThread.interrupt();
        }
    }

    @Test
    void testServerHandlesAbruptClientDisconnection() throws IOException, InterruptedException {
        int testPort = 9095;
        TCPServer server = new TCPServer(testPort);

        Thread serverThread = new Thread(server::launch);
        serverThread.start();

        try (Socket clientSocket = new Socket("localhost", testPort)) {
            // Simulate abrupt disconnection
        } finally {
            Thread.sleep(1000); // Give server time to process disconnection
            server.stop();
            serverThread.interrupt();
        }
    }

    @Test
    void testServerHandlesLargeMessage() throws IOException {
        int testPort = 9096;
        TCPServer server = new TCPServer(testPort);

        Thread serverThread = new Thread(server::launch);
        serverThread.start();

        String largeMessage = "A".repeat(1000); // 1000-character message

        try (Socket clientSocket = new Socket("localhost", testPort);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            out.println(largeMessage);
            String response = in.readLine();
            assertNotNull(response, "Server should respond to large messages.");
            assertTrue(response.contains(largeMessage), "Server response should contain the large message.");
        } finally {
            server.stop();
            serverThread.interrupt();
        }
    }

    @Test
    void testMultipleServerLaunch() throws InterruptedException {
        int testPort = 9097;

        TCPServer firstServer = new TCPServer(testPort);
        TCPServer secondServer = new TCPServer(testPort);

        Thread firstThread = new Thread(firstServer::launch);
        Thread secondThread = new Thread(() -> {
            assertThrows(RuntimeException.class, secondServer::launch, "Launching a second server on the same port should fail.");
        });

        firstThread.start();
        secondThread.start();

        try {
            Thread.sleep(1000); // Wait for server to start
            firstServer.stop();
        } catch (IOException e) {
            fail("Failed to stop the first server.");
        } finally {
            firstThread.interrupt();
            secondThread.interrupt();
        }
    }

    @Test
    void testServerStopWhenNotStarted() {
        TCPServer server = new TCPServer(9098);
        try {
            server.stop();
        } catch (IOException e) {
            fail("Server stop should not throw exception when not started: " + e.getMessage());
        }
    }
}