package jaam.fpm.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import jaam.fpm.packet.NewPlayerPacket;
import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.packet.TileArrayPacket;
import jaam.fpm.shared.Tile;

import java.io.IOException;

public class ClientNet {

    public Client client;

    public volatile World world;

    public ClientNet() {
        this.client = new Client(20000, 20000);
        Kryo kryo = client.getKryo();
        kryo.register(NewPlayerPacket.class);
        kryo.register(PlayerActionPacket.class);
        kryo.register(jaam.fpm.packet.PlayerActionPacket.Action.class);
        kryo.register(TileArrayPacket.class);
        kryo.register(Tile.class);
        kryo.register(Tile[].class);
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
                System.out.println("Disconnected");
            }

            @Override
            public void received(Connection connection, Object object) {
				while (world == null) {}
                super.received(connection, object);
                if (object instanceof  TileArrayPacket){
                    System.out.println("Received world");
					world.setPacket((TileArrayPacket) object);
                }
                else if (object instanceof NewPlayerPacket){ // Other player added
                    System.out.println("Connection id: "+((NewPlayerPacket) object).connection_id);
                    world.addPlayer(((NewPlayerPacket) object).connection_id, new Player(world, false));
                }
                else if (object instanceof PlayerActionPacket){ // Action from other player

                }
            }
        });

        try {
            client.connect(5000, "127.0.0.1", 54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.sendTCP(PlayerActionPacket.make(PlayerActionPacket.Action.READY));
    }
}
