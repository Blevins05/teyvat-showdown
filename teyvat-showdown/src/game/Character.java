package game;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// clase base para definir a un personaje
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
	protected ArrayList<Item> inventory = new ArrayList<>();

	// los atributos son: nombre, vida, vidaMaxima, ataque base, defensa, precision de ataque, elemento, turnos restantes hasta ulti, cooldown de la ulti, efectos activos e inventario.

	public Character(String name, int maxHp, int hp, int atk, int def, double precision, Element element, int turnsRemaining, int ultimateCooldown) {
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
		
	    this.inventory = new ArrayList<>();
        inventory.add(Item.SMALL_POTION);
        inventory.add(Item.MEDIUM_POTION);
        inventory.add(Item.LARGE_POTION);
	
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
	
	 public ArrayList<Item> getInventory() {
	    return inventory;
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
	    double randomFactor = 0.9 + Math.random() * 0.2; // 90%-110% (es decir, el ataque puede variar un +-10%)
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
	
	 public void useItem(Item item) {
        if (inventory.contains(item)) {
            this.heal(item);
            inventory.remove(item); 
            System.out.println(item.name() + " consumed!");
            
        } else {
            System.out.println("You don't have that item!");
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
	
	// controla que la ulti se pueda usar (si te equivocas, pasa el turno al contrario, hay que tener cuidado)
	public void ultimate(Character enemy) {
	    if (turnsUntilUltimate > 0) {
	        System.out.println(this.getClass().getSimpleName() + " cant use the ultimate, " + turnsUntilUltimate + "more turn(s) remaining.");
	        return;
	    }

	    performUltimate(enemy);

	    turnsUntilUltimate = ultimateCooldown;
	}
	
	// ejecuta una ultimate del personaje (cada personaje tiene su ulti, por lo que es abstracto)
	protected abstract void performUltimate(Character enemy);
	
	public void applyEffect(Effect effect) {
	    activeEffects.add(effect);
	}
	
	// procesa y aplica los efectos activos sobre los personajes
	public boolean processEffects() {
	    boolean loseTurn = false;
	    Iterator<Effect> iterator = activeEffects.iterator();
	    
	    while(iterator.hasNext()) {
	        Effect e = iterator.next();
	        
	        e.apply(this);
	        
	        if (e instanceof Freeze) {
	            loseTurn = true;
	        }
	        
	        e.decreaseDuration();
	        
	        if (e.isExpired()) {
	            System.out.println(e.getClass().getSimpleName() + " expired on " + this.getName());
	            iterator.remove();
	        }
	    }
	    
	    return loseTurn;
	}
	
}
