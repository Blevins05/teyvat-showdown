package client; 

import javax.swing.*;

import game.Durin;
import game.Eula;
import game.Flins;
import game.Furina;
import game.Item;
import game.Kinich;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

public class ClientInterface extends JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String playerName;
    private String selectedCharacterName;
    private String opponentCharacterName = null;
    private Map<String, Integer> maxHPs = new HashMap<>(); // esto es para poder gestionar bien la vida en las barras de HP
    private List<Item> inventory = new ArrayList<>(); // replica del inventario

    private JProgressBar playerHpBar, enemyHpBar;
    private JProgressBar playerUltBar, enemyUltBar;
    private JLabel playerNameLabel, enemyNameLabel;
    private JLabel playerSprite, enemySprite;
    private JLabel turnIndicator;
    private JTextArea battleLog;
    private JButton attackBtn, ultimateBtn, itemBtn;
    
    // Para los iconos de efectos
    private JPanel playerEffectsPanel, enemyEffectsPanel;
    
    // Posiciones originales para animaciones
    private Point playerOriginalPos;
    private Point enemyOriginalPos;
    
    // Timer para parpadeo del indicador de turno
    private Timer blinkTimer;

    private static final Color BG_COLOR = new Color(30, 30, 40);
    private static final Color LOG_COLOR = new Color(20, 20, 30);
    private static final int GUI_WIDTH = 1000;
    private static final int GUI_HEIGHT = 855;
    
    // clase interna que he necesitado para el escenario de fondo 
    private class BackgroundPanel extends JPanel {
        private Image bgImage;

        public BackgroundPanel(String imagePath) {
            try {
                bgImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Error cargando la imagen de fondo: " + imagePath);
                bgImage = null;
            }
            setLayout(null); 
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
    
    // Constructor
    public ClientInterface(String serverAddress, int port, String name, String characterName, int characterIndex) {
        inventory.add(Item.SMALL_POTION);
        inventory.add(Item.MEDIUM_POTION);
        inventory.add(Item.LARGE_POTION);
        
        maxHPs.put("Flins", new Flins().getMaxHp());
        maxHPs.put("Eula", new Eula().getMaxHp());
        maxHPs.put("Kinich", new Kinich().getMaxHp());
        maxHPs.put("Durin", new Durin().getMaxHp()); 
        maxHPs.put("Furina", new Furina().getMaxHp());
        
        this.playerName = name;
        this.selectedCharacterName = characterName;
        
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            out.println(this.playerName); 
            out.println(characterIndex);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to server\n" + serverAddress + ":" + port);
            System.exit(0);
        }
        
        initGUI();
        setTitle("Teyvat Showdown - " + playerName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(GUI_WIDTH, GUI_HEIGHT);
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
        
        // barra de la habilidad definitiva de un jugador
        playerUltBar = new JProgressBar(0, 3); // max 3 turnos de cooldown
        playerUltBar.setValue(0);
        playerUltBar.setString("Ultimate: READY");
        playerUltBar.setStringPainted(true);
        playerUltBar.setForeground(new Color(255, 215, 0)); 
        playerUltBar.setBackground(new Color(50, 50, 60));
        playerUltBar.setPreferredSize(new Dimension(300, 20));
        playerUltBar.setFont(new Font("Consolas", Font.BOLD, 12));
        
        // panel de los efectos de un personaje (saldra si esta quemado, etc...)
        playerEffectsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        playerEffectsPanel.setOpaque(false);
        playerEffectsPanel.setPreferredSize(new Dimension(200, 30));
        
        JPanel playerBarsPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        playerBarsPanel.setOpaque(false);
        playerBarsPanel.add(playerHpBar);
        playerBarsPanel.add(playerUltBar);
        
        playerHPPanel.add(playerNameLabel, BorderLayout.NORTH);
        playerHPPanel.add(playerBarsPanel, BorderLayout.CENTER);
        playerHPPanel.add(playerEffectsPanel, BorderLayout.SOUTH);

        JPanel enemyHPPanel = new JPanel(new BorderLayout(5, 2));
        enemyNameLabel = new JLabel("OPPONENT", SwingConstants.RIGHT);
        enemyHpBar = createProgressBar(100, new Color(255, 50, 100));
        
        // barra de la habilidad definitiva de un jugador
        enemyUltBar = new JProgressBar(0, 3);
        enemyUltBar.setValue(0);
        enemyUltBar.setString("Ultimate: READY");
        enemyUltBar.setStringPainted(true);
        enemyUltBar.setForeground(new Color(255, 215, 0));
        enemyUltBar.setBackground(new Color(50, 50, 60));
        enemyUltBar.setPreferredSize(new Dimension(300, 20));
        enemyUltBar.setFont(new Font("Consolas", Font.BOLD, 12));
        
        // panel de los efectos de un personaje enemigo (saldra si esta quemado, etc...)
        enemyEffectsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        enemyEffectsPanel.setOpaque(false);
        enemyEffectsPanel.setPreferredSize(new Dimension(200, 30));
        
        JPanel enemyBarsPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        enemyBarsPanel.setOpaque(false);
        enemyBarsPanel.add(enemyHpBar);
        enemyBarsPanel.add(enemyUltBar);
        
        enemyHPPanel.add(enemyNameLabel, BorderLayout.NORTH);
        enemyHPPanel.add(enemyBarsPanel, BorderLayout.CENTER);
        enemyHPPanel.add(enemyEffectsPanel, BorderLayout.SOUTH);
        
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
    
    // para crear un componente que represente una barra (HP/Ulti)
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
    
    // aqui creo el panel de juego (el escenario de la partida)
    private JPanel createArenaPanel() {
        BackgroundPanel panel = new BackgroundPanel("img/arena_bg.jpg");

        playerSprite = new JLabel();
        loadSprite(playerSprite, selectedCharacterName, new Color(0, 200, 255), 250);
        playerSprite.setBounds(100, 150, 250, 250);
        panel.add(playerSprite);

        enemySprite = new JLabel("⚔"); // fallback por si no cargase el sprite de un personaje
        enemySprite.setFont(new Font("Arial", Font.BOLD, 120));
        enemySprite.setForeground(Color.RED);
        enemySprite.setHorizontalAlignment(SwingConstants.CENTER);
        enemySprite.setBounds(650, 150, 250, 250);
        panel.add(enemySprite);
        
        // indicador de turnos (aparece mensaje en el centro)
        turnIndicator = new JLabel("WAITING...", SwingConstants.CENTER);
        turnIndicator.setFont(new Font("Arial", Font.BOLD, 36));
        turnIndicator.setForeground(Color.YELLOW);
        turnIndicator.setOpaque(true);
        turnIndicator.setBackground(new Color(0, 0, 0, 180)); 
        turnIndicator.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        turnIndicator.setBounds(300, 50, 400, 80);
        turnIndicator.setVisible(false);
        panel.add(turnIndicator);
        
        SwingUtilities.invokeLater(() -> {
            playerOriginalPos = playerSprite.getLocation();
            enemyOriginalPos = enemySprite.getLocation();
        });
        
        return panel;
    }
    
    // panel de batalla de un jugador (desde aqui podra atacar o abrir su inventario)
    private JPanel createControlPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(BG_COLOR);

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
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(false); 
        btn.setContentAreaFilled(false); 
        btn.setBorder(BorderFactory.createLineBorder(color.darker(), 4));
        btn.setPreferredSize(new Dimension(200, 50));
        return btn;
    }
    
    // animaciones simples que he creado para mejorar la experiencia
    
    private void showTurnIndicator(String text, Color color, boolean blink) {
        SwingUtilities.invokeLater(() -> {
            turnIndicator.setText(text);
            turnIndicator.setForeground(color);
            turnIndicator.setBorder(BorderFactory.createLineBorder(color, 3));
            turnIndicator.setVisible(true);

            if (blinkTimer != null && blinkTimer.isRunning()) {
                blinkTimer.stop();
            }
            
            if (blink) {
                final boolean[] visible = {true};
                blinkTimer = new Timer(500, e -> {
                    visible[0] = !visible[0];
                    turnIndicator.setVisible(visible[0]);
                });
                blinkTimer.start();
            }
            
            Timer hideTimer = new Timer(3000, e -> {
                if (blinkTimer != null) blinkTimer.stop();
                turnIndicator.setVisible(false);
            });
            hideTimer.setRepeats(false);
            hideTimer.start();
        });
    }
    
    private void updateUltimateBar(JProgressBar ultBar, int turnsRemaining, int maxCooldown) {
        SwingUtilities.invokeLater(() -> {
            ultBar.setMaximum(maxCooldown); // el cooldown maximo es 3 turnos (algunos personajes tienen 2)
            
            if (turnsRemaining == 0) {

                ultBar.setValue(0);
                ultBar.setString("CHARGED!");
                ultBar.setForeground(new Color(255, 215, 0));
            } else {
                int progress = maxCooldown - turnsRemaining;
                ultBar.setValue(progress);
                ultBar.setString("Ultimate: " + turnsRemaining + " turns");
                ultBar.setForeground(new Color(100, 100, 150)); 
            }
        });
    }
    
    // para animar un ataque
    private void animateAttack(JLabel attacker, Point originalPos, boolean facingRight) {
        Timer timer = new Timer(30, null);
        final int[] frame = {0};
        final int moveDistance = 80;
        
        timer.addActionListener(e -> {
            frame[0]++;
            
            if (frame[0] <= 10) {

                int offset = (frame[0] * moveDistance) / 10;
                if (facingRight) {
                    attacker.setLocation(originalPos.x + offset, originalPos.y);
                } else {
                    attacker.setLocation(originalPos.x - offset, originalPos.y);
                }
            } else if (frame[0] <= 20) {
                int offset = ((20 - frame[0]) * moveDistance) / 10;
                if (facingRight) {
                    attacker.setLocation(originalPos.x + offset, originalPos.y);
                } else {
                    attacker.setLocation(originalPos.x - offset, originalPos.y);
                }
            } else {
                // Terminar
                attacker.setLocation(originalPos);
                timer.stop();
            }
        });
        
        timer.start();
    }
    
    // simula el tambaleo de un personaje en campo
    private void animateShake(JLabel target, Point originalPos) {
        Timer timer = new Timer(40, null);
        final int[] frame = {0};
        
        timer.addActionListener(e -> {
            frame[0]++;
            
            if (frame[0] <= 8) {
                int offsetX = (frame[0] % 2 == 0) ? 5 : -5;
                int offsetY = (frame[0] % 3 == 0) ? 3 : -3;
                target.setLocation(originalPos.x + offsetX, originalPos.y + offsetY);
            } else {
                target.setLocation(originalPos);
                timer.stop();
            }
        });
        
        timer.start();
    }
    
    // para añadir el efecto cuando un pj se encuentra bajo un efecto negativo
    private void addEffectIcon(JPanel effectsPanel, String effect) {
        String text = "";
        Color color = Color.WHITE;
        
        switch(effect.toLowerCase()) {
            case "burn":
            case "burned":
                text = "Burned";
                color = new Color(255, 100, 0);
                break;
            case "freeze":
            case "frozen":
                text = "Frozen";
                color = new Color(100, 200, 255);
                break;
            case "poison":
            case "poisoned":
            case "bloom":
                text = "Poisoned";
                color = new Color(100, 255, 100);
                break;
        }
        
        if (!text.isEmpty()) {
            for (Component c : effectsPanel.getComponents()) {
                if (c instanceof JLabel && ((JLabel)c).getText().contains(text)) {
                    return; 
                }
            }
            
            JLabel effectLabel = new JLabel(text);
            effectLabel.setFont(new Font("Arial", Font.PLAIN, 24));
            effectLabel.setForeground(color);
            effectsPanel.add(effectLabel);
            effectsPanel.revalidate();
            effectsPanel.repaint();
            
            Timer removeTimer = new Timer(5000, e -> {
                effectsPanel.remove(effectLabel);
                effectsPanel.revalidate();
                effectsPanel.repaint();
            });
            removeTimer.setRepeats(false);
            removeTimer.start();
        }
    }
    
    // para comunicarme con la logica de negocio (parte del servidor)
    
    private void sendAction(String action, String actionName) {
        out.println(action);
        log(">>> YOU: " + actionName);
        setButtonsEnabled(false);
        
        if (action.equals("1") || action.equals("2")) {
            SwingUtilities.invokeLater(() -> {
                if (playerOriginalPos != null) {
                    animateAttack(playerSprite, playerOriginalPos, true);
                }
            });
        }
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

    // escuchar mensajes del servidor (esto va actualizando el estado de una partida)
    private void listenServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                final String msg = line;
                SwingUtilities.invokeLater(() -> {
                    log(msg);

                    detectAndLoadOpponent(msg);
                    updateHPBars(msg);
                    updateUltimateBars(msg);
                    detectEffects(msg);
                    detectActions(msg);
                    detectTurnChanges(msg);

                    if (msg.contains("Your turn") || msg.contains("Choose action:")) {
                        setButtonsEnabled(true);
                        showTurnIndicator("YOUR TURN", new Color(50, 255, 50), true);
                    }
                    if (msg.contains("Waiting...") || msg.contains("turn. Waiting")) {
                        setButtonsEnabled(false);
                        showTurnIndicator("OPPONENT'S TURN", new Color(255, 100, 100), false);
                    }

                    if (msg.contains("BATTLE ENDED") || msg.contains("WINNER")) {
                        setButtonsEnabled(false);
                        if (blinkTimer != null) blinkTimer.stop();
                        showGameOverScreen(msg);
                    }
                });
            }
        } catch (IOException e) {
            log("\nDisconnected from server");
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Connection with server lost", "Connection Error", JOptionPane.ERROR_MESSAGE));
        }
    }
    
    private void detectTurnChanges(String message) {
        if (message.contains("starts") && opponentCharacterName != null) {
            if (message.contains(selectedCharacterName)) {
                showTurnIndicator("YOU START!", new Color(50, 255, 50), true);
            } else {
                showTurnIndicator("OPPONENT STARTS", new Color(255, 100, 100), false);
            }
        }
    }
    
    private void updateUltimateBars(String message) {
        if (!message.contains("Ultimate:")) return;
        
        try {
            boolean isMyChar = message.contains("(" + selectedCharacterName + ")");
            boolean isEnemyChar = opponentCharacterName != null && message.contains("(" + opponentCharacterName + ")");
            
            if (!isMyChar && !isEnemyChar) return;
            
            String ultPart = message.split("Ultimate:")[1].trim();
            int turnsRemaining;
            int maxCooldown = 3;
            
            if (ultPart.contains("CHARGED") || ultPart.contains("READY")) {
                turnsRemaining = 0;
            } else {
                String[] parts = ultPart.split(" ");
                turnsRemaining = Integer.parseInt(parts[0].trim());
            }
            
            if (isMyChar) {
                updateUltimateBar(playerUltBar, turnsRemaining, maxCooldown);
            } else {
                updateUltimateBar(enemyUltBar, turnsRemaining, maxCooldown);
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    // cuando se acaba la partida, aparece un mini modal para el ganador y perdedor del juego
    private void showGameOverScreen(String message) {
        SwingUtilities.invokeLater(() -> {
            boolean youWon = false;
            String winnerName = "";
            
            if (message.contains("WINNER:")) {
                String[] parts = message.split("WINNER:");
                if (parts.length > 1) {
                    winnerName = parts[1].trim().split(" ")[0];
                    youWon = winnerName.equals(selectedCharacterName);
                }
            }
            
            JPanel gameOverPanel = new JPanel();
            gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
            gameOverPanel.setOpaque(true);
            
            if (youWon) {
                gameOverPanel.setBackground(new Color(0, 100, 0)); 
            } else {
                gameOverPanel.setBackground(new Color(100, 0, 0)); 
            }
            
            gameOverPanel.setBounds(300, 150, 400, 200); 
            gameOverPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
            
            JLabel titleLabel = new JLabel(youWon ? "YOU WIN!" : "YOU LOSE");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 40)); 
            titleLabel.setForeground(Color.YELLOW);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel subtitleLabel = new JLabel("Winner: " + winnerName);
            subtitleLabel.setFont(new Font("Arial", Font.BOLD, 18)); 
            subtitleLabel.setForeground(Color.WHITE);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel resultsLabel = new JLabel("(See battle log for details)");
            resultsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            resultsLabel.setForeground(Color.LIGHT_GRAY);
            resultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            gameOverPanel.add(Box.createVerticalGlue());
            gameOverPanel.add(titleLabel);
            gameOverPanel.add(Box.createVerticalStrut(15));
            gameOverPanel.add(subtitleLabel);
            gameOverPanel.add(Box.createVerticalStrut(8));
            gameOverPanel.add(resultsLabel);
            gameOverPanel.add(Box.createVerticalGlue());
            
            JPanel arenaPanel = (JPanel) getContentPane().getComponent(1);
            arenaPanel.add(gameOverPanel);
            arenaPanel.setComponentZOrder(gameOverPanel, 0);
            arenaPanel.revalidate();
            arenaPanel.repaint();
            
            gameOverPanel.setVisible(true);
            Timer scaleTimer = new Timer(20, null);
            final double[] scale = {0.5};
            scaleTimer.addActionListener(e -> {
                if (scale[0] < 1.0) {
                    scale[0] += 0.05;
                    int newWidth = (int)(400 * scale[0]);
                    int newHeight = (int)(200 * scale[0]); 
                    int x = 500 - newWidth/2;
                    int y = 250 - newHeight/2;
                    gameOverPanel.setBounds(x, y, newWidth, newHeight);
                } else {
                    scaleTimer.stop();
                }
            });
            scaleTimer.start();
        });
    }
    
    // esto es para detectar las acciones de nuestro oponente
    private void detectActions(String message) {
        if (opponentCharacterName != null && message.contains(opponentCharacterName) && 
            (message.contains("attacks") || message.contains("ultimate"))) {
            
            if (enemyOriginalPos != null) {
                animateAttack(enemySprite, enemyOriginalPos, false);
            }
        }
        
        if (message.contains(selectedCharacterName) && 
            (message.contains("took") && message.contains("damage"))) {
            
            if (playerOriginalPos != null) {
                animateShake(playerSprite, playerOriginalPos);
            }
        }
        
        if (opponentCharacterName != null && message.contains(opponentCharacterName) && 
            (message.contains("took") && message.contains("damage"))) {
            
            if (enemyOriginalPos != null) {
                animateShake(enemySprite, enemyOriginalPos);
            }
        }
    }
    
    // esto es para detectar efectos (quemadura, congelado) en ambos jugadores
    private void detectEffects(String message) {
    
        if (message.contains(selectedCharacterName) || message.contains(playerName)) {
            if (message.contains("burned") || message.contains("is burned")) {
                addEffectIcon(playerEffectsPanel, "burn");
            }
            if (message.contains("frozen") || message.contains("is frozen")) {
                addEffectIcon(playerEffectsPanel, "freeze");
            }
            if (message.contains("poisoned") || message.contains("is poisoned")) {
                addEffectIcon(playerEffectsPanel, "poison");
            }
        }
        
        if (opponentCharacterName != null && message.contains(opponentCharacterName)) {
            if (message.contains("burned") || message.contains("is burned")) {
                addEffectIcon(enemyEffectsPanel, "burn");
            }
            if (message.contains("frozen") || message.contains("is frozen")) {
                addEffectIcon(enemyEffectsPanel, "freeze");
            }
            if (message.contains("poisoned") || message.contains("is poisoned")) {
                addEffectIcon(enemyEffectsPanel, "poison");
            }
        }
    }
    
    // para filtrar y determinar los mensajes del servidor que van a salir en el log 
    private void log(String message) {
        // filtrando los mensajes que no quiero, esos se quedan para la version de consola, con interfaz son redundantes
        if (message.contains("BATTLE STATUS:") ||
            message.contains("Choose action:") ||
            message.contains("1. Attack") ||
            message.contains("2. Ultimate") ||
            message.contains("3. Use Item") ||
            message.contains("Choice:") ||
            message.contains("--- Processing effects ---") ||
            message.contains("Your turn,") ||
            message.contains("turn. Waiting...") ||
            message.contains("| Ultimate:") ||
            message.isEmpty()) {
            return; 
        }
        
        String cleanMsg = message.replaceAll("\\[HP: \\d+\\]", "").trim();
        
        SwingUtilities.invokeLater(() -> {
            battleLog.append(cleanMsg + "\n");
            battleLog.setCaretPosition(battleLog.getDocument().getLength());
        });
    }

    // para habilitar botones cuando es tu turno 
    private void setButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            attackBtn.setEnabled(enabled);
            ultimateBtn.setEnabled(enabled);
            itemBtn.setEnabled(enabled);
        });
    }

    // aqui cargo sprites 
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
    }  // los emojis son fallback por si no carga el pj
    
    // para cargar el sprite de nuestro oponente
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
    
    // aqui actualizo las barras de vida de ambos jugadores
    private void updateHPBars(String message) {
        if (!message.contains("HP") || !message.contains("/")) return;
        
        try {
            boolean isMyChar = message.contains("(" + selectedCharacterName + ")");
            boolean isEnemyChar = opponentCharacterName != null && message.contains("(" + opponentCharacterName + ")"); 

            if (!isMyChar && !isEnemyChar) return; 

            int hpIndex = message.indexOf("HP");
            if (hpIndex == -1) return;
            
            // splits complicado, necesite ayuda
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
    
    // iniciamos la interfaz aqui 
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {} // ignoro la excepcion, en algunos casos he hecho esto
        
        SwingUtilities.invokeLater(() -> new SelectionMenu("localhost", 8080));
    }
}