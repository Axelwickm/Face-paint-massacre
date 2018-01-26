package jaam.fpm.packet;

public class TestPacket {
    public float VAL;

    private TestPacket() {
    }

    public static TestPacket make(float VAL){
        TestPacket p = new TestPacket();
        p.VAL = VAL;
        return p;
    }
}
