package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class SelectionMenu extends JFrame {

    private JTextField nameField;
    private JLabel selectedCharacterLabel;
    private String selectedCharacter = "Flins"; 

    private final Map<String, Integer> CHARACTERS = new LinkedHashMap<>();
    
    private static final Color BG_COLOR = new Color(30, 30, 40);
    private static final Color SELECTION_COLOR = new Color(50, 50, 60);
    private static final Color ACCENT_COLOR = new Color(0, 200, 255);
    private static final int MENU_WIDTH = 750;
    private static final int MENU_HEIGHT = 550;
    public SelectionMenu(String serverAddress, int port) {
        
        CHARACTERS.put("Flins", 1);
        CHARACTERS.put("Eula", 2);
        CHARACTERS.put("Kinich", 3);
        CHARACTERS.put("Durin", 4); 
        CHARACTERS.put("Furina", 5);

        setTitle("Teyvat Showdown - Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(MENU_WIDTH, MENU_HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(20, 20));
        
        add(createTitlePanel(), BorderLayout.NORTH);

        add(createCharacterSelectionPanel(), BorderLayout.CENTER);

        add(createStartButtonPanel(serverAddress, port), BorderLayout.SOUTH);

 
        updateSelectedCharacterLabel("Flins");
        setVisible(true);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(BG_COLOR);
        
        JLabel titleLabel = new JLabel("ENTER NAME & SELECT FIGHTER", SwingConstants.CENTER);
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        nameField = new JTextField("Player");
        nameField.setPreferredSize(new Dimension(200, 30));
        nameField.setFont(new Font("Consolas", Font.PLAIN, 16));
        
        panel.add(titleLabel);
        panel.add(nameField);
        return panel;
    }
    
    // aqui creo los iconos de los pjs
    private JPanel createCharacterSelectionPanel() {
        JPanel selectionPanel = new JPanel(new BorderLayout(10, 10));
        selectionPanel.setBackground(BG_COLOR);
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        selectedCharacterLabel = new JLabel("", SwingConstants.CENTER);
        selectedCharacterLabel.setFont(new Font("Arial", Font.BOLD, 24));
        selectedCharacterLabel.setPreferredSize(new Dimension(100, 250));
        selectionPanel.add(selectedCharacterLabel, BorderLayout.NORTH);

        JPanel iconPanel = new JPanel(new GridLayout(1, CHARACTERS.size(), 15, 0));
        iconPanel.setBackground(BG_COLOR);
        iconPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String charName : CHARACTERS.keySet()) {
            iconPanel.add(createCharacterIcon(charName));
        }

        selectionPanel.add(iconPanel, BorderLayout.CENTER);
        return selectionPanel;
    }

    // aqui creo los iconos de los pjs
    private JLabel createCharacterIcon(String charName) {
        JLabel icon = new JLabel();
        icon.setPreferredSize(new Dimension(80, 80));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setVerticalAlignment(SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(SELECTION_COLOR);
        icon.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        loadSprite(icon, charName, 60); 

        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateSelectedCharacterLabel(charName);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                icon.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 3));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                 if (!charName.equals(selectedCharacter)) {
                     icon.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                 }
            }
        });

        return icon;
    }

    // para actualizar el index del pj seleccionado y su sprite
    private void updateSelectedCharacterLabel(String charName) {
        for (Component comp : ((JPanel)selectedCharacterLabel.getParent().getComponent(1)).getComponents()) {
            if (comp instanceof JLabel) {
                ((JComponent) comp).setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            }
        }
        
        selectedCharacter = charName;
        selectedCharacterLabel.setText(charName);
        selectedCharacterLabel.setForeground(Color.WHITE);
        selectedCharacterLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        selectedCharacterLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        selectedCharacterLabel.setIconTextGap(10);
        
        loadSprite(selectedCharacterLabel, charName, 150);
        
        for (Component comp : ((JPanel)selectedCharacterLabel.getParent().getComponent(1)).getComponents()) {
            if (comp instanceof JLabel && ((JLabel)comp).getText().equals(charName)) {
                ((JComponent) comp).setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 3));
            }
        }
    }
    
    // para cargar los sprites de los pjs en el menu de seleccion
    private void loadSprite(JLabel label, String characterName, int size) {
         try {
            String path = "img/" + characterName.toLowerCase() + ".png"; 
            ImageIcon icon = new ImageIcon(path);
            
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(img));
            } else {
                label.setIcon(null);
                label.setText(characterName.substring(0, 1) + " (FAIL)");
                label.setFont(new Font("Arial", Font.BOLD, size / 3));
                label.setForeground(ACCENT_COLOR);
            }
        } catch (Exception e) {
             label.setIcon(null);
             label.setText(characterName.substring(0, 1) + " (ERR)");
             label.setFont(new Font("Arial", Font.BOLD, size / 3));
             label.setForeground(ACCENT_COLOR);
        }
    }
    
    // para crear botones de inicio
    private JPanel createStartButtonPanel(String serverAddress, int port) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(BG_COLOR);
        
        JButton statsButton = new JButton("STATS");
        statsButton.setFont(new Font("Arial", Font.BOLD, 18));
        statsButton.setBackground(new Color(100, 100, 200));
        statsButton.setForeground(Color.WHITE);
        statsButton.setFocusPainted(false);
        statsButton.setPreferredSize(new Dimension(150, 50));
        statsButton.addActionListener(e -> new StatsInterface());
        
        JButton startButton = new JButton("START BATTLE");
        startButton.setFont(new Font("Arial", Font.BOLD, 22));
        startButton.setBackground(new Color(50, 200, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(250, 50));
        
        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            new ClientInterface(serverAddress, port, name, selectedCharacter, CHARACTERS.get(selectedCharacter));
            this.dispose(); 
        });
        
        panel.add(statsButton);
        panel.add(startButton);
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new SelectionMenu("localhost", 8080));
    }
}