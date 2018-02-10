package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class AdamDialogue extends Dialogue {
	
	public AdamDialogue(Player player) {
		this.player = player;
	}
	
	private final int[] IRON_ARMOUR = { 
		12810, 12811, 12812, 12813, 12814, 12815
	};

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		case DialogueConstants.OPTIONS_2_1:
			for (int i = 0; i < IRON_ARMOUR.length; i++) {
				if (player.getEquipment().isWearingItem(IRON_ARMOUR[i])) {
					DialogueManager.sendNpcChat(player, 311, Emotion.ANNOYED, "Please remove your Iron Man armour first!");
					return false;
				}
			}
			player.setRights(0);
			player.setIron(false);
			player.setUltimateIron(false);
			PlayerSave.save(player);
			player.send(new SendRemoveInterfaces());
			player.send(new SendMessage("You are now a normal player."));
			player.getUpdateFlags().setUpdateRequired(true);
			break;
			
		case DialogueConstants.OPTIONS_2_2:
			player.send(new SendRemoveInterfaces());
			break;
		
		case DialogueConstants.OPTIONS_4_1:
			if (player.isIron()) {
				if (ItemCheck.hasIronArmour(player)) {
					DialogueManager.sendNpcChat(player, 311, Emotion.ANNOYED, "It appears you already have some armour!");
					return false;
				}
				player.getInventory().add(12810, 1, true);
				player.getInventory().add(12811, 1, true);
				player.getInventory().add(12812, 1, true);
				DialogueManager.sendItem1(player, "Adam has given you some armour.", 12810);
			} else if (player.isUltimateIron()) {
				if (ItemCheck.hasUltimateArmour(player)) {
					DialogueManager.sendNpcChat(player, 311, Emotion.ANNOYED, "It appears you already have some armour!");
					return false;
				}
				player.getInventory().add(12813, 1, true);
				player.getInventory().add(12814, 1, true);
				player.getInventory().add(12815, 1, true);
				DialogueManager.sendItem1(player, "Adam has given you some armour.", 12813);
			}
			break;
			
		case DialogueConstants.OPTIONS_4_2:
			player.start(new OptionDialogue("General", p -> {
				player.getShopping().open(38);
			} , "Armours", p -> {
				player.getShopping().open(39);
			} , "Miscellaneous", p -> {
				player.getShopping().open(41);
			} , "Herblore", p -> {
				player.getShopping().open(33);
			} , "Farming", p -> {
				player.getShopping().open(32);
			}));
			break;
			
		case DialogueConstants.OPTIONS_4_3:
			setNext(5);
			execute();
			break;
			
		case DialogueConstants.OPTIONS_4_4:
			player.send(new SendRemoveInterfaces());
			break;

		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			if (!player.ironPlayer() && !PlayerConstants.isOwner(player)) {
				DialogueManager.sendNpcChat(player, 311, Emotion.SLIGHTLY_SAD, "Sorry, since you are not an Iron Man I cannot help you.");
				end();
				return;
			}
			DialogueManager.sendNpcChat(player, 311, Emotion.HAPPY_TALK, "Welcome " + player.getUsername() + "!", "My name is Adam,",  "I can help you with your Iron Man account.");
			next++;
			break;
			
		case 1:
			DialogueManager.sendOption(player, "Obtain some armour", "View Iron Man stores", "Remove Iron Man restrictions", "Nevermind");
			break;
			
		case 5:
			DialogueManager.sendNpcChat(player, 311, Emotion.HAPPY_TALK, "I can remove your Iron Man restrictions.", "However, you will not be able to gain it back.");
			next++;
			break;
			
		case 6:
			DialogueManager.sendOption(player, "Do it", "Nevermind");
			break;
		
		}
	}
	

}
