package jaam.fpm.client;

import com.esotericsoftware.kryo.NotNull;
import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.shared.State;
import jaam.fpm.shared.Tile;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class Player implements KeyListener {

	public static final int SIZE = 32;

	public static final float DEFAULT_SPEED = .3f;
	public static final int DEFAULT_HEALTH = 5;

	public static final int VIEW_RADIUS = 1;

	private Vector2f position = new Vector2f();
	private Vector2f dir =  new Vector2f();

	private float speed = DEFAULT_SPEED;

	private World world;

	private boolean uninitializedFace = true;

	private boolean controllable;

	private byte[] face;
	private transient Image faceImage;

	public byte[] getFace() {
		if (uninitializedFace) throw new RuntimeException("Give Me A Face First");
		return face;
	}

	public Image getFaceImage() {
		if (uninitializedFace) throw new RuntimeException("Give Me A Face First");
		return faceImage;
	}

	/**
	 * Gets the face Image of this Player, creating it if it hasn't already been created.
	 *
	 * @return the Image representing the face of this Player.
	 * @throws NullPointerException if this Player doesn't have a face Image or a face byte array yet.
	 * @throws RuntimeException if the image can't be created because the current thread has no OpenGL context.
	 * @throws SlickException if creating the Image fails for other reasons.
	 */
	public Image makeFaceImage() throws SlickException {
		if (faceImage != null) return faceImage;
		if (face == null) throw new NullPointerException("This player has no face.");

		BufferedImage ni = new BufferedImage(Drawing.DRAWING_WIDTH, Drawing.DRAWING_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR_PRE); // TODO: Can I insert things here?

		ni.setRGB(0, 0, ni.getWidth(), ni.getHeight(), face, 0, 1); // TODO: THIS ISN'T WORKING

		ImageBuffer ib = new ImageBuffer(Drawing.DRAWING_WIDTH, Drawing.DRAWING_HEIGHT); // Aproach 2: Doesn't seem to work at all.
		ib.getImageBufferData().put(face);
		faceImage = ib.getImage(Image.FILTER_NEAREST);

		//faceImage = new Image(new ByteArrayInputStream(face), "", false, Image.FILTER_NEAREST); // Approach 3: Doesn't seem to work either.

		boolean bar = false;
		for (int i = 3; i < face.length; i+=4) {

			if (face[i] != 0) {
				bar = true;
				break;
			}
		}
		if (bar) System.err.println("The array isn't empty");
		else System.err.println("The array is empty");
		boolean foo = false;
		for (int y = 0; y < faceImage.getHeight() && !foo; ++y) for (int x = 0; !foo && x < faceImage.getWidth(); ++x) {
			if (faceImage.getColor(x, y).getAlphaByte() != 0) foo = true;
		}
		if (foo) System.err.println("The image is not empty");
		else System.err.println("The image is empty");

		return faceImage;
	}

	public void setFace(byte[] face) {
		if (face == null) throw new IllegalArgumentException("FACE ME!");
		uninitializedFace = false;
		this.face = face;
		faceImage = null;
	}

	public void setFaceImage(Image faceImage) throws SlickException {
		this.faceImage = faceImage;
		face = PlayState.exportImageData(faceImage);
		uninitializedFace = false;
	}

	private int health = DEFAULT_HEALTH;

	private State state = State.AlIVE;

	private Weapon weapon;

	public Player(World world) {
		this(world, true);
	}

	public Player(World world, boolean controllable) {
		this.world = world;
		this.controllable = controllable;
		weapon = new Knife(this);
	}

	@Override public void inputStarted() { }

	@Override
	public void keyPressed(final int key, final char c) {
		boolean pressed = false;

		if (key == KeyConfig.WALK_UP) {
			dir.y -= 1;
			pressed = true;
		} else if (key == KeyConfig.WALK_DOWN) {
			dir.y += 1;
			pressed = true;
		} else if (key == KeyConfig.WALK_LEFT) {
			dir.x -= 1;
			pressed = true;
		} else if (key == KeyConfig.WALK_RIGHT) {
			dir.x += 1;
			pressed = true;
		}

		if (pressed) {
			sendWalkPacket();
		}

		if (key == KeyConfig.TOGGLE_WEAPON)
			weapon.toggle();
		if (key == KeyConfig.USE_WEAPON)
			weapon.use();
	}

	@Override
	public void keyReleased(final int key, final char c) {

		boolean released = false;

		if (key == KeyConfig.WALK_UP) {
			dir.y += 1;
			released = true;
		} else if (key == KeyConfig.WALK_DOWN) {
			dir.y -= 1;
			released = true;
		} else if (key == KeyConfig.WALK_LEFT) {
			dir.x += 1;
			released = true;
		} else if (key == KeyConfig.WALK_RIGHT) {
			dir.x -= 1;
			released = true;
		}

		if (released) {
			if (dir.lengthSquared() == 0) {
				sendStopPacket();
			} else {
				sendWalkPacket();
			}
		}
	}

	@Override public void setInput(final Input input) { }

	@Override public boolean isAcceptingInput() { return true; }

	@Override public void inputEnded() { }

	public void update(final GameContainer gameContainer, final int dt) {

		// Move
		if (dir.lengthSquared() != 0) {
			Vector2f newPos = position.copy().add(dir.copy().normalise().scale(speed * dt));

			if (dir.x != 0) {
				if (world.getTileFromWorldPosition(
						new Vector2f(newPos.x + dir.x * (SIZE / 2), position.y - (SIZE / 2)+1)).SOLID ||
					world.getTileFromWorldPosition(new Vector2f(newPos.x + dir.x * (SIZE / 2),
																position.y + (SIZE / 2)-1)).SOLID) {

					newPos.x += (dir.x < 0 ?
								 Tile.PIXELS - ((newPos.x + dir.x * (SIZE / 2)) % Tile.PIXELS) :
								 -(((newPos.x + dir.x * (SIZE / 2)) % Tile.PIXELS)));
				}
			}

			if (dir.y != 0) {
				if (world.getTileFromWorldPosition(
						new Vector2f(position.x - (SIZE / 2)+1, newPos.y + dir.y * (SIZE / 2))).SOLID ||
					world.getTileFromWorldPosition(new Vector2f(position.x + (SIZE / 2)-1,
																newPos.y + dir.y * (SIZE / 2))).SOLID) {

					newPos.y += (dir.y < 0 ?
								 Tile.PIXELS - ((newPos.y + dir.y * (SIZE / 2)) % Tile.PIXELS) :
								 -(((newPos.y + dir.y * (SIZE / 2)) % Tile.PIXELS)));
				}
			}

			position.set(newPos);
		}

		weapon.update(dt);
	}

	public void render(final Graphics g) throws SlickException {
		g.pushTransform();
		g.setColor(Color.white);
		g.translate(position.x, position.y);
		g.fillRect(- SIZE / 2, - SIZE / 2, SIZE, SIZE);
		weapon.render(g);

		if (face != null) {// Don't bother drawing the face if we don't have one.)
			Image img = makeFaceImage();

			float xScale = (float)Player.SIZE / img.getWidth();
			float yScale = (float)Player.SIZE / img.getHeight();
			float scale = Math.min(xScale, yScale);

			img.draw(- SIZE / 2, - SIZE / 2, scale);
			//img.draw(position.x - SIZE / 2, position.y - SIZE / 2);
		} else System.err.println("FACELESS ONE");
		g.popTransform();
	}

	public int getChunkX() {
		return Math.floorDiv((int) position.x, Chunk.PIXELS);
	}

	public int getChunkY() {
		return Math.floorDiv((int) position.y, Chunk.PIXELS);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(final Vector2f position) {
		this.position = position;
	}

	public Vector2f getDir() {
		return dir;
	}

	public void setDir(final Vector2f dir) {
		this.dir = dir;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(final float speed) {
		this.speed = speed;
	}

	public void sendWalkPacket() {
		PlayerActionPacket p = PlayerActionPacket.make(PlayerActionPacket.Action.START_WALKING);

		p.velocity = new float[] {dir.x, dir.y};

		world.getClient().sendTCP(p);
	}

	public void sendStopPacket() {
		PlayerActionPacket p = PlayerActionPacket.make(PlayerActionPacket.Action.STOP_WALKING);

		p.stopPosition = new float[] {position.x, position.y};

		world.getClient().sendTCP(p);
	}

	public World getWorld() {
		return world;
	}
}
