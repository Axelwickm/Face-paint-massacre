package jaam.fpm.server;

import org.newdawn.slick.geom.Vector2f;

public class Player {
    private final int connection_id;

    private Vector2f position = new Vector2f();
    private Vector2f velocity = new Vector2f();
    private boolean dead;

    public Player(int connection_id, Vector2f position) {
        this.connection_id = connection_id;
        this.position = position;
        this.velocity.set(0, 0);

        this.dead = false;
    }

    public void update(double delta){

    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public Vector2f getVelocity() {
        return velocity;
    }
}
