package database;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class MatchHistory {
	private static final String XML_FILE = "resources/partidas.xml";
	
	public static void saveMatch(String player1, String c1, String player2, String c2, 
					String resultP1, String resultP2, int hp1, int hp2, int rounds) 
			{
			
			try {
				File xmlFile = new File(XML_FILE);
				Document doc;
				Element root;
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				if (xmlFile.exists()) {
					doc = db.parse(xmlFile);
					root = doc.getDocumentElement();
				} else {
					doc = db.newDocument();
					root = doc.createElement("games");
					doc.appendChild(root);
				}
				
				
				Element game = doc.createElement("game");
				game.setAttribute("date", LocalDate.now().toString());
				game.setAttribute("rounds", String.valueOf(rounds));
				
				Element p1 = doc.createElement("player");
				p1.setAttribute("name", player1);
				p1.setAttribute("character", c1);
				p1.setAttribute("result", resultP1);
				p1.setAttribute("remaining_hp", String.valueOf(hp1));

				Element p2 = doc.createElement("player");
				p2.setAttribute("name", player2);
				p2.setAttribute("character", c2);
				p2.setAttribute("result", resultP2);
				p2.setAttribute("remaining_hp", String.valueOf(hp2));
				
				game.appendChild(p1);
				game.appendChild(p2);
				root.appendChild(game);
				
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
				Transformer transformer = transformerFactory.newTransformer(); 
				DOMSource source = new DOMSource(doc); 
				StreamResult result = new StreamResult(new File(XML_FILE));
				transformer.transform(source, result);
				
				System.out.println("[SERVER] Match saved to " + XML_FILE);
				
			} catch (TransformerException ex) {
				ex.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			}

}
