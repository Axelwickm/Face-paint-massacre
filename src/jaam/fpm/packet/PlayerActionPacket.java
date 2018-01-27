package jaam.fpm.packet;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class PlayerActionPacket {
    public Action action;
    public int connection_id; // Who did it (like a player id)
    public Vector2f velocity;
    public Vector2f stopPosition;

    public byte[] drawing;

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
