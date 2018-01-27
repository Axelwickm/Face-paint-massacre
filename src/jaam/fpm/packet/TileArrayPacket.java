package jaam.fpm.packet;

import jaam.fpm.shared.Tile;

public class TileArrayPacket {
    public int tilesX, tilesY;
    public  Tile[][] tiles;

    private TileArrayPacket(){}

    public static TileArrayPacket make(int tilesX, int tilesY, Tile[][] tiles){
        TileArrayPacket p = new TileArrayPacket();
        p.tilesX = tiles[0].length;
        p.tilesY = tiles.length;
        return p;
    }
}
