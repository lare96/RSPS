package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.DropTable;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for npc guide
 * @author Daniel
 *
 */
public class HariDialogue extends Dialogue {

	/**
	 * Npc guide
	 * @param player
	 */
	public HariDialogue(Player player) {
		this.player = player;
	}
	
	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case DialogueConstants.OPTIONS_2_1: 
			DropTable.open(player);
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
			DialogueManager.sendNpcChat(player, 1305, Emotion.CALM, "Hello adventurer.", "I can show you the drop table for any NPC.");
			next ++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Open Drop Tables", "Nevermind");
			break;
		}	
	}

}
