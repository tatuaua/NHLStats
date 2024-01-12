package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class Betting {

    static final int TEAM_AMOUNT = 32;
    // I hope that the teams are populated at this point
    static Team[] teams = APICommunication.getTeams();
    static int checkedBetWon;

    /** Part of the betting game, rewrites the bet.txt file */ 
    public static void addBet(String winOrLoss, String teamAb, String amount){

        String fileName = "src\\main\\resources\\bet.txt";
        String date = LocalDate.now().toString();

        try {

            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(winOrLoss + ":" + teamAb  + ":" + date + ":" + amount);
            bufferedWriter.close();
            System.out.println("Added bet: " + winOrLoss + ":" + teamAb + ":" + date + ":" + amount);
            TLog.info("Added bet: " + winOrLoss + ":" + teamAb + ":" + date + ":" + amount);

        } catch (IOException e) {
            System.err.println("ERROR: problem writing to the bet file");
            TLog.error("Problem writing to the bet file");
            e.printStackTrace();
        }
    }

    /** Part of the betting game, checks the bet.txt file and updates the points accordingly */
    public static void checkBet() throws URISyntaxException, IOException{

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        String data = "";

        try {
            File myObj = new File("src\\main\\resources\\bet.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Problem reading bet file");
            TLog.error("Problem reading bet file");
            e.printStackTrace();
        }

        if(data.length() == 0){
            System.out.println("No active bet");
            TLog.info("No active bet");
            return;
        }

        String winOrLose = data.split(":")[0];
        String teamAb = data.split(":")[1];

        for(int i = 0; i < TEAM_AMOUNT; i++){
        }
        
        String betDate = data.split(":")[2];
        int amount = Integer.parseInt(data.split(":")[3]);
        
        for(int i = 0; i < TEAM_AMOUNT; i++){
            
            if(teams[i].ab.equals(teamAb)){

                LocalDate lastGameDate = LocalDate.parse(teams[i].lastGameDate, formatter);
                LocalDate betDatee = LocalDate.parse(betDate, formatter);

                if(lastGameDate.isAfter(betDatee)){
                    if(teams[i].allSeasonMatches[teams[i].allSeasonMatches.length-1].equals("W") && winOrLose.equals("W")){
                        checkedBetWon = 1;
                        changePoints(amount);
                        clearBet();
                    } else if (teams[i].allSeasonMatches[teams[i].allSeasonMatches.length-1].equals("L") && winOrLose.equals("L")){
                        checkedBetWon = 1;
                        changePoints(amount);
                        clearBet();
                    } else {
                        checkedBetWon = 2;
                        changePoints(-amount);
                        clearBet();
                    }
                } else {
                    checkedBetWon = 0;
                    System.out.println("Game has not been played yet");
                }
            }
        }
    }

    /** Part of the betting game, changes the user's points (points.txt). Returns the new points amount */
    public static int changePoints(int modifier){

        String data = "";

        try {

            File myObj = new File("src\\main\\resources\\points.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Problem reading points file");
            TLog.error("Problem reading points file");
            e.printStackTrace();
        }

        int currPoints = Integer.parseInt(data);

        int newAmount = currPoints+modifier;

        try {

            FileWriter fileWriter = new FileWriter("src\\main\\resources\\points.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("" + newAmount);
            bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("ERROR: Problem writing to points file");
            TLog.error("Problem writing to points file");
            e.printStackTrace();
        }

        return newAmount;
    }

    /** Clears the bet file if the active bet was concluded */
    private static void clearBet(){

        String fileName = "src\\main\\resources\\bet.txt";

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("");
            bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("ERROR: problem clearing the bet file");
            TLog.error("Problem clearing the bet file");
            e.printStackTrace();
        }

        TLog.info("Bet cleared");
        System.out.println("Bet cleared");

    }
}