package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.core.network.mysql.MembershipRewards;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.membership.RankHandler;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Membership dialogue
 * @author Daniel
 *
 */
public class MembershipDialogue extends Dialogue {
	
	public MembershipDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		case DialogueConstants.OPTIONS_5_1:
			setNext(2);
			execute();
			break;
		case DialogueConstants.OPTIONS_5_2: //What is this
			player.send(new SendString("http://www.rennatscape.proboards.com", 12000));
			break;
		case DialogueConstants.OPTIONS_5_3:
			setNext(3);
			execute();
			break;
		case DialogueConstants.OPTIONS_5_4:
			RankHandler.upgrade(player);
			break;
		case DialogueConstants.OPTIONS_5_5:
			player.send(new SendRemoveInterfaces());
			break;
		
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "Hello adventurer. How may I help you?");
			next ++;
			break;
		case 1:
			DialogueManager.sendOption(player, "What are credits?", "Benifits of membership", "I have purchased something", "Update rank", "Nevermind");
			break;
		case 2:
			DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "Credits can be purchased on our online store.", "They can be used for buying items from my store ", "and many other features in game.", "Including purchasing credits in the credits tab.");
			setNext(1);
			break;
		case 3:
			DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "Alright, let me check out the database...");
			next ++;
			break;
		case 4:
			MembershipRewards.collectReward(player);
			break;
		
		}
		
	}

}
