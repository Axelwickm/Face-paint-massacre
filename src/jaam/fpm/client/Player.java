package jaam.fpm.client;

import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.shared.Settings;
import jaam.fpm.shared.State;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.Image;

import java.awt.*;

public class Player implements KeyListener {

	public static final int SIZE = 32;

	public static final float DEFAULT_SPEED = .3f;
	public static final int DEFAULT_HEALTH = 5;

	public static final int VIEW_RADIUS = 1;

	private Vector2f position = new Vector2f();
	private Vector2f dir =  new Vector2f();

	private Vector2f specPosition = new Vector2f();

	private float speed = DEFAULT_SPEED;

	private World world;

	private boolean controllable;

	public int health = DEFAULT_HEALTH;

	private State state = State.AlIVE;

	private Weapon weapon;

	private TrueTypeFont font;

	private Image image;

	private volatile byte[] imgToDecode;

	public Player(World world) {
		this(world, true);
	}

	public Player(World world, boolean controllable) {
		this.world = world;
		this.controllable = controllable;
		weapon = new Knife(this);

		if (controllable)
			font = new TrueTypeFont(new Font("Verdana", Font.BOLD, 32), true);
	}

	@Override public void inputStarted() { }

	@Override
	public void keyPressed(final int key, final char c) {
		//TODO: FIX HEALTH BEHAVIOUR

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

		if (health <= 0)
			return;

		if (pressed) {
			sendWalkPacket();
		}

		if (key == KeyConfig.TOGGLE_WEAPON && world.isMurdererChosen()) {
			world.getClient().sendTCP(PlayerActionPacket.make(PlayerActionPacket.Action.TOGGLE_WEAPON));
			weapon.toggle();
		}
		if (key == KeyConfig.USE_WEAPON && world.isMurdererChosen())
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

		if (health <= 0)
			return;

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
		if (health <= 0 && (state == State.AlIVE || state == State.MURDERER)){
			specPosition.set(position);
			kill();
		}

		if (state == State.DEAD){
			specPosition.add(dir.copy().normalise().scale(speed * dt));
			return;
		}

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

	public void render(final Graphics g) {
		if (world.getMe() != this) {
			while (imgToDecode == null) { }

			if (image == null) {
				decodeImage();
				System.out.println("hej");
			}
		}

		g.pushTransform();
		g.setColor(Color.white);
		g.translate(position.x, position.y);
		image.draw(- SIZE / 2, - SIZE / 2);
		weapon.render(g);
		g.popTransform();
	}

	public void renderHUD(final Graphics g) {
		if (font != null)
			font.drawString(world.getCameraPosition().x - Settings.SCREEN_WIDTH / 2,
						  world.getCameraPosition().y + Settings.SCREEN_HEIGHT / 2 - font.getHeight(),
							state.name(), state == State.MURDERER ? Color.red : Color.white);
	}

	public void kill(){
		if (this == world.getMe()){
			System.out.println("I died.");
			PlayerActionPacket p = PlayerActionPacket.make(PlayerActionPacket.Action.DIE);
			world.getClient().sendTCP(p);
		}
		state = State.DEAD;
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

	public Vector2f getSpecPosition() {
		return specPosition;
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

	public Weapon getWeapon() {
		return weapon;
	}

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isDead() {
		return state == State.DEAD;
	}

	public void setImage(Image img) {
		try {
			Image img2 = new Image(Player.SIZE, Player.SIZE);
			Graphics g = img2.getGraphics();
			g.setColor(new Color(130, 76, 35, 255));
			g.fillOval(0, 0, Player.SIZE, Player.SIZE);
			g.drawImage(img, 0, 0);
			g.flush();
			this.image = img2;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public Image getImage() {
		return image;
	}

	public void decodeImage(byte[] data) {
		imgToDecode = data;
	}

	private void decodeImage() {
		byte[] data = imgToDecode;
		ImageBuffer ib = new ImageBuffer(256, 256);
		for (int i = 0; i < data.length; i += 4) {
			int r = data[i + 0];
			int g = data[i + 1];
			int b = data[i + 2];
			int a = data[i + 3];

			System.out.println(r + " "+ g+ " " +b +" "+ a);

			ib.setRGBA((i / 4) % 256, Math.floorDiv((i / 4), 256), r, g, b, a);
		}
		setImage(ib.getImage().getScaledCopy(Player.SIZE, Player.SIZE));
	}
}
