package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Scanner;

class Betting {

    static final int TEAM_AMOUNT = 32;
    // I hope that the teams are populated at this point
    static Team[] teams = DataFetcher.getTeams();

    /** Part of the betting game, rewrites the bet.txt file */ 
    public static void addBet(String winOrLoss, String teamAb, String amount){

        String fileName = "src\\main\\resources\\bet.txt";
        String date = LocalDate.now().toString();

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(winOrLoss + ":" + teamAb  + ":" + date + ":" + amount);
            bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("ERROR: problem writing to the bet file");
            e.printStackTrace();
        }
    }

    /** Part of the betting game, checks the bet.txt file and updates the points accordingly */
    public static void checkBet() throws URISyntaxException, IOException{
        
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
            e.printStackTrace();
        }

        if(data.length() == 0){
            System.out.println("No active bet");
            return;
        }

        String winOrLose = data.split(":")[0];
        String teamAb = data.split(":")[1];

        for(int i = 0; i < TEAM_AMOUNT; i++){
        }
        
        String date = data.split(":")[2];
        int amount = Integer.parseInt(data.split(":")[3]);

        int betDateToNum = Integer.parseInt(date.split("-")[0]) * Integer.parseInt(date.split("-")[1]) * Integer.parseInt(date.split("-")[2]);
        
        for(int i = 0; i < TEAM_AMOUNT; i++){
            
            if(teams[i].ab.equals(teamAb)){
                if(teams[i].lastGameDateToNum > betDateToNum){
                                                    //[5] because last5 has leading whitespace 
                    if(teams[i].last5.split(" ")[5].equals("W") && winOrLose.equals("W")){
                        System.out.println("You had a bet: " + data + " and you won!");
                        changePoints(amount);
                        clearBet();
                    } else if (teams[i].last5.split(" ")[5].equals("L") && winOrLose.equals("L")){
                        System.out.println("You had a bet: " + data + " and you won!");
                        changePoints(amount);
                        clearBet();
                    } else {
                        System.out.println("You had a bet: " + data + " and you lost!");
                        changePoints(-amount);
                        clearBet();
                    }
                } else {
                    System.out.println("Game has not been played yet");
                }
            }
        }
    }

    /** Part of the betting game, changes the user's points (points.txt) */
    public static void changePoints(int modifier){

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
            e.printStackTrace();
        }

        int currPoints = Integer.parseInt(data);

        int newAmount = currPoints+modifier;

        String fileName = "src\\main\\resources\\points.txt";

        try {

            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("" + newAmount);
            bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("ERROR: Problem writing to points file");
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }

        System.out.println("Bet cleared");

    }

}