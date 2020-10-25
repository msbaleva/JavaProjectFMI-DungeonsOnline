package bg.sofia.uni.fmi.mjt.dungeon.command;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public abstract class CommandImpl implements Command {
	
	protected Hero hero;
	protected String[] splitCommand;
	
	protected CommandImpl(Hero hero, String[] splitCommand) {
		this.hero = hero;
		this.splitCommand = splitCommand;
	}

}
