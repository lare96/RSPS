package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.skill.firemaking.FireColor;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class FiremakingDialogue extends Dialogue {

	public FiremakingDialogue(Player player) {
		this.player = player;
	}
	
	@Override
	public boolean clickButton(int id) {
		switch (id) {

		case DialogueConstants.OPTIONS_3_1:
			setNext(2);
			execute();
			break;

		case DialogueConstants.OPTIONS_3_2:
			if (!PlayerConstants.isOwner(player)) {
				DialogueManager.sendNpcChat(player, 118, Emotion.HAPPY_TALK, "Coming soon!");
			} else {
				FireColor.open(player);
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
		switch (next) {

		case 0:
			DialogueManager.sendNpcChat(player, 118, Emotion.HAPPY_TALK, "Hello my friend.", "I am " + GameDefinitionLoader.getNpcDefinition(118).getName() + ".", "How may I help you?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Benifits of changing fire color", "Change fire color (10 credits)", "Nothing");
			break;
		case 2:
			DialogueManager.sendNpcChat(player, 118, Emotion.HAPPY_TALK, "By paying 10 credits you can change all fire colors.", "You will earn double cooking & firemaking exp for 30mins.");
			break;
			
		}

	}

}
