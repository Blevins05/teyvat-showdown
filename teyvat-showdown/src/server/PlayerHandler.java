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
import game.Torch;

public class PlayerHandler implements Runnable {
	
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
			out = new PrintWriter(new OutputStreamWriter(player.getOutputStream()));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void run() { 	
		// TODO Auto-generated method stub
		try {

            sendMessage("Connected to server!\n");
            flushWriter();
            selectedCharacter = selectCharacter();
            
            sendMessage("\nYou selected: " + selectedCharacter.getName());
            sendMessage("Waiting for opponent...\n");
            flushWriter();;
            
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
        sendMessage("   HP: 110 | ATK: 23 | DEF: 20");
        sendMessage("");
        sendMessage("3. Kinich (Dendro) - Poisoner");
        sendMessage("   HP: 98 | ATK: 23 | DEF: 20");
        sendMessage("");
        sendMessage("4. Torch (Pyro) - Scaling");
        sendMessage("   HP: 105 | ATK: 23 | DEF: 20");
        sendMessage("");
        sendMessage("5. Furina (Hydro) - Support/Healer");
        sendMessage("   HP: 125 | ATK: 20 | DEF: 24");
        sendMessage("");
        sendMessage("Enter choice (1-5): ");
        flushWriter();
        String choice = in.readLine();

        switch (choice) {
            case "1": return new Flins();
            case "2": return new Eula();
            case "3": return new Kinich();
            case "4": return new Torch();
            case "5": return new Furina();
            default:
                sendMessage("Invalid input. Default character is Flins");
                flushWriter();
                return new Flins();
        }

	}
	
	public void sendMessage(String message) {
        out.println(message);
    }
		
    public String receiveMessage() throws IOException {
        return in.readLine();
    }
    
    public void flushWriter() {
    	out.flush();
    }
    
    public Character getCharacter() {
        return selectedCharacter;
    }
    
    public Socket getSocket() {
        return player;
    }
    
}
