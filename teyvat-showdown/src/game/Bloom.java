package game;

// efecto de veneno/florecimiento: tras una ulti de Kinich, los 3 proximos turnos el enemigo está envenenado y pierde 10HP en cada uno
public class Bloom extends Effect {
private int damagePerTurn;
	
	public Bloom(int duration, int dpt) {
		super(duration);
		this.damagePerTurn = dpt;
	}
	
	@Override
    public void apply(Character target) {
        target.takeDamage(this.damagePerTurn);
        System.out.println(target.getName() + " is poisoned and took " + this.damagePerTurn + " damage");
    }
}
