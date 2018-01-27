package jaam.fpm.shared;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public enum Tile {

	FLOOR(false), WALL(true);

	public static final int PIXELS = 64;

	public final boolean SOLID;

	private Tile(boolean solid) {
		this.SOLID = solid;
	}

	public Image loadImage() {
		try {
			switch(this) {
				case FLOOR:   return new Image("res/texture/floor.png");
				default:      return null;
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}

		return null;
	}
}
