package com.vencillio.rs2.content.dialogue.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterString;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Dialogue for the Player Shop Exchange
 * 
 * @author Daniel
 *
 */
public class ShopExchangeDialogue extends Dialogue {

	public ShopExchangeDialogue(Player player) {
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
			
		/* Shop collecting */
		case DialogueConstants.OPTIONS_5_5:
			if (player.getShopCollection() == 0) {
				DialogueManager.sendStatement(player, "You do not have any coins to collect!");
				return true;
			}
			if (player.getInventory().getFreeSlots() == 0) {
				DialogueManager.sendStatement(player, "Please free up some space first.");
				return true;
			}
			if (player.getInventory().hasItemId(995)) {
				DialogueManager.sendStatement(player, "Please remove all coins from your inventory.");
				return true;
			}
			player.getInventory().add(new Item(995, (int) player.getShopCollection()));
			player.setShopCollection(0);
			return true;
			
			/* Searching player */	
		case DialogueConstants.OPTIONS_5_3:
			player.setEnterXInterfaceId(55777);
			player.getClient().queueOutgoingPacket(new SendEnterString());
			return true;
			
			/* Searching item */	
		case DialogueConstants.OPTIONS_5_4:
			player.setEnterXInterfaceId(55778);
			player.getClient().queueOutgoingPacket(new SendEnterString());
			return true;
			
			/* Editing */
		case DialogueConstants.OPTIONS_5_2:
			DialogueManager.sendOption(player, "Edit shop", "Edit shop motto (10 credits)", "Edit shop color (10 credits)");
			return true;
			
			/* Show all active shops */
		case DialogueConstants.OPTIONS_5_1:
			player.getClient().queueOutgoingPacket(new SendInterface(53500));
			List<Player> available = Arrays.stream(World.getPlayers()).filter(p -> p != null && p.isActive() && p.getPlayerShop().hasAnyItems()).collect(Collectors.toList());
			for (int i = 53516; i < 53716; i++) {
				Player p = null;
				if (i - 53516 < available.size()) {
					p = available.get(i - 53516);
					String color = "";
					if (p.getShopColor() == null) {
						color = "</col>" + p.determineIcon(p);
					} else {
						color = p.getShopColor();
					}
					
					player.getClient().queueOutgoingPacket(new SendString(p.determineIcon(p) + p.getUsername(), i));
					
					if (player.getShopMotto() != null) {
						player.getClient().queueOutgoingPacket(new SendString(color + p.getShopMotto(), i + 200));
					} else {
						player.getClient().queueOutgoingPacket(new SendString(color + "No shop description set.", i + 200));
					}
				} else {
					player.getClient().queueOutgoingPacket(new SendString("", i));
					player.getClient().queueOutgoingPacket(new SendString("", i + 200));
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		case 0:
			DialogueManager.sendOption(player, "View all shops", "Edit your Shop", "Search for player's shop", "Search for specific item", "Collect coins");
			break;
		}
	}

}
