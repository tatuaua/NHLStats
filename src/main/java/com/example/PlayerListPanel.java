package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PlayerListPanel extends JPanel {

    private JList<String> playerList;

    public PlayerListPanel(ArrayList<Player> players) {
        setLayout(new BorderLayout());

        // Create a DefaultListModel and add player names
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Player player : players) {
            listModel.addElement(player.name);
        }

        // Create JList with the DefaultListModel
        playerList = new JList<>(listModel);

        // Create JScrollPane and add JList to it
        JScrollPane scrollPane = new JScrollPane(playerList);

        // Add JScrollPane to the panel
        add(scrollPane, BorderLayout.CENTER);
    }

}