package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class Weapon extends CombatTreasure {

	Weapon(String name, int level, int attack, int experience) {
		super(name, level, attack, experience);
	}

	@Override
	public String use(Hero hero) {
		if (hero.equip(this)) {
			return "Equiped with weapon " + name + "! Attack points: " + attack;
		}

		return "You need to be level " + level + " to use this weapon.";
	}

	@Override
	public String getTreasureStats() {
		return "Weapon <" + name + "> Attack points <" + attack + ">";
	}

	public static Weapon createWeapon(String name, int level, int attack, int experience) {
		if (name == null || level <= 0 || attack <= 0 || experience <= 0) {
			throw new IllegalArgumentException(INVALID_ARGUMENTS);
		}

		return new Weapon(name, level, attack, experience);
	}

}
