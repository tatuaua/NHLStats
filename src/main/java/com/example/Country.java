package com.example;

import java.util.ArrayList;

public class Country {

    /** Country code eg. "FIN" */
    String code;
    /** List of players born in this country */
    ArrayList<Player> players;
    /** Average ppg for all players born in this country */
    double avgPpg;

    Country(String code){
        this.code = code;
    }
    
}
