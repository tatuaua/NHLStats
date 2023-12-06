package com.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONException;
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


    // General variables
    DecimalFormat df = new DecimalFormat("0.00");
    static JFrame frame;
    JLabel topBar;
    JButton topBarTeams, moreStatsButton;
    Team[] teams = new Team[TEAM_AMOUNT];
    JLabel[] images = new JLabel[TEAM_AMOUNT];
    int currentSelectedTeamIndex;
    int currentPage = 0;
    String lastSearch;
    
    Font myFont = new Font(null, Font.BOLD, 15);
    Font myFontBigger = new Font(null, Font.BOLD, 20);
    Font myFontLighter = new Font(null, Font.PLAIN, 15);
    Font myFontLighterBigger = new Font(null, Font.PLAIN, 20);
    Color myOrange = new Color(248,158,124);
    Color myDarkGray = new Color(30, 30, 30);

    /** Defining visible elements of the app */
    App() throws IOException, URISyntaxException{

        APIStuff.populateArrays();
        teams = APIStuff.getTeams();

        frame = new JFrame("NHL Stats");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(650, 850);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        ////////////////////////////

        topBarTeams = new JButton("Teams");
        topBarTeams.setBounds(20, 10, 80, 30);
        topBarTeams.setForeground(myOrange);
        topBarTeams.setBackground(myDarkGray);
        topBarTeams.addActionListener(this);
        topBarTeams.setBorder(new RoundedBorder(20));
        topBarTeams.setFocusPainted(false);
        frame.add(topBarTeams);
        topBarTeams.setVisible(true);

        moreStatsButton = new JButton("More stats");
        moreStatsButton.setBounds(310, 555, 120, 30);
        moreStatsButton.setForeground(myOrange);
        moreStatsButton.setBackground(myDarkGray);
        moreStatsButton.addActionListener(this);
        moreStatsButton.setBorder(new RoundedBorder(20));
        moreStatsButton.setFocusPainted(false);
        frame.add(moreStatsButton);
        moreStatsButton.setVisible(true);

        ImageIcon topBarImg = new ImageIcon("images/topBar.png");
        topBar = new JLabel(topBarImg);
        topBar.setBounds(0, 0, 650, 50);
        topBar.setBackground(myDarkGray);
        frame.add(topBar);
        topBar.setVisible(true);

        ////////////////////////////

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the images from a local folder

            ImageIcon img = new ImageIcon("images/" + teams[teamIndex].ab + ".png");
            images[teamIndex] = new JLabel();
            images[teamIndex].setIcon(img);
            images[teamIndex].setBounds(275, 330, 270, 200);
            frame.add(images[teamIndex]);
            images[teamIndex].setVisible(false);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the team name buttons

            teamNameButton = new JButton(teams[teamIndex].name + " (" + teams[teamIndex].points + ")");
            teamNameButton.setBounds(30, teamIndex*21+90, 200, 20);
            teamNameButton.setForeground(Color.white);
            teamNameButton.setBackground(null);
            teamNameButton.setFont(myFont);
            teamNameButton.setBorder(null);
            teamNameButton.addActionListener(this);
            teamNameButtons[teamIndex] = teamNameButton;
            frame.add(teamNameButton);
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
                    showPlayerInfo(rosterSearch.getText());
                } catch (JSONException | IOException | URISyntaxException e1) {
                    System.out.println("ERROR: problem showing playerinfo when pressing enter");
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

        if(e.getSource() == moreStatsButton && currentPage != 1){
            currentPage = 1;

            try {

                showMoreStatsPage(currentSelectedTeamIndex);

            } catch (JSONException | IOException | URISyntaxException e1) {
                System.out.println("ERROR: problem showing more stats page");
                e1.printStackTrace();
            }
        }

        if(e.getSource() == topBarTeams && currentPage != 0){
            currentPage = 0;
            showTeamsPage();
        }

        if(e.getSource() == rosterSearchButton){

            String searched = rosterSearch.getText();

            System.out.println("Searching for " + searched);

            if(searched.length() == 0){
                rosterSearch.setText("Insert player name");
            } else {

                try {
                    showPlayerInfo(searched);
                } catch (JSONException | IOException | URISyntaxException e1) {
                    System.out.println("Problem showing player info");
                e1.printStackTrace();
            }

            }
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Doing this with a loop to avoid code bloat
            if(e.getSource() == teamNameButtons[teamIndex]){

                try {

                    showTeamInfo(teamIndex);
                    currentSelectedTeamIndex = teamIndex;

                } catch (IOException | URISyntaxException e1) {
                    System.out.println("ERROR: problem showing team info");
                    e1.printStackTrace();
                }
            } else {
                teamNameButtons[teamIndex].setForeground(Color.white);
            }
        }
    }

    /** Shows the team info for a specified index */
    public void showTeamInfo(int index) throws IOException, URISyntaxException{

        System.out.println(teams[index].ab);

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
    public void showMoreStatsPage(int index) throws JSONException, IOException, URISyntaxException{

        if(teams[index].roster == null){
            APIStuff.populateTeamRoster(index);
        }

        moreStatsButton.setBackground(myDarkGray);
        moreStatsButton.setVisible(false);
        topBarTeams.setBackground(myDarkGray);

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
        moreStatsTeamLogo.setBounds((teams[index].name.length()*11)+8, 55, 70, 70);
        frame.add(moreStatsTeamLogo);
        moreStatsTeamLogo.setVisible(true);

        moreStatsTeamName.setText(teams[index].name);
        moreStatsTeamName.setBounds(20, 75, 230, 30);
        moreStatsTeamName.setFont(myFontBigger);
        moreStatsTeamName.setBackground(Color.darkGray);
        moreStatsTeamName.setForeground(Color.white);
        moreStatsTeamName.setEditable(false);
        frame.add(moreStatsTeamName);
        moreStatsTeamName.setVisible(true);

        seasonPerformance.setBounds(70, 170, 200, 200);
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

        // Draw data for all games in the season for this team. Green point means win, red means loss. Win increases Y by 6 pixels and loss decreases by 6
        for(int i = 0; i < teams[index].allSeasonMatches.length; i++){

            dataPoints[i] = new JLabel();

            if(teams[index].allSeasonMatches[i].equals("W")){
    
                wins++;
                
                dataPoints[i].setIcon(greenPoint);

                if(i == 0){
                    dataPoints[i].setBounds(270, 280, 5, 5);
                } else {
                    dataPoints[i].setBounds(dataPoints[i-1].getX()+8, dataPoints[i-1].getY()-6, 5, 5);
                }
                frame.add(dataPoints[i]);
                dataPoints[i].setVisible(true);

            } else {

                losses++;

                dataPoints[i].setIcon(redPoint);

                if(i == 0){
                    dataPoints[i].setBounds(270, 280, 5, 5);
                } else {
                    dataPoints[i].setBounds(dataPoints[i-1].getX()+8, dataPoints[i-1].getY()+6, 5, 5);
                }
                frame.add(dataPoints[i]);
                dataPoints[i].setVisible(true);

            }
        }


        winPct = (double)wins/(double)teams[index].allSeasonMatches.length*100;

        seasonPerformance.setText("Season performance:" + "\n\nWins: " + wins + "\n\nLosses: " + losses + "\n\nWin %: " + df.format(winPct));
  
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
                    roster.setText(roster.getText() + teams[index].roster[j].name.split(" ")[1] + ", ");
                }
            }
        }

        roster.setText(roster.getText().substring(0, roster.getText().length()-2));
        
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

        rosterBackground.setBounds(40, dataPointsBackground.getY()+310, 500, 300);
        rosterBackground.setBackground(myDarkGray);
        rosterBackground.setEditable(false);
        rosterBackground.setBorder(BorderFactory.createLineBorder(myOrange));
        frame.add(rosterBackground);
        rosterBackground.setVisible(true);

    }

    /** Shows player info for a given name */ 
    public void showPlayerInfo(String name) throws JSONException, IOException, URISyntaxException{

        Player foundPlayer = new Player();

        for(Player player : teams[currentSelectedTeamIndex].roster){
            if(player.name.equalsIgnoreCase(name) || player.name.split(" ")[1].equalsIgnoreCase(name)){ // Also works with only last name
                foundPlayer = player;
            }
        }

        playerInfo.setBounds(315, 660, 200, 60);
        playerInfo.setFont(myFontLighter);
        playerInfo.setBackground(myDarkGray);
        playerInfo.setForeground(Color.white);
        playerInfo.setEditable(false);
        frame.add(playerInfo);
        playerInfo.setVisible(true);

        if(foundPlayer.playerId.length() != 0){
            if(!name.equals(lastSearch)){  // To avoid spamming API calls, eg. holding enter on the search bar
                APIStuff.populatePlayerInfo(foundPlayer.playerId, currentSelectedTeamIndex);
            }
            playerInfo.setText(
                "G: " + foundPlayer.goals + ", A: " + foundPlayer.assists + ", P: " + foundPlayer.points 
                + "\nSeason PPG: " + df.format(foundPlayer.ppg)
                + "\nAll time PPG: " + df.format(foundPlayer.historicalPpg)
            );

        } else {
            
            playerInfo.setText("\n\nPlayer not found");
        }

        lastSearch = name;
    }


    /** Shows the teams page */
    public void showTeamsPage(){

        moreStatsButton.setBackground(myDarkGray);
        moreStatsButton.setVisible(true);
        topBarTeams.setBackground(myDarkGray);

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
        rosterTitle.setVisible(false);
        roster.setVisible(false);
        rosterBackground.setVisible(false);
        rosterSearch.setVisible(false);
        rosterSearchButton.setVisible(false);
        playerInfo.setVisible(false);
        ///////////////////////////////////

        infoArray[currentSelectedTeamIndex].setVisible(true);
        images[currentSelectedTeamIndex].setVisible(true);
    }
}
