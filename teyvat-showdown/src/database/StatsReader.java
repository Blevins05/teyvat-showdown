package database;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

// Un simple contador de estadisticas usando un arbol DOM: Cuenta V/D por jugador y personaje mas jugado

public class StatsReader {
	private static final String file = "resources/partidas.xml";
	
	private static Document loadDocument() throws Exception {
	    File xmlFile = new File(file);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(xmlFile);
	}

	public static Map<String, Integer> getWins() {
	    Map<String, Integer> wins = new HashMap<>();

	    try {
	        Document doc = loadDocument();
	        NodeList games = doc.getElementsByTagName("game");

	        for (int i = 0; i < games.getLength(); i++) {
	            Element game = (Element) games.item(i);
	            NodeList players = game.getElementsByTagName("player");

	            for (int j = 0; j < players.getLength(); j++) {
	                Element player = (Element) players.item(j);

	                if ("winner".equals(player.getAttribute("result"))) {
	                    String name = player.getAttribute("name");
	                    wins.put(name, wins.getOrDefault(name, 0) + 1);
	                }
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return wins;
	}

	public static Map<String, Integer> getLosses() {
	    Map<String, Integer> losses = new HashMap<>();

	    try {
	        Document doc = loadDocument();
	        NodeList games = doc.getElementsByTagName("game");

	        for (int i = 0; i < games.getLength(); i++) {
	            Element game = (Element) games.item(i);
	            NodeList players = game.getElementsByTagName("player");

	            for (int j = 0; j < players.getLength(); j++) {
	                Element player = (Element) players.item(j);

	                if ("loser".equals(player.getAttribute("result"))) {
	                    String name = player.getAttribute("name");
	                    losses.put(name, losses.getOrDefault(name, 0) + 1);
	                }
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return losses;
	}

	public static Map<String, Map<String, Integer>> getCharactersPerPlayer() {
	    Map<String, Map<String, Integer>> charsPerPlayer = new HashMap<>();

	    try {
	        Document doc = loadDocument();
	        NodeList games = doc.getElementsByTagName("game");

	        for (int i = 0; i < games.getLength(); i++) {
	            Element game = (Element) games.item(i);
	            NodeList players = game.getElementsByTagName("player");

	            for (int j = 0; j < players.getLength(); j++) {
	                Element player = (Element) players.item(j);

	                String name = player.getAttribute("name");
	                String character = player.getAttribute("character");

	                charsPerPlayer.putIfAbsent(name, new HashMap<>());
	                Map<String, Integer> map = charsPerPlayer.get(name);

	                map.put(character, map.getOrDefault(character, 0) + 1);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return charsPerPlayer;
	}

	public static String getMainCharacter(String playerName, Map<String, Map<String, Integer>> charsPerPlayer) {
	    if (!charsPerPlayer.containsKey(playerName)) return "";

	    String main = "";
	    int max = 0;

	    for (Map.Entry<String, Integer> entry : charsPerPlayer.get(playerName).entrySet()) {
	        if (entry.getValue() > max) {
	            main = entry.getKey();
	            max = entry.getValue();
	        }
	    }

	    return main;
	}

}