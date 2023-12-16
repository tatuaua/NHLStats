package com.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class DataFetcher {

    final static int TEAM_AMOUNT = 32;
    public static Team[] teams = new Team[TEAM_AMOUNT];
    static String dataPayload;

    /** Runs the method to fetch data from the MongoDB and the methods to parse that data into objects */
    public static void fetchAndParse() throws IOException, URISyntaxException{

        System.out.println("Starting fetch and parse process");
        long start = System.nanoTime();

        getJSON();
        populateTeams();
        populatePrevMatches();
        populateNextMatches();
        populateTeamRosters();

        long end = System.nanoTime();

        System.out.println("Fetching and parsing took " + (end-start)/1000000 + " ms");
    }

    /** Populates the basic info for teams */ 
    public static void populateTeams() throws IOException, URISyntaxException {
        
        long start = System.nanoTime();

        JSONObject jsonObj = new JSONObject(dataPayload);
        
        //Fetching nested Json using JSONArray
        JSONArray arrObj = jsonObj.getJSONArray("documents");
        for (int i = 0; i < arrObj .length(); i++) {
            
            teams[i] = new Team();
            teams[i].name = arrObj.getJSONObject(i).getString("name");
            teams[i].ab = arrObj.getJSONObject(i).getString("ab");
            teams[i].points = arrObj.getJSONObject(i).getInt("points");

            if(teams[i].ab.equals("MTL")){
                teams[i].name = "Montreal Canadiens";
            }
        }

        long total = System.nanoTime()-start;
        System.out.println("Populating arrays took " + total/1000000 + " ms");

        getIDs();
    }

    /** Gets the last 5 games for all teams */ 
    public static void populatePrevMatches() throws URISyntaxException, IOException{

        long start = System.nanoTime();

        for(int teamIndex = 0; teamIndex < 32; teamIndex++){

            String allSeasonMatches = "";

            JSONObject jsonObj = new JSONObject(dataPayload);
            JSONArray gamesJsonArray = jsonObj.getJSONArray("documents").getJSONObject(teamIndex).getJSONArray("allSeasonMatches");

            for(int j = 0; j < gamesJsonArray.length(); j++){

                allSeasonMatches = allSeasonMatches + gamesJsonArray.get(j) + " ";

            }

            teams[teamIndex].allSeasonMatches = allSeasonMatches.split(" ");
        }

        long total = System.nanoTime()-start;
        System.out.println("Populating prev matches took " + total/1000000 + " ms");

    }

    /** Gets the next match info for all teams */ 
    public static void populateNextMatches() throws IOException, URISyntaxException { 
        long start = System.nanoTime();

        JSONObject jsonObj = new JSONObject(dataPayload);

        for(int teamIndex = 0; teamIndex < 32; teamIndex++){

            teams[teamIndex].nextMatch = jsonObj.getJSONArray("documents").getJSONObject(teamIndex).getString("nextMatch");
        }
    
        long total = System.nanoTime()-start;    
        System.out.println("Populating next match took " + total/1000000 + " ms");
    }

    public static void populateTeamRosters() throws JSONException, IOException, URISyntaxException{

        long start = System.nanoTime();

        JSONObject jsonObj = new JSONObject(dataPayload);

        for(int teamIndex = 0; teamIndex < 32; teamIndex++){

            JSONArray jsonArray = jsonObj.getJSONArray("documents").getJSONObject(teamIndex).getJSONArray("roster");
            teams[teamIndex].roster = new Player[jsonArray.length()];

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject playerObj = jsonArray.getJSONObject(i);

                teams[teamIndex].roster[i] = new Player(playerObj.getString("name") , playerObj.getString("playerId"), playerObj.getString("position"));
                teams[teamIndex].roster[i].points = playerObj.getInt("points");
                teams[teamIndex].roster[i].goals = playerObj.getInt("goals");
                teams[teamIndex].roster[i].assists = playerObj.getInt("assists");
                teams[teamIndex].roster[i].ppg = playerObj.getDouble("ppg");
                teams[teamIndex].roster[i].historicalPpg = playerObj.getDouble("historicalPpg");
            }
        }

        long total = System.nanoTime()-start;    
        System.out.println("Populating roster took " + total/1000000 + " ms");
    }

    private static void getJSON() throws IOException, URISyntaxException {

        HttpClient httpClient = HttpClient.newHttpClient();

        String url = "https://data.mongodb-api.com/app/data-hflvq/endpoint/data/v1/action/find";
        String jsonPayload = "{\"dataSource\":\"Cluster0\",\"database\":\"movie-api-db\",\"collection\":\"nhldata\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Access-Control-Request-Headers", "*")
                .header("api-key", "wbU8snLQsz21IAQTNGsvfTREnoH3oRh1lZiYzsmWu2f6fIKIOCxleCNk4QkuVzuk")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            dataPayload = response.body();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Gets the teamIDs for all teams */
    private static void getIDs() throws FileNotFoundException{

        for(int i = 0; i < TeamIds.dataArray.length; i++){

            for(int j = 0; j < teams.length; j++){

                if(TeamIds.dataArray[i].equals(teams[j].ab)){
                    teams[j].teamId = Integer.parseInt(TeamIds.dataArray[i+1]);
                }
            }
        }
    }
    
    /** Returns all the teams */ 
    public static Team[] getTeams() {
    	return teams;
    }
}