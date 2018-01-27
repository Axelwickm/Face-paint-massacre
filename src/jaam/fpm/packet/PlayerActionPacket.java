package jaam.fpm.packet;

import org.newdawn.slick.geom.Vector2f;

public class PlayerActionPacket {
    public Vector2f velocity;

    private PlayerActionPacket(){}

    public static PlayerActionPacket make(Vector2f velocity){
        PlayerActionPacket p = new PlayerActionPacket();
        p.velocity = velocity;
        return p;
    }
}
