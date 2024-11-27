import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class TCPServerTest {

    @Test
    void testServerInitializationWithInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> new TCPServer(0), "Port 0 should be invalid.");
        assertThrows(IllegalArgumentException.class, () -> new TCPServer(70000), "Port 70000 should be invalid.");
    }

    @Test
    void testServerLaunchAndClientConnection() throws InterruptedException {
        int testPort = 9090;
        TCPServer server = new TCPServer(testPort);

        Thread serverThread = new Thread(() -> {
            try {
                server.launch();
            } catch (RuntimeException e) {
                fail("Server failed to start: " + e.getMessage());
            }
        });
        serverThread.start();

        try (Socket clientSocket = new Socket("localhost", testPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String message = "Hello, Server!";
            out.println(message);

            String response = in.readLine();
            assertNotNull(response, "Server should respond.");
            assertTrue(response.contains(message), "Server response should contain the client message.");

        } catch (IOException e) {
            fail("Client connection failed: " + e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (IOException e) {
                fail("Failed to stop server: " + e.getMessage());
            }
            serverThread.interrupt();
        }
    }

    @Test
    void testServerStop() throws InterruptedException {
        int testPort = 9091;
        TCPServer server = new TCPServer(testPort);

        Thread serverThread = new Thread(() -> {
            try {
                server.launch();
            } catch (RuntimeException e) {
                fail("Server failed to start: " + e.getMessage());
            }
        });
        serverThread.start();


        try {
            server.stop();
        } catch (IOException e) {
            fail("Failed to stop the server: " + e.getMessage());
        }
    }

    @Test
    void testServerHandlesMultipleClients() throws InterruptedException {
        int testPort = 9092;
        TCPServer server = new TCPServer(testPort);

        Thread serverThread = new Thread(() -> {
            try {
                server.launch();
            } catch (RuntimeException e) {
                fail("Server failed to start: " + e.getMessage());
            }
        });
        serverThread.start();

        final int clientCount = 3;
        Thread[] clientThreads = new Thread[clientCount];

        for (int i = 0; i < clientCount; i++) {
            final int clientId = i;
            clientThreads[i] = new Thread(() -> {
                try (Socket clientSocket = new Socket("localhost", testPort);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String message = "Client " + clientId + " says hello!";
                    out.println(message);

                    String response = in.readLine();
                    assertNotNull(response, "Server should respond to client " + clientId);
                    assertTrue(response.contains(message), "Server response should include client message for client " + clientId);
                } catch (IOException e) {
                    fail("Client " + clientId + " connection failed: " + e.getMessage());
                }
            });
            clientThreads[i].start();
        }

        for (Thread clientThread : clientThreads) {
            clientThread.join();
        }

        try {
            server.stop();
        } catch (IOException e) {
            fail("Failed to stop the server: " + e.getMessage());
        }

        serverThread.interrupt();
    }
}
