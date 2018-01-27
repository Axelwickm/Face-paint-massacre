package jaam.fpm.packet;

import org.newdawn.slick.geom.Vector2f;

public class PlayerActionPacket {
    public Action action;
    public Vector2f velocity;

    private PlayerActionPacket(){}

    public static PlayerActionPacket make(Action action, Vector2f velocity){
        PlayerActionPacket p = new PlayerActionPacket();
        p.action = action;
        p.velocity = velocity;
        return p;
    }

    public enum Action {
        START_WALKING,
        STOP_WALKING
    }
}
