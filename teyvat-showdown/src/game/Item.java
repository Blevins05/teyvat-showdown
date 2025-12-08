package game;

// items del juego
public enum Item {
	SMALL_POTION(20),
    MEDIUM_POTION(40),
    LARGE_POTION(60);
	
	  private final int healAmount;

	    Item(int healAmount) {
	        this.healAmount = healAmount;
	    }

	    public int getHealAmount() {
	        return healAmount;
	    }

}
