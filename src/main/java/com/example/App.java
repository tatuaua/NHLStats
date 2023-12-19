package com.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONException;
import java.awt.Image;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class App implements ActionListener{

    final int TEAM_AMOUNT = 32; // Have to change this if a new NHL team is born

    //Part of the teams page
    JButton teamButton, moreStatsButton;
    JButton betButtonW = new JButton("W");
    JButton betButtonL = new JButton("L");
    JButton[] teamButtons = new JButton[TEAM_AMOUNT];
    JTextArea[] infoArray = new JTextArea[TEAM_AMOUNT];
    JButton githubButton = new JButton("");

    // Part of the more stats page
    JTextPane playerInfo = new JTextPane();
    JLabel[] dataPoints;
    JTextPane seasonPerformance = new JTextPane();
    JTextArea moreStatsTeamName = new JTextArea();
    JLabel moreStatsTeamLogo = new JLabel();
    JTextArea dataPointsBackground = new JTextArea();
    JTextPane rosterTitle = new JTextPane();
    JTextPane roster = new JTextPane();
    JTextArea rosterBackground = new JTextArea();
    JTextField rosterSearch = new JTextField();
    JButton rosterSearchButton = new JButton("");

    //Part of the leaderboards page
    JTextPane goalsLeadersTitle = new JTextPane();
    JTextPane pointsLeadersTitle = new JTextPane();
    JTextArea goalsLeaders = new JTextArea();
    JTextArea pointsLeaders = new JTextArea();

    // General variables
    DecimalFormat df = new DecimalFormat("0.00");
    static JFrame frame;
    JLabel topBar;
    JButton topBarTeams, topBarLeaderboards;
    JTextPane topBarBettingPoints;
    Team[] teams = new Team[TEAM_AMOUNT];
    JLabel[] images = new JLabel[TEAM_AMOUNT];
    int currentSelectedTeamIndex;
    int currentPage = 0;
    
    Font myFont = new Font(null, Font.BOLD, 15);
    Font myFontBigger = new Font(null, Font.BOLD, 20);
    Font myFontLighter = new Font(null, Font.PLAIN, 15);
    Font myFontLighterBigger = new Font(null, Font.PLAIN, 20);
    Color myOrange = new Color(248,158,124);
    Color myDarkGray = new Color(30, 30, 30);

    /** Defining visible elements of the app */
    App() throws IOException, URISyntaxException{

        TLog.info("\n\n---------------------LAUNCHING APPLICATION\n");

        frame = new JFrame("NHL Stats");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(650, 850);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setResizable(false);
        frame.setAutoRequestFocus(true);
        setKeyListeners();

        // No idea what these do
        frame.getContentPane().setFocusable(true);
        frame.getContentPane().setFocusTraversalKeysEnabled(false);

        // Configuring tooltips (when you hover over an element it gives you a hint, this customizes the popup box)
        UIManager.put("ToolTip.background", Color.gray);
        UIManager.put("ToolTip.border", new LineBorder(myOrange));
        UIManager.put("Tooltip.foreground", Color.white);

        DataFetcher.fetchAndParse();
        teams = DataFetcher.getTeams();

        // Check the status of the currently active bet and set the points accordingly

        topBarTeams = new JButton("Teams");
        topBarTeams.setBounds(20, 10, 80, 30);
        topBarTeams.setForeground(myOrange);
        topBarTeams.setBackground(myDarkGray);
        topBarTeams.addActionListener(this);
        topBarTeams.setBorder(new RoundedBorder(20));
        topBarTeams.setFocusPainted(false);
        frame.add(topBarTeams);
        topBarTeams.setVisible(true);

        topBarLeaderboards = new JButton("Leaderboards");
        topBarLeaderboards.setBounds(120, 10, 150, 30);
        topBarLeaderboards.setForeground(myOrange);
        topBarLeaderboards.setBackground(myDarkGray);
        topBarLeaderboards.addActionListener(this);
        topBarLeaderboards.setBorder(new RoundedBorder(20));
        topBarLeaderboards.setFocusPainted(false);
        frame.add(topBarLeaderboards);
        topBarLeaderboards.setVisible(true);

        Betting.checkBet();
        int points = Betting.changePoints(0); //TODO

        topBarBettingPoints = new JTextPane();
        topBarBettingPoints.setText(Integer.toString(points));
        topBarBettingPoints.setFont(myFontBigger);
        topBarBettingPoints.setBounds(590, 10, 50, 30);
        if(Betting.checkedBetWon == 1){
            topBarBettingPoints.setForeground(Color.green);
        } else if (Betting.checkedBetWon == 2){
            topBarBettingPoints.setForeground(Color.red);
        } else {
            topBarBettingPoints.setForeground(Color.white);
        }
        topBarBettingPoints.setBackground(myDarkGray);
        frame.add(topBarBettingPoints);

        betButtonW.setBounds(310, 305, 30, 30);
        betButtonW.setFont(new Font(null, Font.BOLD, 10));
        betButtonW.setForeground(Color.green);
        betButtonW.setBackground(myDarkGray);
        betButtonW.addActionListener(this);
        betButtonW.setBorder(new RoundedBorder(10));
        betButtonW.setFocusPainted(false);
        betButtonW.setToolTipText(" Bet 10 points that this team will win their next match ");
        frame.add(betButtonW);
        betButtonW.setVisible(true);

        betButtonL.setBounds(345, 305, 30, 30);
        betButtonL.setFont(new Font(null, Font.BOLD, 8));
        betButtonL.setForeground(Color.red);
        betButtonL.setBackground(myDarkGray);
        betButtonL.addActionListener(this);
        betButtonL.setBorder(new RoundedBorder(10));
        betButtonL.setFocusPainted(false);
        betButtonL.setToolTipText(" Bet 10 points that this team will lose their next match ");
        frame.add(betButtonL);
        betButtonL.setVisible(true);

        moreStatsButton = new JButton("More stats");
        moreStatsButton.setBounds(310, 555, 120, 30);
        moreStatsButton.setForeground(myOrange);
        moreStatsButton.setBackground(myDarkGray);
        moreStatsButton.addActionListener(this);
        moreStatsButton.setBorder(new RoundedBorder(20));
        moreStatsButton.setFocusPainted(false);
        frame.add(moreStatsButton);
        moreStatsButton.setVisible(true);

        ImageIcon githubIcon = new ImageIcon("images/github.png");
        githubIcon = new ImageIcon(githubIcon.getImage().getScaledInstance(80, 40, Image.SCALE_SMOOTH));
        githubButton.setIcon(githubIcon);
        githubButton.setBorder(null);
        githubButton.setBackground(Color.darkGray);
        githubButton.setBounds(550, 750, 80, 40);
        githubButton.addActionListener(this);
        frame.add(githubButton);
        githubButton.setVisible(true);

        ImageIcon topBarImg = new ImageIcon("images/topBar.png");
        topBar = new JLabel(topBarImg);
        topBar.setBounds(0, 0, 650, 50);
        topBar.setBackground(myDarkGray);
        frame.add(topBar);
        topBar.setVisible(true);

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the images from a local folder

            ImageIcon img = new ImageIcon("images/" + teams[teamIndex].ab + ".png");
            images[teamIndex] = new JLabel();
            images[teamIndex].setIcon(img);
            images[teamIndex].setBounds(275, 330, 270, 200);
            frame.add(images[teamIndex]);
            images[teamIndex].setVisible(false);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the team name buttons

            teamButton = new JButton(teams[teamIndex].name + " (" + teams[teamIndex].points + ")");
            teamButton.setBounds(30, teamIndex*21+90, 200, 20);
            teamButton.setForeground(Color.white);
            teamButton.setBackground(null);
            teamButton.setFont(myFont);
            teamButton.setBorder(null);
            teamButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            teamButton.addActionListener(this);
            teamButtons[teamIndex] = teamButton;
            frame.add(teamButton);
        }
        
        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex] = new JTextArea();
            infoArray[teamIndex].setBounds(300, 95, 250, 500);
            infoArray[teamIndex].setBackground(myDarkGray);
            infoArray[teamIndex].setForeground(Color.white);
            infoArray[teamIndex].setFont(myFontLighter);
            infoArray[teamIndex].setEditable(false);
            infoArray[teamIndex].setBorder(BorderFactory.createLineBorder(myOrange));
            frame.add(infoArray[teamIndex]);
            infoArray[teamIndex].setVisible(false);
        }

        // Configuring buttons (when you hover over a button, the cursor changes to a hand)
        topBarTeams.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moreStatsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        betButtonW.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        betButtonL.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rosterSearchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        githubButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        showTeamInfo(0);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ImageIcon topLeftIcon = new ImageIcon("images/nhlstatslogo.png");
        frame.setIconImage(topLeftIcon.getImage());

        Action action = new AbstractAction() // Allows the user to press enter to search
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try {
                    //showPlayerInfo(rosterSearch.getText());
                    rosterSearchButton.doClick();
                } catch (JSONException e1) {
                    System.out.println("ERROR: problem showing player info when pressing enter");
                    TLog.error("Problem showing player info when pressing enter");
                    e1.printStackTrace();
                }
            }
        };

        rosterSearch.addActionListener(action);
        rosterSearch.getDocument().addDocumentListener(new DocumentListener() { // Empties the playerinfo when a change is noticed

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateToolTip();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateToolTip();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events
            }

            private void updateToolTip() {
                playerInfo.setText("");
            }
        });
    }


    /** Starts an instance of the app */
    public static void main(String[] args) throws IOException, URISyntaxException{ 

        @SuppressWarnings("unused")
        App e = new App();

    }

    /** Handle clicks */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == betButtonW){
            Betting.addBet("W", teams[currentSelectedTeamIndex].ab, "10");
        }

        if(e.getSource() == betButtonL){
            Betting.addBet("L", teams[currentSelectedTeamIndex].ab, "10");
        }

        if(e.getSource() == moreStatsButton && currentPage != 1){

            if(currentPage == 2){
                hideLeaderboardsPageElements();
            } else {
                hideTeamsPageElements();
            }
            currentPage = 1;
            try {

                showMoreStatsPage(currentSelectedTeamIndex);

            } catch (JSONException | IOException | URISyntaxException e1) {
                System.out.println("ERROR: Problem showing more stats page");
                TLog.error("Problem showing more stats page");
                e1.printStackTrace();
            }
        }

        if(e.getSource() == topBarTeams && currentPage != 0){

            if(currentPage == 2){
                hideLeaderboardsPageElements();
            } else {
                hideMoreStatsPageElements();
            }

            currentPage = 0;
            showTeamsPage();
        }

        if(e.getSource() == topBarLeaderboards && currentPage != 2){

            if(currentPage == 0){
                hideTeamsPageElements();
            } else {
                hideMoreStatsPageElements();
            }

            currentPage = 2;

            showLeaderboardsPage();
        }

        if(e.getSource() == githubButton){

            try {
                openGitHub();
            } catch (IOException | URISyntaxException e1) {
                System.out.println("ERROR: Problem opening github link");
                TLog.error("Problem opening github link");
                e1.printStackTrace();
            }
        }

        if(e.getSource() == rosterSearchButton){

            String searched = rosterSearch.getText();

            if(searched.length() > 50){
                searched = searched.substring(0, 50);
            }

            System.out.println("Searching for " + searched);
            TLog.info("Searching for " + searched);

            try {
                showPlayerInfo(searched);
            } catch (JSONException | IOException | URISyntaxException e1) {
                System.out.println("Problem showing player info");
                TLog.error("Problem showing player info");
            e1.printStackTrace();
            }
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Doing this with a loop to avoid code bloat
            if(e.getSource() == teamButtons[teamIndex]){

                try {

                    showTeamInfo(teamIndex);
                    currentSelectedTeamIndex = teamIndex;

                } catch (IOException | URISyntaxException e1) {
                    System.out.println("ERROR: problem showing team info");
                    TLog.error("Problem showing team info");
                    e1.printStackTrace();
                }
            } else {
                teamButtons[teamIndex].setForeground(Color.white);
            }
        }
    }

    /** Shows the team info for a specified index */
    private void showTeamInfo(int index) throws IOException, URISyntaxException{

        String bonusMsg = "";

        if(teams[index].points == teams[0].points){
            bonusMsg = " (Leading the NHL)";
        }

        teamButtons[index].setForeground(myOrange);

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);
        }
        
        infoArray[index].setVisible(true);
        infoArray[index].setText(
                "\n  "
                + teams[index].name 
                + "\n\n  Current team points:\n   " 
                + teams[index].points + bonusMsg
                + "\n\n  Upcoming match:\n   " 
                + teams[index].nextMatch 
                + "\n\n  Place bet:\n   " 
            );
        images[index].setVisible(true);
    }

    /** Shows more stats for chosen team */
    private void showMoreStatsPage(int index) throws JSONException, IOException, URISyntaxException{

        moreStatsButton.setBackground(myDarkGray);
        topBarTeams.setBackground(myDarkGray);
        
        ImageIcon resizedLogo = new ImageIcon("images/" + teams[index].ab + ".png");
        resizedLogo = new ImageIcon(resizedLogo.getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        moreStatsTeamLogo.setIcon(resizedLogo);
        moreStatsTeamLogo.setBounds((teams[index].name.length()*11)+26, 75, 70, 70);
        frame.add(moreStatsTeamLogo);
        moreStatsTeamLogo.setVisible(true);

        moreStatsTeamName.setText(teams[index].name);
        moreStatsTeamName.setBounds(38, 95, 230, 30);
        moreStatsTeamName.setFont(myFontBigger);
        moreStatsTeamName.setBackground(Color.darkGray);
        moreStatsTeamName.setForeground(Color.white);
        moreStatsTeamName.setEditable(false);
        frame.add(moreStatsTeamName);
        moreStatsTeamName.setVisible(true);

        seasonPerformance.setBounds(70, 170, 130, 220);
        seasonPerformance.setFont(myFontLighterBigger);
        seasonPerformance.setBackground(myDarkGray);
        seasonPerformance.setForeground(Color.white);
        seasonPerformance.setEditable(false);
        frame.add(seasonPerformance);
        seasonPerformance.setVisible(true);

        dataPoints = new JLabel[teams[index].allSeasonMatches.length];
        ImageIcon greenPoint = new ImageIcon("images/greenpoint.png");
        ImageIcon redPoint = new ImageIcon("images/redpoint.png");
        int wins = 0;
        int losses = 0;
        double winPct = 0.0;

        // Draw data for all games in the season for this team. Green point means win, red means loss. Win increases Y by 5 pixels and loss decreases by 5
        for(int matchIndex = 0; matchIndex < teams[index].allSeasonMatches.length; matchIndex++){

            dataPoints[matchIndex] = new JLabel();

            if(teams[index].allSeasonMatches[matchIndex].equals("W")){
    
                wins++;
                
                dataPoints[matchIndex].setIcon(greenPoint);

                if(matchIndex == 0){
                    dataPoints[matchIndex].setBounds(230, 280, 5, 5);
                } else {
                    dataPoints[matchIndex].setBounds(dataPoints[matchIndex-1].getX()+6, dataPoints[matchIndex-1].getY()-5, 5, 5);
                }
                frame.add(dataPoints[matchIndex]);
                dataPoints[matchIndex].setVisible(true);

            } else {

                losses++;

                dataPoints[matchIndex].setIcon(redPoint);

                if(matchIndex == 0){
                    dataPoints[matchIndex].setBounds(230, 280, 5, 5);
                } else {
                    dataPoints[matchIndex].setBounds(dataPoints[matchIndex-1].getX()+6, dataPoints[matchIndex-1].getY()+5, 5, 5);
                }
                frame.add(dataPoints[matchIndex]);
                dataPoints[matchIndex].setVisible(true);

            }
        }


        winPct = (double)wins/(double)teams[index].allSeasonMatches.length*100;

        seasonPerformance.setText("\n\nWins: " + wins + "\n\nLosses: " + losses + "\n\nWin %: " + df.format(winPct));
  
        dataPointsBackground.setBounds(40, 145, 500, 300);
        dataPointsBackground.setBackground(myDarkGray);
        dataPointsBackground.setEditable(false);
        dataPointsBackground.setBorder(BorderFactory.createLineBorder(myOrange));
        frame.add(dataPointsBackground);
        dataPointsBackground.setVisible(true);

        // Roster info //

        rosterTitle.setText(teams[index].name + " roster:");
        rosterTitle.setBounds(70, 480, 440, 50);
        rosterTitle.setFont(myFontLighterBigger);
        rosterTitle.setBackground(myDarkGray);
        rosterTitle.setForeground(Color.white);
        rosterTitle.setEditable(false);
        frame.add(rosterTitle);
        rosterTitle.setVisible(true);

        String[] positions = {"Offense", "Defense", "Goalie"};
        roster.setText("");

        for(String position : positions){ // I could make them separate text elements but wheres the fun in that

            for(int j = 0; j < teams[index].roster.length; j++){

                if(j == 0 && !position.equals("Offense")){
                    roster.setText(roster.getText() + "\n\n");
                }

                if(j == 0){
                    roster.setText(roster.getText() + position + ": ");
                }

                if(teams[index].roster[j].position.equals(position)){
                    String fullName = teams[index].roster[j].name;
                    roster.setText(roster.getText() + fullName.substring(fullName.indexOf(" ") + 1) + ", ");
                }
            }
        }

        roster.setText(roster.getText().substring(0, roster.getText().length()-2)); // Removes last comma
        
        roster.setBounds(70, 530, 440, 170);
        roster.setFont(myFontLighter);
        roster.setBackground(myDarkGray);
        roster.setForeground(Color.white);
        roster.setEditable(false);
        frame.add(roster);
        roster.setVisible(true);

        rosterSearch.setBounds(70, 700, 200, 30);
        rosterSearch.setFont(myFontLighter);
        rosterSearch.setBackground(myDarkGray);
        rosterSearch.setForeground(Color.white);
        rosterSearch.setBorder(BorderFactory.createLineBorder(myOrange));
        rosterSearch.setEditable(true);
        rosterSearch.setText("Insert player name");
        frame.add(rosterSearch);
        rosterSearch.setVisible(true);

        ImageIcon searchIcon = new ImageIcon("images/search.png");
        searchIcon = new ImageIcon(searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        rosterSearchButton.setIcon(searchIcon);
        rosterSearchButton.setBorder(null);
        rosterSearchButton.setBackground(myDarkGray);
        rosterSearchButton.setBounds(275, 700, 30, 30);
        rosterSearchButton.addActionListener(this);
        frame.add(rosterSearchButton);
        rosterSearchButton.setVisible(true);

        rosterBackground.setBounds(40, dataPointsBackground.getY()+310, 500, 330);
        rosterBackground.setBackground(myDarkGray);
        rosterBackground.setEditable(false);
        rosterBackground.setBorder(BorderFactory.createLineBorder(myOrange));
        frame.add(rosterBackground);
        rosterBackground.setVisible(true);

    }

    /** Shows player info for a given name */ 
    private void showPlayerInfo(String name) throws JSONException, IOException, URISyntaxException{

        Player foundPlayer = new Player();

        boolean exactMatch = false;
        for(Player player : teams[currentSelectedTeamIndex].roster){
            if(player.name.equalsIgnoreCase(name) || player.name.split(" ")[1].equalsIgnoreCase(name)){ // Also works with only last name
                foundPlayer = player;
                exactMatch = true;
                System.out.println("Player " + player.name + " adhered to the search term: " + name);
                TLog.info("Player " + player.name + " adhered to the search term: " + name);
            }
        }

        playerInfo.setBounds(315, 665, 200, 60);
        playerInfo.setFont(myFontLighter);
        playerInfo.setBackground(myDarkGray);
        playerInfo.setForeground(Color.white);
        playerInfo.setEditable(false);
        frame.add(playerInfo);
        playerInfo.setVisible(true);

        if(exactMatch){

            playerInfo.setText(
                "G: " + foundPlayer.goals + ", A: " + foundPlayer.assists + ", P: " + foundPlayer.points 
                + "\nSeason PPG: " + df.format(foundPlayer.ppg)
                + "\nAll time PPG: " + df.format(foundPlayer.historicalPpg)
            );

        } else {

            // Fuzzy string search and call showplayerinfo with closest string
            List<String> list = new ArrayList<String>();
            for(int playerIndex = 0; playerIndex < teams[currentSelectedTeamIndex].roster.length; playerIndex++){
                list.add(teams[currentSelectedTeamIndex].roster[playerIndex].name);
            }

            Comparator<String> fuzzyStringComparator = Comparator.comparingInt(s -> calculateLevenshteinDistance(name, s));
            Collections.sort(list, fuzzyStringComparator);

            playerInfo.setText("\nDid you mean:\n" + list.get(0));
        }
    }


    /** Shows the teams page */
    private void showTeamsPage(){

        moreStatsButton.setBackground(myDarkGray);
        moreStatsButton.setVisible(true);
        topBarTeams.setBackground(myDarkGray);

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){
            teamButtons[teamIndex].setVisible(true);
        }

        teamButtons[currentSelectedTeamIndex].setForeground(myOrange);
        infoArray[currentSelectedTeamIndex].setVisible(true);
        images[currentSelectedTeamIndex].setVisible(true);
        betButtonW.setVisible(true);
        betButtonL.setVisible(true);
    } 

    private void showLeaderboardsPage(){

        List<Player> list = new ArrayList<Player>();
        for(int teamIndex = 0; teamIndex < 32; teamIndex++){
            for(int playerIndex = 0; playerIndex < teams[teamIndex].roster.length; playerIndex++){
                list.add(teams[teamIndex].roster[playerIndex]);
            }
        }

        // Sorts all players based on goals, highest to lowest
        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o2.goals-o1.goals;
            }
        });

        StringBuilder top10goal = new StringBuilder();
        for(int i = 0; i < 10; i++){
            top10goal.append(" " + list.get(i).name + " (" + list.get(i).goals + ") \n");
        }

        // Sorts all players based on points, highest to lowest
        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o2.points-o1.points;
            }
        });

        StringBuilder top10point = new StringBuilder();
        for(int i = 0; i < 10; i++){
            top10point.append(list.get(i).name + " (" + list.get(i).points + ") \n");
        }

        goalsLeadersTitle.setText("Goals leaders:");
        goalsLeadersTitle.setBounds(70, 170, 200, 30);
        goalsLeadersTitle.setFont(myFontLighterBigger);
        goalsLeadersTitle.setBackground(myDarkGray);
        goalsLeadersTitle.setForeground(Color.white);
        goalsLeadersTitle.setEditable(false);
        frame.add(goalsLeadersTitle);
        goalsLeadersTitle.setVisible(true);

        goalsLeaders.setText(top10goal.toString());
        goalsLeaders.setBounds(70, 200, 200, 200);
        goalsLeaders.setFont(myFontLighter);
        goalsLeaders.setBackground(myDarkGray);
        goalsLeaders.setForeground(Color.white);
        goalsLeaders.setEditable(false);
        frame.add(goalsLeaders);
        goalsLeaders.setVisible(true);

        pointsLeadersTitle.setText("Points leaders:");
        pointsLeadersTitle.setBounds(270, 170, 200, 30);
        pointsLeadersTitle.setFont(myFontLighterBigger);
        pointsLeadersTitle.setBackground(myDarkGray);
        pointsLeadersTitle.setForeground(Color.white);
        pointsLeadersTitle.setEditable(false);
        frame.add(pointsLeadersTitle);
        pointsLeadersTitle.setVisible(true);

        pointsLeaders.setText(top10point.toString());
        pointsLeaders.setBounds(270, 200, 200, 200);
        pointsLeaders.setFont(myFontLighter);
        pointsLeaders.setBackground(myDarkGray);
        pointsLeaders.setForeground(Color.white);
        pointsLeaders.setEditable(false);
        frame.add(pointsLeaders);
        pointsLeaders.setVisible(true);
    
    }

    /** Hides all elements of the moreStatsPage */
    private void hideMoreStatsPageElements() {

        moreStatsButton.setBackground(myDarkGray);
        moreStatsButton.setVisible(false);
        topBarTeams.setBackground(myDarkGray);

        moreStatsTeamLogo.setVisible(false);
        moreStatsTeamName.setVisible(false);
        seasonPerformance.setVisible(false);
        dataPointsBackground.setVisible(false);

        for (int i = 0; i < dataPoints.length; i++) {
            dataPoints[i].setVisible(false);
        }

        rosterTitle.setVisible(false);
        roster.setVisible(false);
        rosterBackground.setVisible(false);
        rosterSearch.setVisible(false);
        rosterSearchButton.setVisible(false);
        playerInfo.setVisible(false);
    }

    /** Hides all elements of the teamsPage */
    private void hideTeamsPageElements() {

        for (int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++) {
            infoArray[teamIndex].setVisible(false);
            teamButtons[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);
        }
        
        moreStatsButton.setVisible(false);
        betButtonW.setVisible(false);
        betButtonL.setVisible(false);

    }

    /** Hides all elements of the leaderBoardsPage */
    private void hideLeaderboardsPageElements() {

        goalsLeadersTitle.setVisible(false);
        goalsLeaders.setVisible(false);
        pointsLeadersTitle.setVisible(false);
        pointsLeaders.setVisible(false);
    }


    /** Opens the GitHub page of this project in the default browser */
    private void openGitHub() throws IOException, URISyntaxException{

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://github.com/tatuaua/NHLStats"));
        }
    }

    /** Allows user to use arrow keys to traverse teams */
    private void setKeyListeners(){

        frame.getContentPane().addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                    if(currentSelectedTeamIndex == teamButtons.length-1){ // If user goes too low
                        currentSelectedTeamIndex = 0;
                        teamButtons[currentSelectedTeamIndex].doClick();
                    } else {
                        teamButtons[currentSelectedTeamIndex+1].doClick();
                    }
                }
            }
        });

        frame.getContentPane().addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    
                    if(currentSelectedTeamIndex <= 0){ // If user goes over the top
                        currentSelectedTeamIndex = teamButtons.length-1;
                        teamButtons[currentSelectedTeamIndex].doClick();
                    } else {
                        teamButtons[currentSelectedTeamIndex-1].doClick();
                    }
                }
            }
        });
    }

    private static int calculateLevenshteinDistance(String str1, String str2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return levenshteinDistance.apply(str1, str2);
    }
}

