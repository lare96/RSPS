package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue that handles genie skill reset
 * 
 * @author Daniel
 */
public class GenieResetDialogue extends Dialogue {

	public GenieResetDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case 9157:
			player.send(new SendInterface(59500));
			break;
		case 9158:
			player.send(new SendRemoveInterfaces());
			break;
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		case 0:
			DialogueManager.sendNpcChat(player, 326, Emotion.HAPPY_TALK, "Hello adventurer.", "I can reset any combat skill for 1,000,000 coins.", "Are you interested?");
			next ++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Yes", "No");
			break;
		}
	}

}
