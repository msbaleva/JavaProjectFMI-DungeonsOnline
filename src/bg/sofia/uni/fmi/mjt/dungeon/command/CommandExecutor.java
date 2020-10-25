package bg.sofia.uni.fmi.mjt.dungeon.command;

import java.nio.channels.SocketChannel;
import java.util.Collection;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class CommandExecutor {

	private static final int ZERO_WORD = 0;
	private static final int SECOND_WORD = 2;
	private static final int PLAY_COMMAND_LENGTH = 3;
	private static final String YOU_ARE_NOT_REGISTERED_MESSAGE = "You are not registered yet.";
	private static final String COMMAND_DELIMINATOR = " ";

	private PlayerRepository playerRepository;
	private GameRepository gameRepository;

	public CommandExecutor(GameRepository gameRepository) {
		playerRepository = new PlayerRepository();
		this.gameRepository = gameRepository;
	}

	private Command determineCommand(String[] splitCommand, CommandType firstCommandAsEnum,
			SocketChannel socketChannel) {
		Hero hero = playerRepository.getHeroByGivenSocketChannel(socketChannel);
		switch (firstCommandAsEnum) {
		case MOVE:
			return new MoveCommand(hero, splitCommand, gameRepository);
		case BACKPACK:
			return new BackpackCommand(hero, splitCommand, gameRepository);
		case GIVE:
			return new GiveCommand(hero, splitCommand, playerRepository.getHeroBySocketChannel());
		case COLLECT:
			return new CollectCommand(hero, splitCommand, gameRepository);
		case BATTLE:
			return new BattleCommand(hero, splitCommand, playerRepository, gameRepository);
		default:
			return null;
		}
	}

	public String checkCommandForRepositoryUpdate(String[] splitCommand, CommandType firstCommandAsEnum,
			SocketChannel socketChannel) {
		if (firstCommandAsEnum.equals(CommandType.PLAY) && splitCommand.length == PLAY_COMMAND_LENGTH) {
			return playerRepository.registerUser(socketChannel, splitCommand[SECOND_WORD], gameRepository);
		}

		if (!playerRepository.isUserRegistered(socketChannel)) {
			return YOU_ARE_NOT_REGISTERED_MESSAGE;
		}

		if (firstCommandAsEnum.equals(CommandType.QUIT)) {
			return playerRepository.removeUser(socketChannel, gameRepository);
		}

		return null;
	}

	public String checkCommandResultForRepositoryUpdate(String commandResult, SocketChannel socketChannel,
			UserRecipient userRecipient) {
		if (commandResult.equals(BattleCommand.HERO_LOST_MESSAGE)) {
			return commandResult + playerRepository.removeUser(socketChannel, gameRepository);
		}

		if (userRecipient.getMessage() != null
				&& userRecipient.getMessage().endsWith(BattleCommand.KILLED_YOU_MESSAGE)) {
			userRecipient.updateMessage(playerRepository.removeUser(userRecipient.getSocketChannel(), gameRepository));
		}

		return commandResult;
	}

	public String executeCommand(String command, SocketChannel socketChannel, UserRecipient userRecipient) {
		String[] splitCommand = command.trim().split(COMMAND_DELIMINATOR);
		CommandType firstCommandAsEnum = CommandType.getCommandAsEnum(splitCommand[ZERO_WORD]);
		if (firstCommandAsEnum == null) {
			return Command.UNSUPPORTED_OPERATION_MESSAGE;
		}

		String updateResult = checkCommandForRepositoryUpdate(splitCommand, firstCommandAsEnum, socketChannel);
		if (updateResult != null) {
			return updateResult;
		}

		Command specificCommand;
		specificCommand = determineCommand(splitCommand, firstCommandAsEnum, socketChannel);
		String commandResult = (specificCommand != null) ? specificCommand.execute(userRecipient)
				: Command.UNSUPPORTED_OPERATION_MESSAGE;
		return checkCommandResultForRepositoryUpdate(commandResult, socketChannel, userRecipient);

	}

	public Collection<SocketChannel> getSocketChannelsFromRepository() {
		return playerRepository.getSocketChannels();
	}

	public String getDungeonMapFromRepository() {
		return gameRepository.printDungeonMap();
	}

}
