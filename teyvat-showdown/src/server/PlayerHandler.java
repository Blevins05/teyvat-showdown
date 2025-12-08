package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import game.Character;
import game.Eula;
import game.Flins;
import game.Furina;
import game.Kinich;
import game.Durin;

// clase manejador de un jugador (viene a ser el "AtenderPeticion")
public class PlayerHandler implements Runnable {
	
	private String playerName;
	private Socket player;
	private Matchmaking matchMaking;
	private BufferedReader in;
	private PrintWriter out;
	private Character selectedCharacter;
	
	public PlayerHandler(Socket player, Matchmaking m) {
		this.player = player;
		this.matchMaking = m;
		
		try 
		{
			in = new BufferedReader(new InputStreamReader(player.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(player.getOutputStream()), true); // hace auto flush gracias al segundo parametro
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void run() { 	
		try {
            sendMessage("Connected to server!\n");
                     
            sendMessage("Please enter your name");
            String playerName = in.readLine();
            this.playerName = playerName;
            sendMessage("Welcome " + this.playerName);
            
            selectedCharacter = selectCharacter();
            
            sendMessage(playerName + " selected: " + selectedCharacter.getName());
            sendMessage("Waiting for opponent...\n");
            
            matchMaking.addPlayer(this);
            
        } catch (IOException e) {
            System.out.println("Player disconnected.");
            e.printStackTrace();
        } 

	}
	
	
	private Character selectCharacter() throws IOException {
		sendMessage("Select your character:");
        sendMessage("1. Flins (Electro) - Crit DPS");
        sendMessage("   HP: 100 | ATK: 26 | DEF: 17");
        sendMessage("");
        sendMessage("2. Eula (Cryo) - Control/Freeze");
        sendMessage("   HP: 120 | ATK: 23 | DEF: 20");
        sendMessage("");
        sendMessage("3. Kinich (Dendro) - Poisoner");
        sendMessage("   HP: 105 | ATK: 23 | DEF: 20");
        sendMessage("");
        sendMessage("4. Durin (Pyro) - Scaling");
        sendMessage("   HP: 105 | ATK: 25 | DEF: 20");
        sendMessage("");
        sendMessage("5. Furina (Hydro) - Support/Healer");
        sendMessage("   HP: 125 | ATK: 20 | DEF: 24");
        sendMessage("");
        sendMessage("Enter choice (1-5): ");
        String choice = in.readLine();

        switch (choice) {
            case "1": return new Flins();
            case "2": return new Eula();
            case "3": return new Kinich();
            case "4": return new Durin();
            case "5": return new Furina();
            default:
                sendMessage("Invalid input. Default character is Flins");
                return new Flins();
        }

	}
	
	public void sendMessage(String message) {
        out.println(message);
    }
		
    public String receiveMessage() throws IOException {
        return in.readLine();
    }
 
    
    public Character getCharacter() {
        return selectedCharacter;
    }
    
    public String getPlayerName() {
    	return playerName;
    }
    
    public Socket getSocket() {
        return player;
    }
    
}
