package jaam.fpm.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import jaam.fpm.client.Drawing;
import jaam.fpm.packet.NewPlayerPacket;
import jaam.fpm.packet.PlayerActionPacket;
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
        kryo.register(float[].class);

        kryo.register(byte[].class);
        //kryo.register(Image.class);
        //kryo.register(Drawing.class); // I literally never send Drawings, but I still get exceptions if I don't register them. Because of course I do.

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
            if (((PlayerActionPacket) object).action == PlayerActionPacket.Action.READY){
                playState.playerReady(connection.getID());
                Image img;
                PlayerActionPacket packet = (PlayerActionPacket) object;

                int numNonZeroBytes = 0;
                for (int i = 0; i < packet.drawing.length; ++i) {
                    if (packet.drawing[i] != 0) numNonZeroBytes++;
                }
                System.out.println(numNonZeroBytes);

                try (FileImageOutputStream out = new javax.imageio.stream.FileImageOutputStream(Paths.get("out.bmp").toFile())) {
                    out.write(packet.drawing);
                } catch (IOException ex) {

                }
            }
            else if (((PlayerActionPacket) object).action == PlayerActionPacket.Action.START_WALKING){
                playState.startMovingPlayer(connection.getID(), new Vector2f(((PlayerActionPacket) object).velocity[0],
																			 ((PlayerActionPacket) object).velocity[1]));
            }
            else if (((PlayerActionPacket) object).action  == PlayerActionPacket.Action.STOP_WALKING){
                playState.stopMovingPlayer(connection.getID());
            }

            ((PlayerActionPacket) object).connection_id = connection.getID();
            server.sendToAllExceptTCP(connection.getID(), object);
        }
    }
}
