package client;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.StatsReader;

public class StatsInterface extends JFrame {
    
	private static final Color BG_COLOR = new Color(30, 30, 40);
    private static final Color ACCENT_COLOR = new Color(0, 200, 255);
    
    public StatsInterface() {
        setTitle("Game Statistics");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("STATS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(ACCENT_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setBackground(BG_COLOR);
        statsArea.setForeground(Color.WHITE);
        statsArea.setFont(new Font("Consolas", Font.BOLD, 18));
        statsArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        Map<String, Integer> wins = StatsReader.getWins();
        Map<String, Integer> losses = StatsReader.getLosses();
        Map<String, Map<String, Integer>> charsPerPlayer = StatsReader.getCharactersPerPlayer();

        StringBuilder sb = new StringBuilder();

        Set<String> allPlayers = new HashSet<>();
        allPlayers.addAll(wins.keySet());
        allPlayers.addAll(losses.keySet());

        for (String player : allPlayers) {
            int w = wins.getOrDefault(player, 0);
            int l = losses.getOrDefault(player, 0);

            String mainChar = StatsReader.getMainCharacter(player, charsPerPlayer);

            sb.append("Player: ").append(player).append("\n");
            sb.append("Record: ").append(w).append("/").append(l).append("\n");

            if (!mainChar.isEmpty()) {
                sb.append("Most Played: ").append(mainChar).append("\n");
            }

            sb.append("\n");
        }

        
        statsArea.setText(sb.toString());
        
        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setBackground(new Color(200, 50, 50));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setPreferredSize(new Dimension(150, 40));
        closeBtn.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
}