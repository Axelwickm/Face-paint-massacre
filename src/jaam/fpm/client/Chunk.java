package jaam.fpm.client;

import jaam.fpm.shared.Tile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Chunk {

	public static int SIZE = 8;

	public static int PIXELS = SIZE * Tile.PIXELS;

	private Tile[][] tiles;

	private int x;
	private int y;

	private Image image;

	public Chunk(int x, int y, Tile[][] tiles) {
		this.x = x;
		this.y = y;
		this.tiles = tiles;

		try {
			image = new Image(Chunk.PIXELS, Chunk.PIXELS);
		} catch (SlickException e) {
			e.printStackTrace();
		}

		Graphics gfx = null;

		try {
			gfx = image.getGraphics();
		} catch (SlickException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < Chunk.SIZE; i++) {
			for (int j = 0; j < Chunk.SIZE; j++) {
				if (tiles[i][j] == null)
					break;

				gfx.drawImage(tiles[i][j].loadImage(), j * Tile.PIXELS, i * Tile.PIXELS);
			}
		}

		gfx.flush();
	}

	public void render() {
		image.draw(x * Chunk.PIXELS, y * Chunk.PIXELS);
	}

	public Tile getTile(int tx, int ty) {
		return tiles[ty][tx];
	}
}