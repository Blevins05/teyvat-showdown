package game;

public class Burn extends Effect{
	private int damagePerTurn;
	
	public Burn(int duration, int dpt) {
		super(duration);
		this.damagePerTurn = dpt;
	}
	
	@Override
    public void apply(Character target) {
        target.takeDamage(this.damagePerTurn);
        System.out.println(target.getName() + "is burned and took" + this.damagePerTurn + "damage");
    }
}
