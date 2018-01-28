package jaam.fpm.server;

import com.fisherevans.procedural_generation.dungeons.DungeonGenerator;
import com.fisherevans.procedural_generation.dungeons.DungeonRenderer;
import com.fisherevans.procedural_generation.dungeons.Room;
import jaam.fpm.shared.Tile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class MapGenerator {
    public static final int ROOMS_PER_PLAYER = 3;
    public static final int CORRIDORS_PER_PLAYER = 3;
    public static final float UNITS_PER_PLAYER = 10;

    public static ArrayList<Float[]> positions;

    public static Tile[][] generate(int players){
        DungeonGenerator.roomCount = players*ROOMS_PER_PLAYER;
        DungeonGenerator.corridorCount = players*CORRIDORS_PER_PLAYER;

        DungeonGenerator.mapSize = (int) Math.sqrt(players*UNITS_PER_PLAYER);

        DungeonGenerator.generate();
        DungeonRenderer.view();

        BufferedImage img = DungeonRenderer.drawFinalDungeon();
        ArrayList<Float[]> nicePositions = new ArrayList<>();

        Tile[][] tiles = new Tile[img.getHeight()][img.getWidth()];
        for (int y = 0; y < img.getHeight(); y++){
            for (int x = 0; x < img.getWidth(); x++){
                if (img.getRGB(x, y) == -16711936){
                    tiles[y][x] = Tile.FLOOR;
                    Float[] p = {x+.5f, y+.5f};
                    nicePositions.add(p);
                }
                else {
                    tiles[y][x] = Tile.WALL;
                }
            }
        }

        positions = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i<players; i++){
            MapGenerator.positions.add(nicePositions.get(rand.nextInt(nicePositions.size())));
        }

        return tiles;
    }

    public static float[][] getFreePosition(){
        float[][] pos = new float[positions.size()][2];
        for (int i = 0; i < positions.size(); i++){
            for (int j = 0; j<2; j++){
                pos[i][j] = positions.get(i)[j];
            }
        }
        System.out.println(pos);
        return pos;
    }
}
