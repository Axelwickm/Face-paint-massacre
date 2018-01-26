package jaam.fpm.client;

import jaam.fpm.client.Drawing;
import org.newdawn.slick.*;

public class PlayState extends BasicGame
{

	private Drawing currentDrawing;

	public PlayState(String name) {
		super(name);
	}

	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		System.out.println("Init game");
	}

	@Override
	public void update(final GameContainer gameContainer, final int i) throws SlickException {
		Input input = gameContainer.getInput();
		if (currentDrawing == null && input.isKeyPressed(KeyConfig.START_DRAWING)) {
			currentDrawing = new Drawing();
		} else if (currentDrawing != null && input.isKeyPressed(KeyConfig.STOP_DRAWING)) {
			currentDrawing = null;
		}

		if (currentDrawing != null) currentDrawing.update(gameContainer, i);

	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {
		if (currentDrawing != null) currentDrawing.render(gameContainer, graphics);
	}
}
