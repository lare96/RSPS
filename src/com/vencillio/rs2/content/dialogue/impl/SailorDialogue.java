package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class SailorDialogue extends Dialogue {
	
	public SailorDialogue(Player player) {
		this.player = player;
	}
	
	private final int COST = 2_500;
	
	public boolean can() {
		if (player.isPouchPayment()) {
			if (player.getMoneyPouch() < COST) {
				return false;
			} else {
				player.setMoneyPouch(player.getMoneyPouch() - COST);
				player.send(new SendString(player.getMoneyPouch() + "", 8135));
				return true;
			}
		} else {
			if (!player.getInventory().hasItemAmount(995, COST)) {
				return false;
			} else {
				player.getInventory().remove(995,COST);
				return true;
			}
		}
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case DialogueConstants.OPTIONS_5_1:
			if (can()) {
				player.getMagic().teleport(2948, 3147, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		case DialogueConstants.OPTIONS_5_2:
			if (can()) {
				player.getMagic().teleport(2964, 3378, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		case DialogueConstants.OPTIONS_5_3:
			if (can()) {
				player.getMagic().teleport(2662, 3305, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		case DialogueConstants.OPTIONS_5_4:
			if (can()) {
				player.getMagic().teleport(2569, 3098, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		case DialogueConstants.OPTIONS_5_5:
			next = 2;
			execute();
			break;
		case DialogueConstants.OPTIONS_4_1:
			if (can()) {
				player.getMagic().teleport(2708, 3492, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		case DialogueConstants.OPTIONS_4_2:
			if (can()) {
				player.getMagic().teleport(3093, 3244, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		case DialogueConstants.OPTIONS_4_3:
			if (can()) {
				player.getMagic().teleport(3210, 3424, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		case DialogueConstants.OPTIONS_4_4:
			if (can()) {
				player.getMagic().teleport(2827, 2995, 0, TeleportTypes.SPELL_BOOK);
			} else {
				DialogueManager.sendNpcChat(player, 3936, Emotion.ANNOYED, "You do not have " + COST + " coins for this!");
				next = 0;
			}
			break;
		}
	return false;
	}

	@Override
	public void execute() {
		switch (next) {
		case 0: 
			DialogueManager.sendNpcChat(player, 3936, Emotion.HAPPY_TALK, "Hello adventurer!", "Are you interested in traveling the lands of Tannerscape?", "I can take you places, for " + COST + " coins!");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Karamja", "Falador", "Ardougne", "Yanille", "Next");
			break;
		case 2:
			DialogueManager.sendOption(player, "Seer's Village", "Dranyor", "Varrock", "Shilo Village");
			break;
		}
	}

}
