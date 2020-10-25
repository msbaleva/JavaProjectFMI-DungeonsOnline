package bg.sofia.uni.fmi.mjt.dungeon.command;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Position;

public class MoveCommand extends CommandImpl {

	private static final String INVALID_MOVE_WALL_MESSAGE = "Invalid move. You cannot go through a wall.";
	private static final String SUCCESSFULL_MOVE_MESSAGE = "You moved successfully to the next position.";
	private static final String INVALID_MOVE_OBSTACLE_MESSAGE = "Invalid move. There is an obstacle and you cannot bypass it.";
	private static final String POSITION_FULL_MESSAGE = "The position is full, try another one.";

	private static final int MOVE_COMMAND_LENGTH = 2;

	private GameRepository gameRepository;
	private int heroRow;
	private int heroCol;
	private int horizontalMove;
	private int verticalMove;

	MoveCommand(Hero hero, String[] splitCommand, GameRepository gameRepository) {
		super(hero, splitCommand);
		this.gameRepository = gameRepository;
		this.heroRow = hero.getPosition().getRow();
		this.heroCol = hero.getPosition().getCol();
	}

	private void moveHero() {
		gameRepository.updateMovingHeroFromPosition(new Position(hero.getPosition()), hero.getId());
		hero.setPosition(heroRow + verticalMove, heroCol + horizontalMove);
	}

	private boolean isNotValidMove() {
		return heroRow + verticalMove < 0 || heroCol + horizontalMove < 0
				|| heroRow + verticalMove >= gameRepository.getDungeonMapRows()
				|| heroCol + horizontalMove >= gameRepository.getDungeonMapCols();
	}

	private boolean isNotValidCommand() {
		if (splitCommand.length != MOVE_COMMAND_LENGTH) {
			return true;
		}

		CommandType commandAsEnum = CommandType.getCommandAsEnum(splitCommand[FIRST_WORD].trim());
		if (commandAsEnum == null) {
			return true;
		}

		switch (commandAsEnum) {
		case LEFT:
			horizontalMove = -1;
			break;
		case UP:
			verticalMove = -1;
			break;
		case RIGHT:
			horizontalMove = 1;
			break;
		case DOWN:
			verticalMove = 1;
			break;
		default:
			return true;
		}

		return false;
	}

	private String executeMove(UserRecipient userRecipient) {
		int rowAfterVerticalMove = heroRow + verticalMove;
		int colAfterHorizontalMove = heroCol + horizontalMove;
		Position newPosition = Position.createNewPosition(rowAfterVerticalMove, colAfterHorizontalMove);
		String positionContent = gameRepository.getContentAtPosition(newPosition);
		String heroId = hero.getId();
		switch (positionContent) {
		case GameRepository.TWO_FREE_SPACES:
			moveHero();
			gameRepository.setContentAtPosition(newPosition,
					GameRepository.FREE_SYMBOL + heroId + GameRepository.FREE_SYMBOL);
			gameRepository.removeFreePosition(Position.createNewPosition(rowAfterVerticalMove, colAfterHorizontalMove));
			break;
		case GameRepository.NO_FREE_SPACES:
			return INVALID_MOVE_OBSTACLE_MESSAGE;
		case GameRepository.TREASURE_ONE_SPACE:
			moveHero();
			gameRepository.setContentAtPosition(newPosition, GameRepository.TREASURE_NO_SPACES + heroId);
			break;
		case GameRepository.MINION_ONE_SPACE:
			moveHero();
			gameRepository.setContentAtPosition(newPosition, GameRepository.MINION_NO_SPACES + heroId);
			break;
		default:
			if (!positionContent.endsWith(GameRepository.FREE_SYMBOL)) {
				return POSITION_FULL_MESSAGE;
			}

			moveHero();
			gameRepository.setContentAtPosition(newPosition,
					positionContent.charAt(FIRST_WORD) + GameRepository.DELIMINATOR_SYMBOL + heroId);
		}

		return SUCCESSFULL_MOVE_MESSAGE;
	}

	@Override
	public String execute(UserRecipient userRecipient) {

		if (isNotValidCommand()) {
			return UNSUPPORTED_OPERATION_MESSAGE;
		}

		if (isNotValidMove()) {
			return INVALID_MOVE_WALL_MESSAGE;
		}

		return executeMove(userRecipient);

	}

}
