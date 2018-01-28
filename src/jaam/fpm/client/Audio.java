package jaam.fpm.client;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.SoundStore;

import java.util.HashMap;

public class Audio {

	private Audio() {}

	private static HashMap<String, Sound> sounds;

	private static Music music;

	private static SoundStore soundStore;

	public static final void play(String sound) {
		sounds.get(sound).play();
	}

	public static final void init() {
		sounds = new HashMap<>();

		soundStore = SoundStore.get();

		try {
			music = new Music("res/audio/music.wav");
			sounds.put("stab", new Sound("res/audio/stab.wav"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public static final void playMusic() {
		music.loop();
	}

	public static final void setMusicPitch() {
		soundStore.setMusicPitch(.5f);
	}

}