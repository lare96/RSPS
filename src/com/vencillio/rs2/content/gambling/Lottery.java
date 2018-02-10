package com.vencillio.rs2.content.gambling;

import java.util.ArrayList;
import java.util.List;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.io.PlayerSaveUtil;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Lottery
 * @author Daniel
 *
 */
public class Lottery {
	
	/**
	 * Players entered in the lottery
	 */
	private static final List<Player> entries = new ArrayList<Player>();
	
	/**
	 * The lottery limit
	 */
	private static final int LOTTERY_LIMIT = 10_000_000;
	
	/**
	 * The entry price of lottery
	 */
	private static final int ENTRY_PRICE = 1_000_000;
	
	/**
	 * Current threshold of lottery
	 */
	private static int CURRENT_POT = 0;
	
	/**
	 * The winner of lottery
	 */
	private static Player winner = null;
	
	/**
	 * Handles player entering the lottery
	 * @param player
	 */
	public static void enterLotter(Player player) {
		if (player.getRights() == 2 || player.getRights() == 4) {
			DialogueManager.sendStatement(player, "You may not enter the lottery because of your rights.");
			return;
		}
		
		if (hasEntered(player)) {
			DialogueManager.sendStatement(player, "You are already entered in the lottery!");
			return;
		}
		
		if (CURRENT_POT >= LOTTERY_LIMIT) {
			DialogueManager.sendStatement(player, "The lottery is currently full.");
			return;
		}
		
		if (!player.getInventory().hasItemAmount(995, ENTRY_PRICE)) {
			DialogueManager.sendStatement(player, "You need " + Utility.format(ENTRY_PRICE) + " coins to enter the lottery!");
			return;
		}
		
		player.getInventory().remove(995, ENTRY_PRICE);
		CURRENT_POT += ENTRY_PRICE;
		entries.add(player);
		World.sendGlobalMessage("[ <col=C46423>Lottery </col>] <col=C46423>" + player.determineIcon(player) + " " + player.getUsername() + "</col> has just entered the lottery! Pot: <col=C46423>" + Utility.format(CURRENT_POT) + " </col>/ <col=C46423>" + Utility.format(LOTTERY_LIMIT) + "</col>.");
		AchievementHandler.activateAchievement(player, AchievementList.ENTER_THE_LOTTERY_5_TIMES, 1);
		
		if (CURRENT_POT == LOTTERY_LIMIT) {
			draw();
		}
		
	}
	
	/**
	 * Draws the lottery
	 */
	public static void draw() {
		if (entries.isEmpty()) {
			return;
		}
		winner = Utility.randomElement(entries);
		
		if (winner == null) {
			PlayerSaveUtil.addToOfflineContainer(winner.getUsername(), new Item(995, CURRENT_POT));			
		} else {
			winner.send(new SendMessage("Congratulations! You have won the lottery. Money has been sent to your pouch."));
			AchievementHandler.activateAchievement(winner, AchievementList.WIN_THE_LOTTERY_3_TMES, 1);
			winner.setMoneyPouch(winner.getMoneyPouch() + CURRENT_POT);
			winner.send(new SendString(winner.getMoneyPouch() + "", 8135));
		}
		
		World.sendGlobalMessage("[ <col=C46423>Lottery </col>] <col=C46423>" + winner.getUsername() + "</col> has just won the lottery of <col=C46423>" + Utility.format(CURRENT_POT) + "</col>!");
		reset();
	}
	
	/**
	 * Resets the lottery
	 */
	public static void reset() {
		winner = null;
		CURRENT_POT = 0;
		entries.clear();
	}
	
	/**
	 * Does an announcement for lottery
	 */
	public static void announce() {
		World.sendGlobalMessage("[ <col=C46423>Lottery </col>] The current pot is at <col=C46423>" + Utility.format(CURRENT_POT) + " </col>/ <col=C46423>" + Utility.format(LOTTERY_LIMIT) + "</col>.");
	}
	
	/**
	 * Gets the current lottery pot
	 * @return
	 */
	public static int getPot() {
		return CURRENT_POT;
	}
	
	/**
	 * Gets the current lottery limit
	 * @return
	 */
	public static int getLimit() {
		return LOTTERY_LIMIT;
	}
	
	/**
	 * Gets the amount of players involved in lottery
	 * @return
	 */
	public static int getPlayers() {
		return entries.size();
	}
	
	/**
	 * Checks if player has entered the lotter
	 * @param player
	 * @return
	 */
	public static boolean hasEntered(Player player) {
		if (entries.contains(player)) {
			return true;
		}
		return false;
	}

}
