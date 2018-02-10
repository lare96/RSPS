package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.skill.slayer.Slayer;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterString;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Handles the Slayer dialogue
 * @author Daniel
 *
 */
public class VannakaDialogue extends Dialogue {
	
	/**
	 * Vannaka Dialogue
	 * @param player
	 */
	public VannakaDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		case DialogueConstants.OPTIONS_3_1:
			if (player.getSlayer().hasTask()) {
				DialogueManager.sendNpcChat(player, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
				break;
			}
			player.getSlayer().assign(Slayer.SlayerDifficulty.LOW);
			next = 6;
			execute();
			break;
			
		case DialogueConstants.OPTIONS_3_2:
			if (player.getSlayer().hasTask()) {
				DialogueManager.sendNpcChat(player, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
				break;
			}
			player.getSlayer().assign(Slayer.SlayerDifficulty.MEDIUM);
			next = 6;
			execute();
			break;
			
		case DialogueConstants.OPTIONS_3_3:
			if (player.getSlayer().hasTask()) {
				DialogueManager.sendNpcChat(player, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
				break;
			}
			player.getSlayer().assign(Slayer.SlayerDifficulty.HIGH);
			next = 6;
			execute();
			break;
		
		case DialogueConstants.OPTIONS_4_1:
			if (player.getSlayer().hasTask()) {
				DialogueManager.sendNpcChat(player, 403, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
			} else {
				setNext(4);
				execute();
			}
			break;
			
		case DialogueConstants.OPTIONS_4_2:
			player.send(new SendRemoveInterfaces());
			setNext(2);
			execute();
			break;
			
		case DialogueConstants.OPTIONS_4_3:
			if (!player.getSlayer().hasTask()) {
				DialogueManager.sendStatement(player, new String[] { "A slayer task is required to reset it!" });
				end();
			} else if (!player.getInventory().hasItemAmount(995, 250000)) {
				DialogueManager.sendStatement(player, new String[] { "@blu@250,000</col> coins is required to do this;", "which you do not have!" });
				end();
			} else {
				player.getSlayer().reset();
				DialogueManager.sendStatement(player, new String[] { "You have reset your task for @blu@250,000 </col>coins." });
				player.getInventory().remove(995, 250000, true);
				InterfaceHandler.writeText(new QuestTab(player));
				end();
			}
			break;
			
		case DialogueConstants.OPTIONS_4_4:
			player.getShopping().open(6);
			break;
		
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 403, Emotion.HAPPY_TALK, "Hello adventurer!", "I am Vannaka, master of slayer.", "How may I assist you today?");
			next ++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Obtain task", "Set co-op slayer partner", "Reset my task", "Trade");
			break;
		case 2:
			DialogueManager.sendNpcChat(player, 403, Emotion.HAPPY_TALK, "Coming soon!");
			//DialogueManager.sendNpcChat(player, 403, Emotion.HAPPY_TALK, "Please enter the player's username you want to pair up with.");
			//next ++;
			break;
		case 3:
			end();
			player.setEnterXInterfaceId(100);
			player.getClient().queueOutgoingPacket(new SendEnterString());
			break;
		case 4:
			DialogueManager.sendNpcChat(player, 403, Emotion.HAPPY, "Please select the level of difficulty.");
			next ++;
			break;
		case 5:
			DialogueManager.sendOption(player, "Easy", "Medium", "Hard");
			break;
		case 6:
			String task = player.getSlayer().getTask();
			byte am = player.getSlayer().getAmount();
			DialogueManager.sendNpcChat(player, 403, Emotion.CALM, new String[] { "You have been assigned the task of killing:", "@blu@" + am + " " + task, });
			end();
			break;
			
		}
	}

	
}
