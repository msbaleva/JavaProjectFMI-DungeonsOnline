package bg.sofia.uni.fmi.mjt.dungeon.client;

public class GameClientMain {

	public static void main(String[] args) {
		GameClient gameClient = GameClient.createGameClient();
		gameClient.startGameClient(System.in, System.out);
	}

}
