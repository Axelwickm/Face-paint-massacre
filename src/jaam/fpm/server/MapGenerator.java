package jaam.fpm.server;

import jaam.fpm.shared.Tile;

public class MapGenerator {
    public static Tile[][] generate(int tilesX, int tilesY){

        Tile[][] tiles = new Tile[tilesY][tilesX];
        for (int y = 0; y < tilesY; y++){
            for (int x = 0; x < tilesY; x++){
                tiles[y][x] = Tile.FLOOR;
            }
        }

        tiles[0][0] = Tile.WALL;
        tiles[0][2] = Tile.WALL;
        tiles[0][4] = Tile.WALL;
        tiles[1][0] = Tile.WALL;
        tiles[2][0] = Tile.WALL;

        return tiles;
    }
}
