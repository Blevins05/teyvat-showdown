package client; 

import javax.swing.*;
import game.Item; 
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage; 
import java.util.LinkedHashMap;

public class ClientInterface extends JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String playerName;
    private String selectedCharacterName;
    private String opponentCharacterName = null;
    private Map<String, Integer> maxHPs = new HashMap<>(); 
    private List<Item> inventory = new ArrayList<>(); 
    
    private final Map<String, Integer> CHARACTERS = new LinkedHashMap<>(); 

    private JProgressBar playerHpBar, enemyHpBar;
    private JLabel playerNameLabel, enemyNameLabel;
    private JLabel playerSprite, enemySprite;
    private JTextArea battleLog;
    private JButton attackBtn, ultimateBtn, itemBtn;

    private static final Color BG_COLOR = new Color(30, 30, 40);
    private static final Color LOG_COLOR = new Color(20, 20, 30);
    
    private class BackgroundPanel extends JPanel {
        private Image bgImage;

        public BackgroundPanel(String imagePath) {
            try {
                bgImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Error cargando la imagen de fondo: " + imagePath);
                bgImage = null;
            }
            setLayout(new BorderLayout()); 
            setOpaque(false); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                 g.setColor(new Color(130, 180, 130)); 
                 g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    
    public ClientInterface(String serverAddress, int port, String name, String characterName, int characterIndex) {
        inventory.add(Item.SMALL_POTION);
        inventory.add(Item.MEDIUM_POTION);
        inventory.add(Item.LARGE_POTION);
        
        maxHPs.put("Flins", 100);
        maxHPs.put("Eula", 115);
        maxHPs.put("Kinich", 98);
        maxHPs.put("Durin", 105); 
        maxHPs.put("Furina", 125);
        
        this.playerName = name;
        this.selectedCharacterName = characterName;
        
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            out.println(this.playerName); 
            out.println(characterIndex);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor\n" + serverAddress + ":" + port);
            System.exit(0);
        }
        
        initGUI();
        setTitle("Teyvat Showdown - " + playerName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);

        new Thread(this::listenServer).start();
    }
    
    private void initGUI() {
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(BG_COLOR);

        JPanel topPanel = createStatusPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createArenaPanel(); 
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createControlPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setButtonsEnabled(false);
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JPanel playerHPPanel = new JPanel(new BorderLayout(5, 2));
        playerNameLabel = new JLabel(playerName + " (" + selectedCharacterName + ")", SwingConstants.LEFT);
        playerHpBar = createProgressBar(maxHPs.get(selectedCharacterName), new Color(50, 200, 50));
        playerHPPanel.add(playerNameLabel, BorderLayout.NORTH);
        playerHPPanel.add(playerHpBar, BorderLayout.CENTER);

        JPanel enemyHPPanel = new JPanel(new BorderLayout(5, 2));
        enemyNameLabel = new JLabel("OPPONENT", SwingConstants.RIGHT);
        enemyHpBar = createProgressBar(100, new Color(255, 50, 100));
        enemyHPPanel.add(enemyNameLabel, BorderLayout.NORTH);
        enemyHPPanel.add(enemyHpBar, BorderLayout.CENTER);
        
        playerNameLabel.setForeground(Color.WHITE);
        enemyNameLabel.setForeground(Color.WHITE);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        enemyNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        playerHPPanel.setBackground(BG_COLOR);
        enemyHPPanel.setBackground(BG_COLOR);

        panel.add(playerHPPanel);
        panel.add(enemyHPPanel);
        return panel;
    }

    private JProgressBar createProgressBar(int maxHp, Color color) {
        JProgressBar bar = new JProgressBar(0, maxHp);
        bar.setValue(maxHp);
        bar.setString(maxHp + "/" + maxHp + " HP");
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(new Color(50, 50, 60));
        bar.setPreferredSize(new Dimension(300, 25));
        bar.setFont(new Font("Consolas", Font.BOLD, 14));
        return bar;
    }
    
    private JPanel createArenaPanel() {
        BackgroundPanel panel = new BackgroundPanel("img/arena_bg.jpg"); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); 

        playerSprite = new JLabel();
        loadSprite(playerSprite, selectedCharacterName, new Color(0, 200, 255), 250); 
        playerSprite.setHorizontalAlignment(SwingConstants.LEFT);
        playerSprite.setVerticalAlignment(SwingConstants.BOTTOM);
        panel.add(playerSprite, BorderLayout.WEST);

        enemySprite = new JLabel("⚔");
        enemySprite.setFont(new Font("Arial", Font.BOLD, 120));
        enemySprite.setForeground(Color.RED);
        enemySprite.setHorizontalAlignment(SwingConstants.RIGHT);
        enemySprite.setVerticalAlignment(SwingConstants.BOTTOM);
        panel.add(enemySprite, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(BG_COLOR);

        attackBtn = createButton("ATTACK (1)", new Color(200, 50, 50));
        ultimateBtn = createButton("ULTIMATE (2)", new Color(255, 180, 0));
        itemBtn = createButton("ITEMS (3)", new Color(50, 150, 255));

        attackBtn.addActionListener(e -> sendAction("1", "ATTACK"));
        ultimateBtn.addActionListener(e -> sendAction("2", "ULTIMATE"));
        itemBtn.addActionListener(e -> openInventoryDialog());

        buttonPanel.add(attackBtn);
        buttonPanel.add(ultimateBtn);
        buttonPanel.add(itemBtn);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setBackground(LOG_COLOR);
        battleLog.setForeground(new Color(0, 255, 100));
        battleLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        
        JScrollPane scroll = new JScrollPane(battleLog);
        scroll.setPreferredSize(new Dimension(900, 120));
        scroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        bottomPanel.add(scroll, BorderLayout.CENTER);

        return bottomPanel;
    }
    
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setPreferredSize(new Dimension(200, 50));
        return btn;
    }
    
    // --- Lógica de Juego y Red ---
    
    private void sendAction(String action, String actionName) {
        out.println(action);
        log(">>> YOU: " + actionName);
        setButtonsEnabled(false);
    }
    
    private void openInventoryDialog() {
        if (inventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no items left!", "Inventory", JOptionPane.WARNING_MESSAGE);
            return;
        }

        sendAction("3", "INVENTORY"); 
        setButtonsEnabled(false);
        
        String[] itemNames = inventory.stream()
                .map(item -> item.name() + " (+" + item.getHealAmount() + " HP)")
                .toArray(String[]::new);

        String[] options = new String[itemNames.length + 1];
        System.arraycopy(itemNames, 0, options, 0, itemNames.length);
        options[itemNames.length] = "Cancel"; 
        
        int choiceIndex = JOptionPane.showOptionDialog(
            this,
            "Select item to use:",
            "Inventory",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choiceIndex >= 0 && choiceIndex < inventory.size()) {
            int itemIndex = choiceIndex + 1;
            out.println(itemIndex); 
            
            Item selectedItem = inventory.get(choiceIndex);
            log(">>> Used: " + selectedItem.name());
            
            inventory.remove(choiceIndex); 
            
        } else {
            int cancelIndex = inventory.size() + 1;
            out.println(cancelIndex); 
            log(">>> Action cancelled.");
        }
    }

    private void listenServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                final String msg = line;
                SwingUtilities.invokeLater(() -> {
                    log(msg);

                    detectAndLoadOpponent(msg);
                    
                    updateHPBars(msg);

                    if (msg.contains("Your turn") || msg.contains("Choose action:")) {
                        setButtonsEnabled(true);
                        log("--- It's your turn! ---");
                    }
                    if (msg.contains("Waiting...") || msg.contains("turn. Waiting")) {
                        setButtonsEnabled(false);
                    }

                    if (msg.contains("BATTLE ENDED") || msg.contains("WINNER")) {
                        setButtonsEnabled(false);
                        JOptionPane.showMessageDialog(this, "GAME OVER! See log for result.");
                    }
                });
            }
        } catch (IOException e) {
            log("\n❌ Disconnected from server");
             SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "❌ Conexión perdida con el servidor.", "Error de Conexión", JOptionPane.ERROR_MESSAGE));
        }
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            battleLog.append(message + "\n");
            battleLog.setCaretPosition(battleLog.getDocument().getLength());
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            attackBtn.setEnabled(enabled);
            ultimateBtn.setEnabled(enabled);
            itemBtn.setEnabled(enabled);
        });
    }

    private void loadSprite(JLabel label, String characterName, Color fallbackColor, int size) {
        try {
            String path = "img/" + characterName.toLowerCase() + ".png"; 
            ImageIcon icon = new ImageIcon(path);
            
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(img));
                label.setText(null); 
            } else {
                label.setIcon(null);
                label.setText("⚔");
                label.setFont(new Font("Arial", Font.BOLD, size / 2));
                label.setForeground(fallbackColor);
            }
        } catch (Exception e) {
            label.setIcon(null);
            label.setText("⚔");
            label.setFont(new Font("Arial", Font.BOLD, size / 2));
            label.setForeground(fallbackColor);
        }
    }

    private void detectAndLoadOpponent(String message) {
        if (opponentCharacterName != null) return;
        
        String[] chars = {"Flins", "Eula", "Kinich", "Durin", "Furina"};
        
        for (String c : chars) {
            if (message.contains("(" + c + ")") && !c.equals(selectedCharacterName)) {
                opponentCharacterName = c;
                
                SwingUtilities.invokeLater(() -> {
                    loadSprite(enemySprite, opponentCharacterName, new Color(255, 50, 100), 250); 
                    enemyNameLabel.setText("OPPONENT (" + opponentCharacterName + ")");
                    
                    int enemyMaxHP = maxHPs.getOrDefault(opponentCharacterName, 100);
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
            boolean isMyChar = message.contains("(" + selectedCharacterName + ")");
            boolean isEnemyChar = opponentCharacterName != null && message.contains("(" + opponentCharacterName + ")"); 

            if (!isMyChar && !isEnemyChar) return; 

            int hpIndex = message.indexOf("HP");
            if (hpIndex == -1) return;
            
            String hpPart = message.substring(hpIndex + 3).split("\\|")[0].trim();
            String[] hpValues = hpPart.split("/");
            int currentHP = Integer.parseInt(hpValues[0].trim());
            int maxHP = Integer.parseInt(hpValues[1].trim());
            
            SwingUtilities.invokeLater(() -> {
                JProgressBar bar = isMyChar ? playerHpBar : enemyHpBar;
                
                bar.setMaximum(maxHP);
                bar.setValue(currentHP);
                bar.setString(currentHP + "/" + maxHP + " HP");
                
                Color newColor;
                double hpRatio = (double)currentHP / maxHP;
                if (hpRatio > 0.5) {
                    newColor = new Color(50, 200, 50); 
                } else if (hpRatio > 0.25) {
                    newColor = new Color(255, 180, 0); 
                } else {
                    newColor = new Color(255, 50, 50); 
                }
                bar.setForeground(newColor);
            });
        } catch (Exception e) {

        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new SelectionMenu("localhost", 8080));
    }
}