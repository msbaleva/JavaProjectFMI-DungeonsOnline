package bg.sofia.uni.fmi.mjt.dungeon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.dungeon.actor.ActorFactory;
import bg.sofia.uni.fmi.mjt.dungeon.actor.ActorType;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeon.command.GameRepository;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public class DungeonGenerator {

	private static final int DUNGEON_MAP_ROW_DIMENSION = 4;
	private static final int DUNGEON_MAP_COL_DIMENSION = 10;

	public static String[][] generateDungeonMap() {
		String[][] dungeonMap = { { " . ", " . ", " # ", " # ", " . ", " . ", " . ", " . ", " . ", " T " },
				{ " T ", " # ", " # ", " . ", " . ", " # ", " # ", " # ", " . ", " # " },
				{ " . ", " . ", " . ", " . ", " M ", " . ", " . ", " . ", " M ", " T " },
				{ " . ", " M ", " . ", " . ", " . ", " . ", " . ", " . ", " # ", " # " } };
		return dungeonMap;
	}

	public static Map<Position, Minion> generateMinions() {
		Map<Position, Minion> minions = new HashMap<>();
		minions.put(Position.createNewPosition(2, 4),
				(Minion) ActorFactory.getActor(ActorType.MINION, "minion1", "1", Position.createNewPosition(2, 4)));
		minions.put(Position.createNewPosition(2, 8),
				(Minion) ActorFactory.getActor(ActorType.MINION, "minion2", "2", Position.createNewPosition(2, 8)));
		minions.put(Position.createNewPosition(3, 1),
				(Minion) ActorFactory.getActor(ActorType.MINION, "minion3", "3", Position.createNewPosition(3, 1)));
		return minions;
	}

	public static Map<Position, Treasure> generateTreasures() {
		Map<Position, Treasure> treasures = new HashMap<>();
		treasures.put(Position.createNewPosition(0, 9), HealthPotion.createHealthPotion("HealthPotion", 20, 10));
		treasures.put(Position.createNewPosition(1, 0), Weapon.createWeapon("SilverSword", 1, 20, 10));
		treasures.put(Position.createNewPosition(2, 9), Spell.createSpell("FireSpell", 1, 30, 10, 10));
		return treasures;
	}

	public static Set<Position> generateFreePositions(String[][] dungeonMap) {
		Set<Position> positions = new HashSet<>();
		for (int i = 0; i < DUNGEON_MAP_ROW_DIMENSION; i++) {
			for (int j = 0; j < DUNGEON_MAP_COL_DIMENSION; j++) {
				if (dungeonMap[i][j].equals(" . ")) {
					positions.add(Position.createNewPosition(i, j));
				}
			}
		}

		return positions;
	}

	public static GameRepository generateGameRepository() {
		String[][] dungeonMap = generateDungeonMap();
		Map<Position, Minion> minions = generateMinions();
		Map<Position, Treasure> treasures = generateTreasures();
		Set<Position> freePositions = generateFreePositions(dungeonMap);
		return GameRepository.createNewGameRepository(dungeonMap, minions, treasures, freePositions);
	}
}
