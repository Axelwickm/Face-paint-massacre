package jaam.fpm.server;

import jaam.fpm.shared.Tile;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;

import static java.lang.Math.abs;

public class PlayState {
    final int TARGET_FPS = 60;
    final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

    public boolean running;
    public int ticks;

    public Tile[][] world;
    public HashMap<Integer, Player> players;

    public PlayState() {
        running = false;
        ticks = 0;

        world = MapGenerator.generate(100,100);
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
        for (Player  p : players.values()){
            p.update(delta);
        }
        return true;
    }

    public void addPlayer(Player player){
        players.put(player.connection_id, player);
    }

    public void startMovingPlayer(int player_id, Vector2f velocity){
        players.get(player_id).setVelocity(velocity);
    }

    public void stopMovingPlayer(int player_id){
        players.get(player_id).setVelocity(new Vector2f(0,0));
    }
}
