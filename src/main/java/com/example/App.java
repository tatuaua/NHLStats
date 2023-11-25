package com.example;

import javax.swing.*;

import java.awt.Image;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

public class App implements ActionListener{

    final int TEAM_AMOUNT = 32; // Have to change this if a new NHL team is born

    //Part of the teams page
    JButton teamNameButton;
    JButton[] teamNameButtons = new JButton[TEAM_AMOUNT];
    JTextArea[] infoArray = new JTextArea[TEAM_AMOUNT];

    // Part of the more stats page
    JLabel[] dataPoints;
    JTextPane seasonPerformance = new JTextPane();
    JTextArea moreStatsTeamName = new JTextArea();
    JLabel moreStatsTeamLogo = new JLabel();
    JTextArea dataPointsBackground = new JTextArea();

    // General variables
    static JFrame frame;
    JLabel topBar;
    JButton topBarTeams, moreStatsButton;
    Team[] teams = new Team[TEAM_AMOUNT];
    JLabel[] images = new JLabel[TEAM_AMOUNT];
    int currentSelectedTeamIndex;
    int currentPage = 0;
    Font myFont = new Font(null, Font.BOLD, 15);
    Font myFontBigger = new Font(null, Font.BOLD, 20);
    Font myFontLighter = new Font(null, Font.PLAIN, 15);
    Font myFontLighterBigger = new Font(null, Font.PLAIN, 20);
    Color myOrange = new Color(248,158,124);

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
        topBarTeams.setBackground(myOrange);
        topBarTeams.addActionListener(this);
        frame.add(topBarTeams);

        moreStatsButton = new JButton("More stats");
        moreStatsButton.setBounds(300, 590, 120, 30);
        moreStatsButton.setVisible(true);
        moreStatsButton.setForeground(Color.black);
        moreStatsButton.setBackground(Color.white);
        moreStatsButton.addActionListener(this);
        frame.add(moreStatsButton);

        ImageIcon topBarImg = new ImageIcon("images/topBar.png");
        topBar = new JLabel(topBarImg);
        topBar.setBounds(0, 0, 650, 50);
        topBar.setVisible(true);
        topBar.setBackground(new Color(30, 30, 30));
        frame.add(topBar);

        ////////////////////////////

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the images from a local folder

            ImageIcon img = new ImageIcon("images/" + teams[teamIndex].ab + ".png");
            images[teamIndex] = new JLabel();
            images[teamIndex].setIcon(img);
            images[teamIndex].setBounds(275, 330, 270, 200);
            images[teamIndex].setVisible(false);
            frame.add(images[teamIndex]);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the team name buttons

            teamNameButton = new JButton(teams[teamIndex].name + " (" + teams[teamIndex].points + ")");
            teamNameButton.setBounds(20, teamIndex*18+60, 200, 20);
            teamNameButton.setForeground(Color.white);
            teamNameButton.setBackground(null);
            teamNameButton.setBorder(null);
            teamNameButton.addActionListener(this);
            teamNameButtons[teamIndex] = teamNameButton;
            frame.add(teamNameButton);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex] = new JTextArea();
            infoArray[teamIndex].setVisible(false);
            infoArray[teamIndex].setBounds(300, 70, 250, 500);
            infoArray[teamIndex].setBackground(new Color(30, 30, 30));
            infoArray[teamIndex].setForeground(Color.white);
            infoArray[teamIndex].setFont(myFontLighter);
            infoArray[teamIndex].setEditable(false);
            infoArray[teamIndex].setBorder(BorderFactory.createLineBorder(myOrange));
            frame.add(infoArray[teamIndex]);
        }

        showTeamInfo(0);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ImageIcon topLeftIcon = new ImageIcon("images/nhlstatslogo.png");
        frame.setIconImage(topLeftIcon.getImage());

    }


    /** Starts an instance of the app */
    public static void main(String[] args) throws IOException, URISyntaxException{ 

        @SuppressWarnings("unused")
        App e = new App();
    }

    /** Handle clicks */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == moreStatsButton && currentPage != 1){
            currentPage = 1;
            showMoreStatsPage(currentSelectedTeamIndex);
        }

        if(e.getSource() == topBarTeams && currentPage != 0){
            currentPage = 0;
            showTeamsPage();
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Doing this with a loop to avoid code bloat
            if(e.getSource() == teamNameButtons[teamIndex]){
                try {
                    showTeamInfo(teamIndex);
                    currentSelectedTeamIndex = teamIndex;
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

        teamNameButtons[index].setForeground(myOrange);

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

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
                "\n  "
                + teams[index].name 
                + "\n\n  Current team points:\n   " 
                + teams[index].points + bonusMsg
                + "\n\n  Upcoming match:\n   " 
                + teams[index].nextMatch 
                + "\n\n  Last 5 matches:\n   " 
                + teams[index].last5
            );
        images[index].setVisible(true);
    }

    /** Shows more stats for chosen team */
    public void showMoreStatsPage(int index){

        moreStatsButton.setBackground(myOrange);
        moreStatsButton.setVisible(false);
        topBarTeams.setBackground(Color.white);

        // Hiding things from previous page
        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);

        }
        ///////////////////////////////////
        
        ImageIcon resizedLogo = new ImageIcon("images/" + teams[index].ab + ".png");
        resizedLogo = new ImageIcon(resizedLogo.getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        moreStatsTeamLogo.setIcon(resizedLogo);
        moreStatsTeamLogo.setBounds((teams[index].name.length()*11)+8, 50, 70, 70);
        moreStatsTeamLogo.setVisible(true);
        frame.add(moreStatsTeamLogo);

        moreStatsTeamName.setText(teams[index].name);
        moreStatsTeamName.setBounds(20, 70, 230, 30);
        moreStatsTeamName.setFont(myFontBigger);
        moreStatsTeamName.setBackground(Color.darkGray);
        moreStatsTeamName.setForeground(Color.white);
        moreStatsTeamName.setVisible(true);
        moreStatsTeamName.setEditable(false);
        frame.add(moreStatsTeamName);

        seasonPerformance.setBounds(70, 185, 200, 200);
        seasonPerformance.setFont(myFontLighterBigger);
        seasonPerformance.setBackground(new Color(30, 30, 30));
        seasonPerformance.setForeground(Color.white);
        seasonPerformance.setVisible(true);
        seasonPerformance.setEditable(false);
        frame.add(seasonPerformance);

        dataPoints = new JLabel[teams[index].allSeasonMatches.length];
        ImageIcon greenPoint = new ImageIcon("images/greenpoint.png");
        ImageIcon redPoint = new ImageIcon("images/redpoint.png");
        int wins = 0;
        int losses = 0;
        double winPct = 0.0;

        // Draw data for all games in the season for this team. Green point means win, red means loss. Win increases Y by 6 pixels and loss decreases by 6
        for(int i = 0; i < teams[index].allSeasonMatches.length; i++){

            dataPoints[i] = new JLabel();

            if(teams[index].allSeasonMatches[i].equals("W")){
    
                wins++;
                
                dataPoints[i].setIcon(greenPoint);

                if(i == 0){
                    dataPoints[i].setBounds(270, 280, 5, 5);
                } else {
                    dataPoints[i].setBounds(dataPoints[i-1].getX()+9, dataPoints[i-1].getY()-6, 5, 5);
                }
                dataPoints[i].setVisible(true);
                frame.add(dataPoints[i]);

            } else {

                losses++;

                dataPoints[i].setIcon(redPoint);

                if(i == 0){
                    dataPoints[i].setBounds(270, 280, 5, 5);
                } else {
                    dataPoints[i].setBounds(dataPoints[i-1].getX()+9, dataPoints[i-1].getY()+6, 5, 5);
                }
                dataPoints[i].setVisible(true);
                frame.add(dataPoints[i]);

            }
        }


        winPct = (double)wins/(double)teams[index].allSeasonMatches.length*100;
        DecimalFormat df = new DecimalFormat("0.00");

        seasonPerformance.setText("Season performance" + "\n\n Wins: " + wins + "\n\n Losses: " + losses + "\n\n Win %: " + df.format(winPct));
  
        dataPointsBackground.setBounds(40, 145, 500, 300);
        dataPointsBackground.setBackground(new Color(30, 30, 30));
        dataPointsBackground.setVisible(true);
        dataPointsBackground.setEditable(false);
        dataPointsBackground.setBorder(BorderFactory.createLineBorder(myOrange));
        frame.add(dataPointsBackground);
    }

    /** Shows the teams page */
    public void showTeamsPage(){

        moreStatsButton.setBackground(Color.white);
        moreStatsButton.setVisible(true);
        topBarTeams.setBackground(myOrange);

        // Hiding things from previous page
        for(int i = 0; i < dataPoints.length; i++){

            dataPoints[i].setVisible(false);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(true);
            images[teamIndex].setVisible(false);
        }

        moreStatsTeamLogo.setVisible(false);
        moreStatsTeamName.setVisible(false);
        seasonPerformance.setVisible(false);
        dataPointsBackground.setVisible(false);
        ///////////////////////////////////

        infoArray[currentSelectedTeamIndex].setVisible(true);
        images[currentSelectedTeamIndex].setVisible(true);
    }
}
