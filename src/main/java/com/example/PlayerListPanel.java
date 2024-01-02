package com.example;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PlayerListPanel extends JPanel {

    private JList<String> playerList;
    DefaultListModel<String> listModel;
    ArrayList<Player> players;
    JScrollPane scrollPane;

    public PlayerListPanel(ArrayList<Player> players) {
        setLayout(new BorderLayout());
        this.players = players;

        // Create a DefaultListModel and add player names
        listModel = new DefaultListModel<>();
        for (Player player : players) {
            listModel.addElement(player.name);
        }

        // Create JList with the DefaultListModel
        playerList = new JList<>(listModel);

        // Create JScrollPane and add JList to it
        scrollPane = new JScrollPane(playerList);

        // Add JScrollPane to the panel
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateListModel() {
        listModel.clear();
        for (Player player : players) {
            listModel.addElement(player.name);
        }
    }

    public void sortByPoints() {
        players = Helpers.sortPlayersByPoints(players);
        updateListModel();
    }

    public void sortByGoals() {
        players = Helpers.sortPlayersByGoals(players);
        updateListModel();
    }

    public void sortBySavePctg() {
        players = Helpers.sortPlayersBySavePctg(players);
        updateListModel();
    }

    public void sortByPpg() {
        players = Helpers.sortPlayersByPpg(players);
        updateListModel();
    }

}