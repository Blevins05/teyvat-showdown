package game;

public class Eula extends Character {
	private double freezeChance; // solo para Eula
	
	public Eula () {
		super("Eula", 105, 27, 18, 0.85, Element.CRYO, 2, 2);
		this.freezeChance = 0.30;
	}
	
	public String getName() {
		return super.getName();
	}

	@Override
	protected void performUltimate(Character enemy) {
		// TODO Auto-generated method stub
		
		/* 
		 Cryo
			
			Nombre: Congelación Ártica
			
			Efecto: Daño moderado (1.3× ataque base)
			
			Extra: 30% de probabilidad de congelar al enemigo 1 turno
			
			Rol: Control de enemigo, ralentizar o impedir acción
		 * */
		
		int baseDamage = this.getBaseDamage(enemy);
		int modifiedAttack = (int) (baseDamage * 1.3);;
		
		enemy.takeDamage(modifiedAttack);
		if (Math.random() <= this.freezeChance) {
			enemy.applyEffect(new Freeze(1));
		} 
		
		if (this.freezeChance < 0.6) {
			this.freezeChance += 0.05;
		}
	}

	
}
