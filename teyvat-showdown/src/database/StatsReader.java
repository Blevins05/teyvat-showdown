package database;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

// Un simple contador de estadisticas usando un arbol DOM: Cuenta V/D por jugador y personaje(s) mas jugados
// TODO: Arreglar esta mierda (los atributos no estan bien matcheados con el xml, tengo que cambiarlos)

public class StatsReader {
    private static final String file = "resources/partidas.xml";
    public static Map<String, Integer> getWins() {
        Map<String, Integer> wins = new HashMap<>();
        
        try {
            File xmlFile = new File(file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            
            NodeList matches = doc.getElementsByTagName("match");
            
            for (int i = 0; i < matches.getLength(); i++) {
                Element match = (Element) matches.item(i);
                String winner = match.getAttribute("winnerName");
                wins.put(winner, wins.getOrDefault(winner, 0) + 1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return wins;
    }
    
    public static Map<String, Integer> getLosses() {
        Map<String, Integer> losses = new HashMap<>();
        
        try {
            File xmlFile = new File(file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            
            NodeList matches = doc.getElementsByTagName("match");
            
            for (int i = 0; i < matches.getLength(); i++) {
                Element match = (Element) matches.item(i);
                String loser = match.getAttribute("loserName");
                losses.put(loser, losses.getOrDefault(loser, 0) + 1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return losses;
    }
    
    public static Map<String, Integer> getMostPlayedCharacters() {
        Map<String, Integer> characters = new HashMap<>();
        
        try {
            File xmlFile = new File(file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            
            NodeList matches = doc.getElementsByTagName("match");
            
            for (int i = 0; i < matches.getLength(); i++) {
                Element match = (Element) matches.item(i);
                String winnerChar = match.getAttribute("winnerCharacter");
                String loserChar = match.getAttribute("loserCharacter");
                
                characters.put(winnerChar, characters.getOrDefault(winnerChar, 0) + 1);
                characters.put(loserChar, characters.getOrDefault(loserChar, 0) + 1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return characters;
    }
    
    public static void printStats() {
        System.out.println("=== PLAYER STATS ===");
        
        Map<String, Integer> wins = getWins();
        Map<String, Integer> losses = getLosses();
        
        System.out.println("\n--- WINS ---");
        wins.forEach((player, count) -> System.out.println(player + ": " + count));
        
        System.out.println("\n--- LOSSES ---");
        losses.forEach((player, count) -> System.out.println(player + ": " + count));
        
        System.out.println("\n--- MOST PLAYED CHARACTERS ---");
        Map<String, Integer> chars = getMostPlayedCharacters();
        chars.forEach((character, count) -> System.out.println(character + ": " + count));
    }
}