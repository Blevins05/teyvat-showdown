package game;

public class Eula extends Character {
	private double freezeChance; // solo para Eula
	
	public Eula () {
		super("Eula", 120, 120, 23, 20, 0.85, Element.CRYO, 2, 2);
		this.freezeChance = 0.30;
		
		// Cambios a Eula:
		// Vida: 115 -> 120
	}
	
	public String getName() {
		return super.getName();
	}

	@Override
	protected void performUltimate(Character enemy) {
		final double eulaDamageMultiplier = 1.3;
		/* 
		 Eula (Cryo)
			
			Efecto: Daño moderado (1.3× ataque base)
			
			Extra: 30% de probabilidad de congelar al enemigo 1 turno (con el tiempo va aumentando esa prob)
			
			Rol: Personaje de late game, va escalando su prob de congelacion con el tiempo
		 * */
		
		int baseDamage = this.getBaseDamage(enemy);
		int modifiedAttack = (int) (baseDamage * eulaDamageMultiplier);;
		
		enemy.takeDamage(modifiedAttack);
		if (Math.random() <= this.freezeChance) {
			enemy.applyEffect(new Freeze(1));
		} 
		
		if (this.freezeChance < 0.6) {
			this.freezeChance += 0.05;
		}
	}

	
}
