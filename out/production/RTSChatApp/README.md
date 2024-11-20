# RTSChatApp

### Students
- Clément Malige
- Paul Imbaud

## Overview
This project implements a simple Chat application using Java Sockets. The application allows communication between a UDP Server and UDP Client, and similarly between a TCP Server and TCP Client. The servers accept messages from clients and respond with an echo, prepended with the client’s IP address. The TCPServer is further enhanced to handle multiple simultaneous client connections using multithreading.

## Key Features
### UDP Server & Client:
- UDP Server listens on a specified port and prints messages from clients.
- UDP Client sends messages to the server using UDP datagrams.
### TCP Server & Client:
- TCP Server accepts connections from clients and echoes the message back with the client's IP address.
- TCP Client sends a message to the server and receives a response, repeating the operation until user termination.
### Multithreading:
- A multi-client capable TCP Server (TCPMultiServer) is implemented using threads to handle multiple simultaneous client connections.

## Project Structure
```css
├── src/
│   ├── UDPServer.java
│   ├── UDPClient.java
│   ├── TCPServer.java
│   ├── TCPClient.java
│   ├── TCPMultiServer.java
│   └── ThreadTest.java
├── README.md
└── .gitignore
```

## Usage

1. **UDP Server**

    To start the UDP Server, use the following command:
    ```bash
   java UDPServer <port> 
   ```
   
2. **UDP Client**

   To start the UDP Client, use the following command:
    ```bash
   java UDPClient <server_address> <port> 
   ```

3. **TCP Server**

   To start the TCP Server, use the following command:
    ```bash
    java TCPServer <port> 
    ```

4. **TCP Client**

    To start the UDP Client, use the following command:
     ```bash
    java TCPClient <server_address> <port> 
    ```

5. **TCP Multi Server**

   To start the TCP Multi Server, use the following command:
    ```bash
    java TCPMultiServer <port> 
    ```
   
