package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Security dialogue
 * @author Daniel
 *
 */
public class SecurityDialogue extends Dialogue {

	/**
	 * Bank pin
	 * @param player
	 */
	public SecurityDialogue(Player player) {
		this.player = player;
	}
	
	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case DialogueConstants.OPTIONS_3_1:
			player.send(new SendInterface(56000));
			break;
		case DialogueConstants.OPTIONS_3_2:
			player.send(new SendString("</col>Bank pin status: " + (player.getPin() == null ? "@red@None!" : "@gre@Set!"), 43753));
			if (player.getPin() == null) {
				player.send(new SendString("Type in a 4 digit number and press enter to set a pin.", 43754));
				player.send(new SendString("Be sure to write this down!", 43756));
			} else {
				player.send(new SendString("Type in your 4 digit pin to remove it.", 43754));
				player.send(new SendString("", 43756));
			}
			player.send(new SendString("Bank Pin Management", 43752));
			player.send(new SendMessage("Bank Pin: " + player.getPin()));
			player.send(new SendInterface(43750));
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
			DialogueManager.sendNpcChat(player, 3951, Emotion.DEFAULT, "Hello " + player.getUsername() + ".","I can help you set extra security to your account.");
			next ++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Set security details", "Bank pin management", "Nevermind");
			break;
		}
	}
}
