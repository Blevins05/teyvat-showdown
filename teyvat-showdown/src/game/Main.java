package game;

import java.util.Scanner;

// main para jugar una partida (1 solo jugador) esta obsoleto también
public class Main {
    
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        showWelcome();
        
        System.out.println("PLAYER 1 - Choose your fighter:");
        Character player1 = selectCharacter();
        System.out.println("\nPlayer 1 selected: " + player1.getName() + " [" + player1.getElement() + "]\n");
        
        System.out.println("PLAYER 2 - Choose your fighter:");
        Character player2 = selectCharacter();
        System.out.println("\nPlayer 2 selected: " + player2.getName() + " [" + player2.getElement() + "]\n");

        System.out.println("Press Enter to start the battle...");
        scanner.nextLine();
        
        Battle battle = new Battle(player1, player2);
        battle.start();
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  Thanks for playing TEYVAT SHOWDOWN!   ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    private static void showWelcome() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       TEYVAT SHOWDOWN                  ║");
        System.out.println("║       Elemental Battle Arena           ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        System.out.println("A turn-based battle game with");
        System.out.println("elemental reactions and abilities");
        System.out.println();
    }
    // las estadisticas de los pjs no están actualizadas
    private static Character selectCharacter() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("1. Flins (Electro) - Crit DPS");
        System.out.println("   HP: 100 | ATK: 32 | DEF: 17 | Precision: 85%");
        System.out.println("   Ultimate: Tormenta Electrica (25% crit chance)");
        System.out.println();
        
        System.out.println("2. Eula (Cryo) - Control/Freeze");
        System.out.println("   HP: 110 | ATK: 23 | DEF: 20 | Precision: 85%");
        System.out.println("   Ultimate: Congelacion Artica (30% freeze)");
        System.out.println();
        
        System.out.println("3. Kinich (Dendro) - DoT Specialist");
        System.out.println("   HP: 98 | ATK: 23 | DEF: 20 | Precision: 92%");
        System.out.println("   Ultimate: Esporas Toxicas (poison 3 turns)");
        System.out.println();
        
        System.out.println("4. Torch (Pyro) - Scaling Bruiser");
        System.out.println("   HP: 105 | ATK: 23 | DEF: 20 | Precision: 85%");
        System.out.println("   Ultimate: Explosion Ignea (burn + ATK boost)");
        System.out.println();
        
        System.out.println("5. Furina (Hydro) - Healer/Support");
        System.out.println("   HP: 115 | ATK: 20 | DEF: 23 | Precision: 90%");
        System.out.println("   Ultimate: Ola Vital (heal + cleanse)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.print("Enter choice (1-5): ");
        
        int choice = -1;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Defaulting to Flins.");
            return new Flins();
        }
        
        switch (choice) {
            case 1: return new Flins();
            case 2: return new Eula();
            case 3: return new Kinich();
            case 4: return new Durin();
            case 5: return new Furina();
            default:
                System.out.println("Invalid choice! Defaulting to Flins.");
                return new Flins();
        }
    }
}