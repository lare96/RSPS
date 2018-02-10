package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.Player;

public class UseBankDialogue extends Dialogue {

	public UseBankDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int button) {
		switch (button) {
		case DialogueConstants.OPTIONS_2_1:
			player.getBank().openBank();
			return true;
		case DialogueConstants.OPTIONS_2_2:
			player.start(new ShopExchangeDialogue(player));
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		DialogueManager.sendOption(player, "Open bank", "POS Options");
	}

}
