package game;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class Character {
	protected String name;
	protected Integer healthPoints;
	protected Integer maxHp;
	protected Integer baseAttack;
	protected Integer defense;
	protected double precision;
	protected Element element;
	protected Integer turnsUntilUltimate;
	protected Integer ultimateCooldown;
	protected ArrayList<Effect> activeEffects = new ArrayList<>();


	public Character(String name, int maxHp, int hp, int atk, int def, double precision, Element element, int turnsRemaining, int ultimateCooldown) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.maxHp = maxHp;
		this.healthPoints = hp; 
		this.baseAttack = atk;
		this.defense = def;
		this.precision = precision;
		this.element = element;
		this.turnsUntilUltimate = turnsRemaining;
		this.ultimateCooldown = ultimateCooldown;
		this.activeEffects = new ArrayList<Effect>();
	
	}
	
	// getters
	public String getName() {
		return this.name;
	}
	
	public int getMaxHp() {
	    return this.maxHp;
	}
	
	public int getHP() {
		return this.healthPoints;
		}
	
	public int getBaseAttack() {
		return this.baseAttack;
	}
	
	public int getDefense() {
		return this.defense;
	}
	
	public double getPrecision() {
		return this.precision;
	}
	
	public Element getElement() {
		return element;
	}
	
	public int getTurnsUntilUltimate() {
		return this.turnsUntilUltimate;
	}
	
	public int getUltimateCooldown() {
		return this.ultimateCooldown;
	}

	// setters
	public void setHP(int hp) {
	    this.healthPoints = hp;
	}

	public void setBaseAttack(int atk) {
	    this.baseAttack = atk;
	}

	public void setDefense(int def) {
	    this.defense = def;
	}

	public void setPrecision(double precision) {
	    this.precision = precision;
	}

	public void setElement(Element element) {
	    this.element = element;
	}
	
	public int getBaseDamage(Character target) {
		return this.baseAttack - (target.getDefense() / 2);
	}
	
	public void attack(Character target) {
	    double hitChance = Math.random();
	    if (hitChance > this.precision) {
	        System.out.println(": " + this.getName() + " missed the attack!");
	        this.reduceCooldown(); 
	        return;
	    }
	    
	    int baseDamage = getBaseDamage(target);
	    double randomFactor = 0.9 + Math.random() * 0.2; // 90%-110%
	    int damage = (int) (baseDamage * randomFactor);
	    
	    System.out.println(": " + this.getName() + " attacks " + target.getName() + " and deals " + damage + " damage");
	    target.takeDamage(damage);
	    
	    this.reduceCooldown(); 
	}
	
	public void takeDamage(Integer damage) {
	    this.healthPoints -= damage;
	    if (this.isDead()) {
	        System.out.println(":" + this.getName() + " is dead!");
	    } else {
	        System.out.println(": " + this.getName() + " took " + damage + " damage! [HP: " + this.healthPoints + "]");
	    }
	}
	
	public void heal(Item item) {
		this.healthPoints = Math.min(this.healthPoints + item.getHealAmount(), this.maxHp);
		  System.out.println(":" + this.getName() + " used " 
		+ item.name() + " and recovers " + item.getHealAmount() + " HP!");
	}
	
	public void reduceCooldown() {
	    if (turnsUntilUltimate > 0) {
	        turnsUntilUltimate--;
	    }
	}
	
	public boolean isDead() {
		return this.healthPoints <= 0;
	}
	
	public void ultimate(Character enemy) {
	    if (turnsUntilUltimate > 0) {
	        System.out.println(this.getClass().getSimpleName() + " cant use the ultimate, " + turnsUntilUltimate + "more turn(s) remaining.");
	        return;
	    }

	    performUltimate(enemy);

	    turnsUntilUltimate = ultimateCooldown;
	}

	protected abstract void performUltimate(Character enemy);
	
	public void applyEffect(Effect effect) {
	    activeEffects.add(effect);
	    effect.apply(this); 
	}
	
	public boolean processEffects() {
	    boolean loseTurn = false;
	    Iterator<Effect> iterator = activeEffects.iterator();
	    
	    while(iterator.hasNext()) {
	        Effect e = iterator.next();
	        
	        // Aplicar el efecto
	        e.apply(this);
	        
	        // Si está congelado, pierde turno
	        if (e instanceof Freeze) {
	            loseTurn = true;
	        }
	        
	        // Reducir duración
	        e.decreaseDuration();
	        
	        // Eliminar si expiró
	        if (e.isExpired()) {
	            iterator.remove();
	        }
	    }
	    
	    return loseTurn;
	}
	
}
