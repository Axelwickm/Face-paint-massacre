package jaam.fpm.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import jaam.fpm.packet.*;
import jaam.fpm.shared.Settings;
import jaam.fpm.shared.State;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientNet {

    public Client client;

    public volatile World world;

    public ClientNet() {
        this.client = new Client(307_200, 307_200);
        Kryo kryo = client.getKryo();
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
                    world.playerCount++;
                }
                else if (object instanceof PlayerActionPacket){ // Action from other player
                    PlayerActionPacket p = (PlayerActionPacket) object;
                    switch (p.action) {
                        case READY:
                            world.readyCount++;
                            System.out.println(world.readyCount+" / "+world.playerCount+" players ready.");
                            //world.getPlayer(p.connection_id)/*.setFace(packet.drawing) /* TODO: Store player faces locally too */;

                            world.getPlayer(p.connection_id).decodeImage(p.drawing);
                            break;
                        case DIE:
                            world.getOthers().get(p.connection_id).kill();
                            break;
                        case START_WALKING:
							world.getOthers().get(p.connection_id).setDir(new Vector2f(p.velocity[0], p.velocity[1]));
                            break;
                        case STOP_WALKING:
							world.getOthers().get(p.connection_id).setDir(new Vector2f());
							world.getOthers().get(p.connection_id).setPosition(new Vector2f(p.stopPosition[0], p.stopPosition[1]));
                            break;
                        case POST_NOTE:
                            //world.addNote(new Vector2f(p.notePosition[0], p.notePosition[1]));
                            break;
						case USE_WEAPON:
							world.getOthers().get(p.connection_id).getWeapon().use();
							break;
						case TOGGLE_WEAPON:
							world.getOthers().get(p.connection_id).getWeapon().toggle();
							break;
                        default:
                            throw new UnsupportedOperationException("Can't handle PlayerActionPacket with action " + p.action.name());
                    }

                }
                else if (object instanceof GameStatusChangePacket){
                    GameStatusChangePacket p = (GameStatusChangePacket) object;
                    switch (p.statusChange){
                        case GAME_OVER:
                            world.prompt(p.murderWin ? "THE FACEPAINT MASSARE IS COMPLETE" : "THE MURDERER HAS BEEN THWARTED",
                                         p.murderWin ? Color.red : Color.green);
                            break;
                        case RESTART_GAME:
                            LaunchClient.getPlayState().shouldRestart = true;
                            break;
                        case MURDERER_CHOOSEN:
                        	world.setMurdererChosen(true);
                            world.getMe().setState(p.IAmTheMurderer ? State.MURDERER : State.AlIVE);
                            world.prompt("I am "+(p.IAmTheMurderer ? "" : "not ")+"the murderer.",
                                         p.IAmTheMurderer ? Color.red : Color.white);
                            if (p.IAmTheMurderer)
                            	Audio.setMusicPitch();
                            break;
                    }
                }
            }
        });

        try {
            client.connect(5000, "127.0.0.1", 54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
