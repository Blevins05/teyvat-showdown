package game;

public class Durin extends Character{
	
	public Durin() {
		super("Durin", 105, 105, 25, 20, 0.85, Element.PYRO, 2, 2);
		
		// Buff a Durin:
		// Ataque Base: 23 -> 25
	}

	@Override
	protected void performUltimate(Character enemy) {
		final double torchDamageMultiplier = 1.5;
		/* 
		    Durin (Pyro)
		    
			Efecto: Daño alto (1.5× ataque base) y gana 5% de ataque por ulti
			
			Extra: Deja al enemigo “quemado” → pierde 5 HP por turno durante 2 turnos
			
			Rol: Sub-DPS/Support, mete buen daño directo y va escalando con el tiempo
			
		 * */
		
		int baseDamage = this.getBaseDamage(enemy);
		int modifiedAttack = (int) (baseDamage * torchDamageMultiplier);
		
		enemy.takeDamage(modifiedAttack);
		enemy.applyEffect(new Burn(2, 5));
		System.out.println(enemy.getName() + " is now burned for 2 turns ");
		
		this.baseAttack = (int) (this.baseAttack * 1.05);
		System.out.println("Durin's attack has improved a little bit!");
		
	}
}
