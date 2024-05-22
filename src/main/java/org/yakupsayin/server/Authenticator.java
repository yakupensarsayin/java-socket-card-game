package org.yakupsayin.server;

import java.util.ArrayList;

public class Authenticator {
    public static Boolean canUserLogin(String username, ArrayList<ClientHandler> clientHandlers){
        for (ClientHandler clientHandler : clientHandlers){
            if (clientHandler.getClientUsername() != null){
                if (clientHandler.getClientUsername().equals(username)){
                    return false;
                }
            }
        }

        return true;
    }
}
