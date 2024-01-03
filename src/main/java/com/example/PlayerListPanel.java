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

    private void updateListModel(int type) {

        listModel.clear();
        switch(type){
            case 0:
                for (Player player : players) {
                    listModel.addElement(player.name + " (" + player.points + ")");
                }
                break;
            case 1:
                for (Player player : players) {
                    listModel.addElement(player.name + " (" + player.goals + ")");
                }
                break;
            case 2:
                for (Player player : players) {
                    listModel.addElement(player.name + " (" + Constants.df3.format(player.savePctg) + ")");
                }
                break;
            case 3:
                for (Player player : players) {
                    listModel.addElement(player.name + " (" + Constants.df2.format(player.ppg) + ")");
                }
                break;
            default:
        }
    }

    public void sortByPoints() {
        players = Helpers.sortPlayersByPoints(players);
        updateListModel(0);
    }

    public void sortByGoals() {
        players = Helpers.sortPlayersByGoals(players);
        updateListModel(1);
    }

    public void sortBySavePctg() {
        players = Helpers.sortPlayersBySavePctg(players);
        updateListModel(2);
    }

    public void sortByPpg() {
        players = Helpers.sortPlayersByPpg(players);
        updateListModel(3);
    }

}