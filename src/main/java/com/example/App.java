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

    /** Defining visible elements of the app */
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

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){ // Loads the images from a local folder

            ImageIcon img = new ImageIcon("images/" + teams[teamIndex].ab + ".png");
            images[teamIndex] = new JLabel();
            images[teamIndex].setIcon(img);
            images[teamIndex].setBounds(275, 330, 270, 200);
            images[teamIndex].setVisible(false);
            frame.add(images[teamIndex]);
        }

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){ // Loads the team name buttons

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
                BorderFactory.createEmptyBorder(8, 8, 8, 8))); //Adds padding to the info box
            frame.add(infoArray[teamIndex]);
        }

        showTeamInfo(0);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    /** Starts an instance of the app */
    public static void main(String[] args) throws IOException, URISyntaxException{ 

        @SuppressWarnings("unused")
        App e = new App();
    }

    /** Handle clicks */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == topBarPlayers){
            showPlayersPage();
        }

        if(e.getSource() == topBarTeams){
            showTeamsPage();
        }

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){ // Doing this with a loop to avoid code bloat
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

    /** Shows the team info for a specified index */
    public void showTeamInfo(int index) throws IOException, URISyntaxException{

        String bonusMsg = "";

        if(teams[index].points == teams[0].points){
            bonusMsg = " (Leading the NHL)";
        }

        teamNameButtons[index].setForeground(Color.green);

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);
        }

        if(teams[index].nextMatch == null){
            APIStuff.populateNextMatch(index);
        }

        if(teams[index].last5 == null){
            APIStuff.populateLast5(index);
        }
        
        infoArray[index].setVisible(true);
        infoArray[index].setText(
                teams[index].name 
                + "\n\nCurrent team points:\n  " + teams[index].points + bonusMsg
                + "\n\nUpcoming match:\n  " + teams[index].nextMatch 
                + "\n\nLast 5 matches:\n " + teams[index].last5
            );
        images[index].setVisible(true);
    }

    /** TODO: Shows the players page */
    public void showPlayersPage(){

        topBarPlayers.setBackground(Color.green);
        topBarTeams.setBackground(Color.white);

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);

        }
    }

    /** Shows the teams page */
    public void showTeamsPage(){

        topBarPlayers.setBackground(Color.white);
        topBarTeams.setBackground(Color.green);


        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(true);
            teamNameButtons[teamIndex].setVisible(true);
            images[teamIndex].setVisible(false);
        }

        images[0].setVisible(true);
    }
}
