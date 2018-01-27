package jaam.fpm.client;

import jaam.fpm.shared.Tile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class World {

	private Camera camera;

	private Player player;

	private Chunk[][] chunks;
	private int chunksX, chunksY;
	private int tilesX, tilesY;

	private Image background;

	public World() {
		player = new Player();
		camera = new Camera();

		// TODO: REMOVE
		int t_cx = 5;
		int t_cy = 5;

		Tile[][] t_tiles = new Tile[Chunk.SIZE * t_cx][Chunk.SIZE * t_cy];
		for (int i = 0; i < t_tiles.length; i++) {
			for (int j = 0; j < t_tiles[i].length; j++) {
				if (j % Chunk.SIZE == 0 || i % Chunk.SIZE == 0)
					t_tiles[i][j] = Tile.WALL;
				else
					t_tiles[i][j] = Tile.FLOOR;
			}
		}

		createChunks(Chunk.SIZE * t_cx, Chunk.SIZE * t_cy, t_tiles);
	}

	public Vector2f getCameraPosition() {
		return camera.getPosition();
	}

	public void init(final GameContainer gc) {
		gc.getInput().addKeyListener(player);

		try {
			background = new Image("res/texture/bg.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void update(final GameContainer gc, final int dt) {
		player.update(gc, dt);

		camera.update(player.getPosition(), dt);
	}

	public void render(final Graphics g) {
		background.draw(0, 0);

		camera.translate(g);

		for (int i = 0; i < Player.VIEW_RADIUS * 2 + 1; i++) {
			for (int j = 0; j < Player.VIEW_RADIUS * 2 + 1; j++) {
				renderChunk(j - Player.VIEW_RADIUS + player.getChunkX(),
							i - Player.VIEW_RADIUS + player.getChunkY());
			}
		}

		player.render(g);
	}

	private boolean renderChunk(final int x, final int y) {
		if (x >= 0 && x < chunksX && y >= 0 && y < chunksY) {
			chunks[y][x].render();
			return true;
		}

		return false;
	}

	public void createChunks(int tilesX, int tilesY, Tile[][] tiles) {
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		this.chunksX = (int) Math.ceil(((float) tilesX) / Chunk.SIZE);
		this.chunksY = (int) Math.ceil(((float) tilesY) / Chunk.SIZE);

		chunks = new Chunk[chunksY][chunksX];

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

		this.chunks = chunks;
	}
}
