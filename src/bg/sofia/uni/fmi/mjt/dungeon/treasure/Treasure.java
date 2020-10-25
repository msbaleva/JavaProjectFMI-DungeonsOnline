package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public interface Treasure {

	String INVALID_ARGUMENTS = "Invalid arguments.";

	String getName();

	String use(Hero hero);

	String getTreasureStats();

	int giveExperienceWhenCollected();

}
