package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.PlayerTitle;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class DunceDialogue extends Dialogue {

	public DunceDialogue(Player player) {
		this.player = player; 
	}
	
	private void title(String title, int color) {
		player.setPlayerTitle(PlayerTitle.create(title, color, false));
		player.send(new SendMessage("Special title has been set!"));
		player.setAppearanceUpdateRequired(true);
		player.send(new SendRemoveInterfaces());
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		case DialogueConstants.OPTIONS_4_1:
			if (PlayerConstants.isStaff(player) || player.getRights() == 5 || player.getRights() == 6 || player.getRights() == 7 || player.getRights() == 8) {
				title("Member", 0xCC0000);
			} else {
				DialogueManager.sendNpcChat(player, 6749, Emotion.SAD, "You need to at least be a <img=5> <col=0x015E7D>Member</col>!");
			}
			break;
			
		case DialogueConstants.OPTIONS_4_2:
			if (PlayerConstants.isStaff(player) || player.getRights() == 6 || player.getRights() == 7 || player.getRights() == 8) {
				title("Super", 0x3366CC);
			} else {
				DialogueManager.sendNpcChat(player, 6749, Emotion.SAD, "You need to at least be a <img=6> <col=0x015E7D>Super Member</col>!");
			}
			break;
			
		case DialogueConstants.OPTIONS_4_3:
			if (PlayerConstants.isStaff(player) || player.getRights() == 7 || player.getRights() == 8) {
				title("Extreme", 0x244700);
			} else {
				DialogueManager.sendNpcChat(player, 6749, Emotion.SAD, "You need to at least be a <img=7> <col=0x158A08>Extreme Member</col>!");
			}
			break;
			
		case DialogueConstants.OPTIONS_4_4:
			if (PlayerConstants.isStaff(player) || player.getRights() == 8) {
				title("Elite", 0x9900FF);
			} else {
				DialogueManager.sendNpcChat(player, 6749, Emotion.SAD, "You need to at least be a <img=8> <col=0x7D088A>Elite Member</col>!");
			}
			break;
		
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 6749, Emotion.HAPPY_TALK, "Hello " + player.getUsername() + "!", "I can give you a special title.", "You must be privilaged enough of course!");
			next++;
			break;
			
		case 1:
			DialogueManager.sendOption(player, "Member", "Super", "Extreme", "Elite");
			break;
		
		}
	}
	
	
	
}
