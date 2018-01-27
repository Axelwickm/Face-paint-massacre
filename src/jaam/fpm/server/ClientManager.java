package jaam.fpm.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import jaam.fpm.packet.NewPlayerPacket;
import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.packet.TileArrayPacket;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;

public class ClientManager extends Listener {
    private final Server server;
    private final PlayState playState;

    public ClientManager(PlayState playState) {
        this.playState = playState;

        this.server = new Server();
        Kryo kryo = this.server.getKryo();
        kryo.register(NewPlayerPacket.class);
        kryo.register(PlayerActionPacket.class);
        kryo.register(jaam.fpm.packet.PlayerActionPacket.Action.class);
        kryo.register(TileArrayPacket.class);
        kryo.register(Tile[][].class);

        kryo.register(org.newdawn.slick.geom.Vector2f.class);

        new Thread(this.server).start();

        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(this);
    }

    public void sendWorld(){
        TileArrayPacket p = TileArrayPacket.make(playState.world);
        server.sendToAllTCP(p);
    }

    @Override
    public void connected(Connection connection) {
        Player player = new Player(connection.getID(), new Vector2f(0,0));
        playState.addPlayer(player);
        sendWorld();
        server.sendToAllTCP(NewPlayerPacket.make(connection.getID()));
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected");
    }

    @Override
    public void received(Connection connection, Object object){
        if (object instanceof PlayerActionPacket){
            if (((PlayerActionPacket) object).action == PlayerActionPacket.Action.START_WALKING){
                playState.startMovingPlayer(connection.getID(), ((PlayerActionPacket) object).velocity);
            }
            else if (((PlayerActionPacket) object).action  == PlayerActionPacket.Action.STOP_WALKING){
                playState.stopMovingPlayer(connection.getID());
            }
        }
    }
}
