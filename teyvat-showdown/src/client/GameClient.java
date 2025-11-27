package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    
    public static void main(String[] args) {
        String serverAddress = "localhost"; 
        int port = 8080;
        
        System.out.println("Welcome to Teyvat-Showdown, a 1v1 online fighting game!!");
        System.out.println("Connecting to server at " + serverAddress + ":" + port + "...\n");
        
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {
            

            // este hilo se encarga de recibir y enviar mensajes al gestor de las partidas
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) { 
                        System.out.println(line);
                    }
                    System.out.println("\n[Connection closed by server]");
                } catch (IOException e) {
                    System.out.println("\n[Disconnected from server]");
                }
            });
            readerThread.setDaemon(true); 
            readerThread.start();
            
            String input;
            while (scanner.hasNextLine() && !socket.isClosed()) {
                input = scanner.nextLine();
                out.println(input);
            }
            
        } catch (IOException e) {
            System.out.println("Could not connect to server.");
            System.out.println("Make sure the server is running at " + serverAddress + ":" + port);
            e.printStackTrace();
        }
        
        System.out.println("\nClient terminated. Press Enter to exit...");
    }
}