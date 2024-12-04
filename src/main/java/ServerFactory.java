public class ServerFactory {
    public TCPMultiServer createTCPServer(int port) {
        return new TCPMultiServer(port);
    }

    public UDPServer createUDPServer(int port) {
        return new UDPServer(port);
    }
}