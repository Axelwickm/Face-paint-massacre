package jaam.fpm.client;

import jaam.fpm.shared.Settings;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LaunchClient {

	private static AppGameContainer appgc;
	private static PlayState playState;
	private static ClientNet clientNet;

	public static void main(String[] args) {
		clientNet = new ClientNet();
		try
		{
			playState = new PlayState("Face paint massacre", clientNet.client);
			appgc = new AppGameContainer(playState);
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

	public static final ClientNet getClientNet() {
		return clientNet;
	}

	public static AppGameContainer getAppgc() {
		return appgc;
	}

	public static PlayState getPlayState() {
		return playState;
	}
}
