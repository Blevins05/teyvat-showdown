package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		Matchmaking matchMaker = new Matchmaking();
		int port = 8080;
		try (ServerSocket ss = new ServerSocket(port)) 
		{
			while(true) {
				try {
					Socket player = ss.accept();
					pool.execute(new PlayerHandler(player, matchMaker));
					
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (IOException ex) {ex.printStackTrace(); pool.shutdown(); }
	}
}
