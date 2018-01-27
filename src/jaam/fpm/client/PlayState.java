package jaam.fpm.client;

import jaam.fpm.client.Drawing;
import org.newdawn.slick.*;

public class PlayState extends BasicGame
{
	private World world;
	private Drawing currentDrawing;

	private Image lastImage;

	public PlayState(String name) {
		super(name);
	}

	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		System.out.println("Init game");
		world = new World();
	}

	@Override
	public void update(final GameContainer gameContainer, final int dt) throws SlickException {

		world.update(dt);

		Input input = gameContainer.getInput();
		if (currentDrawing == null && input.isKeyPressed(KeyConfig.START_DRAWING)) {
			currentDrawing = new Drawing();
		} else if (currentDrawing != null && input.isKeyPressed(KeyConfig.STOP_DRAWING)) {
			lastImage = currentDrawing.getImage();
			currentDrawing = null;
		}

		if (currentDrawing != null) currentDrawing.update(gameContainer, dt);
	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {
		world.render(graphics);
		//if (currentDrawing != null) currentDrawing.render(gameContainer, graphics);
	}
}
