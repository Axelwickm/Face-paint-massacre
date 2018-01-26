package jaam.fpm.client;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Player {

	public static final int SIZE = 10;

	private Rectangle rect;

	private Vector2f position = new Vector2f();

	public Player() {
		rect = new Rectangle(position.x, position.y, SIZE, SIZE);
	}

	public void update(final int dt) {
		System.out.println(dt);
	}

	public void render(final Graphics g) {
		g.translate(400, 300);
		g.draw(rect);
	}
}
