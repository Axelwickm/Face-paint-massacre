package jaam.fpm.packet;

import jaam.fpm.server.Player;

public class GameStatusChangePacket {
    private GameStatusChangePacket(){}

    public StatusChange statusChange;
    public boolean IAmTheMurderer;
    public boolean murderWin;


    public static GameStatusChangePacket make(StatusChange statusChange){
        GameStatusChangePacket p = new GameStatusChangePacket();
        p.statusChange = statusChange;
        return p;
    }

    public enum StatusChange {
        MURDERER_CHOOSEN,
        GAME_OVER,
        RESTART_GAME
    }
}
