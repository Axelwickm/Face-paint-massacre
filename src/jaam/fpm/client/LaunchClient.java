package jaam.fpm.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import jaam.fpm.packet.TestPacket;
import jaam.fpm.server.ClientManager;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaunchClient {

	public static void main(String[] args) {
		ClientNet clientNet = new ClientNet();


		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new PlayState("Simple Slick Game"));
			appgc.setDisplayMode(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(PlayState.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
