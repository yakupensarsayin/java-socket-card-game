package org.yakupsayin.client;

import com.squareup.moshi.Moshi;
import org.yakupsayin.common_classes.JoinGameRequest;
import org.yakupsayin.common_classes.MessageType;
import org.yakupsayin.common_classes.PlayerJoinedNotification;
import org.yakupsayin.common_classes.PlayerLeftNotification;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final String username;
    private final Moshi moshi = new Moshi.Builder().build();

    public Client(Socket socket, String username){
        try {
            this.socket = socket;
            this.username = username;
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(){
        try {
            JoinGameRequest request = new JoinGameRequest(username);
            String json = moshi.adapter(JoinGameRequest.class).toJson(request);

            outputStream.write(json.getBytes());
            outputStream.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenForMessage(){
        new Thread(() -> {
            String json;

            while (socket.isConnected()){
                try {
                    byte[] message = new byte[1024];
                    int bytesRead = inputStream.read(message);
                    json = new String(message, 0, bytesRead);

                    MessageType messageType = moshi.adapter(MessageType.class).fromJson(json);
                    assert messageType != null;

                    String result;

                    switch (messageType.getMessageType()) {
                        case "PlayerJoinedNotification" -> {
                            PlayerJoinedNotification joinedNotification = moshi.adapter(PlayerJoinedNotification.class).fromJson(json);
                            assert joinedNotification != null;
                            result = String.format("A new player has joined! Name: %s | Player count: %s",
                                    joinedNotification.getPlayerName(), joinedNotification.getNumPlayers().toString());
                            System.out.println(result);
                        }
                        case "PlayerLeftNotification" -> {
                            PlayerLeftNotification leftNotification = moshi.adapter(PlayerLeftNotification.class).fromJson(json);
                            assert leftNotification != null;
                            result = String.format("A player has left! Name: %s | Player count: %s",
                                    leftNotification.getPlayerName(), leftNotification.getNumPlayers().toString());
                            System.out.println(result);
                        }
                    }

                } catch (Exception e) {
                    closeEverything();
                    break;
                }
            }
        }).start();
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
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 8081);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
