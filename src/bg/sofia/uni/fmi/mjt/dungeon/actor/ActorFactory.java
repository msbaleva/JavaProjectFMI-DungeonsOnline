package bg.sofia.uni.fmi.mjt.dungeon.actor;

public class ActorFactory {

	private final static String INVALID_ARGUMENTS = "Illegal arguments: cannot be null";

	public static Actor getActor(ActorType actorType, String name, String id, Position position) {

		if (actorType == null || name == null || id == null || position == null) {
			throw new IllegalArgumentException(INVALID_ARGUMENTS);
		}

		if (actorType.equals(ActorType.HERO)) {
			return new Hero(name, id, position);
		}
		
		return new Minion(name, id, position);

	}

}
