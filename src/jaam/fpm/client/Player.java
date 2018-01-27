package jaam.fpm.client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Player implements KeyListener {

	public static final int SIZE = 10;

	public static final float DEFAULT_SPEED = 1.0f;

	private Rectangle rect;

	private Vector2f position = new Vector2f();
	private Vector2f dir =  new Vector2f();

	private float speed = DEFAULT_SPEED;

	public Player() {
		rect = new Rectangle(position.x, position.y, SIZE, SIZE);
	}

	@Override public void inputStarted() { }

	@Override
	public void keyPressed(final int key, final char c) {
		if (key == KeyConfig.KEYCODE_WALK_UP) {
			dir.y += 1;
		} else if (key == KeyConfig.KEYCODE_WALK_DOWN) {
			dir.y -= 1;
		} else if (key == KeyConfig.KEYCODE_WALK_LEFT) {
			dir.x -= 1;
		} else if (key == KeyConfig.KEYCODE_WALK_RIGHT) {
			dir.x += 1;
		}
	}

	@Override
	public void keyReleased(final int key, final char c) {
		if (key == KeyConfig.KEYCODE_WALK_UP) {
			dir.y -= 1;
		} else if (key == KeyConfig.KEYCODE_WALK_DOWN) {
			dir.y += 1;
		} else if (key == KeyConfig.KEYCODE_WALK_LEFT) {
			dir.x += 1;
		} else if (key == KeyConfig.KEYCODE_WALK_RIGHT) {
			dir.x -= 1;
		}
	}

	@Override public void setInput(final Input input) { }

	@Override public boolean isAcceptingInput() { return true; }

	@Override public void inputEnded() { }

	public void update(final GameContainer gameContainer, final int dt) {
		
	}

	public void render(final Graphics g) {
		g.translate(400, 300);
		g.draw(rect);
	}
}
