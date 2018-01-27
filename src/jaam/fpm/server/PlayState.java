package jaam.fpm.server;

import jaam.fpm.packet.TileArrayPacket;
import jaam.fpm.shared.State;
import jaam.fpm.shared.Tile;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Math.abs;

public class PlayState {
    final int TARGET_FPS = 60;
    final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

    public boolean running;
    public int ticks;

    public boolean drawingMode;
    public Tile[][] world;
    public HashMap<Integer, Player> players;
    public int playerCount;
    public int aliveCount;

    private HashMap<Vector2f, byte[]> notes = new HashMap<>();

    public PlayState() {
        resetGame();
    }

    public void resetGame(){
        running = false;
        ticks = 0;
        playerCount = 0;
        aliveCount = 0;

        drawingMode = true;
        players = new HashMap<>();
    }

    public void start(){
        running = true;
        long lastTime = System.nanoTime();
        double delta = 0;


        while (running){
            long now = System.nanoTime();
            delta = (double) (now-lastTime)/ (OPTIMAL_TIME);
            lastTime = now;

            this.running = update(delta);

            this.ticks++;

            try{Thread.sleep( abs((lastTime-System.nanoTime() + OPTIMAL_TIME)/1000000 ));} catch (InterruptedException e){}
        }
    }

    private boolean update(double delta){
        if (!drawingMode){
            this.aliveCount = 0;
            for (Player  p : players.values()){
                p.update(delta);
                this.aliveCount += p.state == State.AlIVE ? 1 : 0;
            }
            if (aliveCount == 0){
                //resetGame();
            }
        }
        else {
            boolean allReady = true;
            this.playerCount = 0;
            for (Player  p : players.values()){
                if (!p.ready) allReady = false;
                this.playerCount++;
            }
            if (allReady && this.playerCount > 0){
                startGame();
            }
        }

        return true;
    }

    public void startGame(){
        System.out.println("All players ready, starting game.");

        this.drawingMode = false;
        Tile[][] world = MapGenerator.generate(playerCount);
        float[][] positions = MapGenerator.getFreePosition();

        TileArrayPacket tileArrayPacket = TileArrayPacket.make(world, positions);
        for (Player  p : players.values()){
            p.sendWorld(tileArrayPacket);
        }

    }

    public void addPlayer(Player player){
        players.put(player.connection_id, player);
    }

    public void playerReady(int player_id){
        players.get(player_id).ready = true;
    }

    public void startMovingPlayer(int player_id, Vector2f velocity){
        players.get(player_id).setVelocity(velocity);
    }

    public void stopMovingPlayer(int player_id){
        players.get(player_id).setVelocity(new Vector2f(0,0));
    }

    public void placeNote(Vector2f location, byte[] image) { notes.put(location, image); }
}
