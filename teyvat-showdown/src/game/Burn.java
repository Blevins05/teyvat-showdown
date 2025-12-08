package game;

// efecto de quemadura: tras una ulti de Durin, el enemigo es quemado y pierde 5 de vida durante 2 turnos. 
public class Burn extends Effect{
	private int damagePerTurn;
	
	public Burn(int duration, int dpt) {
		super(duration);
		this.damagePerTurn = dpt;
	}
	
	@Override
    public void apply(Character target) {
        target.takeDamage(this.damagePerTurn);
        System.out.println(target.getName() + " is burned and took " + this.damagePerTurn + " damage");
    }
}
