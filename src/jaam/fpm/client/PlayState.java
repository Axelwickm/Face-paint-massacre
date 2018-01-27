package jaam.fpm.client;

import jaam.fpm.Drawing;
import org.newdawn.slick.*;

public class PlayState extends BasicGame
{
	private World world;
	private Drawing currentDrawing;

	public PlayState(String name) {
		super(name);
	}

	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		System.out.println("Init game");
		world = new World();
		world.init(gameContainer);
	}

	@Override
	public void update(final GameContainer gameContainer, final int dt) throws SlickException {

		world.update(gameContainer, dt);

		/*Input input = gameContainer.getInput();
		if (currentDrawing == null && input.isKeyPressed(KeyConfig.KEYCODE_START_DRAWING)) {
			currentDrawing = new Drawing();
		} else if (currentDrawing != null && input.isKeyPressed(KeyConfig.KEYCODE_STOP_DRAWING)) {
			currentDrawing = null;
		}

		if (currentDrawing != null) currentDrawing.update(gameContainer, dt);*/
	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {
		world.render(graphics);
		//if (currentDrawing != null) currentDrawing.render(gameContainer, graphics);
	}
}
