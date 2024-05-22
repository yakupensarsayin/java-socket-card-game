package org.yakupsayin.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String clientUsername = null;

    ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            clientHandlers.add(this);
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void closeEverything() {
        try {
            if (socket != null){
                socket.close();
            }

            if (inputStream != null){
                inputStream.close();
            }

            if(outputStream != null){
                outputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        // TODO: Read JoinGameRequest.
        // TODO: Check if user can login
        // TODO: if user can login, broadcast message
        // TODO: if not, close connection

        while (socket.isConnected()){
            // TODO: Get requests.
            // TODO: Handle them.
        }

    }
}
