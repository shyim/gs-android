package de.shyim.gameserver_sponsor.Model;

public class Gameserver {
    private String ip;
    private String port;
    private String game;
    private String slots;

    public Gameserver(String IP, String Port, String Game, String Slots) {
        this.ip = IP;
        this.port = Port;
        this.game = Game;
        this.slots = Slots;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getGame() {
        return game;
    }

    public String getSlots() {
        return slots;
    }
}
