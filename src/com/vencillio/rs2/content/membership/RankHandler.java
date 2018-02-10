package com.vencillio.rs2.content.membership;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.CreditTab;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendBanner;

/**
 * Handles upgrading rank
 * @author Daniel
 *
 */
public class RankHandler {
	
	/**
	 * Upgrades rank
	 * @param player
	 */
	public static void upgrade(Player player) {
	
		if (player.ironPlayer()) {
			DialogueManager.sendStatement(player, "<img=12>@gry@Iron players@bla@ may not change their rank!");
			return;
		}
		
		if (PlayerConstants.isStaff(player)) {
			DialogueManager.sendStatement(player, "Your rank will not be changed as you are part of the staff team.");
			return;
		}
		
		int rights = 0;
		if (player.getMoneySpent() < 5)
			rights = 0;
		if (player.getMoneySpent() >= 5) 
			rights = 5;
		if (player.getMoneySpent() >= 25)
			rights = 6;
		if (player.getMoneySpent() >= 75)
			rights = 7;
		if (player.getMoneySpent() >= 150)
			rights = 8;
		
		if (rights != 0 && player.getRights() != rights) {
			player.setRights(rights);
			player.getUpdateFlags().setUpdateRequired(true);
			InterfaceHandler.writeText(new QuestTab(player));
			InterfaceHandler.writeText(new CreditTab(player));	
			player.send(new SendBanner("You are now " + Utility.getAOrAn(player.determineRank(player)) + " " + player.determineIcon(player) + " " + player.determineRank(player) + "!", 0x1C889E));
			DialogueManager.sendStatement(player, "You are now " + Utility.getAOrAn(player.determineRank(player)) + " " + player.determineIcon(player) + " " + player.determineRank(player) + "!");
		}
	}

}
