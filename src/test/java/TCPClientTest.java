import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TCPClientTest {

    @Test
    void testValidConstructor() {
        TCPClient client = new TCPClient("127.0.0.1", 8080);
        assertEquals("127.0.0.1", client.getServerAddress(), "Server address should be 127.0.0.1.");
        assertEquals(8080, client.getServerPort(), "Server port should be 8080.");
    }

    @Test
    void testValidHostnameConstructor() {
        TCPClient client = new TCPClient("localhost", 9090);
        assertEquals("localhost", client.getServerAddress(), "Server address should be 'localhost'.");
        assertEquals(9090, client.getServerPort(), "Server port should be 9090.");
    }

    @Test
    void testNullAddress() {
        assertThrows(IllegalArgumentException.class, () -> new TCPClient(null, 8080), "Null address should throw IllegalArgumentException.");
    }

    @Test
    void testEmptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> new TCPClient("", 8080), "Empty address should throw IllegalArgumentException.");
    }

    @Test
    void testInvalidPortTooLow() {
        assertThrows(IllegalArgumentException.class, () -> new TCPClient("127.0.0.1", 0), "Port 0 should throw IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> new TCPClient("127.0.0.1", -1), "Negative port should throw IllegalArgumentException.");
    }

    @Test
    void testInvalidPortTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> new TCPClient("127.0.0.1", 70000), "Port above 65535 should throw IllegalArgumentException.");
    }

    @Test
    void testInvalidIPAddress() {
        assertThrows(IllegalArgumentException.class, () -> new TCPClient("999.999.999.999", 8080), "Invalid IP should throw IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> new TCPClient("abc.def.ghi.jkl", 8080), "Non-numeric IP should throw IllegalArgumentException.");
    }

    @Test
    void testBoundaryPorts() {
        TCPClient clientMinPort = new TCPClient("127.0.0.1", 1);
        assertEquals(1, clientMinPort.getServerPort(), "Port should be 1.");

        TCPClient clientMaxPort = new TCPClient("127.0.0.1", 65535);
        assertEquals(65535, clientMaxPort.getServerPort(), "Port should be 65535.");
    }
}
