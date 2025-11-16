package game;

public class Torch extends Character{
	
	public Torch() {
		super("Torch", 105, 105, 23, 20, 0.85, Element.PYRO, 2, 2);
	}

	@Override
	protected void performUltimate(Character enemy) {
		final double torchDamageMultiplier = 1.5;
		/* Ulti: 
			Nombre: Explosión Ígnea
			
			Efecto: Daño alto (1.5× ataque base) y gana 5% de ataque por ultimate
			
			Extra: Deja al enemigo “quemado” → pierde 5 HP por turno durante 2 turnos
			
			Rol: Combina daño directo con daño residual
		 * */
		
		int baseDamage = this.getBaseDamage(enemy);
		int modifiedAttack = (int) (baseDamage * torchDamageMultiplier);
		
		enemy.takeDamage(modifiedAttack);
		enemy.applyEffect(new Burn(2, 5));
		System.out.println(enemy.getName() + " is now burned for 2 turns ");
		
		this.baseAttack = (int) (this.baseAttack * 1.05);
		System.out.println("Torch's attack has improved a little bit!");
		
	}
}
