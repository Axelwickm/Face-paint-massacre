package jaam.fpm.client;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Knife extends Weapon {

	public static final int SIZE = 12;

	public Knife(Player player) {
		super(player);
	}

	@Override
	public void use() {
		for (Player o : player.getWorld().getOthers().values()) {

		}
	}

	public void render(final Graphics g) {
		g.setColor(Color.black);
		g.drawRect(player.getPosition().x, player.getPosition().y, SIZE, SIZE / 3);
	}
}
