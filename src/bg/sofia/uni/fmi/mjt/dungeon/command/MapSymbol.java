package bg.sofia.uni.fmi.mjt.dungeon.command;

public enum MapSymbol {
	DELIMINATOR_SYMBOL("."),
	FREE_SYMBOL(" "),
	MINION_SYMBOL("M"),
	TREASURE_SYMBOL("T"),
	NO_FREE_SPACES(" # "),
	TWO_FREE_SPACES(" . "),
	MINION_ONE_SPACE(" M "),
	TREASURE_ONE_SPACE(" T "),
	MINION_NO_SPACES("M."),
	TREASURE_NO_SPACES("T.");
	
	private String symbol;
	
	private MapSymbol(String symbol) { 
        this.symbol = symbol; 
    } 
	
	 public String getSymbol() { 
	        return symbol; 
	   } 
}
