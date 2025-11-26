 package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import game.Character;
import game.Item;




public class OnlineBattle {
	private PlayerHandler player1Handler;
	private PlayerHandler player2Handler;
	private Character p1;
	private Character p2;
	private Character currentCharacter;
    private boolean gameOver;
    private int totalTurns;
    
	public OnlineBattle(PlayerHandler player1, PlayerHandler player2) {
		// TODO Auto-generated constructor stub
		player1Handler = player1;
		player2Handler = player2;
		p1 = player1.getCharacter();
		p2 = player2.getCharacter();
		currentCharacter = (Math.random() < 0.5) ? p1 : p2;
		gameOver = false;
		totalTurns = 0;
	}

	public void start() {
		// TODO Auto-generated method stub
		
		
		if (currentCharacter == p1) {
	        broadcast("Player 1 (" + p1.getName() + ") starts first!");
	    } else {
	        broadcast("Player 2 (" + p2.getName() + ") starts first!");
	    }
	    broadcast("");
		
		while (!gameOver) {
			playTurn();
			
			if (checkGameOver()) {
				gameOver = true;
				endBattle();
				break;
			}
			
			switchTurn();
			totalTurns++;
		}
		
	}
	
	
	public void playTurn() {
		PlayerHandler currentHandler = (currentCharacter== p1) ? player1Handler : player2Handler;
	    PlayerHandler opponentHandler = (currentCharacter== p1) ? player2Handler : player1Handler;
	    Character opponent = (currentCharacter == p1) ? p2 : p1;
	    boolean actionTaken = false;
	    
		currentHandler.sendMessage("Your turn " + currentCharacter.getName());
		opponentHandler.sendMessage(currentCharacter.getName() + "'s turn. Waiting...");
		
		showGameState();
		
		broadcast("\n--- Processing effects ---");
	    boolean skipped = currentCharacter.processEffects();
	    
	    if (skipped) {
	    	currentHandler.sendMessage("You are frozen and skip the turn!");
	        opponentHandler.sendMessage(currentCharacter.getName() + " is FROZEN and skips the turn!");
	        return; 
	    }
	    
	    
	    while (!actionTaken) {
			currentHandler.sendMessage("Choose action:");
			currentHandler.sendMessage("1. Attack");
			currentHandler.sendMessage("2. Ultimate");
			currentHandler.sendMessage("3. Use Item");
			currentHandler.sendMessage("Choice: ");
			
			try {
				String choice = currentHandler.receiveMessage();
				actionTaken = processAction(choice, currentCharacter, opponent, currentHandler, opponentHandler);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				  e.printStackTrace();
				  if (!currentHandler.getSocket().isClosed()) {
			            currentHandler.sendMessage("Your opponent disconnected. You win by default!");
			       } else {
			            opponentHandler.sendMessage("Your opponent disconnected. You win by default!");
			        }
				  	gameOver = true;
			}
		}
		
		
	}
	
	private boolean processAction(String choice, Character attacker, Character defender, PlayerHandler currentHandler, PlayerHandler enemyHandler) {
		
		// solucion (temporal) que he encontrado para reenviar info de la partida a ambos jugadores.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);

		try {
			switch (choice) {
			case "1":
				attacker.attack(defender);
				break;
			case "2":
				attacker.ultimate(defender);
				break;
			case "3":
				return openInventory(attacker, currentHandler, enemyHandler);
			default:
				broadcast("Invalid choice. Try again.");
				return false;
		}

		System.out.flush();
		String output = baos.toString();
		

		if (!output.isEmpty()) {
			String[] lines = output.split("\n");
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
		  broadcast(line);
				}
			}
		}
		
		return true;
		
		} 
		finally {
		System.setOut(old); // Restaurar System.out original
		}
}
	
	private boolean openInventory(Character attacker, PlayerHandler currentHandler, PlayerHandler enemyHandler) {
		List<Item> inv = attacker.getInventory();
		enemyHandler.sendMessage("Your opponent has opened his inventory...");
	    if (inv.isEmpty()) {
	        currentHandler.sendMessage("You ran out of potions...");
	        return false;
	    }

	    currentHandler.sendMessage("Your inventory:");
	    for (int i = 0; i < inv.size(); i++) {
	        Item item = inv.get(i);
	        currentHandler.sendMessage((i + 1) + ". " + item.name() + " (+" + item.getHealAmount() + " HP)");
	    }
	    currentHandler.sendMessage((inv.size() + 1) + ". Cancel");

	    try {
	        String choice = currentHandler.receiveMessage();
	        int selected = Integer.parseInt(choice);
	        if (selected > 0 && selected <= inv.size()) {
	        	String itemName = inv.get(selected - 1).name();
	            attacker.useItem(inv.get(selected - 1));
	            enemyHandler.sendMessage("Your opponent has used a " + itemName);
	            return true;
	        } else {
	            currentHandler.sendMessage("Action cancelled.");
	            enemyHandler.sendMessage("Your opponent has cancelled his action...");
	            return false;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    } catch(NumberFormatException nfe) {
	    	currentHandler.sendMessage("Invalid input, please enter a number.");
	        return false;
	    }
	}

	public boolean checkGameOver() {
		return p1.isDead() || p2.isDead();
	}
	
	public void endBattle() {
		Character winner = p1.isDead() ? p2 : p1;
		Character loser = (winner == p1) ? p2 : p1;
		
		broadcast("-------BATTLE ENDED!--------");
		broadcast("WINNER: " + winner.getName());
		broadcast("LOSER: " + loser.getName());
	       
	    broadcast("Total Game Rounds: " + Math.floor(totalTurns / 2 + 1));
	}
	
	public void showGameState() {
	  broadcast("\nBATTLE STATUS:");
       broadcast("Player 1 (" + p1.getName() + "): HP " + p1.getHP() + "/" + p1.getMaxHp() + 
                  " | Ultimate: " + (p1.getTurnsUntilUltimate() == 0 ? "CHARGED" : p1.getTurnsUntilUltimate() + " turns"));
       broadcast("Player 2 (" + p2.getName() + "): HP " + p2.getHP() + "/" + p2.getMaxHp() + 
                  " | Ultimate: " + (p2.getTurnsUntilUltimate() == 0 ? "CHARGED" : p2.getTurnsUntilUltimate() + " turns"));
	}
	

	public void switchTurn() {
		currentCharacter = (currentCharacter== p1) ? p2 : p1;
	}
	
	public Character getOpponent() {
		return this.currentCharacter == p1 ? p2 : p1;
	}
	
	public void broadcast(String message) {
		player1Handler.sendMessage(message);
		player2Handler.sendMessage(message);
	}

	

}
