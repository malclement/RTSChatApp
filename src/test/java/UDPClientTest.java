import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UDPClientTest {

    @Test
    void testValidConstructor() {
        UDPClient client = new UDPClient("127.0.0.1", 8080);
        assertEquals("127.0.0.1", client.getServerAddress(), "Server address should be 127.0.0.1.");
        assertEquals(8080, client.getServerPort(), "Server port should be 8080.");
    }

    @Test
    void testValidHostnameConstructor() {
        UDPClient client = new UDPClient("localhost", 9090);
        assertEquals("localhost", client.getServerAddress(), "Server address should be 'localhost'.");
        assertEquals(9090, client.getServerPort(), "Server port should be 9090.");
    }

    @Test
    void testNullAddress() {
        assertThrows(IllegalArgumentException.class, () -> new UDPClient(null, 8080), "Null address should throw IllegalArgumentException.");
    }

    @Test
    void testEmptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> new UDPClient("", 8080), "Empty address should throw IllegalArgumentException.");
    }

    @Test
    void testInvalidPortTooLow() {
        assertThrows(IllegalArgumentException.class, () -> new UDPClient("127.0.0.1", 0), "Port 0 should throw IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> new UDPClient("127.0.0.1", -1), "Negative port should throw IllegalArgumentException.");
    }

    @Test
    void testInvalidPortTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> new UDPClient("127.0.0.1", 70000), "Port above 65535 should throw IllegalArgumentException.");
    }

    @Test
    void testInvalidIPAddress() {
        assertThrows(IllegalArgumentException.class, () -> new UDPClient("999.999.999.999", 8080), "Invalid IP should throw IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> new UDPClient("abc.def.ghi.jkl", 8080), "Non-numeric IP should throw IllegalArgumentException.");
    }

    @Test
    void testBoundaryPorts() {
        UDPClient clientMinPort = new UDPClient("127.0.0.1", 1);
        assertEquals(1, clientMinPort.getServerPort(), "Port should be 1.");

        UDPClient clientMaxPort = new UDPClient("127.0.0.1", 65535);
        assertEquals(65535, clientMaxPort.getServerPort(), "Port should be 65535.");
    }
}
