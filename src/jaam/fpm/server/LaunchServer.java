package jaam.fpm.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import jaam.fpm.packet.TestPacket;

import java.io.IOException;

public class LaunchServer {
	public static void main(String[] args) {
		Server server = new Server();
		Kryo kryo = server.getKryo();
		kryo.register(TestPacket.class);

		server.start();

		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}

		server.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				super.connected(connection);
				System.out.println(connection.getID());
			}

			@Override
			public void disconnected(Connection connection) {
				super.disconnected(connection);
				System.out.println("Disconnected");
			}

			@Override
			public void received(Connection connection, Object object){
				System.out.println("Got packet");
				if (object instanceof TestPacket){
					System.out.println("TESTVALUE: "+((TestPacket) object).VAL);
				}
			}
		});

		while (true){

		}

	}
}
