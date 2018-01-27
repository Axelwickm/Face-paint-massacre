package jaam.fpm.client;

import jaam.fpm.shared.Tile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.geom.Vector2f;

public class Player implements KeyListener {

	public static final int SIZE = 32;

	public static final float DEFAULT_SPEED = .3f;

	public static final int VIEW_RADIUS = 1;

	private Vector2f position = new Vector2f();
	private Vector2f dir =  new Vector2f();

	private float speed = DEFAULT_SPEED;

	private World world;

	public Player(World world) {
		this.world = world;

		position.x = 100;
		position.y = 100;
	}

	@Override public void inputStarted() { }

	@Override
	public void keyPressed(final int key, final char c) {
		if (key == KeyConfig.WALK_UP) {
			dir.y -= 1;
		} else if (key == KeyConfig.WALK_DOWN) {
			dir.y += 1;
		} else if (key == KeyConfig.WALK_LEFT) {
			dir.x -= 1;
		} else if (key == KeyConfig.WALK_RIGHT) {
			dir.x += 1;
		}
	}

	@Override
	public void keyReleased(final int key, final char c) {
		if (key == KeyConfig.WALK_UP) {
			dir.y += 1;
		} else if (key == KeyConfig.WALK_DOWN) {
			dir.y -= 1;
		} else if (key == KeyConfig.WALK_LEFT) {
			dir.x += 1;
		} else if (key == KeyConfig.WALK_RIGHT) {
			dir.x -= 1;
		}
	}

	@Override public void setInput(final Input input) { }

	@Override public boolean isAcceptingInput() { return true; }

	@Override public void inputEnded() { }

	public void update(final GameContainer gameContainer, final int dt) {

		// Move
		if (dir.lengthSquared() != 0) {
			Vector2f newPos = position.copy().add(dir.copy().normalise().scale(speed * dt));

			if (world.getTileFromWorldPosition(new Vector2f(newPos.x + dir.x * (SIZE / 2), position.y)).SOLID) {
				newPos.x += (dir.x < 0 ? Tile.PIXELS - ((newPos.x + dir.x * (SIZE / 2)) % Tile.PIXELS)
									   : -(((newPos.x + dir.x * (SIZE / 2)) % Tile.PIXELS)));
			}

			if (world.getTileFromWorldPosition(new Vector2f(position.x, newPos.y + dir.y * (SIZE / 2))).SOLID) {
				newPos.y += (dir.y < 0 ? Tile.PIXELS - ((newPos.y + dir.y * (SIZE / 2)) % Tile.PIXELS)
									   : -(((newPos.y + dir.y * (SIZE / 2)) % Tile.PIXELS)));
			}

			position.set(newPos);
		}
	}

	public void render(final Graphics g) {
		g.fillRect(position.x - SIZE / 2, position.y - SIZE / 2, SIZE, SIZE);
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
}
