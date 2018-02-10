package com.vencillio.core.network.mysql;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.core.util.logger.PlayerLogger;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;

public class MembershipRewards {

	private static ExternalDatabase database = null;

	private static boolean prepared = false;

	private static ExecutorService executorService = null;

	public static void prepare() {
		database = new ExternalDatabase("vdaniel_ipb", "PBDQC7UqKdDa", "108.167.182.37/vdaniel_ipb", 3);
		database.initialise();
		executorService = Executors.newSingleThreadExecutor();
		prepared = true;
	}

	public static void shutdown() {
		database.shutdown();
		executorService.shutdown();
	}

	/**
	 * Handles a donation request for a player
	 */
	public static void collectReward(Player player) {
		if (!prepared) {
			throw new IllegalStateException("unprepared");
		}

		if ((System.currentTimeMillis() - player.getLastRequestedLookup()) < 30000) {
			DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "I can only check the database once per 30 seconds!");
			return;
		}

		player.setLastRequestedLookup(System.currentTimeMillis());

		executorService.submit(() -> {
			try (ResultSet resultSet = database.executeQuery("SELECT `productid`, `price` FROM `donation` WHERE `username` = '" + player.getUsername().trim().replaceAll(" ", "_") + "' OR `username` = '" + player.getUsername().trim() + "'")) {
				boolean go = false;

				while (resultSet.next()) {
					final int prod = resultSet.getInt("productid");
					final int price = resultSet.getInt("price");
					
					if (prod == 1 && price == 1) {
						giveItem(player, 13190);
					} else if (prod == 2 && price == 3) {
						giveItem(player, 13191);
					} else if (prod == 3 && price == 5) {
						giveItem(player, 13192);
					} else if (prod == 4 && price == 8) {
						giveItem(player, 13193);
					} else if (prod == 5 && price == 10) {
						giveItem(player, 13194);
					} else if (prod == 6 && price == 20) {
						giveItem(player, 13195);
					} else if (prod == 7 && price == 50) {
						giveItem(player, 13196);
					} else if (prod == 8 && price == 100) {
						giveItem(player, 13197);
					} else if (prod == 9 && price == 200) {
						giveItem(player, 13198);
					}
					
					go = true;
					PlayerLogger.DONATION_LOGGER.log(Utility.formatPlayerName(player.getUsername()), String.format("%s has purchased package %s for $%s.", Utility.formatPlayerName(player.getUsername()), prod, price));
					DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "Thank you for purchase, " + Utility.formatPlayerName(player.getUsername()) + "!");
					database.executeQuery("DELETE FROM `donation` WHERE `username` = '" + player.getUsername().trim().replaceAll(" ", "_") + "' OR `username` = '" + player.getUsername().trim() + "'");
				}

				if (!go) {
					DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "It seems there is nothing here for you to claim!");
					player.getDialogue().setNext(0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
	
	public static void giveItem(Player player, int item) {
		String name = GameDefinitionLoader.getItemDef(item).getName();
		DialogueManager.sendItem1(player, "You have been given " + Utility.getAOrAn(name) + " " + name + "!" , item);
		player.getInventory().addOrCreateGroundItem(item, 1, true);
	}
	
	
	
}