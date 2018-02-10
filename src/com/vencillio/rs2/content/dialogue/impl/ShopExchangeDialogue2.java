package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterString;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for the Player Shop Exchange
 * 
 * @author Daniel
 *
 */
public class ShopExchangeDialogue2 extends Dialogue {

	public ShopExchangeDialogue2(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int button) {
		switch (button) {
		
		/* Opens players shop */
		case DialogueConstants.OPTIONS_3_1:
			player.getShopping().open(player);
			break;
			
		/* Setting motto */
		case DialogueConstants.OPTIONS_3_2:
			if (player.getCredits() < 10) {
				DialogueManager.sendStatement(player, "You do not have enough credits to do this!");
				return false;
			}
			player.setEnterXInterfaceId(55776);
			player.getClient().queueOutgoingPacket(new SendEnterString());
			break;
			
			/* Setting Color */
			case DialogueConstants.OPTIONS_3_3:
				if (player.getCredits() < 10) {
					DialogueManager.sendStatement(player, "You do not have enough credits to do this!");
					return false;
				}
				player.start(new OptionDialogue("Red", p -> {
					player.setShopColor("@red@");
					player.setCredits(player.getCredits() - 10);
					player.send(new SendRemoveInterfaces());
					DialogueManager.sendInformationBox(player, "Player Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Red", "", "");		
				} , "Blue", p -> {
					player.setShopColor("@blu@");
					player.setCredits(player.getCredits() - 10);
					player.send(new SendRemoveInterfaces());
					DialogueManager.sendInformationBox(player, "Player Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Blue", "", "");
				} , "Green", p -> {
					player.setShopColor("@gre@");
					player.setCredits(player.getCredits() - 10);
					player.send(new SendRemoveInterfaces());
					DialogueManager.sendInformationBox(player, "Player Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Green", "", "");
				} , "Cyan", p -> {
					player.setShopColor("@cya@");
					player.setCredits(player.getCredits() - 10);
					player.send(new SendRemoveInterfaces());
					DialogueManager.sendInformationBox(player, "Player Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Cyan", "", "");
				} , "Default", p -> {
					player.setShopColor("</col>");
					player.setCredits(player.getCredits() - 10);
					player.send(new SendRemoveInterfaces());
					DialogueManager.sendInformationBox(player, "Player Owned Shops Exchange", "You have successfully changed your shop color.", "It's now Default	", "", "");
				}));
				break;			
			

		}

		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		case 0:
			DialogueManager.sendOption(player, "Edit shop", "Edit shop motto (10 credits)", "Edit shop color (10 credits)");
			break;
		}
	}

}
