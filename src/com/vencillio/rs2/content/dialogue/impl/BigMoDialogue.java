package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Handles the Big Mo Dialogue
 * @author Daniel
 *
 */
public class BigMoDialogue extends Dialogue {

	public BigMoDialogue(Player player) {
		this.player = player;
	}
	
	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		case DialogueConstants.OPTIONS_2_1:
			player.send(new SendInterface(55000));
			break;
			
		case DialogueConstants.OPTIONS_2_2:
			player.send(new SendRemoveInterfaces());
			break;
		
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 5860, Emotion.HAPPY_TALK, "Hello small one.");
			next ++;
			break;
			
		case 1:
			DialogueManager.sendPlayerChat(player, Emotion.CALM, "Small one?", "You might be bigger than me in height", "but I have a bigger co-");
			next ++;
			break;
			
		case 2:
			DialogueManager.sendNpcChat(player, 5860, Emotion.HAPPY_TALK, "Enough!", "No one wants to talk about that.", "Now what do you want?");
			next ++;
			break;
			
		case 3:
			DialogueManager.sendPlayerChat(player, Emotion.CALM, "What do you mean what do I want?", "You are standing in my home.", "Obviously you are meant to do something for me.");
			next ++;
			break;
			
		case 4:
			DialogueManager.sendNpcChat(player, 5860, Emotion.HAPPY_TALK, "Yes, that is true.", "I can manage your player titles.");
			next ++;
			break;
			
		case 5:
			DialogueManager.sendOption(player, "Open title manager", "Nevermind");
			break;
		
		}
	}

}
