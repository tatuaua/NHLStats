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
    int currSortOption;

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

    private void updateListModel(int sortOption) {

        listModel.clear();
        switch(sortOption){
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
        currSortOption = 0;
        players = Helpers.sortPlayersByPoints(players);
        updateListModel(currSortOption);
    }

    public void sortByGoals() {
        currSortOption = 1;
        players = Helpers.sortPlayersByGoals(players);
        updateListModel(currSortOption);
    }

    public void sortBySavePctg() {
        currSortOption = 2;
        players = Helpers.sortPlayersBySavePctg(players);
        updateListModel(currSortOption);
    }

    public void sortByPpg() {
        currSortOption = 3;
        players = Helpers.sortPlayersByPpg(players);
        updateListModel(currSortOption);
    }

    public void filterByCountry(String country){
        if(country.equals("ALL COUNTRIES")){
            players = Helpers.getAllPlayers(APICommunication.getTeams());
        } else {
            players = Helpers.sortPlayersByPoints(Helpers.filterPlayersByCountry(country, Helpers.getAllPlayers(APICommunication.getTeams())));
        }
        updateListModel(currSortOption);
    }

}