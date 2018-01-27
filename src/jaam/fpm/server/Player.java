package jaam.fpm.server;

import org.newdawn.slick.geom.Vector2f;

public class Player {
    private final int connection_id;

    private Vector2f position = new Vector2f();

    public Player(int connection_id) {
        this.connection_id = connection_id;
    }
}
