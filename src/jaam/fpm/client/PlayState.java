package jaam.fpm.client;

import com.esotericsoftware.kryonet.Client;
import jaam.fpm.client.Drawing;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public class PlayState extends BasicGame
{
	private Client client;
	private World world;
	private Drawing currentDrawing;

	private Image lastImage;

	public PlayState(String name, Client client) {
		super(name);
		this.client = client;
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

		Input input = gameContainer.getInput();
		if (currentDrawing == null && input.isKeyPressed(KeyConfig.START_DRAWING)) {
			currentDrawing = new Drawing();
		} else if (currentDrawing != null && input.isKeyPressed(KeyConfig.STOP_DRAWING)) {
			lastImage = currentDrawing;
			currentDrawing = null;
		}

		if (currentDrawing != null) currentDrawing.update(gameContainer, dt);

		// Exit
		if (input.isKeyPressed(KeyConfig.EXIT))
			LaunchClient.exit();
	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {
		world.render(graphics);

		if (currentDrawing != null) {
			Vector2f cameraPos = world.getCameraPosition();

			int dx = (int)((cameraPos.x - Settings.SCREEN_WIDTH / 2));
			int dy = (int)((cameraPos.y - Settings.SCREEN_HEIGHT / 2));
			currentDrawing.render(gameContainer, graphics, dx, dy);

			currentDrawing.render(gameContainer, graphics, dx, dy);
		}
	}

	public Client getClient() {
		return this.client;
	}
}
