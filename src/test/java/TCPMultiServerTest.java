import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TCPMultiServerTest {

    @Test
    void testServerInitializationWithInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> new TCPMultiServer(0));
        assertThrows(IllegalArgumentException.class, () -> new TCPMultiServer(70000));
    }

    @Test
    void testServerLaunchAndClientConnection() throws InterruptedException {
        int testPort = 9090;
        TCPMultiServer server = new TCPMultiServer(testPort);

        Thread serverThread = new Thread(server::launch);
        serverThread.start();

        Thread.sleep(1000);

        try (Socket clientSocket = new Socket("localhost", testPort)) {
            assertTrue(clientSocket.isConnected(), "Client should connect to the server.");

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("Hello, Server!");

            Scanner in = new Scanner(clientSocket.getInputStream());
            assertTrue(in.hasNextLine(), "Server should respond to the client.");
            String response = in.nextLine();
            System.out.println("Response from server: " + response);

        } catch (IOException e) {
            fail("Client connection failed: " + e.getMessage());
        } finally {
            serverThread.interrupt();
        }
    }

    @Test
    void testMultipleClientConnections() throws InterruptedException {
        int testPort = 9091;
        TCPMultiServer server = new TCPMultiServer(testPort);

        Thread serverThread = new Thread(server::launch);
        serverThread.start();

        Thread.sleep(1000);

        final int clientCount = 5;
        Thread[] clientThreads = new Thread[clientCount];

        for (int i = 0; i < clientCount; i++) {
            int clientId = i;
            clientThreads[i] = new Thread(() -> {
                try (Socket clientSocket = new Socket("localhost", testPort)) {
                    assertTrue(clientSocket.isConnected(), "Client " + clientId + " should connect to the server.");

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("Client " + clientId + ": Hello, Server!");

                    Scanner in = new Scanner(clientSocket.getInputStream());
                    if (in.hasNextLine()) {
                        System.out.println("Response from server for Client " + clientId + ": " + in.nextLine());
                    }
                } catch (IOException e) {
                    fail("Client " + clientId + " connection failed: " + e.getMessage());
                }
            });
            clientThreads[i].start();
        }

        for (Thread clientThread : clientThreads) {
            clientThread.join();
        }

        serverThread.interrupt();
    }
}
