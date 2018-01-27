package jaam.fpm.server;


public class LaunchServer {
	public static void main(String[] args) {
		ClientManager clientManager = new ClientManager();
		PlayState playState = new PlayState();
		playState.start();

	}
}
