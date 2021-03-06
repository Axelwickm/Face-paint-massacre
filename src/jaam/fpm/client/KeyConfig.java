package jaam.fpm.client;

import org.newdawn.slick.Input;

public class KeyConfig {

	public static final int EXIT = Input.KEY_ESCAPE;

	public static final int WALK_UP = Input.KEY_W;
	public static final int WALK_DOWN = Input.KEY_S;
	public static final int WALK_LEFT = Input.KEY_A;
	public static final int WALK_RIGHT = Input.KEY_D;
	public static final int TOGGLE_WEAPON = Input.KEY_E;
	public static final int USE_WEAPON = Input.KEY_SPACE;

	public static final int NEXT_COLOR = Input.KEY_Q;
	public static final int PREV_COLOR = Input.KEY_E;
	public static final int UNDO = Input.KEY_Z;

	public static final int START_DRAWING = Input.KEY_Q;
	public static final int STOP_DRAWING = Input.KEY_R;

	public static final int BIGGER_BRUSH = Input.KEY_X;
	public static final int SMALLER_BRUSH = Input.KEY_C;

	private KeyConfig() { }
}
