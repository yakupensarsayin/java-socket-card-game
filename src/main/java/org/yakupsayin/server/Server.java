package org.yakupsayin.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Integer port = 8080;
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {

            while (!serverSocket.isClosed()){
                System.out.println("Waiting for clients...");
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.run();

            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try {
            if (serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while closing the server socket." + e.getMessage());
        }
    }
    public static void main(String[] args){
        initializePort(args);

        try(ServerSocket serverSocket = new ServerSocket(port)){
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            System.out.println("An error occurred while starting the server." + e.getMessage());
        }
    }
    public static void initializePort(String[] args){
        if (args.length == 0){
            System.out.println("The port number is not specified in the arguments.");
            System.out.println("By default, the port number '8080' will be used.");
            return;
        }

        System.out.println("Port number found in arguments.");
        System.out.println(args[0] + " will be used as the port number.");
        port = Integer.valueOf(args[0]);
    }


}
