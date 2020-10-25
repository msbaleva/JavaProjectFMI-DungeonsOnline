package bg.sofia.uni.fmi.mjt.dungeon.command;

public enum CommandType {
	BACKPACK,
	BATTLE,
	CHECK,
	COLLECT,
	DOWN,
	GIVE,
	LEFT, 
	MOVE,
	PLAY,
	QUIT,
	RIGHT,
	THROW,
	UP,	
	USE
	;
	
	public static CommandType getCommandAsEnum(String command) {
		CommandType firstCommandAsEnum = null;
		try {
			firstCommandAsEnum = CommandType.valueOf(command.toUpperCase());
		} catch (IllegalArgumentException exception) {
			return null;
		}
		
		return firstCommandAsEnum;
	}
}
