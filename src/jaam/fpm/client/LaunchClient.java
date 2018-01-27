package jaam.fpm.client;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LaunchClient {

	private static AppGameContainer appgc;

	public static void main(String[] args) {
		ClientNet clientNet = new ClientNet();


		try {
			appgc = new AppGameContainer(new PlayState("Simple Slick Game"));
			appgc.setDisplayMode(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(PlayState.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static final void exit() {
		appgc.exit();
	}
}
