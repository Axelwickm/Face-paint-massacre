package jaam.fpm.server;

import com.esotericsoftware.kryonet.Server;
import jaam.fpm.packet.GameStatusChangePacket;
import jaam.fpm.packet.TileArrayPacket;
import jaam.fpm.shared.State;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class Player {
    private final Server server;
    public final int connection_id;

    public boolean ready;
    private Vector2f position = new Vector2f();
    private Vector2f velocity = new Vector2f();
    public State state;

    private PlayerRole role = PlayerRole.REGULAR;

    private byte[] face = null;
	public byte[] getFace() {
		return face;
	}
	public void setFace(byte[] face) {
		this.face = face;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public Player(Server server, int connection_id, Vector2f position) {
        this.server = server;
        this.connection_id = connection_id;
        this.ready = false;
        this.position = position;
        this.velocity.set(0, 0);

        this.state = State.AlIVE;
    }

    public void update(double delta){
        this.position = this.position.add(velocity.scale((float) delta));
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public Vector2f getVelocity() {
        return velocity;
    }


    public void sendWorld(TileArrayPacket p){
        server.sendToTCP(connection_id, p);
    }

    public void sendGameStatusChange(GameStatusChangePacket p){ server.sendToTCP(connection_id, p); }

}

