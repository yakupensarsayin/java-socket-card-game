package org.yakupsayin.common_classes;

public class JoinGameRequest {
    private final String messageType;
    private final String playerName;

    public JoinGameRequest(String playerName){
        this.messageType = "JoinGameRequest";
        this.playerName = playerName;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getPlayerName() {
        return playerName;
    }
}
