package com.example;

import java.awt.Desktop;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class Helpers {

    public static int calculateLevenshteinDistance(String str1, String str2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return levenshteinDistance.apply(str1, str2);
    }

    /** Sorts all players based on goals, highest to lowest */
    public static List<Player> getTop10Goals(Team[] teams){

        List<Player> list = new ArrayList<Player>();
        for(int teamIndex = 0; teamIndex < 32; teamIndex++){
            for(int playerIndex = 0; playerIndex < teams[teamIndex].roster.length; playerIndex++){
                list.add(teams[teamIndex].roster[playerIndex]);
            }
        }

        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o2.goals-o1.goals;
            }
        });

        return list;
    }

    /** Sorts all players based on points, highest to lowest */
    public static List<Player> getTop10Points(Team[] teams){

        List<Player> list = new ArrayList<Player>();
        for(int teamIndex = 0; teamIndex < 32; teamIndex++){
            for(int playerIndex = 0; playerIndex < teams[teamIndex].roster.length; playerIndex++){
                list.add(teams[teamIndex].roster[playerIndex]);
            }
        }

        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o2.points-o1.points;
            }
        });

        return list;
    }

    /** Sorts all goalies based on savePctg */
    public static List<Player> getTop10Goalie(Team[] teams){

        List<Player> list = new ArrayList<Player>();
        for(int teamIndex = 0; teamIndex < 32; teamIndex++){
            for(int playerIndex = 0; playerIndex < teams[teamIndex].roster.length; playerIndex++){
                if(teams[teamIndex].roster[playerIndex].position.equals("Goalie")){
                    list.add(teams[teamIndex].roster[playerIndex]);
                }
            }
        }

        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return Double.compare(o2.savePctg, o1.savePctg);
            }
        });

        return list;
    }

    /** Opens the GitHub page of this project in the default browser */
    public static void openGitHub() throws IOException, URISyntaxException{

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://github.com/tatuaua/NHLStats"));
        }
    }

    /** Gets the imageIcon for a country code from the local files */
    public static ImageIcon getImageForCountry(String countryCode){

        for(int i = 0; i < Constants.countryCodes2.length; i++){
            if(Constants.countryCodes2[i].equals(countryCode)){
                ImageIcon img = new ImageIcon("images/countries/" + Constants.countryCodes[i] + ".png");
                return new ImageIcon(img.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            }
        }

        return null;
    }

    /** Constructs a list of Country objects from the teams and adds the players to the countries */
    public static ArrayList<Country> getCountries(Team[] teams){

        ArrayList<Country> list = new ArrayList<>();

        for(int i = 0; i < Constants.countryCodes.length; i++){

            Country country = new Country(Constants.countryCodes2[i]);

            country.players = new ArrayList<>();

            for(int j = 0; j < teams.length; j++){

                for(Player player : teams[j].roster){
                    if(player.country.equals(country.code)){
                        country.players.add(player);
                    }
                }
            }

            list.add(country);
        }

        return list;
    }

    public static List<Country> getTop10CountriesByAvgPoints(Team[] teams){

        ArrayList<Country> list = getCountries(teams);

        for(Country country : list){

            if(country.players.size() < 5){
                country.avgPpg = 0;
                continue;
            }

            double countryTotalPpg = 0;
            int goaliesToExclude = 0;

            for(Player player : country.players){

                if(player.position.equals("Goalie")){
                    goaliesToExclude++;
                } else {
                    countryTotalPpg += player.ppg;
                }
            }

            country.avgPpg = countryTotalPpg / ((double)country.players.size()-(double)goaliesToExclude);
        }

        Collections.sort(list, new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {
                return Double.compare(o2.avgPpg, o1.avgPpg);
            }
        });

        return list.subList(0, 10);
    }

    public static void setPlayoffStatuses(Team[] teams){


        List<Team> eastern = new ArrayList<>();
        List<Team> atlantic = new ArrayList<>();
        List<Team> metropolitan = new ArrayList<>();

        List<Team> western = new ArrayList<>();
        List<Team> central = new ArrayList<>();
        List<Team> pacific = new ArrayList<>();
        
        for(Team team : teams){

            if(team.conference.equals("E")){
                eastern.add(team);

                if(team.division.equals("A")){
                    atlantic.add(team);
                } else {
                    metropolitan.add(team);
                }
            } else {
                western.add(team);

                if(team.division.equals("C")){
                    central.add(team);
                } else {
                    pacific.add(team);
                }
            }
        }
    }
}
