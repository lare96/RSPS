package com.vencillio.rs2.content.gambling;

import com.vencillio.core.util.Utility;
import com.vencillio.core.util.FileHandler;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class Gambling {

    private static final int CHANCE_OF_WINNING = 55;
    
    private static final int MAXIMUM_AMOUNT = 15000000;
    
    private static final int MINIMUM_AMOUNT = 500000;
    
    public static long MONEY_TRACKER;
    
    public static boolean calculateWin() {
    	return Utility.random(100) >= CHANCE_OF_WINNING;
    }
    
    public static boolean canPlay(Player player, int amount) {
    	if (PlayerConstants.isStaff(player)) {
    		DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Sorry, but Tanner has forbidden you from gambling.");
    		return false;
    	}
		if (amount > MAXIMUM_AMOUNT) {
			DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Woah there fella!", "The maximum bet allowed is " + Utility.format(MAXIMUM_AMOUNT) + "!");
			return false;
		}	
		if (amount < MINIMUM_AMOUNT) {
			DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Sorry buddy, bets have to be more than " + Utility.format(MINIMUM_AMOUNT) +".");
			return false;
		}
		if (player.isPouchPayment()) {
			if (player.getMoneyPouch() < amount) {
				DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "You don't have that much money to bet!");
				return false;
			}
		} else {
			if (!player.getInventory().hasItemAmount(995, amount)) {
				DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "You don't have that much money to bet!");
				return false;
			}			
		}
    	return true;
    }

    public static void play(Player player, int amount) {
    	if (!canPlay(player, amount)) {
    		return;
    	}
		if (calculateWin()) {
			results(player, amount, true);
		} else {
			results(player, amount, false);
		}
    }
    
    public static void results(Player player, int amount, boolean win) {
    	String bet = Utility.format(amount);
	    if (win) {
	    	if (player.isPouchPayment()) {
	    		player.setMoneyPouch(player.getMoneyPouch() + amount);
	    		player.send(new SendString(player.getMoneyPouch() + "", 8135));
	    	} else {
	    		player.getInventory().add(995, amount);	    		
	    	}
			if (amount >= 10_000_000) {
				World.sendGlobalMessage("<img=8> <col=C42BAD>" + player.getUsername() + " has just won " + Utility.format(amount) + " from the Gambler!");
			}
			save(+amount);
			DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Congratulations! You have won " + bet + ".");
			return;
	    }
    	if (player.isPouchPayment()) {
    		player.setMoneyPouch(player.getMoneyPouch() - amount);
    		player.send(new SendString(player.getMoneyPouch() + "", 8135));
    	} else {
    		player.getInventory().remove(995, amount);    		
    	}
		DialogueManager.sendNpcChat(player, 1011, Emotion.DEFAULT, "Sorry! You have lost " + bet + "!");   
		save(-amount);
    }
    
    public static void save(long amount) {
    	MONEY_TRACKER += amount;
    	FileHandler.saveGambling();
    }
	
}
