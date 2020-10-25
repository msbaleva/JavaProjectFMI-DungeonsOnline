package bg.sofia.uni.fmi.mjt.dungeon.treasure;

public abstract class Potion implements Treasure {

	protected String name;
	protected int points;
	protected int experience;

	protected Potion(String name, int points, int experience) {
		this.name = name;
		this.points = points;
		this.experience = experience;
	}

	@Override
	public String getName() {
		return name;
	}

	public int heal() {
		return points;
	}

	@Override
	public int giveExperienceWhenCollected() {
		return experience;
	}

}
