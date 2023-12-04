package com.example;

public class Player {

    String name = "";
    String playerId = "";
    int points = 0;
    int goals = 0;
    int assists = 0;
    /** Points per game this season */
    double ppg;
    /** Average points per game of career */
    double historicalPpg;

    Player(){

    }

    Player(String name, String playerId){

        this.name = name;
        this.playerId = playerId;
    }
    
}
