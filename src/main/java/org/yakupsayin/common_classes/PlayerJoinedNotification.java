package org.yakupsayin.common_classes;

public class PlayerJoinedNotification {
    private final String messageType;
    private final String newPlayerName;
    private final Integer numPlayers;

    public PlayerJoinedNotification(String playerName, Integer numPlayers){
        this.messageType = "PlayerJoinedNotification";
        this.newPlayerName = playerName;
        this.numPlayers = numPlayers;
    }
    public String getPlayerName() {return newPlayerName;}
    public Integer getNumPlayers() {return numPlayers;}
}
