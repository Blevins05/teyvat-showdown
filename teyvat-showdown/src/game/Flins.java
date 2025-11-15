package game;

public class Flins extends Character {
	private double critChance = 0.25;
	public Flins () {
		super("Flins", 105, 27, 18, 0.85, Element.ELECTRO, 2, 2);
	}
	
	public String getName() {
		return super.getName();
	}

	@Override
	protected void performUltimate(Character enemy) {
		// TODO Auto-generated method stub
		
		/*
		   Electro (Flins)
			Nombre: Tormenta Eléctrica
			
			Efecto: Daño alto (1.8× ataque base)
			
			Extra: 25% de chance de crítico (doble daño)
			
			Rol: Daño puro y riesgo de crit, estilo “golpe fuerte y aleatorio”
			
		 */
		
		int baseDamage = this.getBaseDamage(enemy);
		int modifiedAttack = (int) (baseDamage * 1.8);
		double critDamageChance = Math.random();
		
		if (critDamageChance <= this.critChance) {
			modifiedAttack *= 2;
		} else {
			this.critChance += 0.03;
		}
		
		enemy.takeDamage(modifiedAttack);
	    
	}

}
