package jaam.fpm.client;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Knife extends Weapon {

	public static final int SIZE = 16;

	public static final int STAB_DURATION = 200;

	private int stabbing = 0;

	private Vector2f dir = new Vector2f(1, 0);

	public Knife(Player player) {
		super(player);
	}

	@Override
	public void use() {
		if (!active)
			return;

		stabbing = STAB_DURATION;

		for (Player o : player.getWorld().getOthers().values()) {
			if (o.getPosition().distance(player.getPosition().copy().add(dir.copy().scale(SIZE))) < Player.SIZE) {
				System.out.println("Stab!");
			}
		}
	}

	@Override
	public void update(final int dt) {
		if ((dir.x != player.getDir().x || dir.y != player.getDir().y)
				&& player.getDir().lengthSquared() != 0) {
			dir.set(player.getDir());
		}

		if (stabbing > 0) {
			stabbing -= dt;
			if (stabbing <= 0) {
				stabbing = 0;
			}
		}
	}

	@Override
	public void render(final Graphics g) {
		if (!active)
			return;

		g.setColor(Color.black);
		g.rotate(1, 0, (float) dir.getTheta());
		g.translate(Player.SIZE / 4 * (stabbing > 0 ? 3.0f : 1.0f), 0);

		g.fillRect(- SIZE / 2, - SIZE / 4 / 2, SIZE, SIZE / 4);
	}
}
