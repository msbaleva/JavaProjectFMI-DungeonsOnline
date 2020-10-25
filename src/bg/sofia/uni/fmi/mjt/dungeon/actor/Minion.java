package bg.sofia.uni.fmi.mjt.dungeon.actor;

import java.util.Random;

public class Minion extends ActorImpl {

	private int level;
	private int winningPoints;
	private static final int MAX_LEVEL = 30;
	private static final int LEVEL_FACTOR = 10;

	Minion(String name, String id, Position position) {
		super(name, id, position);
		this.level = new Random().nextInt(MAX_LEVEL + 1);
		this.winningPoints = level * LEVEL_FACTOR;
		stats.modifyByLevel(level);
	}

	public void revive(Position position) {
		stats.modifyByLevel(level);
		this.position = position;
	}

	@Override
	public int giveExperiencePointsAfterBattle() {
		return winningPoints;
	}

}
