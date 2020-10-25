package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class HealthPotion extends Potion {

	HealthPotion(String name, int points, int experience) {
		super(name, points, experience);
	}

	@Override
	public String use(Hero hero) {
		if (hero.takeHealing(points)) {
			return points + " health points added to your hero!";
		}
		return null;
	}

	@Override
	public String getTreasureStats() {
		return "Health potion <" + name + "> Health points <" + points + ">";
	}

	public static HealthPotion createHealthPotion(String name, int level, int experience) {
		if (name == null || level <= 0 || experience <= 0) {
			throw new IllegalArgumentException(INVALID_ARGUMENTS);
		}

		return new HealthPotion(name, level, experience);
	}
}
