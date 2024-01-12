package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class APICommunication {

    final static int TEAM_AMOUNT = 32;
    public static Team[] teams = new Team[TEAM_AMOUNT];
    static String dataPayload;

    public static void main(String[] args) throws IOException, URISyntaxException {
        fetchAndParse();
    }

    /** Runs the method to fetch data from the MongoDB and the methods to parse that data into objects */
    public static void fetchAndParse() throws IOException, URISyntaxException{

        System.out.println("Starting fetch and parse process");
        TLog.info("Starting fetch and parse process");
        long start = System.nanoTime();

        getJSON();

        //JSONObject jsonObj = new JSONObject(dataPayload);
        //JSONArray arrObj = jsonObj.getJSONArray("documents");
        JSONArray arrObj = new JSONArray(dataPayload);

        for (int teamIndex = 0; teamIndex < arrObj.length(); teamIndex++) {
            JSONObject teamJson = arrObj.getJSONObject(teamIndex);

            // Populate basic info for team
            teams[teamIndex] = new Team();
            teams[teamIndex].name = teamJson.getString("name");
            teams[teamIndex].conference = teamJson.getString("conference");
            teams[teamIndex].division = teamJson.getString("division");
            teams[teamIndex].lastGameDate = teamJson.getString("lastGameDate");
            teams[teamIndex].points = teamJson.getInt("points");
            teams[teamIndex].ab = teamJson.getString("ab");

            if (teams[teamIndex].ab.equals("MTL")) {
                teams[teamIndex].name = "Montreal Canadiens";
            }

            // Populate previous matches
            String allSeasonMatches = "";
            JSONArray gamesJsonArray = teamJson.getJSONArray("allSeasonMatches");
            for (int j = 0; j < gamesJsonArray.length(); j++) {
                allSeasonMatches = allSeasonMatches + gamesJsonArray.get(j) + " ";
            }
            teams[teamIndex].allSeasonMatches = allSeasonMatches.split(" ");

            // Populate next match info
            teams[teamIndex].nextMatch = teamJson.getString("nextMatch");

            // Populate team roster
            JSONArray rosterJsonArray = teamJson.getJSONArray("roster");
            teams[teamIndex].roster = new Player[rosterJsonArray.length()];
            for (int playerIndex = 0; playerIndex < rosterJsonArray.length(); playerIndex++) {
                JSONObject playerObj = rosterJsonArray.getJSONObject(playerIndex);
                Player tempPlayer = new Player(playerObj.getString("name"), playerObj.getString("playerId"), playerObj.getString("position"));
                if(tempPlayer.position.equals("Goalie")){
                    tempPlayer.savePctg = playerObj.getDouble("savePctg");
                    tempPlayer.relSavePctg = playerObj.getDouble("relSavePctg");
                }
                tempPlayer.points = playerObj.getInt("points");
                tempPlayer.goals = playerObj.getInt("goals");
                tempPlayer.assists = playerObj.getInt("assists");
                tempPlayer.gamesPlayed = playerObj.getInt("gamesPlayed");
                tempPlayer.ppg = playerObj.getDouble("ppg");
                tempPlayer.historicalPpg = playerObj.getDouble("historicalPpg");

                if(playerObj.has("country") && !playerObj.get("country").toString().equals("null")){
                    tempPlayer.country = playerObj.getString("country");
                }
                
                teams[teamIndex].roster[playerIndex] = tempPlayer;
            }
        }


        long end = System.nanoTime();

        System.out.println("Fetching and parsing took " + (end-start)/1000000 + " ms");
        TLog.info("Fetching and parsing took " + (end-start)/1000000 + " ms");
    }

    private static void getJSON() throws IOException, URISyntaxException {

        /*HttpClient httpClient = HttpClient.newHttpClient();

        String url = "https://data.mongodb-api.com/app/data-hflvq/endpoint/data/v1/action/find";
        String jsonPayload = "{\"dataSource\":\"Cluster0\",\"database\":\"movie-api-db\",\"collection\":\"nhldata\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Access-Control-Request-Headers", "*")
                // wow extremely bad idea
                .header("api-key", "wbU8snLQsz21IAQTNGsvfTREnoH3oRh1lZiYzsmWu2f6fIKIOCxleCNk4QkuVzuk")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            dataPayload = response.body();

        } catch (Exception e) {
            TLog.error("Problem with MongoDB API connection");
            e.printStackTrace();
        }*/

        try {
            String url = "https://3tjdsspg-8080.euw.devtunnels.ms/api/v1/teams/all";
            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            dataPayload = response.toString();
            System.out.println(response.toString().substring(0, 50));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int handleUser(String email, String password, String[] favTeams){

        int responseCode = 0;
        StringBuilder response = new StringBuilder();

        StringBuilder b = new StringBuilder();

        for(int i = 0; i < favTeams.length; i++){
            if(i == 0){b.append("[");}
            b.append("\"" + favTeams[i] + "\"");
            if(i != favTeams.length-1){b.append(",");}
            if(i == favTeams.length-1){b.append("]");}
        }

        String favTeamsStr = b.toString();

        try {
            String url = "https://3tjdsspg-8080.euw.devtunnels.ms/api/v1/users/new";
            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString =   "{\"email\":\"" + email
                                    + "\",\"password\":\"" + password + "\"}"
                                    + "\",\"favTeams\":" + favTeamsStr + "\"}"
                                    + "\",\"authId\":\"" + "\"}"
                                    + "\",\"isAuthenticated\":" + "false" + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                TLog.info(response.toString());
                System.out.println(response.toString());
            }

        } catch (Exception e) {
            TLog.error(response.toString());
            System.out.println(response.toString());
        }

        return responseCode;
    }
    
    /** Returns all the teams */ 
    public static Team[] getTeams() {
    	return teams;
    }
}