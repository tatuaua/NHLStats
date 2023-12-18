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

        JSONObject jsonObj = new JSONObject(dataPayload);
        JSONArray arrObj = jsonObj.getJSONArray("documents");

        for (int i = 0; i < arrObj.length(); i++) {
            JSONObject teamJson = arrObj.getJSONObject(i);

            // Populate basic info for teams
            teams[i] = new Team();
            teams[i].name = teamJson.getString("name");
            teams[i].ab = teamJson.getString("ab");
            teams[i].points = teamJson.getInt("points");

            if (teams[i].ab.equals("MTL")) {
                teams[i].name = "Montreal Canadiens";
            }

            // Populate previous matches
            String allSeasonMatches = "";
            JSONArray gamesJsonArray = teamJson.getJSONArray("allSeasonMatches");
            for (int j = 0; j < gamesJsonArray.length(); j++) {
                allSeasonMatches = allSeasonMatches + gamesJsonArray.get(j) + " ";
            }
            teams[i].allSeasonMatches = allSeasonMatches.split(" ");

            // Populate next match info
            teams[i].nextMatch = teamJson.getString("nextMatch");

            // Populate team rosters
            JSONArray rosterJsonArray = teamJson.getJSONArray("roster");
            teams[i].roster = new Player[rosterJsonArray.length()];
            for (int playerIndex = 0; playerIndex < rosterJsonArray.length(); playerIndex++) {
                JSONObject playerObj = rosterJsonArray.getJSONObject(playerIndex);
                teams[i].roster[playerIndex] = new Player(playerObj.getString("name"),
                        playerObj.getString("playerId"), playerObj.getString("position"));
                teams[i].roster[playerIndex].points = playerObj.getInt("points");
                teams[i].roster[playerIndex].goals = playerObj.getInt("goals");
                teams[i].roster[playerIndex].assists = playerObj.getInt("assists");
                teams[i].roster[playerIndex].ppg = playerObj.getDouble("ppg");
                teams[i].roster[playerIndex].historicalPpg = playerObj.getDouble("historicalPpg");
            }
        }


        long end = System.nanoTime();

        System.out.println("Fetching and parsing took " + (end-start)/1000000 + " ms");
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
    
    /** Returns all the teams */ 
    public static Team[] getTeams() {
    	return teams;
    }
}