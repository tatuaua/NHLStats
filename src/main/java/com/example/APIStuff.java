package com.example;

import java.io.BufferedReader;
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

    public static Team[] teams = new Team[32];

	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		populateArrays();
	
	}
	
	
    public static void populateArrays() throws IOException, URISyntaxException {
    	
        URI uri = new URI("https://api-web.nhle.com/v1/standings/now");
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

        String contentString = content.toString();
        //Using JSONObject 
        JSONObject jsonObj = new JSONObject(contentString);
        
        //Fetching nested Json using JSONArray
        JSONArray arrObj = jsonObj.getJSONArray("standings");
        for (int i = 0; i < arrObj .length(); i++) {
            teams[i] = new Team();
            teams[i].name = arrObj.getJSONObject(i).getJSONObject("teamName").getString("default");
            teams[i].ab = arrObj.getJSONObject(i).getJSONObject("teamAbbrev").getString("default");
            teams[i].points = arrObj.getJSONObject(i).getInt("points");
            System.out.println(teams[i].name);
            System.out.println(teams[i].ab);
            System.out.println(teams[i].points);
        }
    }

    public static void populateNextMatch(int index) throws IOException, URISyntaxException { //For specific teams because API has restriction

        String date = LocalDate.now().toString();

        System.out.println("Opening endpoint for " + teams[index].ab);

        URI uri = new URI("https://api-web.nhle.com/v1/club-schedule/" + teams[index].ab +  "/week/now");
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

        String contentString = content.toString();
        //Using JSONObject 
        JSONObject jsonObj = new JSONObject(contentString);
        
        //Fetching nested Json using JSONArray
        JSONArray arrObj = jsonObj.getJSONArray("games");

        int otherIndex = 0;
        //Get current day as integer
        int currentDay = Integer.parseInt(date.charAt(8) + "" + date.charAt(9));
        //Get matchday from json payload
        int nextMatchDay = Integer.parseInt((arrObj.getJSONObject(0).getString("gameDate").charAt(8) + "" + arrObj.getJSONObject(0).getString("gameDate").charAt(9)));
        //Use next match if next match in json payload is today or earlier
        if(nextMatchDay <= currentDay){
            otherIndex = 1;
        }

        teams[index].nextMatch = arrObj.getJSONObject(otherIndex).getString("gameDate") + 
            ", " +
            arrObj.getJSONObject(otherIndex).getJSONObject("homeTeam").getString("abbrev") + 
            " - " +
            arrObj.getJSONObject(otherIndex).getJSONObject("awayTeam").getString("abbrev") ;
    
    }
    
    public static Team[] getTeams() {
    	return teams;
    }


}
