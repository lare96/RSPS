package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.content.cluescroll.ClueScrollManager;
import com.vencillio.rs2.content.minigames.barrows.Barrows;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

/**
 * Digging with a spade
 */
public class DigTask extends Task {

	private final Player player;
	private int time = 0;

	public DigTask(Player player) {
		super(player, 1, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
	}

	@Override
	public void execute() {
		if (++time == 1) {
			player.getUpdateFlags().sendAnimation(830, 0);
			player.getClient().queueOutgoingPacket(new SendMessage("You dig.."));
			return;
		}

		if (++time != 3) {
			return;
		}

		player.getClient().queueOutgoingPacket(new SendSound(380, 10, 0));

		if (ClueScrollManager.SINGLETON.dig(player)) {
			stop();
			return;
		}

		if (Barrows.teleportPlayer(player)) {
			stop();
			return;
		}

		player.getClient().queueOutgoingPacket(new SendMessage("You find nothing of interest."));
		stop();
	}

	@Override
	public void onStop() {
		player.getUpdateFlags().sendAnimation(65535, 0);
	}

}
