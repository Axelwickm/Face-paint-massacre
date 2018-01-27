package jaam.fpm.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import jaam.fpm.packet.PlayerActionPacket;
import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;

public class ClientManager extends Listener {
    private final PlayState playState;

    public ClientManager(PlayState playState) {
        this.playState = playState;

        Server server = new Server();
        Kryo kryo = server.getKryo();
        kryo.register(PlayerActionPacket.class);

        new Thread(server).start();

        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(this);
    }

    @Override
    public void connected(Connection connection) {
        Player player = new Player(connection.getID(), new Vector2f(0,0));
        playState.addPlayer(player);
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected");
    }

    @Override
    public void received(Connection connection, Object object){
        if (object instanceof PlayerActionPacket){

        }
    }
}
