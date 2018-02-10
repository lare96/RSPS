package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.skill.slayer.Slayer;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class NeiveDialogue extends Dialogue {
	
	public NeiveDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		case DialogueConstants.OPTIONS_3_1:
			if (player.getCredits() < 2) {
				DialogueManager.sendNpcChat(player, 490, Emotion.DEFAULT, "You do not have enough credits to do this!");
				end();
				return false;
			}
			if (player.getSlayer().hasTask()) {
				DialogueManager.sendNpcChat(player, 490, Emotion.ANNOYED, "You already have a task!", "Complete your current task or reset it to get a new.");
				return false;
			}
			player.setCredits(player.getCredits() - 2);
			player.getSlayer().assign(Slayer.SlayerDifficulty.BOSS);
			next = 2;
			execute();
			break;
			
		case DialogueConstants.OPTIONS_3_2:
			player.send(new SendRemoveInterfaces());
			break;
			
		case DialogueConstants.OPTIONS_3_3:
			if (!player.getSlayer().hasTask()) {
				DialogueManager.sendNpcChat(player, 490, Emotion.DEFAULT, "A slayer task is required to reset it!" );
				end();
				return false;
			}
			if (!player.getInventory().hasItemAmount(995, 250_000)) {
				DialogueManager.sendNpcChat(player, 490, Emotion.DEFAULT, "You do not have enough coins to do this!");
				end();
				return false;
			}
			player.getInventory().remove(995, 250_000);
			player.getSlayer().reset();
			DialogueManager.sendNpcChat(player, 490, Emotion.DEFAULT, "Your current task has been reset!");
			end();
			break;
		
		}
	return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 490, Emotion.DEFAULT, "Hello my elite friend.", "I can give you a boss task.", "It will cost you 2 credits.", "Are you interested?");
			next ++;
			break;
			
		case 1:
			DialogueManager.sendOption(player, "Yes", "No", "Reset current task (@dre@250k</col>)");
			break;
			
		case 2:
			String task = player.getSlayer().getTask();
			byte am = player.getSlayer().getAmount();
			DialogueManager.sendNpcChat(player, 490, Emotion.CALM, new String[] { "You have been assigned the task of killing:", "@dre@" + am + " " + task, });
			end();
			break;
		
		}
	}
	

}
