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

			if (facepaintMode) {
				// Send a READY PA-packet and exit facepaint mode
				packet = PlayerActionPacket.make(PlayerActionPacket.Action.READY);
				world.readyCount++;
			} else {
				packet = PlayerActionPacket.make(PlayerActionPacket.Action.POST_NOTE);
				float[] pos = {world.getMe().getPosition().x, world.getMe().getPosition().y};
				packet.notePosition = pos;
				System.err.println("Note placed");
			}
			Image img = ((Drawing) lastImage).getImage();
			packet.drawing = exportImageData(img);
			world.getMe().setImage(img.getScaledCopy(Player.SIZE, Player.SIZE));
			client.sendTCP(packet);
			if (facepaintMode) facepaintMode = false;

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
		byte[] array = new byte[256 * 256 * 4];

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
		}
	}

	public Client getClient() {
		return this.client;
	}
}
