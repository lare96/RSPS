package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public abstract class ConfirmDialogue extends Dialogue {
	
	private final String[] confirm;

	public ConfirmDialogue(Player player) {
		this.player = player;
		confirm = null;
	}

	public ConfirmDialogue(Player player, String[] confirm) {
		this.player = player;
		this.confirm = confirm;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case 9157:
			end();
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			onConfirm();
			return true;
		case 9158:
			end();
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		switch (next) {
		case 0:
			if (confirm == null)
				DialogueManager.sendStatement(player, new String[] { "Are you sure?" });
			else {
				DialogueManager.sendStatement(player, confirm);
			}
			next += 1;
			break;
		case 1:
			DialogueManager.sendOption(player, new String[] { "Yes.", "No." });
		}
	}

	public abstract void onConfirm();
}
