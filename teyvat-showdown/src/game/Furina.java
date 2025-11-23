package game;

public class Furina extends Character {
	public Furina() {
		   super("Furina", 125, 125, 20, 24, 0.90, Element.HYDRO, 3, 3);
		   
		    // nuevos buffs a furina:
		    // HP: 120→125 
		    // DEF: 23→24 
		    // Añadida una pasiva unica, su daño es inversamente proporcional a su vida
	}
	
	  @Override
	    public int getBaseDamage(Character target) {
	        int hpPercent = (this.healthPoints * 100) / this.maxHp;
	        int bonusAtk = 0;
	        
	        if (hpPercent <= 50) {
	            bonusAtk = (50 - hpPercent) / 8; // le da entre 0-6 de atk extra
	        }
	        
	        int effectiveAtk = this.baseAttack + bonusAtk;
	        return effectiveAtk - (target.getDefense() / 2);
	 
	  } 
	        
	@Override
	protected void performUltimate(Character enemy) {
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
		
        int healAmount = (int) (this.maxHp * 0.30); 
        int newHp = Math.min(this.healthPoints + healAmount, this.maxHp);
        
        int actualHealed = newHp - this.healthPoints;
        this.healthPoints = newHp;
        
        System.out.println("Furina healed herself and has now " + this.healthPoints + "/" + this.maxHp);
        
	}
}
