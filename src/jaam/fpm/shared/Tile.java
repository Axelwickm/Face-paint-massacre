package jaam.fpm.shared;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.HashMap;

public enum Tile {

	FLOOR(false), WALL(true);

	public static final int PIXELS = 64;

	public final boolean SOLID;

	private Tile(boolean solid) {
		this.SOLID = solid;
	}

	public Image loadImage() {
		return images.get(this);
	}

	public static HashMap<Tile, Image> images = new HashMap<>();

	public static void loadImages() {
		try {
			images.put(FLOOR, new Image("res/texture/floor.png"));
			images.put(WALL, new Image("res/texture/wall.png"));
		} catch (SlickException e) { e.printStackTrace(); }
	}
}
