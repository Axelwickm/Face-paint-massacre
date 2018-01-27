package jaam.fpm.packet;

import jaam.fpm.shared.Tile;

public class TileArrayPacket {
    public int tilesX, tilesY;
    public  Tile[][] tiles;

    private TileArrayPacket(){}

    public static TileArrayPacket make(Tile[][] tiles){
        TileArrayPacket p = new TileArrayPacket();
        p.tilesX = tiles.length;
        p.tilesY = tiles[0].length;
        return p;
    }
}
