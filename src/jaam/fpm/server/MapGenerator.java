package jaam.fpm.server;

import com.fisherevans.procedural_generation.dungeons.DungeonGenerator;
import com.fisherevans.procedural_generation.dungeons.DungeonRenderer;
import com.fisherevans.procedural_generation.dungeons.Room;
import jaam.fpm.shared.Tile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class MapGenerator {
    public static ArrayList<Float[]> positions;

    public static Tile[][] generate(int players){
        DungeonGenerator.generate();
        DungeonRenderer.view();

        BufferedImage img = DungeonRenderer.drawFinalDungeon();

        Tile[][] tiles = new Tile[img.getHeight()][img.getWidth()];
        for (int y = 0; y < img.getHeight(); y++){
            for (int x = 0; x < img.getWidth(); x++){
                if (img.getRGB(x, y) == -16711936){
                    tiles[y][x] = Tile.FLOOR;
                }
                else {
                    tiles[y][x] = Tile.WALL;
                }
            }
        }

        positions = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i<players; i++){
            Room randomRoom = DungeonGenerator.rooms.get(rand.nextInt(DungeonGenerator.rooms.size()));
            Float[] position = new Float[2]; /*{
                    (float) randomRoom.getCenterX()+img.getWidth()/2.f+0.5f,
                    (float) randomRoom.getCenterY()+img.getWidth()/2.f+0.5f
            };*/
            position[0] = 20.f;
            position[1] = 20.f;
            MapGenerator.positions.add(position);
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
