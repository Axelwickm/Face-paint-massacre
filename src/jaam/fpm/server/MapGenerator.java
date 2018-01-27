package jaam.fpm.server;

import com.fisherevans.procedural_generation.dungeons.DungeonGenerator;
import com.fisherevans.procedural_generation.dungeons.DungeonRenderer;
import jaam.fpm.shared.Tile;

import java.awt.image.BufferedImage;

public class MapGenerator {
    public static Tile[][] generate(int size){


        DungeonGenerator.generate();

        BufferedImage img = DungeonRenderer.drawFinalDungeon();

        Tile[][] tiles = new Tile[img.getHeight()][img.getWidth()];
        for (int y = 0; y < img.getHeight(); y++){
            for (int x = 0; x < img.getWidth(); x++){
                if (img.getRGB(x, y) == DungeonRenderer.green.hashCode()){
                    tiles[y][x] = Tile.FLOOR;
                }
                else {
                    tiles[y][x] = Tile.WALL;
                }
            }
        }

        DungeonRenderer.view();

        return tiles;
    }
}
