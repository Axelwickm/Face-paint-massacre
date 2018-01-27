package jaam.fpm.client;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Camera {

	private static final float DEFAULT_SPEED = .1f;

	private static final float THRESHOLD     = 1.0f;

	private Vector2f position = new Vector2f();

	private float speed = DEFAULT_SPEED;

	public Vector2f getPosition() {
		return position;
	}

	public Camera() {

	}

	public void update(final Vector2f target, final int dt) {
		if (Math.abs(target.x - position.x) > THRESHOLD) {
			position.x += (target.x - position.x) * dt / 100.0f;
		}

		if (Math.abs(target.y - position.y) > THRESHOLD) {
			position.y += (target.y - position.y) * dt / 100.0f;
		}
	}

	public void translate(final Graphics g) {
		g.translate(Settings.SCREEN_WIDTH / 2 - position.x, Settings.SCREEN_HEIGHT / 2 - position.y);
	}
}
