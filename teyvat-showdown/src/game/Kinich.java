package game;

public class Kinich extends Character{
	public Kinich() {
		   super("Kinich", 98, 24, 16, 0.92, Element.DENDRO, 2, 2);
	}

	@Override
	protected void performUltimate(Character enemy) {
		// TODO Auto-generated method stub
		
		/* 
		 * Dendro

			Nombre: Esporas Tóxicas
			
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
