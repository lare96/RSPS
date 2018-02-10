package com.vencillio.rs2.content.minigames.duelarena;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class DuelArenaForfeit extends Dialogue {
	
	public DuelArenaForfeit(Player player) {
		setPlayer(player);
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case 9157:
			if (!getPlayer().getDueling().isDueling()) {
				return true;
			}
			getPlayer().getDueling().onDuelEnd(true, false);
			getPlayer().getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return true;
		case 9158:
			getPlayer().getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return true;
		}

		return false;
	}

	@Override
	public void execute() {
		switch (getNext()) {
		case 0:
			DialogueManager.sendStatement(getPlayer(), new String[] { "Are you sure you would like to forfeit?" });
			setNext(1);
			break;
		case 1:
			DialogueManager.sendOption(getPlayer(), new String[] { "Yes", "No" });
			end();
		}
	}
}
