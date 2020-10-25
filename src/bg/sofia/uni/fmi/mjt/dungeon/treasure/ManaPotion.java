package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class ManaPotion extends Potion {

	ManaPotion(String name, int points, int experience) {
		super(name, points, experience);
	}

	@Override
	public String use(Hero hero) {
		if (hero.takeMana(points)) {
			return points + " mana points added to your hero!";
		}

		return null;
	}

	@Override
	public String getTreasureStats() {
		return "Mana potion <" + name + "> Mana points <" + points + ">";
	}

	public static ManaPotion createPotion(String name, int level, int experience) {
		if (name == null || level <= 0 || experience <= 0) {
			throw new IllegalArgumentException(INVALID_ARGUMENTS);
		}

		return new ManaPotion(name, level, experience);
	}
}
