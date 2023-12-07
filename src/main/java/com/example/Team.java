package com.example;

public class Team {
    
    String name;
    /** Used for the guessing game */
    int lastGameDateToNum;
    int points;
    /** This app mostly uses abbreviations to identify teams but this is needed for the API */
    int teamId;
    /** Abbreviation (eg. VGK - Las Vegas Golden Knights) */
    String ab;
    String nextMatch;
    String last5;
    String[] allSeasonMatches;
    Player[] roster;

}
