package com.vencillio.rs2.content.profiles;

import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Handles the profile leaderboards. Based off likes, dislikes, and views.
 * @author Daniel
 *
 */
public class ProfileLeaderboard implements Comparable < Object > {

    String name = "";
    public static String sort = "";
    double score = 0;

    public ProfileLeaderboard(String name, double score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Puts the data to a string
     */
    @Override
    public String toString() {
        return String.format("%s %s: @gre@%s", name, sort, score);
    }

    /**
     * Compares
     */
    public int compareTo(Object o1) {
        if (this.score == ((ProfileLeaderboard) o1).score) {
            return 0;
        } else if ((this.score) > ((ProfileLeaderboard) o1).score) {
            return 1;
        } else {
            return -1;
        }
    }
    
    /**
     * Handles the top players leading in a category
     * @param player
     * @param sort
     */
    public static void open(Player player, String sort) {
        ArrayList < ProfileLeaderboard > leaderboard = new ArrayList < ProfileLeaderboard > ();
        player.send(new SendInterface(47400));
        
        for (int i = 0; i < 25; i ++) {
        	player.send(new SendString("", 51551 + i));
        }
        
        /* Sorts leader board by Views */
        if (sort.equalsIgnoreCase("Views")) {
            sort = "Views";
            for (Player players: World.getPlayers()) {
                if (players == null)
                    continue;
                leaderboard.add(new ProfileLeaderboard(players.getUsername(), players.getProfileViews()));
                updateString(player, "views");
            }
        }
        /* Sorts leader board by Likes */
        if (sort.equalsIgnoreCase("Likes")) {
            sort = "Likes";
            for (Player players: World.getPlayers()) {
                if (players == null)
                    continue;
                leaderboard.add(new ProfileLeaderboard(players.getUsername(), players.getLikes()));
                updateString(player, "likes");
            }
        }
        /* Sorts leader board by Dislikes */
        if (sort.equalsIgnoreCase("Dislikes")) {
            sort = "Dislikes";
            for (Player players: World.getPlayers()) {
                if (players == null)
                    continue;
                leaderboard.add(new ProfileLeaderboard(players.getUsername(), players.getDislikes()));
                updateString(player, "dislikes");
            }
        }
        /* Sorts leader board by Views */
        if (sort.equalsIgnoreCase("Ratio")) {
            sort = "Ratio";
            for (Player players: World.getPlayers()) {
                if (players == null)
                    continue;
                                // 10             /         (5                     + 10               ) * 10
                double ratio = (player.getLikes() / (double) (player.getDislikes() + player.getLikes()) * player.getLikes());
                leaderboard.add(new ProfileLeaderboard(players.getUsername(), ratio));
                updateString(player, "ratio");
            }
        }
        Collections.sort(leaderboard);
        for (int i = 1; i <= leaderboard.size(); i++) {
        	player.send(new SendString("@lre@" + i + ") " + leaderboard.get((leaderboard.size() - i)), 51550 + i));
        }
        leaderboard.clear();
    }
    
    public static void updateString(Player player, String type) {
    	switch (type) {
    	case "views":
            player.send(new SendString("@gre@Views", 47418));
            player.send(new SendString("</col>Likes", 47419));
            player.send(new SendString("</col>Dislikes", 47420));
            player.send(new SendString("</col>Ratio", 47421));
    		break;
    	case "likes":
            player.send(new SendString("</col>Views", 47418));
            player.send(new SendString("@gre@Likes", 47419));
            player.send(new SendString("</col>Dislikes", 47420));
            player.send(new SendString("</col>Ratio", 47421));
    		break;
    	case "dislikes":
            player.send(new SendString("</col>Views", 47418));
            player.send(new SendString("</col>Likes", 47419));
            player.send(new SendString("@gre@Dislikes", 47420));
            player.send(new SendString("</col>Ratio", 47421));
    		break;
    	case "ratio":
            player.send(new SendString("</col>Views", 47418));
            player.send(new SendString("</col>Likes", 47419));
            player.send(new SendString("</col>Dislikes", 47420));
            player.send(new SendString("@gre@Ratio", 47421));
    		break;
    	}
    }

    
    
    

}