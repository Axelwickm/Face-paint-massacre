package jaam.fpm.client;

import com.esotericsoftware.kryonet.Client;
import jaam.fpm.client.Drawing;
import jaam.fpm.packet.PlayerActionPacket;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.ImageData;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class PlayState extends BasicGame
{
	private Client client;
	private World world;
	private Drawing currentDrawing;

	private Image lastImage;

	private boolean facepaintMode = true;

	public PlayState(String name, Client client) throws SlickException {
		super(name);
		this.client = client;
	}

	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		System.out.println("Init game");
		world = new World(client);
		LaunchClient.getClientNet().world = world;
		world.init(gameContainer);
		currentDrawing = new Drawing();
	}

	public void restartGame(){
		facepaintMode = true;
	}

	@Override
	public void update(final GameContainer gameContainer, final int dt) throws SlickException {

		Input input = gameContainer.getInput();
		if (currentDrawing != null) {
			currentDrawing.update(gameContainer, dt);
		}


		if (currentDrawing != null && input.isKeyPressed(KeyConfig.STOP_DRAWING)) {
			lastImage = currentDrawing;
			currentDrawing = null;

			PlayerActionPacket packet;

			if (facepaintMode) {
				// Send a READY PA-packet and exit facepaint mode
				packet = PlayerActionPacket.make(PlayerActionPacket.Action.READY);
			} else {
				packet = PlayerActionPacket.make(PlayerActionPacket.Action.POST_NOTE);
			}
			packet.drawing = exportImageData(lastImage);
			client.sendTCP(packet);
			if (facepaintMode) facepaintMode = false;
			world.getMe().setFaceImage(lastImage);

		}
		if (!facepaintMode) world.update(gameContainer, dt);

		// Exit
		if (input.isKeyPressed(KeyConfig.EXIT))
			LaunchClient.exit();
	}

	public static final byte[] exportImageData(Image img) throws SlickException {
		byte[] array = new byte[Drawing.DRAWING_WIDTH * Drawing.DRAWING_HEIGHT * 4];
		ByteBuffer bb = ByteBuffer.allocateDirect(Drawing.DRAWING_WIDTH * Drawing.DRAWING_HEIGHT * 4);
		Graphics g = img.getGraphics();
		g.getArea(0, 0, Drawing.DRAWING_WIDTH, Drawing.DRAWING_HEIGHT, bb);
		g.flush();

		bb.position(0);

		bb.get(array, 0, array.length);

		for (int i = 0; i < array.length; ++i) {
			if (array[i] != 0) {
				System.err.println("Some byte is nonzero.");
				return array;
			}
		}
		System.err.println("It's zeroes all the way down.");
		return array;
	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {
		/*if (lastImage != null) {
			if (true || gameContainer.getInput().isKeyPressed(Input.KEY_L)) {
				boolean anyOpaque = false;
				outer:for (int y = 0; y < lastImage.getHeight(); ++y) for (int x = 0; x < lastImage.getWidth(); ++x) {
					System.out.println(lastImage.getColor(x,y).r);
					if (lastImage.getColor(x, y).getAlphaByte() != 0 && false) {
						System.err.println("Some pixel is opaque!");
						break outer;
					}
				}
			}
			world.getMe().makeFaceImage().draw(0, 0, 1);
			//return;
		}*/
		if (facepaintMode) {
			graphics.translate(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);

			if (currentDrawing != null) {
				Vector2f cameraPos = world.getCameraPosition();

				int dx = (int) ((cameraPos.x - Settings.SCREEN_WIDTH / 2));
				int dy = (int) ((cameraPos.y - Settings.SCREEN_HEIGHT / 2));
				currentDrawing.render(gameContainer, graphics, dx, dy);

				currentDrawing.render(gameContainer, graphics, dx, dy);
			}
		} else {
			world.render(graphics);
		}
	}

	public Client getClient() {
		return this.client;
	}
}
