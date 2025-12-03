package client;

import javax.swing.*;

import game.Item;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientInterface extends JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Componentes GUI
    private JProgressBar playerHpBar, enemyHpBar;
    private JLabel playerNameLabel, enemyNameLabel;
    private JLabel playerSprite, enemySprite;
    private JTextArea battleLog;
    private JButton attackBtn, ultimateBtn, itemBtn;
    private JPanel buttonPanel;
    private java.util.List<Item> inventory = java.util.List.of(Item.SMALL_POTION, Item.MEDIUM_POTION, Item.LARGE_POTION);
    private String selectedCharacterName;
    private String playerName;
    private String opponentCharacterName = null;
    
    // HP inicial de cada personaje
    private int getMaxHP(String characterName) {
        switch(characterName) {
            case "Flins": return 100;
            case "Eula": return 115;
            case "Kinich": return 98;
            case "Durin": return 105;
            case "Furina": return 125;
            default: return 100;
        }
    }

    public ClientInterface(String serverAddress, int port) {
        // Conectar al servidor
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ No se pudo conectar al servidor\n" + serverAddress + ":" + port);
            System.exit(0);
        }

        // Pedir nombre
        playerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (playerName == null || playerName.isEmpty()) playerName = "Player";
        out.println(playerName);

        // Selección de personaje
        selectedCharacterName = selectCharacter();
        out.println(getCharacterIndex(selectedCharacterName));


        initGUI();
        setTitle("Teyvat Showdown - " + playerName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);

        // Hilo para escuchar servidor
        new Thread(this::listenServer).start();
    }

    private String selectCharacter() {
        String[] options = {
            "Flins (ELECTRO)",
            "Eula (CRYO)", 
            "Kinich (DENDRO)",
            "Durin (PYRO)",
            "Furina (HYDRO)"
        };
        
        int choice = JOptionPane.showOptionDialog(
            null,
            "SELECT YOUR FIGHTER",
            "Character Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == -1) choice = 0; // Default
        
        String[] names = {"Flins", "Eula", "Kinich", "Durin", "Furina"};
        return names[choice];
    }

    private int getCharacterIndex(String name) {
        switch (name) {
            case "Flins": return 1;
            case "Eula": return 2;
            case "Kinich": return 3;
            case "Durin": return 4;
            case "Furina": return 5;
            default: return 1;
        }
    }

    private void initGUI() {
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(new Color(30, 30, 40));

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setBackground(new Color(30, 30, 40));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JPanel playerHPPanel = new JPanel(new BorderLayout(5, 5));
        playerHPPanel.setBackground(new Color(30, 30, 40));
        
        playerNameLabel = new JLabel("YOU (" + selectedCharacterName + ")", SwingConstants.LEFT);
        playerNameLabel.setForeground(new Color(0, 200, 255));
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        int myMaxHP = getMaxHP(selectedCharacterName);
        playerHpBar = new JProgressBar(0, myMaxHP);
        playerHpBar.setValue(myMaxHP);
        playerHpBar.setString(myMaxHP + "/" + myMaxHP + " HP");
        playerHpBar.setStringPainted(true);
        playerHpBar.setForeground(new Color(50, 200, 50));
        playerHpBar.setBackground(new Color(50, 50, 60));
        playerHpBar.setPreferredSize(new Dimension(300, 30));
        playerHpBar.setFont(new Font("Arial", Font.BOLD, 14));
        
        playerHPPanel.add(playerNameLabel, BorderLayout.NORTH);
        playerHPPanel.add(playerHpBar, BorderLayout.CENTER);

        JPanel enemyHPPanel = new JPanel(new BorderLayout(5, 5));
        enemyHPPanel.setBackground(new Color(30, 30, 40));
        
        enemyNameLabel = new JLabel("OPPONENT", SwingConstants.RIGHT);
        enemyNameLabel.setForeground(new Color(255, 50, 100));
        enemyNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        enemyHpBar = new JProgressBar(0, 100);
        enemyHpBar.setValue(100);
        enemyHpBar.setString("100/100 HP");
        enemyHpBar.setStringPainted(true);
        enemyHpBar.setForeground(new Color(255, 50, 100));
        enemyHpBar.setBackground(new Color(50, 50, 60));
        enemyHpBar.setPreferredSize(new Dimension(300, 30));
        enemyHpBar.setFont(new Font("Arial", Font.BOLD, 14));
        
        enemyHPPanel.add(enemyNameLabel, BorderLayout.NORTH);
        enemyHPPanel.add(enemyHpBar, BorderLayout.CENTER);

        topPanel.add(playerHPPanel);
        topPanel.add(enemyHPPanel);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 50, 0));
        centerPanel.setBackground(new Color(100, 120, 140));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        playerSprite = new JLabel("", SwingConstants.CENTER);
        loadSprite(playerSprite, selectedCharacterName, new Color(0, 200, 255));

        enemySprite = new JLabel("⚔", SwingConstants.CENTER);
        enemySprite.setFont(new Font("Arial", Font.BOLD, 100));
        enemySprite.setForeground(new Color(255, 50, 100));
        enemySprite.setOpaque(false);

        centerPanel.add(playerSprite);
        centerPanel.add(enemySprite);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(30, 30, 40));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(new Color(30, 30, 40));

        attackBtn = createButton("ATTACK", new Color(200, 50, 50));
        ultimateBtn = createButton("ULTIMATE", new Color(255, 180, 0));
        itemBtn = createButton("ITEMS", new Color(50, 150, 255));

        attackBtn.addActionListener(e -> sendAction("1", "ATTACK"));
        ultimateBtn.addActionListener(e -> sendAction("2", "ULTIMATE"));
        itemBtn.addActionListener(e -> openInventoryDialog());


        buttonPanel.add(attackBtn);
        buttonPanel.add(ultimateBtn);
        buttonPanel.add(itemBtn);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        // Battle log
        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(20, 20, 30));
        battleLog.setForeground(new Color(0, 255, 100));
        battleLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(battleLog);
        scroll.setPreferredSize(new Dimension(900, 150));
        bottomPanel.add(scroll, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        setButtonsEnabled(false);
    }

    private void loadSprite(JLabel label, String characterName, Color fallbackColor) {
        try {
            String path = "img/" + characterName.toLowerCase() + ".png";
            System.out.println("Intentando cargar: " + path); 
            
            ImageIcon icon = new ImageIcon(path);
            
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(img));
                label.setText(null); 
                System.out.println("Imagen cargada: " + characterName);
            } else {
                System.out.println("NO se cargó la imagen: " + path); 
                label.setIcon(null);
                label.setText("⚔");
                label.setFont(new Font("Arial", Font.BOLD, 100));
                label.setForeground(fallbackColor);
            }
        } catch (Exception e) {
            System.out.println("ERROR cargando: " + characterName + " - " + e.getMessage()); 
            label.setIcon(null);
            label.setText("⚔");
            label.setFont(new Font("Arial", Font.BOLD, 100));
            label.setForeground(fallbackColor);
        }
        label.setOpaque(false);
        label.setBorder(null);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        return btn;
    }

    private void sendAction(String action, String actionName) {
        out.println(action);
        setButtonsEnabled(false);
        log(">>> YOU: " + actionName);
    }

    private void setButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            attackBtn.setEnabled(enabled);
            ultimateBtn.setEnabled(enabled);
            itemBtn.setEnabled(enabled);
        });
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            battleLog.append(message + "\n");
            battleLog.setCaretPosition(battleLog.getDocument().getLength());
        });
    }

    private void listenServer() {
        try {
            String line;
            
            while ((line = in.readLine()) != null) {
                String msg = line;
                log(msg);

                // Detectar personaje enemigo en CADA mensaje hasta que lo encuentre
                detectAndLoadOpponent(msg);
                
                // Parsear y actualizar HP
                updateHPBars(msg);

                // Activar botones en tu turno
                if (msg.contains("Your turn") || msg.contains("Choose action:")) {
                    setButtonsEnabled(true);
                }
                
                // Desactivar botones cuando es turno del otro
                if (msg.contains("Waiting...") || msg.contains("turn. Waiting")) {
                    setButtonsEnabled(false);
                }

                // Game over
                if (msg.contains("BATTLE ENDED") || msg.contains("WINNER")) {
                    setButtonsEnabled(false);
                }
            }
        } catch (IOException e) {
            log("\n❌ Disconnected from server");
        }
    }

    private void detectAndLoadOpponent(String message) {
        if (enemySprite.getIcon() != null) return;
        
        String[] chars = {"Flins", "Eula", "Kinich", "Durin", "Furina"};
        
        for (String c : chars) {
            if (message.contains("(" + c + ")") && !c.equals(selectedCharacterName)) {
                String opponentChar = c;
                opponentCharacterName = c;
                
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Detectado oponente: " + opponentChar);
                    loadSprite(enemySprite, opponentChar, new Color(255, 50, 100));
                    enemyNameLabel.setText("OPPONENT (" + opponentChar + ")");
                    
                    int enemyMaxHP = getMaxHP(opponentChar);
                    enemyHpBar.setMaximum(enemyMaxHP);
                    enemyHpBar.setValue(enemyMaxHP);
                    enemyHpBar.setString(enemyMaxHP + "/" + enemyMaxHP + " HP");
                });
                return; 
            }
        }
    }
    
    private void updateHPBars(String message) {
        if (!message.contains("HP") || !message.contains("/")) return;
        
        try {
            boolean isMyHP = message.contains(playerName) || message.contains("(" + selectedCharacterName + ")");
            boolean isEnemyHP = opponentCharacterName != null && 
                               (message.contains("(" + opponentCharacterName + ")"));
            
            String hpPart = message.split("HP")[1].split("\\|")[0].trim();
            String[] hpValues = hpPart.split("/");
            int currentHP = Integer.parseInt(hpValues[0].trim());
            int maxHP = Integer.parseInt(hpValues[1].trim());
            
            if (isMyHP) {
                SwingUtilities.invokeLater(() -> {
                    playerHpBar.setMaximum(maxHP);
                    playerHpBar.setValue(currentHP);
                    playerHpBar.setString(currentHP + "/" + maxHP + " HP");
                    
                    // Cambiar color según HP restante
                    if (currentHP > maxHP * 0.5) {
                        playerHpBar.setForeground(new Color(50, 200, 50)); // Verde
                    } else if (currentHP > maxHP * 0.25) {
                        playerHpBar.setForeground(new Color(255, 180, 0)); // Amarillo
                    } else {
                        playerHpBar.setForeground(new Color(255, 50, 50)); // Rojo
                    }
                });
            } else if (isEnemyHP) {
                SwingUtilities.invokeLater(() -> {
                    enemyHpBar.setMaximum(maxHP);
                    enemyHpBar.setValue(currentHP);
                    enemyHpBar.setString(currentHP + "/" + maxHP + " HP");
                    
                    // Cambiar color según HP restante
                    if (currentHP > maxHP * 0.5) {
                        enemyHpBar.setForeground(new Color(50, 200, 50)); // Rosa
                    } else if (currentHP > maxHP * 0.25) {
                        enemyHpBar.setForeground(new Color(255, 180, 0)); // Naranja
                    } else {
                        enemyHpBar.setForeground(new Color(255, 50, 50)); // Rojo oscuro
                    }
                });
            }
        } catch (Exception e) {
            // Si falla el parseo, no pasa nada
        }
    }
    
    private void openInventoryDialog() {
        if (inventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no items left!", "Inventory", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(inventory.size() + 1, 1, 5, 5));
        panel.setBackground(new java.awt.Color(30, 30, 40));

        for (Item item : inventory) {
            JButton btn = new JButton(item.name() + " (+" + item.getHealAmount() + " HP)");
            btn.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            btn.setBackground(new java.awt.Color(50, 150, 255));
            btn.setForeground(java.awt.Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                out.println("3:" + item.name());
                log(">>> YOU: used " + item.name() + " (+" + item.getHealAmount() + " HP)");
                setButtonsEnabled(false);
                SwingUtilities.getWindowAncestor(panel).dispose(); 
            });
            panel.add(btn);
        }

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        cancelBtn.setBackground(new java.awt.Color(150, 50, 50));
        cancelBtn.setForeground(java.awt.Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> SwingUtilities.getWindowAncestor(panel).dispose());
        panel.add(cancelBtn);

        JOptionPane.showMessageDialog(this, panel, "Inventory", JOptionPane.PLAIN_MESSAGE);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new ClientInterface("localhost", 8080));
    }
}