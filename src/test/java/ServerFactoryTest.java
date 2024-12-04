import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ServerFactoryTest {

    @Test
    void testCreateTCPServer() throws NoSuchFieldException, IllegalAccessException {
        int port = 8080;
        ServerFactory factory = new ServerFactory();

        // Create a TCP server
        TCPMultiServer tcpServer = factory.createTCPServer(port);

        // Use reflection to access the port field
        Field portField = TCPMultiServer.class.getDeclaredField("port");
        portField.setAccessible(true);
        int actualPort = (int) portField.get(tcpServer);

        assertEquals(port, actualPort, "TCP server should be created with the specified port.");
    }

    @Test
    void testCreateUDPServer() throws NoSuchFieldException, IllegalAccessException {
        int port = 9090;
        ServerFactory factory = new ServerFactory();

        // Create a UDP server
        UDPServer udpServer = factory.createUDPServer(port);

        // Use reflection to access the port field
        Field portField = UDPServer.class.getDeclaredField("port");
        portField.setAccessible(true);
        int actualPort = (int) portField.get(udpServer);

        assertEquals(port, actualPort, "UDP server should be created with the specified port.");
    }
}