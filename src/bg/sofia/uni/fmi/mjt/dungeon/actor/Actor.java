package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public interface Actor {
	
	String getName();
	
	String getId();
	
	boolean isAlive(); 

	Weapon getWeapon();

	Spell getSpell();
	
	Position getPosition();
	
	void setPosition(int row, int col);

	boolean takeDamage(int damagePoints);
	
	int giveExperiencePointsAfterBattle();

	int attack();
	
	Stats getStats();
	
	String getFormattedName();
	
	

}
