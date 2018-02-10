package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.gambling.FlowerGame;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles Flower Game dialogue
 * 
 * @author Daniel
 *
 */
public class FlowerGameDialogue extends Dialogue {

	public FlowerGameDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {

		switch (id) {

		case DialogueConstants.OPTIONS_5_1:
			DialogueManager.sendStatement(player, "Coming soon!");
			break;

		case DialogueConstants.OPTIONS_5_2:
			FlowerGame.play(player, 100_000);
			break;

		case DialogueConstants.OPTIONS_5_3:
			FlowerGame.play(player, 500_000);
			break;

		case DialogueConstants.OPTIONS_5_4:
			FlowerGame.play(player, 1_000_000);
			break;

		case DialogueConstants.OPTIONS_5_5:
			player.start(new GamblerDialogue(player));
			break;

		}

		return false;
	}

	@Override
	public void execute() {

		switch (next) {

		case 0:
			DialogueManager.sendOption(player, "Guide", "Bet 100K", "Bet 500K", "Bet 1M", "Nevermind");
			next++;
			break;

		}

	}

}