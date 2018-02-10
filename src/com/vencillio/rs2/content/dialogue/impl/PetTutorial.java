package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class PetTutorial extends Dialogue {
	public PetTutorial(Player player) {
		this.player = player;
		player.setController(Tutorial.TUTORIAL_CONTROLLER);
	}

	@Override
	public boolean clickButton(int id) {
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		case 0:
			DialogueManager.sendNpcChat(player, 6750, Emotion.CALM,
					new String[] { "Your pet will grow over time.",
							"But you must take care of it.",
							"Your pet will run away if you do not",
							"feed it every 45 minutes." });
			next += 1;
			break;
		case 1:
			player.setController(ControllerManager.DEFAULT_CONTROLLER);
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			end();
		}
	}
}
