package com.example;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class App implements ActionListener{

    
    Team[] teams = new Team[32];
    static JFrame frame;
    JButton teamNameButton, topBarTeams, topBarPlayers;
    JButton[] teamNameButtons = new JButton[32];
    JLabel[] images = new JLabel[32];
    JLabel topBar;
    JTextArea[] infoArray = new JTextArea[32];
    int teamAmount = 32;

    App() throws IOException, URISyntaxException{

        Font myFont = new Font(null, Font.BOLD, 15);

        APIStuff.populateArrays();
        teams = APIStuff.getTeams();

        frame = new JFrame("NHL Stats");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(650, 700);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        ////////////////////////////

        topBarTeams = new JButton("Teams");
        topBarTeams.setBounds(20, 10, 80, 30);
        topBarTeams.setVisible(true);
        topBarTeams.setForeground(Color.black);
        topBarTeams.setBackground(Color.green);
        topBarTeams.addActionListener(this);
        frame.add(topBarTeams);

        topBarPlayers = new JButton("Players");
        topBarPlayers.setBounds(120, 10, 80, 30);
        topBarPlayers.setVisible(true);
        topBarPlayers.setForeground(Color.black);
        topBarPlayers.setBackground(Color.white);
        topBarPlayers.addActionListener(this);
        frame.add(topBarPlayers);

        ImageIcon topBarImg = new ImageIcon("images/topBar.png");
        topBar = new JLabel(topBarImg);
        topBar.setBounds(0, 0, 650, 50);
        topBar.setVisible(true);
        topBar.setBackground(new Color(30, 30, 30));
        frame.add(topBar);

        ////////////////////////////

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            ImageIcon img = new ImageIcon("images/" + teams[teamIndex].ab + ".png");
            images[teamIndex] = new JLabel();
            images[teamIndex].setIcon(img);
            images[teamIndex].setBounds(275, 330, 270, 200);
            images[teamIndex].setVisible(false);
            frame.add(images[teamIndex]);
        }

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            teamNameButton = new JButton(teams[teamIndex].name + " (" + teams[teamIndex].points + ")");
            teamNameButton.setBounds(20, teamIndex*18+60, 200, 20);
            teamNameButton.setForeground(Color.white);
            teamNameButton.setBackground(null);
            teamNameButton.setBorder(null);
            teamNameButton.addActionListener(this);
            teamNameButtons[teamIndex] = teamNameButton;
            frame.add(teamNameButton);
        }

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex] = new JTextArea();
            infoArray[teamIndex].setVisible(false);
            infoArray[teamIndex].setBounds(300, 70, 250, 520);
            infoArray[teamIndex].setBackground(new Color(30, 30, 30));
            infoArray[teamIndex].setForeground(Color.white);
            infoArray[teamIndex].setFont(myFont);
            infoArray[teamIndex].setBorder(BorderFactory.createCompoundBorder(
                infoArray[teamIndex].getBorder(), 
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            frame.add(infoArray[teamIndex]);
        }

        showTeamInfo(0);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public static void main(String[] args) throws IOException, URISyntaxException{

        @SuppressWarnings("unused")
        App e = new App();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == topBarPlayers){
            showPlayersPage();
        }

        if(e.getSource() == topBarTeams){
            showTeamsPage();
        }

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){
            if(e.getSource() == teamNameButtons[teamIndex]){
                try {
                    showTeamInfo(teamIndex);
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            } else {
                teamNameButtons[teamIndex].setForeground(Color.white);
            }
        }
    }

    public void showTeamInfo(int index) throws IOException, URISyntaxException{

        teamNameButtons[index].setForeground(Color.green);

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);
        }

        if(teams[index].nextMatch == null){
            APIStuff.populateNextMatch(index);
        }
        
        infoArray[index].setVisible(true);
        infoArray[index].setText(
                "Team Name:\n\n" + teams[index].name + "\n\nCurrent team points:\n\n" + teams[index].points  + "\n\nUpcoming match:\n\n" + teams[index].nextMatch
            );
        images[index].setVisible(true);
    }

    public void showPlayersPage(){

        topBarPlayers.setBackground(Color.green);
        topBarTeams.setBackground(Color.white);

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);

        }

    }

    public void showTeamsPage(){

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(true);
            teamNameButtons[teamIndex].setVisible(true);
            images[0].setVisible(true);
        }

        topBarPlayers.setBackground(Color.white);
        topBarTeams.setBackground(Color.green);

    }
}
