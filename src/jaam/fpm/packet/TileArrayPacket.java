package jaam.fpm.packet;

import jaam.fpm.shared.Tile;
import org.newdawn.slick.geom.Vector2f;

public class TileArrayPacket {
    public int tilesX, tilesY;
    public  Tile[][] tiles;
    public float[][] playerPositions;

    private TileArrayPacket(){}

    public static TileArrayPacket make(Tile[][] tiles, float[][] playerPositions){
        TileArrayPacket p = new TileArrayPacket();
        p.tilesX = tiles[0].length;
        p.tilesY = tiles.length;
        p.tiles = tiles;
        p.playerPositions = playerPositions;
        return p;
    }
}
