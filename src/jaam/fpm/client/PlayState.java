package jaam.fpm.client;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class PlayState extends BasicGame
{

	public PlayState(String name) {
		super(name);
	}

	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		System.out.println("Init game");
	}

	@Override
	public void update(final GameContainer gameContainer, final int i) throws SlickException {

	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {

	}
}
