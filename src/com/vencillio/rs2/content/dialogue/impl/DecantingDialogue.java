package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.skill.herblore.PotionDecanting;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for Decanting
 * @author Daniel
 *
 */
public class DecantingDialogue extends Dialogue {
	
	public DecantingDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch(id) {
		
		case DialogueConstants.OPTIONS_3_1:
			player.send(new SendRemoveInterfaces());
			setNext(5);
			execute();
			break;
			
		case DialogueConstants.OPTIONS_3_2:
			if (player.getInventory().hasItemId(995)) {
				PotionDecanting.decantAll(player);
				player.send(new SendRemoveInterfaces());
			} else {
				DialogueManager.sendStatement(player, "You do not have any coins!");
			}
			break;
			
		case DialogueConstants.OPTIONS_3_3:
			player.send(new SendRemoveInterfaces());
			break;

			
		}
		return false;
	}

	@Override
	public void execute() {
		switch(next) {
		case 0:
			DialogueManager.sendNpcChat(player, 6524, Emotion.HAPPY, "Hello adventurer.", "I can decant your potions for 250gp a potion.", "What would you like to do?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, "What is decanting?", "Decant inventory.", "Nothing.");
			break;
		case 5:
			DialogueManager.sendNpcChat(player, 6524, Emotion.HAPPY, "Decanting is the method in which a player combines", "partially full potions of the same kind to produce", "one full potion and one partially empty potion.");
			next ++;
			break;
		case 6:
			DialogueManager.sendNpcChat(player, 6524, Emotion.HAPPY, "For example, decanting a potion containing 3 doses and", "another containing 2 doses yields one full potion (4 doses)", "and one partially full potion (1 dose).");
			setNext(1);
			break;
		}
	}


}
