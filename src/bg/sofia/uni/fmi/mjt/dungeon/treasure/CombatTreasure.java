package bg.sofia.uni.fmi.mjt.dungeon.treasure;

public abstract class CombatTreasure implements Treasure {

	protected String name;
	protected int level;
	protected int attack;
	protected int experience;

	protected CombatTreasure(String name, int level, int attack, int experience) {
		this.name = name;
		this.level = level;
		this.attack = attack;
		this.experience = experience;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getAttack() {
		return attack;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public int giveExperienceWhenCollected() {
		return experience;
	}

}
