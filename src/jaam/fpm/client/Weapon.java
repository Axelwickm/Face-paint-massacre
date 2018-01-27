package jaam.fpm.client;

import org.newdawn.slick.Graphics;

public abstract class Weapon {

	protected Player player;

	protected boolean active = false;

	public Weapon(Player player) {
		this.player = player;
	}

	public abstract void use();

	public abstract void update(final int dt);

	public abstract void render(final Graphics g);

	public void toggle() {
		active = !active;
	}

}