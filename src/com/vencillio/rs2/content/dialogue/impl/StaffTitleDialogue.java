package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.PlayerTitle;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterString;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class StaffTitleDialogue extends Dialogue {

	public StaffTitleDialogue(Player player) {
		this.player = player; 
	}
	
	private void title(int color) {
		player.setPlayerTitle(PlayerTitle.create(player.getPlayerTitle().getTitle(), color, false));
		player.send(new SendMessage("Special title has been set!"));
		player.setAppearanceUpdateRequired(true);
		player.send(new SendRemoveInterfaces());
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {

		case DialogueConstants.OPTIONS_2_1:
			player.start(new OptionDialogue("Blue", p -> {
				title(0x3366FF);	
			} , "Red", p -> {
				title(0xB80000);				
			} , "Green", p -> {
				title(0x47B224);
			} , "Purple", p -> {
				title(0x8F24B2);
			} , "Orange", p -> {
				title(0xFF6600);
			}));
			break;
			
		case DialogueConstants.OPTIONS_2_2:
			player.setEnterXInterfaceId(56002);
			player.getClient().queueOutgoingPacket(new SendEnterString());
			break;
		
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			if (!PlayerConstants.isStaff(player)) {
				DialogueManager.sendStatement(player, "This is not for you!");
				return;
			}
			DialogueManager.sendNpcChat(player, 6749, Emotion.HAPPY_TALK, "Hello " + player.getUsername() + "!", "I can give you a special title.", "You must be privilaged enough of course!");
			next++;
			break;
			
		case 1:
			DialogueManager.sendOption(player, "Title color", "Select title");
			break;
		
		}
	}
	
	
	
}
