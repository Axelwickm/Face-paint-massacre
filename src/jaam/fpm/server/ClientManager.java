package jaam.fpm.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import jaam.fpm.client.Drawing;
import jaam.fpm.packet.NewPlayerPacket;
import jaam.fpm.packet.PlayerActionPacket;
import jaam.fpm.packet.SetFacePacket;
import jaam.fpm.packet.TileArrayPacket;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import javax.imageio.stream.FileImageOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;

import static jaam.fpm.packet.PlayerActionPacket.Action.START_WALKING;

public class ClientManager extends Listener {
    private final Server server;
    private final PlayState playState;

    public ClientManager(PlayState playState) {
        this.playState = playState;

        this.server = new Server(307_200, 307_200);
        Kryo kryo = this.server.getKryo();
        kryo.register(NewPlayerPacket.class);
        kryo.register(PlayerActionPacket.class);
        kryo.register(jaam.fpm.packet.PlayerActionPacket.Action.class);
        kryo.register(TileArrayPacket.class);
        kryo.register(Tile.class);
        kryo.register(Tile[].class);
        kryo.register(Tile[][].class);

        kryo.register(byte[].class);

        kryo.register(org.newdawn.slick.geom.Vector2f.class);

        new Thread(this.server).start();

        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(this);
    }


    @Override
    public void connected(Connection connection) {
        for (Player  p : playState.players.values()){
            connection.sendTCP(NewPlayerPacket.make(p.connection_id));
            server.sendToTCP(p.connection_id, NewPlayerPacket.make(connection.getID()));
        }

        Player player = new Player(server, connection.getID(), new Vector2f(0,0));
        playState.addPlayer(player);
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected");
    }

    @Override
    public void received(Connection connection, Object object){
        if (object instanceof PlayerActionPacket){
            PlayerActionPacket packet = (PlayerActionPacket) object;
            switch (((PlayerActionPacket) object).action) {
                case READY:
                    playState.playerReady(connection.getID());
                    Image img;

                    int numNonZeroBytes = 0;
                    for (int i = 0; i < packet.drawing.length; ++i) {
                        if (packet.drawing[i] != 0) numNonZeroBytes++;
                    }
                    System.out.println(numNonZeroBytes);

                    try (FileImageOutputStream out = new javax.imageio.stream.FileImageOutputStream(Paths.get("out.bmp").toFile())) {
                        out.write(packet.drawing);
                    } catch (IOException ex) {
                        // TODO: Actually, do we even want to do this?
                    }

                    break;
                case START_WALKING:
                    playState.startMovingPlayer(connection.getID(), ((PlayerActionPacket) object).velocity);
                    break;
                case STOP_WALKING:
                    playState.stopMovingPlayer(connection.getID());
                    break;
                case POST_NOTE:
                    playState.placeNote(playState.players.get(connection.getID()).getPosition(), packet.drawing);
                    break;
                default:
                    throw new UnsupportedOperationException("Can't handle PlayerActionPacket with action: " + packet.action.name());
            }

            packet.connection_id = connection.getID();
            server.sendToAllExceptTCP(connection.getID(), object);
        }
    }
}
