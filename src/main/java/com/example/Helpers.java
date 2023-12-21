package com.example;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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


}
