package jaam.fpm.packet;

public class NotePacket {
    private NotePacket(){}

    public static NotePacket make(){
        NotePacket p = new NotePacket();
        return p;
    }
}
