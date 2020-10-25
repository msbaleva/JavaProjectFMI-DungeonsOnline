package bg.sofia.uni.fmi.mjt.dungeon.command;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Position;

public class BackpackCommand extends CommandImpl {


	private static final String THROW_AT_FULL_POSITION_MESSAGE = "You can't throw treasure here.";
	private static final int BACKPACK_CHECK_COMMAND_LENGTH = 2;
	private static final int BACKPACK_COMMAND_LENGTH = 3;

	private GameRepository gameRepository;

	BackpackCommand(Hero hero, String[] splitCommand, GameRepository gameRepository) {
		super(hero, splitCommand);
		this.gameRepository = gameRepository;
	}

	private String checkBackpackContents() {
		return hero.getBackpackContents();
	}

	private String useTreasureFromBackpack() {
		if (splitCommand.length != BACKPACK_COMMAND_LENGTH) {
			return UNSUPPORTED_OPERATION_MESSAGE;
		}

		return hero.useTreasure(splitCommand[SECOND_WORD].trim());
	}

	private String throwTreasureFromBackpack() {
		if (splitCommand.length != BACKPACK_COMMAND_LENGTH) {
			return UNSUPPORTED_OPERATION_MESSAGE;
		}
		
		if (!gameRepository.getContentAtPosition(hero.getPosition()).endsWith(GameRepository.FREE_SYMBOL)) {			
			return THROW_AT_FULL_POSITION_MESSAGE;
		}
		
		String thrownTreasureMessage = hero.throwTreasure(splitCommand[SECOND_WORD].trim());
		gameRepository.updateThrownTreasure(new Position(hero.getPosition()), hero.getId(),
				hero.getTreasure(splitCommand[SECOND_WORD].trim()));
		return thrownTreasureMessage;
	}

	@Override
	public String execute(UserRecipient userRecipient) {
		if (splitCommand.length >= BACKPACK_CHECK_COMMAND_LENGTH) {
			CommandType commandAsEnum = CommandType.getCommandAsEnum(splitCommand[FIRST_WORD].trim());
			if (commandAsEnum != null) {
				switch (commandAsEnum) {
				case CHECK:
					return checkBackpackContents();
				case USE:
					return useTreasureFromBackpack();
				case THROW:
					return throwTreasureFromBackpack();
				default:
					return UNSUPPORTED_OPERATION_MESSAGE;
				}
			}
		}

		return UNSUPPORTED_OPERATION_MESSAGE;

	}

}
