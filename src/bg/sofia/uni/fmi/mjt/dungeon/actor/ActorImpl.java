package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public abstract class ActorImpl implements Actor {

	private static final int NO_ATTACK_POINTS = 0;
	private String name;
	protected String id;
	protected Position position;
	protected Weapon currentWeapon;
	protected Spell currentSpell;
	protected Stats stats;

	protected ActorImpl(String name, String id, Position position) {
		this.name = name;
		this.id = id;
		this.position = position;
		stats = Stats.getStatsInstance();
	}

	@Override
	public String getName() {
		return name;
	};

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isAlive() {
		return stats.getHealth() > 0;
	}

	@Override
	public Stats getStats() {
		return stats;
	}

	@Override
	public Weapon getWeapon() {
		return currentWeapon;
	}

	@Override
	public Spell getSpell() {
		return currentSpell;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void setPosition(int row, int col) {
		this.position.setRow(row);
		this.position.setCol(col);
	}

	@Override
	public boolean takeDamage(int damagePoints) {
		if (!isAlive() || damagePoints < 0) {
			return false;
		}

		return stats.decreaseHealth(damagePoints);
	}

	@Override
	public int attack() {
		int attack = stats.getAttack();
		if (currentSpell == null) {
			return attack + ((currentWeapon == null) ? NO_ATTACK_POINTS : currentWeapon.getAttack());
		}

		if (currentWeapon == null || currentWeapon.getAttack() < currentSpell.getAttack()) {
			if (stats.useMana(currentSpell.getManaCost())) {
				return attack + currentSpell.getAttack();
			} else {
				return attack + ((currentWeapon == null) ? NO_ATTACK_POINTS : currentWeapon.getAttack());
			}
		}

		return attack;
	}

	@Override
	public String getFormattedName() {
		return name + " <" + id + ">";
	}

}
