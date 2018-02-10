package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.entity.player.Player;

public class AgilityDialogue extends Dialogue {

	public AgilityDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {

		case DialogueConstants.OPTIONS_3_1:
			player.getMagic().teleport(2480, 3437, 0, TeleportTypes.SPELL_BOOK);
			DialogueManager.sendStatement(player, "You have teleported to the Gnome course.");
			break;

		case DialogueConstants.OPTIONS_3_2:
			player.getMagic().teleport(2552, 3558, 0, TeleportTypes.SPELL_BOOK);
			DialogueManager.sendStatement(player, "You have teleported to the Barbarian course.");
			break;

		case DialogueConstants.OPTIONS_3_3:
			player.getMagic().teleport(3001, 3932, 0, TeleportTypes.SPELL_BOOK);
			DialogueManager.sendStatement(player, "You have teleported to the Agility course.");
			break;

		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {

		case 0:
			DialogueManager.sendOption(player, "Gnome Course", "Barbarian Course", "Wilderness Course");
			break;

		}
	}

}
