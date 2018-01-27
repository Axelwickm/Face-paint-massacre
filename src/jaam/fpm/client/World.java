package jaam.fpm.client;

import jaam.fpm.shared.Tile;
import org.newdawn.slick.Graphics;

public class World {

	private Player player;

	private Chunk[][] chunks;
	private int chunksX, chunksY;
	private int tilesX, tilesY;

	public World() {
		player = new Player();

		// TODO: REMOVE
		Tile[][] t_tiles = new Tile[3][3];
		for (int i = 0; i < t_tiles.length; i++) {
			for (int j = 0; j < t_tiles[i].length; j++) {
				t_tiles[i][j] = Tile.FLOOR;
			}
		}

		createChunks(3, 3, t_tiles);
	}

	public void update(final int dt) {
		player.update(dt);
	}

	public void render(final Graphics g) {
		for (Chunk[] arr : chunks) {
			for (Chunk c : arr) {
				c.render();
			}
		}
		player.render(g);
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
