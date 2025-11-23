package server;

import game.Character;




public class OnlineBattle {
	private PlayerHandler player1Handler;
	private PlayerHandler player2Handler;
	private Character p1;
	private Character p2;
	private Character currentTurn;
    private boolean gameOver;
	
	public OnlineBattle(PlayerHandler player1, PlayerHandler player2) {
		// TODO Auto-generated constructor stub
		player1Handler = player1;
		player2Handler = player2;
		p1 = player1.getCharacter();
		p2 = player2.getCharacter();
		currentTurn = (Math.random() < 0.5) ? p1 : p2;
		gameOver = false;
		
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

}
