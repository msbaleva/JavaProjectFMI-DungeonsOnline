package bg.sofia.uni.fmi.mjt.dungeon.server;

import bg.sofia.uni.fmi.mjt.dungeon.DungeonGenerator;
import bg.sofia.uni.fmi.mjt.dungeon.command.GameRepository;

public class GameServerMain {
	
	public static void main(String[] args) {
		GameRepository gameRepository = DungeonGenerator.generateGameRepository();
		GameServer gameServer = GameServer.createGameServer(gameRepository);
		gameServer.startGameServer();
	}
	

}
