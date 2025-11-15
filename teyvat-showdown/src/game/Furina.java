package game;

public class Furina extends Character {
	public Furina() {
	    super("Furina",115, 115, 20, 22, 0.90, Element.HYDRO, 3, 3);
	}
	
	@Override
	protected void performUltimate(Character enemy) {
		// TODO Auto-generated method stub
		
		/*  
		 Ulti de Furina:
		 
		 Nombre: Ola Vital

		Efecto: Cura propia vida (25–35% HP) y da un golpe de daño base: 1x
		
		Extra: Puede limpiar estados negativos como “quemado”, “congelado” o "bloom"
		
		Rol: Soporte / supervivencia, permite mantenerse en la batalla más tiempo

		 * */
		int baseDamage = this.getBaseDamage(enemy);
		enemy.takeDamage(baseDamage);
		activeEffects.removeIf(e -> e instanceof Burn || e instanceof Bloom || e instanceof Freeze);
		
        int healAmount = (int) (this.maxHp * 0.30); // 34 HP aprox
        int newHp = Math.min(this.healthPoints + healAmount, this.maxHp);
        
        int actualHealed = newHp - this.healthPoints;
        this.healthPoints = newHp;
        
        System.out.println("Furina healed herself and has now" + this.healthPoints + "/" + this.maxHp);
        
	}
}
