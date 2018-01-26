package jaam.fpm.client;

import org.newdawn.slick.Graphics;

public class World {

	private Player player;

	public World() {
		player = new Player();
	}

	public void update(final int dt) {
		player.update(dt);
	}

	public void render(final Graphics g) {
		player.render(g);
	}
}
