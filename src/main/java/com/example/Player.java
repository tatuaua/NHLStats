package com.example;

public class Player implements Comparable<Player>{

    String name = "";
    String playerId = "";
    String position = "";
    int points = 0;
    int goals = 0;
    int assists = 0;
    /** Points per game this season */
    double ppg;
    /** Average points per game of career */
    double historicalPpg;

    Player(){

    }

    Player(String name, String playerId, String position){

        this.name = name;
        this.playerId = playerId;

        if(position.equals("L") || position.equals("C") || position.equals("R")){
            this.position = "Offense";

        } else if(position.equals("G")){
            this.position = "Goalie";
        
        } else {
            this.position = "Defense";
        }
    }

    @Override
    public int compareTo(Player o) {
        return this.goals - o.goals;
    }
    
}
