package jaam.fpm.client;

import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.shared.State;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.geom.Vector2f;

public class Player implements KeyListener {

	public static final int SIZE = 32;

	public static final float DEFAULT_SPEED = .3f;
	public static final int DEFAULT_HEALTH = 5;

	public static final int VIEW_RADIUS = 1;

	private Vector2f position = new Vector2f();
	private Vector2f dir =  new Vector2f();

	private float speed = DEFAULT_SPEED;

	private World world;

	private boolean controllable;

	public int health = DEFAULT_HEALTH;

	private State state = State.AlIVE;

	private Weapon weapon;

	public Player(World world) {
		this(world, true);
	}

	public Player(World world, boolean controllable) {
		this.world = world;
		this.controllable = controllable;
		weapon = new Knife(this);
	}

	@Override public void inputStarted() { }

	@Override
	public void keyPressed(final int key, final char c) {
		//TODO: FIX HEALTH BEHAVIOUR
		if (health <= 0)
			return;

		boolean pressed = false;

		if (key == KeyConfig.WALK_UP) {
			dir.y -= 1;
			pressed = true;
		} else if (key == KeyConfig.WALK_DOWN) {
			dir.y += 1;
			pressed = true;
		} else if (key == KeyConfig.WALK_LEFT) {
			dir.x -= 1;
			pressed = true;
		} else if (key == KeyConfig.WALK_RIGHT) {
			dir.x += 1;
			pressed = true;
		}

		if (pressed) {
			sendWalkPacket();
		}

		if (key == KeyConfig.TOGGLE_WEAPON) {
			world.getClient().sendTCP(PlayerActionPacket.make(PlayerActionPacket.Action.TOGGLE_WEAPON));
			weapon.toggle();
		}
		if (key == KeyConfig.USE_WEAPON)
			weapon.use();
	}

	@Override
	public void keyReleased(final int key, final char c) {

		boolean released = false;

		if (key == KeyConfig.WALK_UP) {
			dir.y += 1;
			released = true;
		} else if (key == KeyConfig.WALK_DOWN) {
			dir.y -= 1;
			released = true;
		} else if (key == KeyConfig.WALK_LEFT) {
			dir.x += 1;
			released = true;
		} else if (key == KeyConfig.WALK_RIGHT) {
			dir.x -= 1;
			released = true;
		}

		if (released) {
			if (dir.lengthSquared() == 0) {
				sendStopPacket();
			} else {
				sendWalkPacket();
			}
		}
	}

	@Override public void setInput(final Input input) { }

	@Override public boolean isAcceptingInput() { return true; }

	@Override public void inputEnded() { }

	public void update(final GameContainer gameContainer, final int dt) {
		if (health <= 0 && (state == State.AlIVE || state == State.MURDERER)){
			kill();
		}

		if (state == State.DEAD){
			return;
		}

		// Move
		if (dir.lengthSquared() != 0) {
			Vector2f newPos = position.copy().add(dir.copy().normalise().scale(speed * dt));

			if (dir.x != 0) {
				if (world.getTileFromWorldPosition(
						new Vector2f(newPos.x + dir.x * (SIZE / 2), position.y - (SIZE / 2)+1)).SOLID ||
					world.getTileFromWorldPosition(new Vector2f(newPos.x + dir.x * (SIZE / 2),
																position.y + (SIZE / 2)-1)).SOLID) {

					newPos.x += (dir.x < 0 ?
								 Tile.PIXELS - ((newPos.x + dir.x * (SIZE / 2)) % Tile.PIXELS) :
								 -(((newPos.x + dir.x * (SIZE / 2)) % Tile.PIXELS)));
				}
			}

			if (dir.y != 0) {
				if (world.getTileFromWorldPosition(
						new Vector2f(position.x - (SIZE / 2)+1, newPos.y + dir.y * (SIZE / 2))).SOLID ||
					world.getTileFromWorldPosition(new Vector2f(position.x + (SIZE / 2)-1,
																newPos.y + dir.y * (SIZE / 2))).SOLID) {

					newPos.y += (dir.y < 0 ?
								 Tile.PIXELS - ((newPos.y + dir.y * (SIZE / 2)) % Tile.PIXELS) :
								 -(((newPos.y + dir.y * (SIZE / 2)) % Tile.PIXELS)));
				}
			}

			position.set(newPos);
		}

		weapon.update(dt);
	}

	public void render(final Graphics g) {
		g.pushTransform();
		g.setColor(Color.white);
		g.translate(position.x, position.y);
		g.fillRect(- SIZE / 2, - SIZE / 2, SIZE, SIZE);
		weapon.render(g);
		g.popTransform();
	}

	public void kill(){
		if (this == world.getMe()){
			System.out.println("I died.");
			PlayerActionPacket p = PlayerActionPacket.make(PlayerActionPacket.Action.DIE);
			world.getClient().sendTCP(p);
		}
		state = State.DEAD;
	}

	public int getChunkX() {
		return Math.floorDiv((int) position.x, Chunk.PIXELS);
	}

	public int getChunkY() {
		return Math.floorDiv((int) position.y, Chunk.PIXELS);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(final Vector2f position) {
		this.position = position;
	}

	public Vector2f getDir() {
		return dir;
	}

	public void setDir(final Vector2f dir) {
		this.dir = dir;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(final float speed) {
		this.speed = speed;
	}

	public void sendWalkPacket() {
		PlayerActionPacket p = PlayerActionPacket.make(PlayerActionPacket.Action.START_WALKING);

		p.velocity = new float[] {dir.x, dir.y};

		world.getClient().sendTCP(p);
	}

	public void sendStopPacket() {
		PlayerActionPacket p = PlayerActionPacket.make(PlayerActionPacket.Action.STOP_WALKING);

		p.stopPosition = new float[] {position.x, position.y};

		world.getClient().sendTCP(p);
	}

	public World getWorld() {
		return world;
	}

	public Weapon getWeapon() {
		return weapon;
	}

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
