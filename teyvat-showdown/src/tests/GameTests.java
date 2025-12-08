package tests;
import game.Character;
import game.Eula;
import game.Flins;
import game.Furina;
import game.Item;
import game.Kinich;
import game.Durin;



public class GameTests {

    private static Character c1, c2, c3, c4, c5;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     TEYVAT SHOWDOWN - GAME TESTS       ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Crear todos los personajes
        c1 = new Flins();
        c2 = new Eula();
        c3 = new Kinich();
        c4 = new Durin();
        c5 = new Furina();
        
        // Test 1: Ataque básico
        System.out.println("=== TEST 1: BASIC ATTACK ===");
        System.out.println("Eula HP before: " + c2.getHP());
        c1.attack(c2);
        System.out.println("Eula HP after: " + c2.getHP() + "\n");
        
        // Test 2: Curación con poción
        System.out.println("=== TEST 2: HEALING WITH POTION ===");
        System.out.println("Eula HP before heal: " + c2.getHP());
        c2.heal(Item.LARGE_POTION);
        System.out.println("Eula HP after heal: " + c2.getHP() + "\n");
        
        // Test 3: Kinich ataca a Torch
        System.out.println("=== TEST 3: KINICH vs TORCH ===");
        System.out.println("Torch HP: " + c4.getHP());
        c3.attack(c4);
        c3.attack(c4);
        System.out.println("Torch HP after 2 attacks: " + c4.getHP() + "\n");
        
        // Test 4: Torch ultimate (debe aplicar burn)
        System.out.println("=== TEST 4: TORCH ULTIMATE (BURN) ===");
        c4.reduceCooldown();
        c4.reduceCooldown();
        System.out.println("Kinich HP before ultimate: " + c3.getHP());
        c4.ultimate(c3);
        System.out.println("Kinich HP after ultimate: " + c3.getHP());
        
        // Procesar efectos de quemadura
        System.out.println("\n--- Processing burn effect (2 turns) ---");
        c3.processEffects();
        System.out.println("Kinich HP after turn 1: " + c3.getHP());
        c3.processEffects();
        System.out.println("Kinich HP after turn 2: " + c3.getHP() + "\n");
        
        // Test 5: Kinich ultimate (debe aplicar bloom/poison)
        System.out.println("=== TEST 5: KINICH ULTIMATE (BLOOM) ===");
        c3.reduceCooldown();
        c3.reduceCooldown();
        System.out.println("Furina HP before ultimate: " + c5.getHP());
        c3.ultimate(c5);
        System.out.println("Furina HP after ultimate: " + c5.getHP());
        
        // Procesar efectos de bloom
        System.out.println("\n--- Processing bloom effect (3 turns) ---");
        for (int i = 1; i <= 3; i++) {
            c5.processEffects();
            System.out.println("Furina HP after turn " + i + ": " + c5.getHP());
        }
        System.out.println();
        
        // Test 6: Furina se cura con ultimate
        System.out.println("=== TEST 6: FURINA ULTIMATE (HEAL + CLEANSE) ===");
        System.out.println("Furina HP before ultimate: " + c5.getHP());
        c5.reduceCooldown();
        c5.reduceCooldown();
        c5.reduceCooldown();
        c5.ultimate(c1); 
        System.out.println("Furina HP after ultimate: " + c5.getHP() + "\n");
        
        // Test 7: Eula ultimate (puede congelar)
        System.out.println("=== TEST 7: EULA ULTIMATE (FREEZE) ===");
        c2.reduceCooldown();
        c2.reduceCooldown();
        System.out.println("Flins HP before: " + c1.getHP());
        c2.ultimate(c1);
        System.out.println("Flins HP after: " + c1.getHP());
        
        // Intentar que Flins ataque (puede estar frozen)
        System.out.println("\n--- Checking if Flins is frozen ---");
        boolean frozen = c1.processEffects();
        if (frozen) {
            System.out.println("Flins is FROZEN! Cannot attack.");
        } else {
            System.out.println("Flins is NOT frozen. Can attack.");
            c1.attack(c2);
        }
        System.out.println();
        
        // Test 8: Flins ultimate (crit chance)
        System.out.println("=== TEST 8: FLINS ULTIMATE (CRIT) ===");
        c1.reduceCooldown();
        c1.reduceCooldown();
        System.out.println("Eula HP before: " + c2.getHP());
        c1.ultimate(c2);
        System.out.println("Eula HP after: " + c2.getHP() + "\n");
        
        // Test 9: Torch scaling (múltiples ultimates)
        System.out.println("=== TEST 9: TORCH ATK SCALING ===");
        Character bot = new Kinich();
        System.out.println("Torch initial ATK: " + c4.getBaseAttack());
        
        // 3 ultimates para ver el scaling
        for (int i = 1; i <= 3; i++) {
            c4.reduceCooldown();
            c4.reduceCooldown();
            c4.ultimate(bot);
            System.out.println("After ultimate " + i + " - ATK: " + c4.getBaseAttack());
            bot = new Kinich(); 
        }
        System.out.println();
        
        // Test 10: Estado final de todos
        System.out.println("=== TEST 10: FINAL STATE ===");
        System.out.println("Flins HP: " + c1.getHP() + "/" + c1.getMaxHp());
        System.out.println("Eula HP: " + c2.getHP() + "/" + c2.getMaxHp());
        System.out.println("Kinich HP: " + c3.getHP() + "/" + c3.getMaxHp());
        System.out.println("Torch HP: " + c4.getHP() + "/" + c4.getMaxHp() + " (ATK: " + c4.getBaseAttack() + ")");
        System.out.println("Furina HP: " + c5.getHP() + "/" + c5.getMaxHp());
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     ALL TESTS COMPLETED!               ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

}