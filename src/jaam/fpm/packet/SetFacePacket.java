package jaam.fpm.packet;

public class SetFacePacket {
	public byte[] face;

	private SetFacePacket() { };
	public static SetFacePacket make(byte[] face) {
		SetFacePacket packet = new SetFacePacket();
		packet.face = face;
		return packet;
	}
}
