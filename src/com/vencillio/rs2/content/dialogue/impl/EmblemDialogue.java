package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.shopping.impl.BountyShop;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue that handles Emblem Trader
 * 
 * @author Daniel
 *
 */
public class EmblemDialogue extends Dialogue {

	/**
	 * Emblem Trader
	 * 
	 * @param player
	 */
	public EmblemDialogue(Player player) {
		this.player = player;
	}

	/**
	 * The bounty total
	 */
	int bountyTotal;

	/**
	 * The tier data
	 */
	public int[][] TIER_DATA = { { 12746, 50_000 }, { 12748, 100_000 }, { 12749, 200_000 }, { 12750, 400_000 }, { 12751, 750_000 }, { 12752, 1_200_000 }, { 12753, 1_750_000 }, { 12754, 2_500_000 }, { 12755, 3_500_000 }, { 12756, 5_000_000 } };

	/**
	 * Calculation of tier
	 */
	public void calculateTotal() {
		bountyTotal = 0;
		for (int[] emblem : TIER_DATA) {
			bountyTotal += player.getInventory().getItemAmount(emblem[0]) * emblem[1];
		}
	}

	/**
	 * Clicking button
	 * 
	 * @param id
	 */
	@Override
	public boolean clickButton(int id) {
		switch (id) {
		// Trading tiers
		case DialogueConstants.OPTIONS_5_1:
			for (int i = 0; i < TIER_DATA.length; i++) {
				if (player.getInventory().hasItemId(new Item(TIER_DATA[i][0]))) {
					player.send(new SendRemoveInterfaces());
					calculateTotal();
					player.setBountyPoints(player.getBountyPoints() + bountyTotal);
					DialogueManager.sendStatement(player, "You traded your emblems for " + Utility.format(bountyTotal) + " points.");
					bountyTotal = 0;
					player.getInventory().remove(new Item(TIER_DATA[i][0]));
					break;
				} else {
					DialogueManager.sendNpcChat(player, 315, Emotion.ANGRY_1, "You do not have any tiers on you!");
				}
			}
			break;

		// Statistics
		case DialogueConstants.OPTIONS_5_2:
			double kdr = ((double) player.getKills()) / ((double) player.getDeaths());
			DialogueManager.sendInformationBox(player, "PvP Statistics:", "Points: @red@" + player.getBountyPoints(), "Kills: @red@" + player.getKills(), "Deaths: @red@" + player.getDeaths(), "KDR: @red@" + kdr);
			break;

		// Trading
		case DialogueConstants.OPTIONS_5_3:
			player.getShopping().open(BountyShop.SHOP_ID);
			break;

		// Skulling
		case DialogueConstants.OPTIONS_5_4:
			if (player.getSkulling().isSkulled()) {
				DialogueManager.sendNpcChat(player, 315, Emotion.DEFAULT, "You already have a wilderness skull!");
				return false;
			}
			player.getSkulling().skull(player, player);
			DialogueManager.sendNpcChat(player, 315, Emotion.DEFAULT, "You have been skulled.");
			break;

		// Close dialogue
		case DialogueConstants.OPTIONS_5_5:
			if (player.getCredits() < 3) {
				DialogueManager.sendStatement(player, "You need 3 credits to do this!");
				return false;
			}
			player.setCredits(player.getCredits() - 3);
			player.setDeaths(0);
			DialogueManager.sendStatement(player, "Your deaths have been reset!");
			break;
		}
		return false;
	}

	/**
	 * Execute dialogue
	 */
	@Override
	public void execute() {
		switch (next) {
		case 0:
			DialogueManager.sendNpcChat(player, 315, Emotion.CALM, "Hello adventurer.", "How may I help you today?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Sell tiers", "Show me my PvP statistics", "I would like to trade", "Give me a wilderness skull", "Reset KDR");
			break;
		}
	}

}
