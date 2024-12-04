import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ServerLauncherTest {

    @Test
    void testTCPServerLaunch() {
        TCPMultiServer mockTCPServer = Mockito.mock(TCPMultiServer.class);
        ServerFactory mockFactory = Mockito.mock(ServerFactory.class);
        Mockito.when(mockFactory.createTCPServer(8080)).thenReturn(mockTCPServer);

        String input = "TCP\n\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        ServerLauncher launcher = new ServerLauncher(mockFactory);
        launcher.run();

        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("Launching TCP Multi-Server on port 8080..."),
                "Should successfully launch TCP server on default port.");
        Mockito.verify(mockTCPServer).launch();
    }

    @Test
    void testUDPServerLaunch() throws IOException {
        UDPServer mockUDPServer = Mockito.mock(UDPServer.class);
        ServerFactory mockFactory = Mockito.mock(ServerFactory.class);
        Mockito.when(mockFactory.createUDPServer(9090)).thenReturn(mockUDPServer);

        String input = "UDP\n9090\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        ServerLauncher launcher = new ServerLauncher(mockFactory);
        launcher.run();

        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("Launching UDP Server on port 9090..."),
                "Should successfully launch UDP server on specified port.");
        Mockito.verify(mockUDPServer).launch();
    }
}