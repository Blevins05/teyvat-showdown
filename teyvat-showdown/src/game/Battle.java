package game;

import java.util.List;
import java.util.Scanner;

public class Battle {
	private Character player1;
	private Character player2;
	private Character currentCharacterTurn;
	private int totalTurns;
	private boolean gameFinished;
	private Scanner scanner;
	
	public Battle(Character char1, Character char2) {
		this.player1 = char1;
		this.player2 = char2;
		currentCharacterTurn = char1;
		totalTurns = 0;
		gameFinished = false;
		scanner = new Scanner(System.in);
	}
	
	
	public void start() {
	   System.out.println("╔════════════════════════════════════════╗");
       System.out.println("║       BATTLE STARTED!                  ║");
       System.out.println("╚════════════════════════════════════════╝");
       System.out.println("The battle between " + player1.getName() + ", " + player1.getElement() + " AND " + player2.getName() + ", " + player2.getElement() + " has begun!" );
       System.out.println();
       
		while(!gameFinished) {
			
			playTurn();  	
			
			if (checkGameOver()) {
				gameFinished = true;
				endBattle();
				break;
			}
			
			switchTurn();
			totalTurns++;		
		}
	}
	
	public void playTurn() {
		Character enemy = getEnemy();
		System.out.println("Your turn " + currentCharacterTurn.getName());
		pressEnterToContinue();
		
	    System.out.println("\n--- Processing effects ---");
	    boolean skipped = currentCharacterTurn.processEffects();
	    
	    if (skipped) {
	        System.out.println(currentCharacterTurn.getName() + " is FROZEN and skips the turn!");
	        pressEnterToContinue();
	        return; 
	    }
	    
		displayGameStats();
		pressEnterToContinue();
		
		  
	    boolean actionTaken = false;
		
	    while (!actionTaken) { // hasta que el pj haga algo "util"
	        System.out.println(": " + getCurrentPlayerLabel() + " (" + currentCharacterTurn.getName() + ") - Choose action:");
	        System.out.println("1. Attack");
	        System.out.println("2. Ultimate" + (currentCharacterTurn.getTurnsUntilUltimate() > 0 ? 
	            " (Cooldown: " + currentCharacterTurn.getTurnsUntilUltimate() + " turns)" : " (CHARGED!)"));
	        System.out.println("3. Use Item");
	        System.out.print("Choice: ");
	        
	        int opt = Integer.parseInt(scanner.nextLine());
	        
	        switch (opt) {
	            case 1:
	                currentCharacterTurn.attack(enemy);
	                actionTaken = true; 
	                break;
	            case 2:
	                currentCharacterTurn.ultimate(enemy);
	                actionTaken = true; 
	                break;
	            case 3: 
	                actionTaken = openInventory();
	                break;
	            default:
	                System.out.println("Invalid option. Please try again.");
	        }
	    }
		pressEnterToContinue();
	}
	
	public Character getEnemy() {
		return this.currentCharacterTurn == player1 ? player2 : player1;
	}
	
	public void switchTurn() {
		currentCharacterTurn = (currentCharacterTurn == player1) ? player2 : player1;
	}
	
	public boolean openInventory() {
		List<Item> inv = currentCharacterTurn.getInventory();
		
		if (inv.isEmpty()) {
			System.out.println("You ran out of potions...");
			return false;
		}
		
		System.out.println("Your current inventory: ");
		 for (int i = 0; i < inv.size(); i++) {
            Item item = inv.get(i);
            System.out.println((i + 1) + ". " + item.name() + " (+" + item.getHealAmount() + " HP)");
		 }
		 System.out.println((inv.size() + 1) + ". Cancel");
		  
		int choice = Integer.parseInt(scanner.nextLine()); 
        scanner.nextLine();
        
        if (choice > 0 && choice <= inv.size()) {
            Item selectedItem = inv.get(choice - 1);
            currentCharacterTurn.useItem(selectedItem);
            return true; 
        } else {
            System.out.println("Action cancelled.");
            return false;
        }
		
	}
	
	public boolean checkGameOver() {
		return player1.isDead() || player2.isDead();
	}
	
	public void endBattle() {
		Character winner = player1.isDead() ? player2 : player1;
		Character loser = (winner == player1) ? player2 : player1;
		
		   System.out.println("\n╔════════════════════════════════════════╗");
	       System.out.println("║       BATTLE ENDED!                    ║");
	       System.out.println("╚════════════════════════════════════════╝");
	       System.out.println("WINNER: " + winner.getName() + " with " + winner.getHP() + " HP remaining!");
	       System.out.println("LOSER: " + loser.getName());
	       
	       System.out.println("Total Game Rounds: " + Math.floor(totalTurns / 2));
	}
	
	 private String getCurrentPlayerLabel() {
	        return (currentCharacterTurn == player1) ? "PLAYER 1" : "PLAYER 2";
	    }
	
	public void displayGameStats() {
		 System.out.println("BATTLE STATUS:");
         System.out.print(player1.getName() + " HP: " + player1.getHP() + "/" + player1.getMaxHp());
         System.out.print("Ultimate: " + (player1.getTurnsUntilUltimate() == 0 ? "CHARGED" : player1.getTurnsUntilUltimate() + " turns"));
         System.out.println("Items: " + player1.getInventory().size());
        
       
         System.out.print(player2.getName() + " HP: " + player2.getHP() + "/" + player2.getMaxHp());
         System.out.print((player2.getTurnsUntilUltimate() == 0 ? "CHARGED" : player2.getTurnsUntilUltimate() + " turns"));
         System.out.println(player2.getInventory().size());
	}
	
	public void pressEnterToContinue() {
		System.out.println("Press enter to continue...");
		scanner.nextLine();
	}
}
