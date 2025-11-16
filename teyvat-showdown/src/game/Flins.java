package game;

public class Flins extends Character {
	private double critChance = 0.25;
	public Flins () {
		super("Flins", 100, 100, 32, 17, 0.85, Element.ELECTRO, 3, 3);
	}
	
	public String getName() {
		return super.getName();
	}

	@Override
	protected void performUltimate(Character enemy) {
		final double flinsDamageMultiplier = 1.8;
		/*
		   Electro (Flins)
			Nombre: Tormenta Eléctrica
			
			Efecto: Daño alto (1.8× ataque base)
			
			Extra: 25% de chance de crítico (doble daño)
			
			Rol: Daño puro y riesgo de crit, estilo “golpe fuerte y aleatorio”
			
		 */
		
		int baseDamage = this.getBaseDamage(enemy);
		int modifiedAttack = (int) (baseDamage * flinsDamageMultiplier);
		double critDamageChance = Math.random();
		
		if (critDamageChance <= this.critChance) {
			modifiedAttack *= 2;
		} else {
			this.critChance += 0.03;
		}
		
		enemy.takeDamage(modifiedAttack);
	    
	}

}
