package jaam.fpm.client;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.util.HashMap;

public class Audio {

	private Audio() {}

	private static HashMap<String, Sound> sounds;

	public static final void play(String sound) {
		sounds.get(sound).play();
	}

	public static final void init() {
		sounds = new HashMap<>();

		try {
			sounds.put("stab", new Sound("res/audio/stab.wav"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
