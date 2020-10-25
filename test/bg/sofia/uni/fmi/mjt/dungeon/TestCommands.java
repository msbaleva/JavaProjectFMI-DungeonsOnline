package bg.sofia.uni.fmi.mjt.dungeon;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeon.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.dungeon.command.GameRepository;
import bg.sofia.uni.fmi.mjt.dungeon.command.UserRecipient;

public class TestCommands {

	private static final String PROBLEM_OPENING_SOCKET_MESSAGE = "Problem opening socket channels.";

	private static final String PLAY_AS_HERO_1_COMMAND = "play as hero1";
	private static final String MOVE_LEFT_COMMAND = "move left";
	private static final String MOVE_RIGHT_COMMAND = "move right";
	private static final String MOVE_UP_COMMAND = "move up";
	private static final String MOVE_DOWN_COMMAND = "move down";
	private static final String COLLECT_COMMAND = "collect";
	private static final String BACKPACK_COMMAND = "backpack";
	private static final String BACKPACK_CHECK_COMMAND = "backpack check";
	private static final String BACKPACK_USE_SWORD_COMMAND = "backpack use SilverSword";
	private static final String USE_SPELL_COMMAND = "backpack use FireSpell";
	private static final String BACKPACK_THROW_SWORD_COMMAND = "backpack throw SilverSword";
	private static final String PLAY_AS_HERO_2_COMMAND = "play as hero2";
	private static final String GIVE_SWORD_COMMAND = "give SilverSword 2";
	private static final String BATTLE_COMMAND = "battle";
	private static final String QUIT_COMMAND = "quit";

	private static final String ENTER_VALID_COMMAND_MESSAGE = "Enter a valid command.";
	private static final String BACKPACK_IS_EMPTY_MESSAGE = "Your backpack is empty.";
	private static final String BATTLE_ENDED_WITH_A_DRAW_MESSAGE = "Battle ended with a draw.";
	private static final String EXPECTED_DUNGEON_MAP = "+++++++++++++++++++++++++++++++++++++++++++\n"
			+ "|  1   .   #   #   .   .   .   .   .   T  |\n" + "|  T   #   #   .   .   #   #   #   .   #  |\n"
			+ "|  .   .   .   .   M   .   .   .   M   T  |\n" + "|  .   M   .   .   .   .   .   .   #   #  |\n"
			+ "===========================================\n";

	GameRepository gameRepository;
	CommandExecutor commandExecutor;
	SocketChannel socketChannel;
	SocketChannel socketChannelRecipient;
	UserRecipient userRecipient;

	@Before
	public void setup() {
		gameRepository = DungeonGenerator.generateGameRepository();
		commandExecutor = new CommandExecutor(gameRepository);

		try {
			socketChannel = SocketChannel.open();
			socketChannelRecipient = SocketChannel.open();
		} catch (IOException e) {
			throw new RuntimeException(PROBLEM_OPENING_SOCKET_MESSAGE, e);
		}

		userRecipient = new UserRecipient(null, null);

	}

	@Test
	public void testRegisterCommandIfSuccessful() {
		String message = "Register command should return welcoming message.";
		String expected = "hero1 <1>, welcome to <DUNGEONS ONLINE>! Ready to spill some blood?";
		String actual = commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testRegisterCommandIfUserAlreadyExists() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Only one user should exist per username.";
		String expected = "Username is taken. Try with another one.";
		String actual = commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testMoveCommandIfSuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Hero should be able to move to free positions.";
		String expected = "You moved successfully to the next position.";
		String actual = commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
		message = "DungeonMap should be updated after the hero moves.";
		String resultDungeonMap = gameRepository.printDungeonMap();
		assertEquals(message, resultDungeonMap, EXPECTED_DUNGEON_MAP);
	}

	@Test
	public void testMoveCommandIfObstacleEncountered() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Hero should not be able to move through obstacles.";
		String expected = "Invalid move. There is an obstacle and you cannot bypass it.";
		String actual = commandExecutor.executeCommand(MOVE_RIGHT_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testMoveCommandIfWallEncountered() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Hero should not be able to move through walls.";
		String expected = "Invalid move. You cannot go through a wall.";
		String actual = commandExecutor.executeCommand(MOVE_UP_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testCollectCommandSpell() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		for (int i = 0; i < gameRepository.getDungeonMapCols(); i++) {
			commandExecutor.executeCommand(MOVE_RIGHT_COMMAND, socketChannel, userRecipient);
		}

		String message = "Hero should be able to collect spells.";
		String expected = "Spell <FireSpell> Attack points <30> Mana cost <10> was added to your backpack.";
		String actual = commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
		message = "Hero should be able to use spell.";
		expected = "Learned spell FireSpell! Attack points: 30, Mana cost: 10";
		actual = commandExecutor.executeCommand(USE_SPELL_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testCollectCommandIfSuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		String message = "Hero should be able to collect weapons.";
		String expected = "Weapon <SilverSword> Attack points <20> was added to your backpack.";
		String actual = commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testCollectCommandIfUnsuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Hero shouldn't be able to collect a treasure that's not in range.";
		String expected = "There is no treasure at this position.";
		String actual = commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testBackpackCommandIfUnsuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Validation of backpack command.";
		String expected = ENTER_VALID_COMMAND_MESSAGE;
		String actual = commandExecutor.executeCommand(BACKPACK_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testBackpackCheckCommandIfSuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		String message = "Hero should be able to check his backpack's contents.";
		String expected = "Weapon <SilverSword> Attack points <20>";
		String actual = commandExecutor.executeCommand(BACKPACK_CHECK_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testBackpackCheckCommandIfEmptyBackpack() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		String message = "Check if backpack is empty.";
		String expected = BACKPACK_IS_EMPTY_MESSAGE;
		String actual = commandExecutor.executeCommand(BACKPACK_CHECK_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testBackpackUseCommandIfSuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		String message = "Hero should be able to equip with weapon from backpack.";
		String expected = "Equiped with weapon SilverSword! Attack points: 20";
		String actual = commandExecutor.executeCommand(BACKPACK_USE_SWORD_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testBackpackUseCommandIfTreasureNotInBackpack() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Hero should not be able to use treasures that are not in his backpack.";
		String expected = "Cannot use treasures that are not yours.";
		String actual = commandExecutor.executeCommand(BACKPACK_USE_SWORD_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testBackpackThrowCommandIfSuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		String message = "Hero should be able to throw treasure from his backpack.";
		String expected = "SilverSword was thrown from your backpack.";
		String actual = commandExecutor.executeCommand(BACKPACK_THROW_SWORD_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
		assertEquals(message, "T.1", gameRepository.getDungeonMap()[1][0]);
		assertEquals(message, "SilverSword",
				gameRepository.getTreasureAtPosition(Position.createNewPosition(1, 0)).getName());
	}

	@Test
	public void testBackpackThrowCommandIfTreasureNotInBackpack() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "User should not be able to throw treasure not in his backpack.";
		String expected = BACKPACK_IS_EMPTY_MESSAGE;
		String actual = commandExecutor.executeCommand(BACKPACK_THROW_SWORD_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testGiveCommandIfNotAtTheSamePosition() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(PLAY_AS_HERO_2_COMMAND, socketChannelRecipient, userRecipient);
		String message = "Hero should not be able to give treasures to players that are out of ragne.";
		String expected = "The player is not in range.";
		String actual = commandExecutor.executeCommand(GIVE_SWORD_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testGiveCommandIfWrongName() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Hero shouldnot be able to give treasures he does not have.";
		String expected = "Treasure not found.";
		String actual = commandExecutor.executeCommand(GIVE_SWORD_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

	@Test
	public void testGiveCommandIfSuccessful() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(PLAY_AS_HERO_2_COMMAND, socketChannelRecipient, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannelRecipient, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannelRecipient, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannelRecipient, userRecipient);
		commandExecutor.executeCommand(MOVE_UP_COMMAND, socketChannelRecipient, userRecipient);
		String message = "Hero and recipient should be notified if a treasure is given.";
		String expected = "You have given a treasure to hero2 <2>";
		String actual = commandExecutor.executeCommand(GIVE_SWORD_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
		expected = "hero1 <1> gave you a treasure. Weapon <SilverSword> Attack points <20> was added to your backpack.";
		actual = userRecipient.getMessage();
		assertEquals(message, expected, actual);
	}

	@Test
	public void testBattleCommandIfDraw() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(PLAY_AS_HERO_2_COMMAND, socketChannelRecipient, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannelRecipient, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannelRecipient, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannelRecipient, userRecipient);
		String message = "Hero should be able to battle and win, lose or draw.";
		String expected = BATTLE_ENDED_WITH_A_DRAW_MESSAGE;
		String actual = commandExecutor.executeCommand(BATTLE_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
		expected = "hero1 <1>'s battle with you ended with a draw.";
		actual = userRecipient.getMessage();

	}

	@Test
	public void testBattleWithMinionCommand() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_LEFT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(COLLECT_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_DOWN_COMMAND, socketChannel, userRecipient);
		commandExecutor.executeCommand(MOVE_RIGHT_COMMAND, socketChannel, userRecipient);
		String message = "Hero should be able to battle minions and win, lose or draw.";
		String actual = commandExecutor.executeCommand(BATTLE_COMMAND, socketChannel, userRecipient);
		Minion minion = gameRepository.getMinionAtPosition(Position.createNewPosition(3, 1));
		String expected = minion.isAlive() ? "You died. Enemy wins. hero1 <1> RIP" : BATTLE_ENDED_WITH_A_DRAW_MESSAGE;
		assertEquals(message, expected, actual);
	}

	@Test
	public void testQuitCommand() {
		commandExecutor.executeCommand(PLAY_AS_HERO_1_COMMAND, socketChannel, userRecipient);
		String message = "Hero should be able to quit at any moment.";
		String expected = "hero1 <1> has quit game.";
		String actual = commandExecutor.executeCommand(QUIT_COMMAND, socketChannel, userRecipient);
		assertEquals(message, expected, actual);
	}

}
