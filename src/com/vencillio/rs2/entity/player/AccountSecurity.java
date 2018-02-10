package com.vencillio.rs2.entity.player;

import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handle Account security
 * @author Daniel
 *
 */
public class AccountSecurity {

	/**
	 * Player
	 */
	private Player player;
	
	/**
	 * Bank Pins
	 * @param player
	 */
	public AccountSecurity(Player player) {
		this.player = player;
	}
	
	/**
	 * Sets the bank pin
	 * @param input
	 */
	public void setPin(String input) {
		if (input.length() != 4) {
			DialogueManager.sendStatement(player, "Your pin must consist of four numbers!");
			return;
		}
		player.setPin(input); 
		DialogueManager.sendInformationBox(player, "Bank Pin", "You have successfully set your bank pin!", "Your new pin is:", "@red@"+player.getPin(), "Write it down so you don't forget!");
		AchievementHandler.activateAchievement(player, AchievementList.SETUP_A_BANK_PIN, 1);
	}
	
	/**
	 * Enters the pin
	 * @param input
	 */
	public void enterPin(String input) {
		if (player.getPin().equalsIgnoreCase(input)) {
			player.enteredPin = true;
			player.send(new SendMessage("You have successfully entered your pin"));
			player.getBank().openBank();
		} else {
			DialogueManager.sendStatement(player, "Wrong pin entered!");
		}
	}
	
	/**
	 * Removes the pin
	 * @param input
	 */
	public void removePin(String input) {
		if (player.getPin().equalsIgnoreCase(input)) {
			player.setPin(null);
			DialogueManager.sendStatement(player, "You have successfully removed your bank pin!");
		} else {
			DialogueManager.sendStatement(player, "You have entered the wrong pin!");
		}
	}
	
	/**
	 * Sets IP address
	 * @param input
	 */
	public void setIP(String input) {
		if (player.getIP() != null) {
			DialogueManager.sendStatement(player, "You already have an IP address set!");
			return;
		}
		player.setIP(input);
		DialogueManager.sendStatement(player, "You have set your IP address to:", player.getIP());
		AchievementHandler.activateAchievement(player, AchievementList.SET_YOUR_ACCOUNT_DETAILS, 1);
	}
	
	/**
	 * Sets full name
	 * @param input
	 */
	public void setName(String input) {
		if (player.getFullName() != null) {
			DialogueManager.sendStatement(player, "You already have your name set!");
			return;
		}
		player.setFullName(input);
		DialogueManager.sendStatement(player, "You have set your name to:", player.getFullName());
		AchievementHandler.activateAchievement(player, AchievementList.SET_YOUR_ACCOUNT_DETAILS, 1);
	}	
	
	/**
	 * Sets recovery
	 * @param input
	 */
	public void setRecovery(String input) {
		if (player.getRecovery() != null) {
			DialogueManager.sendStatement(player, "You already have a recovery set!");
			return;
		}
		player.setRecovery(input);
		DialogueManager.sendStatement(player, "You have set your recovery to:", player.getRecovery());
		AchievementHandler.activateAchievement(player, AchievementList.SET_YOUR_ACCOUNT_DETAILS, 1);
	}	
	
	/**
	 * Sets email
	 * @param input
	 */
	public void setEmail(String input) {
		if (player.getEmailAddress() != null) {
			DialogueManager.sendStatement(player, "You already have a email address set!");
			return;
		}
		player.setEmailAddress(input);
		DialogueManager.sendStatement(player, "You have set your email address to:", player.getEmailAddress());
		AchievementHandler.activateAchievement(player, AchievementList.SET_YOUR_ACCOUNT_DETAILS, 1);
	}	

}
