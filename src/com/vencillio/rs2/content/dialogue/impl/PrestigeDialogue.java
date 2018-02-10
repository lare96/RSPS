package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.Prestige;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for Prestige
 * @author Daniel
 *
 */
public class PrestigeDialogue extends Dialogue {
	
	public PrestigeDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch(id) {
		case DialogueConstants.OPTIONS_4_1:
			player.send(new SendRemoveInterfaces());
			setNext(5);
			execute();
			break;
		case DialogueConstants.OPTIONS_4_2:
			player.send(new SendRemoveInterfaces());
			player.send(new SendMessage("Coming soon!"));
			break;
		case DialogueConstants.OPTIONS_4_3:
			Prestige.update(player);
			player.send(new SendInterface(51000));
			break;
		case DialogueConstants.OPTIONS_4_4:
			break;
			
		}
		return false;
	}

	@Override
	public void execute() {
		switch(next) {
		case 0:
			DialogueManager.sendNpcChat(player, 606, Emotion.HAPPY, "Hello "+player.getUsername()+".", "I am the prestige master.", "How may I help you?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Tell me more about prestiges", "I would like to trade", "I would like to prestige", "Nevermind");
			break;
		case 5:
			DialogueManager.sendNpcChat(player, 606, Emotion.HAPPY, "The prestiging system is quite simple.");
			next ++;
			break;
		case 6:
			DialogueManager.sendNpcChat(player, 606, Emotion.HAPPY, "When you have reached the level 99 in any skill", "you will have the ability to Prestige it.");
			next ++;
			break;
		case 7:
			DialogueManager.sendNpcChat(player, 606, Emotion.HAPPY, "By doing so you will reset your experience to 0.", "In return you will be rewarded with coins as well as your", "skill being a different color representing the prestige level.");
			setNext(1);
			break;
		}
	}


}
