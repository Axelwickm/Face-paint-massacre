package jaam.fpm.packet;

import org.newdawn.slick.geom.Vector2f;

public class PlayerActionPacket {
    public Action action;
    public Vector2f velocity;

    private PlayerActionPacket(){}

    public static PlayerActionPacket make(Action action){
        PlayerActionPacket p = new PlayerActionPacket();
        p.action = action;
        return p;
    }

    public enum Action {
        READY,

        START_WALKING,
        STOP_WALKING
    }
}
