package jaam.fpm.server;

import com.fisherevans.procedural_generation.dungeons.DungeonGenerator;
import com.fisherevans.procedural_generation.dungeons.DungeonRenderer;
import jaam.fpm.shared.Tile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MapGenerator {
    public static ArrayList<Float[]> positions;

    public static Tile[][] generate(){
        int players = 1;
        positions = new ArrayList<>();


        DungeonGenerator.generate();
        DungeonRenderer.view();

        BufferedImage img = DungeonRenderer.drawFinalDungeon();
        System.out.println(DungeonRenderer.green.getRGB());

        Tile[][] tiles = new Tile[img.getHeight()][img.getWidth()];
        for (int y = 0; y < img.getHeight(); y++){
            for (int x = 0; x < img.getWidth(); x++){
                System.out.println(img.getRGB(x, y));
                if (img.getRGB(x, y) == -16711936){
                    tiles[y][x] = Tile.FLOOR;
                    if (players != 0){
                        players--;
                        Float[] position = {(float) x+.5f, (float) y+.5f};
                        MapGenerator.positions.add(position);
                    }
                }
                else {
                    tiles[y][x] = Tile.WALL;
                }
            }
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
