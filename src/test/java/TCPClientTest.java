import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
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

        assertThrows(IllegalArgumentException.class, () -> new TCPClient(invalidServer, port), "Expected IllegalArgumentException for invalid server address.");
    }

    @Test
    void testToHexConversion() {
        assertEquals("68656c6c6f", TCPClient.toHex("hello"), "Hex representation of 'hello' should be '68656c6c6f'.");
        assertEquals("776f726c64", TCPClient.toHex("world"), "Hex representation of 'world' should be '776f726c64'.");
        assertEquals("", TCPClient.toHex(""), "Hex representation of empty string should be empty.");
        assertEquals("7a", TCPClient.toHex("z"), "Hex representation of 'z' should be '7a'.");
    }

    @Test
    void testStartWithCustomNickname() throws IOException {
        Socket socketMock = mock(Socket.class);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        PrintWriter outMock = new PrintWriter(byteOutStream, true);
        BufferedReader inMock = mock(BufferedReader.class);
        BufferedReader userInputMock = mock(BufferedReader.class);

        try (MockedStatic<UUID> mockedUUID = mockStatic(UUID.class)) {
            UUID mockUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            mockedUUID.when(UUID::randomUUID).thenReturn(mockUUID);

            when(userInputMock.readLine()).thenReturn("CustomNick", null); // First nickname, then exit
            when(socketMock.getOutputStream()).thenReturn(byteOutStream);
            when(socketMock.getInputStream()).thenReturn(mock(InputStream.class));

            TCPClient client = spy(new TCPClient("localhost", 8000));
            doReturn(userInputMock).when(client).createUserInputReader();
            doReturn(socketMock).when(client).createSocket("localhost", 8000);

            client.start();

            assertTrue(byteOutStream.toString().contains("CustomNick"), "Output stream should contain the nickname.");
        }
    }

    @Test
    void testToHexWithSpecialCharacters() {
        assertEquals("2122232425", TCPClient.toHex("!\"#$%"), "Hex representation of '!\"#$%' should be '2122232425'.");
        assertEquals("c3a9", TCPClient.toHex("é"), "Hex representation of 'é' should be 'c3a9' (UTF-8 encoding).");
    }

    @Test
    void testToHexWithNullInput() {
        assertThrows(IllegalArgumentException.class, () -> TCPClient.toHex(null), "Null input should throw IllegalArgumentException.");
    }
}
