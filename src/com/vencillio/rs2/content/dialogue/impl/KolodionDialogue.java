package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles Kolodion's dialogue
 * @author Daniel
 *
 */
public class KolodionDialogue extends Dialogue {
	
	public KolodionDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		return false;
	}

	@Override
	public void execute() {
		switch(next) {
			
		case 0:
			DialogueManager.sendNpcChat(player, 1603, Emotion.DEFAULT, "Hello adventurer!", "I need some help.");
			next ++;
			break;
			
		case 1:
			DialogueManager.sendPlayerChat(player, Emotion.DEFAULT, "What kind of help?");
			next ++;
			break;

		case 2:
			DialogueManager.sendNpcChat(player, 1603, Emotion.DEFAULT, "There are these wizards infesting my arena.", "I need them gone by any means.", "Basically just kill them.", "Destroy their crappy existance!");
			next ++;
			break;
			
		case 3:
			DialogueManager.sendPlayerChat(player, Emotion.DEFAULT, "Where are they located?");
			next ++;
			break;
			
		case 4:
			DialogueManager.sendNpcChat(player, 1603, Emotion.DEFAULT, "Click the lever, go outside until you see a cave entrance.", "Click the lever and go in the middle of the arena.", "You can't miss it.");
			next ++;
			break;
			
		case 5:
			DialogueManager.sendPlayerChat(player, Emotion.DEFAULT, "What will I get in return?");
			next ++;
			break;
			
		case 6:
			DialogueManager.sendNpcChat(player, 1603, Emotion.DEFAULT, "For every wizard you kill I will give you one point.", "You can use these points to buy items", "from my shop.");
			next ++;
			break;
			
		case 7:
			DialogueManager.sendPlayerChat(player, Emotion.DEFAULT, "Alright, I will get going.");
			end();
			break;
		
		}
	
	}

}
