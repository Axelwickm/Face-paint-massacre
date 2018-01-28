package jaam.fpm.client;

import com.esotericsoftware.kryonet.Client;
import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.shared.Settings;
import org.lwjgl.openal.AL;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PlayState extends BasicGame
{
	private Client client;
	private World world;
	private Drawing currentDrawing;

	private Image lastImage;

	private boolean facepaintMode = true;
	public boolean shouldRestart = false;

	public PlayState(String name, Client client) throws SlickException {
		super(name);
		this.client = client;
	}

	@Override
	public void init(final GameContainer gameContainer) throws SlickException {
		System.out.println("Init game");
		System.out.println(client.getKryo());
		world = new World(client);
		LaunchClient.getClientNet().world = world;
		world.init(gameContainer);
		currentDrawing = new Drawing();
	}


	@Override
	public void update(final GameContainer gameContainer, final int dt) throws SlickException {

		Input input = gameContainer.getInput();

		if (currentDrawing == null && input.isKeyPressed(KeyConfig.START_DRAWING)) {
			currentDrawing = new Drawing();
		}

		if (currentDrawing != null) {
			currentDrawing.update(gameContainer, dt);
		}


		if (currentDrawing != null && input.isKeyPressed(KeyConfig.STOP_DRAWING)) {
			lastImage = currentDrawing;
			currentDrawing = null;

			PlayerActionPacket packet;
			Image img;
			if (facepaintMode) {
				// Send a READY PA-packet and exit facepaint mode
				packet = PlayerActionPacket.make(PlayerActionPacket.Action.READY);
				world.readyCount++;
				img = ((Drawing) lastImage).getImage();
				packet.drawing = exportImageData(img);
			} else {
				packet = PlayerActionPacket.make(PlayerActionPacket.Action.POST_NOTE);
				float[] pos = {world.getMe().getPosition().x, world.getMe().getPosition().y};
				packet.notePosition = pos;
				img = ((Drawing) lastImage).getImage();
				packet.drawing = exportImageData(img);
				world.addNote(packet.drawing, world.getMe().getPosition());
			}
			if (facepaintMode) {
				world.getMe().setImage(img.getScaledCopy(Player.SIZE, Player.SIZE));
				facepaintMode = false;
			}
			client.sendTCP(packet);


		}
		if (!facepaintMode) world.update(gameContainer, dt);

		if (shouldRestart){
			System.out.println("Restarting game");
			shouldRestart = false;
			AL.destroy();
			gameContainer.reinit();
		}
		// Exit
		if (input.isKeyPressed(KeyConfig.EXIT))
			LaunchClient.exit();
	}

	public static final byte[] exportImageData(Image img) throws SlickException {
		byte[] array = new byte[img.getWidth() * img.getHeight() * 4];

		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				array[(i * img.getWidth() + j) * 4 + 0] = (byte) (img.getColor(j, i).getRedByte());
				array[(i * img.getWidth() + j) * 4 + 1] = (byte) (img.getColor(j, i).getGreenByte());
				array[(i * img.getWidth() + j) * 4 + 2] = (byte) (img.getColor(j, i).getBlueByte());
				array[(i * img.getWidth() + j) * 4 + 3] = (byte) (img.getColor(j, i).getAlphaByte());
			}
		}

		return array;
	}

	public static Image importImageData(byte[] data, int width, int height) throws SlickException {
		Image img = new Image(width, height);
		Graphics g = img.getGraphics();
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				g.setColor(new Color(data[4 * (width * y + x)],
						data[4 * (width * y + x) + 1],
						data[4 * (width * y + x) + 2],
						data[4 * (width * y + x) + 3]));
				g.fillRect(x, y, 1, 1);
			}
		}
		return img;
	}

	@Override
	public void render(final GameContainer gameContainer, final Graphics graphics) throws SlickException {

		if (facepaintMode) {
			graphics.translate(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);


		} else {
			world.render(graphics);
		}

		if (currentDrawing != null) {
			Vector2f cameraPos = world.getCameraPosition();

			int dx = (int) ((cameraPos.x - Settings.SCREEN_WIDTH / 2));
			int dy = (int) ((cameraPos.y - Settings.SCREEN_HEIGHT / 2));
			currentDrawing.render(gameContainer, graphics, dx, dy);

			currentDrawing.render(gameContainer, graphics, dx, dy);

			graphics.drawString("Ready: " + world.readyCount + "/" + world.playerCount,
								cameraPos.x - Settings.SCREEN_WIDTH / 2 + 25,
								cameraPos.y + Settings.SCREEN_HEIGHT / 2 - 50);
		}
	}

	public Client getClient() {
		return this.client;
	}
}
