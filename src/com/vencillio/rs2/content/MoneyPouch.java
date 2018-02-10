package com.vencillio.rs2.content;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles Money Pouch
 * @author Daniel
 *
 */
public class MoneyPouch {
	
	/**
	 * Player
	 */
	private final Player player;
	
	/**
	 * Money Pouch
	 * @param player
	 */
	public MoneyPouch(Player player) {
		this.player = player;
	}
	
	/**
	 * Format coins
	 * @param amount
	 * @return
	 */
	public String formatCoins(long amount) {
		if (amount >= 1_000 && amount < 1_000_000) {
			return "" + (amount / 1_000) + "K";
		}
		
		if (amount >= 1_000_000 && amount < 1_000_000_000) {
			return "" + (amount / 1_000_000) + "M";
		}
		
		if (amount >= 1_000_000_000) {
			return "" + (amount / 1_000_000_000) + "B";
		}
		return "" + amount;
	}
	
	/**
	 * Adds coins to Money Pouch
	 */
	public void addPouch() {
	
		//Checks for Pin
		if (player.getPin() != null && player.enteredPin == false) {
			player.send(new SendInterface(48750));
			return;
		}
	
		//Check if player is in a position to add coins
		if (player.inWGLobby() || player.inWGGame() || player.getMagic().isTeleporting() || player.isDead() || player.inWilderness() || player.getCombat().inCombat() || player.getDueling().isDueling() || player.getInterfaceManager().hasInterfaceOpen()) {
			player.send(new SendMessage("You can't do this right now!"));
			return;
		}
				
		//Check if money pouch is filled
		if (player.getMoneyPouch() == Long.MAX_VALUE) {
			player.send(new SendMessage("Your pouch is already full!"));
			return;
		}
		
		//Grabs amount of coins to store
		int amount = player.getInventory().getItemAmount(995);
		
		//Checks if current stored coins + new coins to store exceed the max value
		if (player.getMoneyPouch() + amount >= Long.MAX_VALUE) {
			player.send(new SendMessage("You can't fit all that into your pouch!"));
			return;
		}
	
		//Removes coins from inventory
		player.getInventory().remove(995, amount);
		
		//Adds coins to Money Pouch
		player.setMoneyPouch(player.getMoneyPouch() + amount);
		
		//Sends confirmation message
		player.send(new SendMessage("@dre@You have added " + Utility.format(amount) + " coins into your pouch. Total: " + formatCoins(player.getMoneyPouch()) + "."));
		
		//Updates string
		player.send(new SendString(player.getMoneyPouch() + "", 8135));
	}
	
	/**
	 * Withdraw coins from Money Pouch
	 * @param amount
	 */
	public void withdrawPouch(long amount) {
	
		//Checks for Pin
		if (player.getPin() != null && player.enteredPin == false) {
			player.send(new SendInterface(48750));
			return;
		}

		// Check if player is in a position to withdraw coins
		if (player.inWGLobby() || player.inWGGame() || player.getMagic().isTeleporting() || player.isDead() || player.inWilderness() || player.getCombat().inCombat() || player.getDueling().isDueling() || player.getInterfaceManager().hasInterfaceOpen()) {
			player.send(new SendMessage("You can't do this right now!"));
			return;
		}

		// Checks if player is withdrawing a negative amount
		if (amount <= 0) {
			player.send(new SendMessage("You can't withdraw a negative amount!"));
			return;
		}

		// Checks if player has the amount to withdraw stored
		if (player.getMoneyPouch() < amount) {
			amount = player.getMoneyPouch();
		}

		// Checks if coins in inventory + amount to withdraw passes max value
		if ((long) (player.getInventory().getItemAmount(995) + amount) > Integer.MAX_VALUE) {
			player.send(new SendMessage("You don't have enough space to withdraw that many coins!"));
			amount = Integer.MAX_VALUE - player.getInventory().getItemAmount(995);
		}

		// Check to see if player is withdrawing more than max value
		if (amount > Integer.MAX_VALUE) {
			player.send(new SendMessage("You can't withdraw more than 2B coins at a time!"));
			return;
		}

		// Checks if player has max value of coins in inventory
		if (player.getInventory().getItemAmount(995) == Integer.MAX_VALUE) {
			player.send(new SendMessage("You can't withdraw any more coins!"));
			return;
		}

		// Checks if player has space to withdraw the coins
		if (!player.getInventory().hasItemId(995) && player.getInventory().getFreeSlots() == 0) {
			player.send(new SendMessage("You do not have enough inventory spaces to withdraw coins."));
			return;
		}

		// Removes coins from pouch
		player.setMoneyPouch(player.getMoneyPouch() - amount);

		// Adds coins to inventory
		player.getInventory().add(995, (int) amount);

		// Sends confirmation dialogue
		DialogueManager.sendItem1(player, "You have withdrawn <col=255>" + Utility.format(amount) + " </col>coins.", 995);

		// Updates string
		player.send(new SendString(player.getMoneyPouch() + "", 8135));
	}

}
