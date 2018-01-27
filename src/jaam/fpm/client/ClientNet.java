package jaam.fpm.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.packet.TileArrayPacket;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;

public class ClientNet {
    public ClientNet() {
        Client client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(PlayerActionPacket.class);
        kryo.register(jaam.fpm.packet.PlayerActionPacket.Action.class);
        kryo.register(TileArrayPacket.class);
        kryo.register(Tile[][].class);

        kryo.register(org.newdawn.slick.geom.Vector2f.class);

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

            @Override
            public void received(Connection connection, Object o) {
                super.received(connection, o);
                if (o instanceof  TileArrayPacket){
                    System.out.printf("Recived world");
                }
            }
        });

        try {
            client.connect(5000, "127.0.0.1", 54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.sendTCP(PlayerActionPacket.make(PlayerActionPacket.Action.START_WALKING, new Vector2f(5,5)));
    }
}
