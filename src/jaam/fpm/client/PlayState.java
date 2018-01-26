package jaam.fpm.client;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class PlayState extends BasicGame
{

	private World world;

	public PlayState(String name) {
		super(name);

		world = new World();
	}

	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		System.out.println("Init game");
	}

	@Override
	public void update(final GameContainer gameContainer, final int dt) throws SlickException {
		world.update(dt);
	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {
		world.render(graphics);
	}
}
