package jaam.fpm.client;

import org.newdawn.slick.Graphics;

public abstract class Weapon {

	protected Player player;

	public Weapon(Player player) {
		this.player = player;
	}

	public abstract void use();

	public abstract void render(final Graphics g);

}