package game;

public class Kinich extends Character{
	public Kinich() {
		   super("Kinich", 105, 105, 23, 20, 0.92, Element.DENDRO, 3, 3);
		   
		   // Cambios a Kinich:
		   // Cooldown de la ultimate: 2 -> 3 turnos (estaba rotisimo)
		   // Vida: 98 -> 105
	}

	@Override
	protected void performUltimate(Character enemy) {
		/* 
		 *  Kinich (Dendro)
			
			Efecto: Daño bajo-moderado (1.1× ataque base)
			
			Extra: Aplica daño residual (10 HP por turno durante 3 turnos)
			
			Rol: Estrategia de desgaste, daño prolongado
	*/
		
		int baseDamage = this.getBaseDamage(enemy);
		int modifiedAttack = (int) (baseDamage * 1.1);
		enemy.takeDamage(modifiedAttack);
		enemy.applyEffect(new Bloom(3, 10));
		System.out.println(enemy.getName() + " is now poisoned for 3 turns ");
		
		
	}
}
