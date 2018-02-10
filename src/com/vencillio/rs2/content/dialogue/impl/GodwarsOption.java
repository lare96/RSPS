package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.Player;

/**
 * Godwars Dialogue
 * @author Daniel
 *
 */
public class GodwarsOption extends Dialogue {

	public GodwarsOption(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {

	

		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {

		case 0:
			DialogueManager.sendStatement(player, "Coming soon!");
			break;

		}
	}

}
