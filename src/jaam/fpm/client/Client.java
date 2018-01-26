package jaam.fpm.client;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	public static void main(String[] args) {
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new PlayState("Simple Slick Game"));
			appgc.setDisplayMode(800, 600, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(PlayState.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
