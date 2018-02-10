package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.player.Player;

public class FinishTeleportingTask extends Task {

	private final Player player;

	public FinishTeleportingTask(Player player, int ticks) {
		super(player, ticks, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
	}

	@Override
	public void execute() {
		player.setTakeDamage(true);
		stop();
	}

	@Override
	public void onStop() {
	}

}
