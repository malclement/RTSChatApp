import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;
import java.util.UUID;

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

    @Test
    void testConnectionFailure() {
        String invalidServer = "invalid.server";
        int port = 8080;

        assertThrows(IllegalArgumentException.class, () -> {
            new TCPClient(invalidServer, port);
        }, "Expected IllegalArgumentException for invalid server address.");
    }

    @Test
    void testToHexConversion() {
        TCPClient client = new TCPClient("127.0.0.1", 8080);

        assertEquals("68656c6c6f", TCPClient.toHex("hello"), "Hex representation of 'hello' should be '68656c6c6f'.");
        assertEquals("776f726c64", TCPClient.toHex("world"), "Hex representation of 'world' should be '776f726c64'.");
        assertEquals("", TCPClient.toHex(""), "Hex representation of empty string should be empty.");
        assertEquals("7a", TCPClient.toHex("z"), "Hex representation of 'z' should be '7a'.");
    }
}
