package jaam.fpm.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import jaam.fpm.packet.*;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import javax.imageio.stream.FileImageOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

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
        kryo.register(GameStatusChangePacket.class);
        kryo.register(GameStatusChangePacket.StatusChange.class);
        kryo.register(TileArrayPacket.class);
        kryo.register(Tile.class);
        kryo.register(Tile[].class);
        kryo.register(Tile[][].class);
        kryo.register(float[].class);

        kryo.register(org.newdawn.slick.geom.Vector2f.class);

        kryo.register(byte[].class);
        kryo.register(float[].class);
        kryo.register(float[][].class);
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
                    playState.startMovingPlayer(connection.getID(), new Vector2f(((PlayerActionPacket) object).velocity[0],
																				 ((PlayerActionPacket) object).velocity[1]));
                    break;
                case STOP_WALKING:
                    playState.stopMovingPlayer(connection.getID());
                    break;
                case POST_NOTE:
                    playState.placeNote(playState.players.get(connection.getID()).getPosition(), packet.drawing);
                    break;
				case USE_WEAPON:
				case TOGGLE_WEAPON:
					//TODO: NOTHING!
					break;
                default:
                    throw new UnsupportedOperationException("Can't handle PlayerActionPacket with action: " + packet.action.name());
            }

            packet.connection_id = connection.getID();
            server.sendToAllExceptTCP(connection.getID(), object);
        }
    }
}
