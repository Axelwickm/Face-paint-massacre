package jaam.fpm.client;

import com.esotericsoftware.kryonet.Client;
import jaam.fpm.packet.TileArrayPacket;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;

public class World {

	private Camera camera;

	private HashMap<Integer, Player> others = new HashMap<>();
	private Player player;
	private ArrayList<Note> notes = new ArrayList<>();

	private Chunk[][] chunks;
	private int chunksX, chunksY;
	private int tilesX, tilesY;

	private Image background;
	private Image fogOfWar;

	private boolean populated = false;

	private volatile TileArrayPacket tileArrayPacket;

	private Client client;

	public World(Client client) {
		this.client = client;
		player = new Player(this);
		camera = new Camera();
	}

	public Vector2f getCameraPosition() {
		return camera.getPosition();
	}

	public void init(final GameContainer gc) {
		gc.getInput().addKeyListener(player);

		try {
			background = new Image("res/texture/bg.png");
			fogOfWar = new Image("res/texture/fow.png");
			Tile.loadImages();
		} catch (SlickException e) {
			e.printStackTrace();
		}

		Audio.init();

		Audio.playMusic();
	}

	public void update(final GameContainer gc, final int dt) {
		if (!populated) {
			if (tileArrayPacket != null) {
                System.out.println("Player pos: "+tileArrayPacket.playerPosition[0]+" "+tileArrayPacket.playerPosition[1]);
				player.setPosition(new Vector2f(
				        tileArrayPacket.playerPosition[0]*Tile.PIXELS,
						tileArrayPacket.playerPosition[1]*Tile.PIXELS
				));
				createChunks(tileArrayPacket.tilesX, tileArrayPacket.tilesY, tileArrayPacket.tiles);


				populated = true;
				tileArrayPacket = null;
			}
			return;
		}

		for (Player p : others.values()) {
			p.update(gc, dt);
		}

		player.update(gc, dt);

		camera.update(player.getPosition(), dt);
	}

	public void render(final Graphics g) {
		if (!populated)
			return;

		background.draw(0, 0);

		camera.translate(g);

		for (int i = 0; i < Player.VIEW_RADIUS * 2 + 1; i++) {
			for (int j = 0; j < Player.VIEW_RADIUS * 2 + 1; j++) {
				renderChunk(j - Player.VIEW_RADIUS + player.getChunkX(),
							i - Player.VIEW_RADIUS + player.getChunkY());
			}
		}

		for (Player p : others.values()) {
			p.render(g);
		}

		player.render(g);

		fogOfWar.draw(camera.getPosition().x - Settings.SCREEN_WIDTH / 2,
					  camera.getPosition().y - Settings.SCREEN_HEIGHT / 2);
	}

	public Tile getTile(int cx, int cy, int tx, int ty) {
		return chunks[cy][cx].getTile(tx, ty);
	}

	public Tile getTileFromWorldPosition(Vector2f pos) {
		if (pos.x < 0 || pos.y < 0 || pos.x > (tilesX - 1) * Tile.PIXELS || pos.y > (tilesY - 1) * Tile.PIXELS)
			return Tile.WALL;

		return getTile(Math.floorDiv((int) pos.x, Chunk.PIXELS),
					   Math.floorDiv((int) pos.y, Chunk.PIXELS),
					   (int) (pos.x % Chunk.PIXELS) / Tile.PIXELS,
					   (int) (pos.y % Chunk.PIXELS) / Tile.PIXELS);
	}

	private boolean renderChunk(final int x, final int y) {
		if (x >= 0 && x < chunksX && y >= 0 && y < chunksY) {
			chunks[y][x].render();
			return true;
		}

		return false;
	}

	public void setPacket(TileArrayPacket tap) {
		tileArrayPacket = tap;
	}

	public void createChunks(int tilesX, int tilesY, Tile[][] tiles) {
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		this.chunksX = (int) Math.ceil(((float) tilesX) / Chunk.SIZE);
		this.chunksY = (int) Math.ceil(((float) tilesY) / Chunk.SIZE);

		chunks = new Chunk[chunksY][chunksX];

		System.out.println(tiles);

		for (int cy = 0; cy < chunksY; cy++) {
			for (int cx = 0; cx < chunksX; cx++) {
				Tile[][] chunkTiles = new Tile[Chunk.SIZE][Chunk.SIZE];

				for (int ty = 0; ty < Chunk.SIZE && cy * Chunk.SIZE + ty < tilesY; ty++) {
					for (int tx = 0; tx < Chunk.SIZE && cx * Chunk.SIZE + tx < tilesX; tx++) {
						chunkTiles[ty][tx] = tiles[cy * Chunk.SIZE + ty][cx * Chunk.SIZE + tx];
					}
				}

				chunks[cy][cx] = new Chunk(cx, cy, chunkTiles);
			}
		}

		populated = true;
	}

	public void addPlayer(int id, Player player) {
		others.put(id, player);
	}
	public void addNote(Vector2f pos){ notes.add(new Note(pos)); }

	public Client getClient() {
		return client;
	}

	public HashMap<Integer, Player> getOthers() {
		return others;
	}
	public Player getPlayer(int id) { return others.get(id); }
	public Player getMe() { return player; }
}
