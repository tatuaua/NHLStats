package com.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;

public class APIStuff {

    final static int TEAM_AMOUNT = 32;
    public static Team[] teams = new Team[TEAM_AMOUNT];

    /** Populates the basic info for teams */ 
    public static void populateArrays() throws IOException, URISyntaxException {
        long start = System.nanoTime();

        JSONObject jsonObj = new JSONObject(getJSON("https://api-web.nhle.com/v1/standings/now"));
        
        //Fetching nested Json using JSONArray
        JSONArray arrObj = jsonObj.getJSONArray("standings");
        for (int i = 0; i < arrObj .length(); i++) {
            
            teams[i] = new Team();
            teams[i].name = arrObj.getJSONObject(i).getJSONObject("teamName").getString("default");
            teams[i].ab = arrObj.getJSONObject(i).getJSONObject("teamAbbrev").getString("default");
            teams[i].points = arrObj.getJSONObject(i).getInt("points");

            if(teams[i].ab.equals("MTL")){
                teams[i].name = "Montreal Canadiens";
            }
        }

        long total = System.nanoTime()-start;
        System.out.println("Populating arrays took " + total/1000000 + " ms");

        getIDs();

    }

    /** Gets the last 5 games for a specified team */ 
    public static void populatePrevMatches(int teamIndex) throws URISyntaxException, IOException{

        long start = System.nanoTime();

        String ab = teams[teamIndex].ab;
        String last5 = "";
        String allSeasonMatches = "";


        JSONObject jsonObj = new JSONObject(getJSON("https://api-web.nhle.com/v1/club-schedule-season/" + ab + "/20232024"));
        
        //Fetching nested Json using JSONArray
        JSONArray arrObj = jsonObj.getJSONArray("games");

        for(int k = 0; k < 100; k++){

            if(arrObj.getJSONObject(k).getString("gameState").equals("FUT")){ // Stop once we find the latest match
                break;
            }

            if(arrObj.getJSONObject(k).getString("gameState").equals("FINAL")){ // Start once we find the earliest match
                continue;
            }

            if(arrObj.getJSONObject(k).getJSONObject("awayTeam").getString("abbrev").equals(teams[teamIndex].ab)){

                if(arrObj.getJSONObject(k).getJSONObject("awayTeam").getInt("score") >= arrObj.getJSONObject(k).getJSONObject("homeTeam").getInt("score")){
                    allSeasonMatches = allSeasonMatches + " W";
                } else {
                    allSeasonMatches = allSeasonMatches + " L";
                }
                
            } else {

                if(arrObj.getJSONObject(k).getJSONObject("awayTeam").getInt("score") >= arrObj.getJSONObject(k).getJSONObject("homeTeam").getInt("score")){
                    allSeasonMatches = allSeasonMatches + " L";
                } else {
                    allSeasonMatches = allSeasonMatches + " W";
                }

            }
        }

        String[] arr = allSeasonMatches.substring(1).split(" "); // Remove first space and split into array of strings

        for(int x = 5; x > 0; x--){ // Getting the last 5 matches' results

            last5 = last5 + " " + arr[arr.length-x];
        }
   
        teams[teamIndex].last5 = last5;
        teams[teamIndex].allSeasonMatches = arr;

        long total = System.nanoTime()-start;
        System.out.println("Populating prev matches took " + total/1000000 + " ms");

    }

    /** Gets the next match info for a specified team */ 
    public static void populateNextMatch(int teamIndex) throws IOException, URISyntaxException { //For specific teams because API has restriction

        long start = System.nanoTime();

        String ab = teams[teamIndex].ab;
        String date = LocalDate.now().toString();

        JSONObject jsonObj = new JSONObject(getJSON("https://api-web.nhle.com/v1/club-schedule/" + ab +  "/week/now"));
        
        //Fetching nested Json using JSONArray
        JSONArray arrObj = jsonObj.getJSONArray("games");

        int otherIndex = 0;
        //Get current day as integer, remember to change this after 8000 years
        int currentDay = Integer.parseInt(date.charAt(8) + "" + date.charAt(9));
        //Get matchday from json payload, remember to change this after 8000 years
        int nextMatchDay = Integer.parseInt((arrObj.getJSONObject(0).getString("gameDate").charAt(8) + "" + arrObj.getJSONObject(0).getString("gameDate").charAt(9)));
        //Use next match if next match in json payload is today or earlier
        if(nextMatchDay <= currentDay){
            otherIndex = 1;
        }

        teams[teamIndex].nextMatch = arrObj.getJSONObject(otherIndex).getString("gameDate") + 
            ", " +
            arrObj.getJSONObject(otherIndex).getJSONObject("homeTeam").getString("abbrev") + 
            " - " +
            arrObj.getJSONObject(otherIndex).getJSONObject("awayTeam").getString("abbrev");
        
    
        long total = System.nanoTime()-start;    
        System.out.println("Populating next match took " + total/1000000 + " ms");
    }

    private static String getJSON(String urlString) throws IOException, URISyntaxException {

        URI uri = new URI(urlString);
        URL url = uri.toURL();
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;       
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return content.toString();
    }

    private static void getIDs() throws FileNotFoundException{

        /** ab, id, ab, id... */
        String[] dataArray = {
            "NYR",
            "3",
            "BOS",
            "6",
            "VGK",
            "54",
            "LAK",
            "26",
            "VAN",
            "23",
            "COL",
            "21",
            "DAL",
            "25",
            "WPG",
            "52",
            "FLA",
            "13",
            "DET",
            "17",
            "TBL",
            "14",
            "CAR",
            "12",
            "TOR",
            "10",
            "STL",
            "19",
            "PHI",
            "4",
            "WSH",
            "15",
            "NYI",
            "2",
            "SEA",
            "55",
            "PIT",
            "5",
            "NSH",
            "18",
            "ARI",
            "53",
            "BUF",
            "7",
            "MTL",
            "8",
            "NJD",
            "1",
            "CGY",
            "20",
            "ANA",
            "24",
            "OTT",
            "9",
            "CBJ",
            "29",
            "EDM",
            "22",
            "MIN",
            "30",
            "CHI",
            "16",
            "SJS",
            "28"
        };

        for(int i = 0; i < dataArray.length; i++){

            for(int j = 0; j < teams.length; j++){

                if(dataArray[i].equals(teams[j].ab)){
                    teams[j].teamId = Integer.parseInt(dataArray[i+1]);
                }
            }
        }

        for(int k = 0; k < teams.length; k++){

            System.out.println(teams[k].ab + teams[k].teamId);
        }
    }
    
    /** Returns all the teams */ 
    public static Team[] getTeams() {
    	return teams;
    }



}
