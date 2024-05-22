package org.yakupsayin.common_classes;

public class PlayerLeftNotification {
    private final String messageType = "PlayerLeftNotification";
    private final Integer numPlayers;
    private final String playerName;

    public PlayerLeftNotification(Integer numPlayers, String playerName){
        this.numPlayers = numPlayers;
        this.playerName = playerName;
    }

    public Integer getNumPlayers() {
        return numPlayers;
    }

    public String getPlayerName() {
        return playerName;
    }
}
