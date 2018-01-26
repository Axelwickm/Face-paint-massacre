package jaam.fpm.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import jaam.fpm.packet.TestPacket;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaunchClient {
	public static void main(String[] args) {
		Client client = new Client();
		Kryo kryo = client.getKryo();
		kryo.register(TestPacket.class);
		client.start();

		client.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				super.connected(connection);
				System.out.println("Connected");
			}

			@Override
			public void disconnected(Connection connection) {
				super.disconnected(connection);
				System.out.printf("Disconnected");
			}
		});

		try {
			client.connect(5000, "127.0.0.1", 54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}

		client.sendTCP(TestPacket.make(0.5f));

		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new PlayState("Simple Slick Game"));
			appgc.setDisplayMode(640, 480, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(PlayState.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
