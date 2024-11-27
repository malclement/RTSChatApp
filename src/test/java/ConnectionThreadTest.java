import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

class ConnectionThreadTest {

    @Mock
    private TCPMultiServer serverMock;

    @Mock
    private Socket socketMock;

    @Mock
    private BufferedReader readerMock;

    @Mock
    private PrintWriter writerMock;

    private ConnectionThread connectionThread;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock socket behavior
        when(socketMock.getInputStream()).thenReturn(mock(InputStream.class));
        when(socketMock.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(socketMock.getInetAddress()).thenReturn(mock(InetAddress.class));
        when(socketMock.getInetAddress().getHostAddress()).thenReturn("192.168.0.1");

        // Mock reader and writer for nickname input and communication
        when(socketMock.getInputStream()).thenReturn(new ByteArrayInputStream("TestUser\n".getBytes())); // Provide nickname input
        when(socketMock.getOutputStream()).thenReturn(new ByteArrayOutputStream()); // For output stream

        // Mock server behavior
        when(serverMock.isNicknameTaken("TestUser")).thenReturn(false);  // Assume nickname is available
        connectionThread = new ConnectionThread(socketMock, serverMock);
    }

    @Test
    void testClientConnectionAndDisconnection() throws IOException, InterruptedException {
        // Simulate client sending nickname "TestUser" and then disconnecting (null input)
        when(readerMock.readLine()).thenReturn("TestUser").thenReturn(null); // Client sends nickname and then disconnects

        // Start the connection thread
        connectionThread.start();

        // Sleep briefly to allow the thread to complete its work (you can adjust timing)
        Thread.sleep(1000);

        // Verify that the addClient method was called on the server with the correct parameters
        verify(serverMock).addClient(eq("TestUser"), eq(socketMock));

        // Verify that the broadcast method was called when the client joined
        verify(serverMock).broadcast("TestUser joined the chat.", "TestUser");

        // Verify that the client was removed from the server on disconnection
        verify(serverMock).removeClient(eq("TestUser"));

        // Verify that the server broadcasts the client leaving
        verify(serverMock).broadcast("TestUser has left the chat.", "TestUser");
    }

    @Test
    void testClientDisconnectWithoutMessage() throws IOException, InterruptedException {
        // Simulate client sending a nickname and immediately disconnecting (no message sent)
        when(readerMock.readLine()).thenReturn("TestUser").thenReturn(null); // Nickname followed by disconnection

        // Start the connection thread
        connectionThread.start();

        // Sleep briefly to allow the thread to complete its work
        Thread.sleep(1000);

        // Verify that the client is removed and broadcasted as disconnected
        verify(serverMock).removeClient(eq("TestUser"));
        verify(serverMock).broadcast("TestUser has left the chat.", "TestUser");
    }

    @Test
    void testExceptionHandlingDuringConnection() throws IOException, InterruptedException {
        // Simulate an exception occurring during the connection (e.g., socket read error)
        when(readerMock.readLine()).thenThrow(new IOException("Simulated connection error"));

        // Start the connection thread
        connectionThread.start();

        // Sleep briefly to allow the thread to handle the exception
        Thread.sleep(1000);

        // Verify that the disconnect message was broadcasted even after an exception
        verify(serverMock).broadcast("TestUser has left the chat.", "TestUser");
    }

    @AfterEach
    void tearDown() {
        connectionThread.interrupt();  // Ensure that the thread is interrupted after the test
    }
}
