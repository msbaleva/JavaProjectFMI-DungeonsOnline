package bg.sofia.uni.fmi.mjt.dungeon.command;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

public class GameRepository {

	static final String DELIMINATOR_SYMBOL = ".";
	static final String FREE_SYMBOL = " ";
	static final String MINION_SYMBOL = "M";
	static final String TREASURE_SYMBOL = "T";
	static final String NO_FREE_SPACES = " # ";
	static final String TWO_FREE_SPACES = " . ";
	static final String MINION_ONE_SPACE = " M ";
	static final String TREASURE_ONE_SPACE = " T ";
	static final String MINION_NO_SPACES = "M.";
	static final String TREASURE_NO_SPACES = "T.";

	private static final String CEIL_SYMBOL = "+";
	private static final String WALL_SYMBOL = "|";
	private static final String FLOOR_SYMBOL = "=";
	private static final String NEWLINE = "\n";
	private static final String INVALID_ARGUMENTS = "Arguments cannot be null.";

	private static final int ZERO_POSITION = 0;
	private static final int SECOND_POSITION = 2;

	private String[][] dungeonMap;
	private Map<Position, Minion> minionsByPosition;
	private Map<Position, Treasure> treasuresByPosition;
	private Set<Position> freePositions;

	GameRepository(String[][] dungeonMap, Map<Position, Minion> minionsByPosition,
			Map<Position, Treasure> treasuresByPosition, Set<Position> freePositions) {
		this.dungeonMap = dungeonMap;
		this.minionsByPosition = minionsByPosition;
		this.treasuresByPosition = treasuresByPosition;
		this.freePositions = freePositions;
	}

	public String[][] getDungeonMap() {
		return dungeonMap;
	}

	public Set<Position> getFreePositions() {
		return freePositions;
	}

	public Minion getMinionAtPosition(Position position) {
		return minionsByPosition.get(position);
	}

	public Treasure getTreasureAtPosition(Position position) {
		return treasuresByPosition.get(position);
	}

	public int getDungeonMapCols() {
		return dungeonMap[0].length;
	}

	public int getDungeonMapRows() {
		return dungeonMap.length;
	}

	public String getContentAtPosition(Position position) {
		return dungeonMap[position.getRow()][position.getCol()];
	}

	public void setContentAtPosition(Position position, String update) {
		dungeonMap[position.getRow()][position.getCol()] = update;
	}

	public void updateTakenTreasureAtPosition(Position position, String update) {
		treasuresByPosition.remove(position);
		dungeonMap[position.getRow()][position.getCol()] = update;
	}

	public void updateDeadMinionAtPosition(Position position, String update) {
		Position newPosition = getFreePosition();
		if (newPosition == null) {
			return;
		}
		minionsByPosition.get(position).revive(newPosition);
		Minion revivedMinion = minionsByPosition.remove(position);
		minionsByPosition.put(newPosition, revivedMinion);
		dungeonMap[position.getRow()][position.getCol()] = update;
	}

	private void insertTreasureAtFreePosition(Treasure treasure) {
		if (treasure == null) {
			return;
		}

		Position newPosition = getFreePosition();
		if (newPosition == null) {
			return;
		}

		dungeonMap[newPosition.getRow()][newPosition.getCol()] = TREASURE_ONE_SPACE;
		treasuresByPosition.put(newPosition, treasure);
	}

	public void updateNewTreasureAtPosition(Position position, Treasure treasure, int row, int col, String heroId) {
		if (treasure != null) {
			dungeonMap[row][col] = TREASURE_NO_SPACES + heroId;
			treasuresByPosition.put(position, treasure);
		}
	}

	public void updateThrownTreasure(Position position, String heroId, Treasure treasure) {
		int row = position.getRow();
		int col = position.getCol();
		if (dungeonMap[row][col].contains(DELIMINATOR_SYMBOL)) {
			insertTreasureAtFreePosition(treasure);
			return;
		}

		updateNewTreasureAtPosition(position, treasure, row, col, heroId);

	}

	public String getStayingHeroId(String idToBeRemoved, int row, int col) {
		char id = dungeonMap[row][col].endsWith(idToBeRemoved) ? dungeonMap[row][col].charAt(ZERO_POSITION)
				: dungeonMap[row][col].charAt(SECOND_POSITION);
		return String.valueOf(id);
	}

	public void updateDeadHeroAtPosition(Position position, String idToBeRemoved, Treasure treasure) {
		int row = position.getRow();
		int col = position.getCol();
		if (dungeonMap[row][col].contains(MINION_SYMBOL)) {
			dungeonMap[row][col] = MINION_ONE_SPACE;
			insertTreasureAtFreePosition(treasure);
			return;
		}

		String stayingHeroId = getStayingHeroId(idToBeRemoved, row, col);
		if (treasure == null) {
			dungeonMap[row][col] = FREE_SYMBOL + stayingHeroId + FREE_SYMBOL;
			return;
		}

		updateNewTreasureAtPosition(position, treasure, row, col, stayingHeroId);

	}

	public void updateMovingHeroFromPosition(Position position, String idToBeRemoved) {
		int row = position.getRow();
		int col = position.getCol();
		if (dungeonMap[row][col].contains(TREASURE_SYMBOL)) {
			dungeonMap[row][col] = TREASURE_ONE_SPACE;
		} else if (dungeonMap[row][col].contains(MINION_SYMBOL)) {
			dungeonMap[row][col] = MINION_ONE_SPACE;
		} else if (dungeonMap[row][col].endsWith(FREE_SYMBOL)) {
			dungeonMap[row][col] = TWO_FREE_SPACES;
			freePositions.add(Position.createNewPosition(row, col));
		} else {
			String otherHeroId = getStayingHeroId(idToBeRemoved, row, col);
			dungeonMap[row][col] = FREE_SYMBOL + otherHeroId + FREE_SYMBOL;
		}

	}

	public Position getFreePosition() {
		if (freePositions.isEmpty()) {
			return null;
		}

		Position freePosition = freePositions.stream().findAny().get();

		removeFreePosition(freePosition);
		return freePosition;
	}

	public void removeFreePosition(Position position) {
		freePositions.remove(position);
	}

	public boolean dungeonMapHasMinionAtPosition(Position position) {
		return dungeonMap[position.getRow()][position.getCol()].contains(MINION_SYMBOL);
	}

	public String printDungeonMap() {
		StringBuilder printedDungeonMap = new StringBuilder();

		printedDungeonMap.append(getStringNTimes(CEIL_SYMBOL) + NEWLINE);

		for (int i = 0; i < dungeonMap.length; i++) {
			printedDungeonMap.append(WALL_SYMBOL + FREE_SYMBOL);
			for (int j = 0; j < dungeonMap[i].length; j++) {
				printedDungeonMap.append(dungeonMap[i][j]).append(FREE_SYMBOL);
			}

			printedDungeonMap.append(WALL_SYMBOL + NEWLINE);
		}

		printedDungeonMap.append(getStringNTimes(FLOOR_SYMBOL) + NEWLINE);

		return printedDungeonMap.toString();
	}

	public String getStringNTimes(String string) {
		return IntStream.range(0, dungeonMap[0].length * 4 + 3).mapToObj(i -> string).collect(Collectors.joining(""));
	}

	public static GameRepository createNewGameRepository(String[][] dungeonMap, Map<Position, Minion> minionsByPosition,
			Map<Position, Treasure> treasuresByPosition, Set<Position> freePositions) {
		if (dungeonMap == null || minionsByPosition == null || treasuresByPosition == null || freePositions == null) {
			throw new IllegalArgumentException(INVALID_ARGUMENTS);
		}
		return new GameRepository(dungeonMap, minionsByPosition, treasuresByPosition, freePositions);
	}

}
