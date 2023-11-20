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
    JButton teamNameButton, topBarTeams, topBarMoreStats;
    JButton[] teamNameButtons = new JButton[32];
    JLabel[] images = new JLabel[32];
    JLabel[] dataPoints;
    JTextArea seasonPerformance = new JTextArea();
    JTextArea moreStatsPageTitle = new JTextArea();
    JLabel topBar;
    JTextArea[] infoArray = new JTextArea[32];
    int teamAmount = 32;
    int currentSelected;
    Font myFont = new Font(null, Font.BOLD, 15);

    /** Defining visible elements of the app */
    App() throws IOException, URISyntaxException{

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

        topBarMoreStats = new JButton("More stats");
        topBarMoreStats.setBounds(120, 10, 120, 30);
        topBarMoreStats.setVisible(true);
        topBarMoreStats.setForeground(Color.black);
        topBarMoreStats.setBackground(Color.white);
        topBarMoreStats.addActionListener(this);
        frame.add(topBarMoreStats);

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

        if(e.getSource() == topBarMoreStats){
            showMoreStatsPage(currentSelected);
        }

        if(e.getSource() == topBarTeams){
            showTeamsPage();
        }

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){ // Doing this with a loop to avoid code bloat
            if(e.getSource() == teamNameButtons[teamIndex]){
                try {
                    showTeamInfo(teamIndex);
                    currentSelected = teamIndex;
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
            APIStuff.populatePrevMatches(index);
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

    /** Shows more stats for chosen team */
    public void showMoreStatsPage(int index){

        topBarMoreStats.setBackground(Color.green);
        topBarTeams.setBackground(Color.white);

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);

        }

        moreStatsPageTitle.setText(teams[index].name);
        moreStatsPageTitle.setBounds(20, 60, 230, 20);
        moreStatsPageTitle.setFont(myFont);
        moreStatsPageTitle.setBackground(Color.darkGray);
        moreStatsPageTitle.setForeground(Color.white);
        moreStatsPageTitle.setVisible(true);
        frame.add(moreStatsPageTitle);

        dataPoints = new JLabel[teams[index].allSeasonMatches.length];
        ImageIcon greenPoint = new ImageIcon("images/greenpoint.png");
        ImageIcon redPoint = new ImageIcon("images/redpoint.png");

        // Draw data for all games in the season for this team. Green point means win, red means loss. Win increases Y by 6 pixels and loss decreases by 6
        for(int i = 0; i < teams[index].allSeasonMatches.length; i++){

            dataPoints[i] = new JLabel();

            if(teams[index].allSeasonMatches[i].equals("W")){
                
                dataPoints[i].setIcon(greenPoint);

                if(i == 0){
                    dataPoints[i].setBounds(i*9+330, 200, 5, 5);
                } else {
                    dataPoints[i].setBounds(i*9+330, dataPoints[i-1].getY()-6, 5, 5);
                }
                dataPoints[i].setVisible(true);
                frame.add(dataPoints[i]);

            } else {

                dataPoints[i].setIcon(redPoint);

                if(i == 0){
                    dataPoints[i].setBounds(i*9+330, 200, 5, 5);
                } else {
                    dataPoints[i].setBounds(i*9+330, dataPoints[i-1].getY()+6, 5, 5);
                }
                dataPoints[i].setVisible(true);
                frame.add(dataPoints[i]);

            }
        }
    }

    /** Shows the teams page */
    public void showTeamsPage(){

        topBarMoreStats.setBackground(Color.white);
        topBarTeams.setBackground(Color.green);

        for(int i = 0; i < dataPoints.length; i++){

            dataPoints[i].setVisible(false);
        }

        for(int teamIndex = 0; teamIndex < teamAmount; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(true);
            images[teamIndex].setVisible(false);
        }

        infoArray[currentSelected].setVisible(true);
        images[currentSelected].setVisible(true);
    }
}
