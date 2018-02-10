package com.vencillio.rs2.content;

import java.util.function.Predicate;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles all players online
 * 
 * @author Daniel
 *
 */
public class PlayersOnline {

	/**
	 * Gets the rank of player
	 * 
	 * @param player
	 * @return
	 */
	public static String rank(Player player) {
		switch (player.getRights()) {
		case 0:
			return "";
		case 1:
			return "<img=0> ";
		case 2:
			return "<img=1> ";
		case 3:
			return "<img=2> ";
		case 4:
			return "<img=3> ";
		case 5:
			return "<img=4> ";
		case 6:
			return "<img=5> ";
		case 7:
			return "<img=6> ";
		case 8:
			return "<img=7> ";
		case 9:
			return "<img=8> ";
		case 10:
			return "<img=9> ";
		case 11:
			return "<img=10> ";
		case 12:
			return "<img=11> ";
		case 13:
			return "<img=13> ";
		}
		return "";
	}
	
	/**
	 * Shows player online
	 * 
	 * @param player
	 * @param playerType
	 */
	public static void showPlayers(Player player, Predicate<Player> playerType) {
		for (int index = 0; index < 50; index++) {
			player.send(new SendString("", 8145 + index));
		}
		
		player.send(new SendString("@dre@Tannerscape's Active Players (</col> " + World.getActivePlayers() + " @dre@)", 8144));

		int frameBegin = 8145;

		for (Player p : World.getPlayers()) {

			if (p == null || !p.isActive()) {
				continue;
			}

			if (playerType.test(p)) {
				player.send(new SendString(rank(p) +  Utility.formatPlayerName(p.getUsername()), frameBegin++));
			}
		}
		
		player.send(new SendInterface(8134));

	}

}
