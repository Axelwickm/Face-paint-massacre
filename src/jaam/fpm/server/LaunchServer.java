package jaam.fpm.server;


public class LaunchServer {
	public static void main(String[] args) {
		PlayState playState = new PlayState();
        ClientManager clientManager = new ClientManager(playState);
		playState.start();

	}
}
