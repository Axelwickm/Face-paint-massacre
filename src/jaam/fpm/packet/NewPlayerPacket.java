package jaam.fpm.packet;

public class NewPlayerPacket {
    public int connection_id;

    private NewPlayerPacket(){}

    public static NewPlayerPacket make(int connection_id){
        NewPlayerPacket p = new NewPlayerPacket();
        p.connection_id = connection_id;
        return p;
    }
}
