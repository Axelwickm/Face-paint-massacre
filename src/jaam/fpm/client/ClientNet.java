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
        this.client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(NewPlayerPacket.class);
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
                System.out.println("Disconnected");
            }

            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                if (object instanceof  TileArrayPacket){
                    System.out.println("Received world");
					TileArrayPacket p = (TileArrayPacket) object;
					while (world == null) {}
					world.createChunks(p.tilesX, p.tilesY, p.tiles);
                }
                else if (object instanceof NewPlayerPacket){
                    System.out.println("Client id: "+((NewPlayerPacket) object).connection_id);
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
