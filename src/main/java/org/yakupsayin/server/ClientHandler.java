package org.yakupsayin.server;

import com.squareup.moshi.Moshi;
import org.yakupsayin.common_classes.JoinGameRequest;
import org.yakupsayin.common_classes.PlayerJoinedNotification;
import org.yakupsayin.common_classes.PlayerLeftNotification;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String clientUsername = null;
    private final Moshi moshi = new Moshi.Builder().build();
    private static int numberOfPlayers = 0;

    private Boolean isListening = true;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            clientHandlers.add(this);
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            // Read JoinGameRequest.
            String json = readMessage();
            JoinGameRequest request = moshi.adapter(JoinGameRequest.class).fromJson(json);
            assert request != null;
            String desiredUsername = request.getPlayerName();

            // Check if user can log in
            Boolean canLogin = Authenticator.canUserLogin(desiredUsername, clientHandlers);

            // if not close connection
            if (!canLogin){
                closeEverything();
            }

            clientUsername = desiredUsername;
            numberOfPlayers += 1;

            PlayerJoinedNotification notification = new PlayerJoinedNotification(clientUsername, numberOfPlayers);
            json = moshi.adapter(PlayerJoinedNotification.class).toJson(notification);

            // Broadcast new user logged in.
            broadcastJson(json);

        } catch (IOException e) {
            closeEverything();

        }

        while(isListening){
            String msg = readMessage();
        }

    }

    public void sendMessage(String json, OutputStream os){
        try {
            os.write(json.getBytes());
            os.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    public String readMessage(){
        try {
            byte[] message = new byte[1024];
            int bytesRead = inputStream.read(message);
            return new String(message, 0, bytesRead, StandardCharsets.UTF_8);
        } catch (IOException e) {
            closeEverything();
            return null;
        }
    }

    public void broadcastJson(String json){
        for (ClientHandler clientHandler : clientHandlers){
            if (clientHandler.getClientUsername() != null){
                clientHandler.sendMessage(json, clientHandler.outputStream);
            }
        }
    }

    public String getClientUsername(){
        return clientUsername;
    }

    private void closeEverything() {
        System.out.println("A user has disconnected.");

        try {
            clientHandlers.remove(this);

            if (clientUsername != null){
                numberOfPlayers -= 1;

                PlayerLeftNotification notification = new PlayerLeftNotification(numberOfPlayers, clientUsername);
                String json = moshi.adapter(PlayerLeftNotification.class).toJson(notification);
                broadcastJson(json);
            }

            if (socket != null){
                socket.close();
            }

            if (inputStream != null){
                inputStream.close();
            }

            if(outputStream != null){
                outputStream.close();
            }

            isListening = false;

            Thread.currentThread().interrupt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
