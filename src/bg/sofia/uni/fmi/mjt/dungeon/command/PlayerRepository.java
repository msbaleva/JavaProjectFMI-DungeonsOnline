package bg.sofia.uni.fmi.mjt.dungeon.command;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeon.actor.ActorFactory;
import bg.sofia.uni.fmi.mjt.dungeon.actor.ActorType;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

public class PlayerRepository {

	private static final String WELCOME_MESSAGE = ", welcome to <DUNGEONS ONLINE>! Ready to spill some blood?";
	private static final String FULL_REPOSITORY_TRY_LATER_MESSAGE = "Server is full, try again later.";
	private static final String SAME_NAME_EXISTS_MESSAGE = "Username is taken. Try with another one.";
	private static final String ALREADY_REGISTERED_MESSAGE = "You are already registered.";
	private static final String USER_QUIT_MESSAGE = " has quit game.";
	private static final String USER_DEAD_MESSAGE = " RIP";
	private static final int ACTIVE_PLAYERS_CAPACITY = 9;

	private Map<SocketChannel, Hero> heroBySocketChannel;
	private Queue<String> freeUserIds;
	private int numberOfActivePlayers;

	protected PlayerRepository() {
		heroBySocketChannel = new HashMap<>();
		freeUserIds = new LinkedList<>();
		for (int i = 1; i <= ACTIVE_PLAYERS_CAPACITY; i++) {
			freeUserIds.offer(String.valueOf(i));
		}

	}

	public Hero getHeroByGivenSocketChannel(SocketChannel socketChannel) {
		return heroBySocketChannel.get(socketChannel);
	}

	public Map<SocketChannel, Hero> getHeroBySocketChannel() {
		return heroBySocketChannel;
	}

	public Collection<SocketChannel> getSocketChannels() {
		return heroBySocketChannel.keySet();
	}

	public boolean isUserRegistered(SocketChannel socketChannel) {
		return heroBySocketChannel.containsKey(socketChannel);
	}

	private boolean isUsernameTaken(String username) {
		Optional<Entry<SocketChannel, Hero>> entryUser = heroBySocketChannel.entrySet().stream()
				.filter(entry -> entry.getValue().getName().equals(username)).findFirst();

		return entryUser.isPresent();
	}

	private boolean isFull() {
		return numberOfActivePlayers == ACTIVE_PLAYERS_CAPACITY;
	}

	private String addToRepository(SocketChannel socketChannel, String username, GameRepository gameRepository) {
		String playerId = freeUserIds.poll();
		Position position = gameRepository.getFreePosition();
		Actor newHero = ActorFactory.getActor(ActorType.HERO, username, playerId, position);
		gameRepository.setContentAtPosition(position, " " + playerId + " ");
		heroBySocketChannel.put(socketChannel, (Hero) newHero);
		numberOfActivePlayers++;
		return newHero.getFormattedName() + WELCOME_MESSAGE;
	}

	public String registerUser(SocketChannel socketChannel, String username, GameRepository gameRepository) {
		if (isFull()) {
			return FULL_REPOSITORY_TRY_LATER_MESSAGE;
		}

		if (isUsernameTaken(username)) {
			return SAME_NAME_EXISTS_MESSAGE;
		}

		if (isUserRegistered(socketChannel)) {
			return ALREADY_REGISTERED_MESSAGE;
		}

		return addToRepository(socketChannel, username, gameRepository);
	}

	public String removeUser(SocketChannel socketChannel, GameRepository gameRepository) {
		Hero userToBeRemoved = heroBySocketChannel.get(socketChannel);
		return updateRepository(userToBeRemoved, gameRepository);

	}

	public String updateRepository(Hero userToBeRemoved, GameRepository gameRepository) {
		numberOfActivePlayers--;
		String idToBeFreed = userToBeRemoved.getId();
		freeUserIds.offer(idToBeFreed);
		Position positionToBeFreed = new Position(userToBeRemoved.getPosition());
		if (userToBeRemoved.isAlive()) {
			gameRepository.updateMovingHeroFromPosition(positionToBeFreed, idToBeFreed);
			return userToBeRemoved.getFormattedName() + USER_QUIT_MESSAGE;
		}

		Treasure treasure = userToBeRemoved.throwTreasureWhenDead();
		gameRepository.updateDeadHeroAtPosition(positionToBeFreed, idToBeFreed, treasure);
		return userToBeRemoved.getFormattedName() + USER_DEAD_MESSAGE;
	}

}
