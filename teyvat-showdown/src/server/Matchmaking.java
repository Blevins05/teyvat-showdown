package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Matchmaking {
	
	private BlockingQueue<PlayerHandler> queue = new LinkedBlockingQueue<>();
	
	public void addPlayer(PlayerHandler pl) {
		try {
			System.out.println("New Player added to matchmaking queue");
			queue.add(pl);
			
			if (queue.size() >= 2) {
				PlayerHandler player1 = queue.take();
				PlayerHandler player2 = queue.take();
				
				  System.out.println("Match found! Starting battle...");
	              System.out.println("Player 1: " + player1.getCharacter().getName());
	              System.out.println("Player 2: " + player2.getCharacter().getName());
	              
	              
	              startBattle(player1, player2);
	                
			}

		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		
	}

	private void startBattle(PlayerHandler player1, PlayerHandler player2) {
		// TODO Auto-generated method stub
		
		// basicamente esto crea un thread, despues dentro crea una instancia de partidaOnline y empieza una nueva partida
		Thread battleThread = new Thread(() -> { 
			OnlineBattle ob = new OnlineBattle(player1, player2);
			ob.start();
		});
		battleThread.start();
	}
	
	
}
