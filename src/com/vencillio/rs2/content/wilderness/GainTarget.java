package com.vencillio.rs2.content.wilderness;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.player.Player;

public class GainTarget extends Task {

	private Player player;

	public GainTarget(Player player, byte delay) {
		super(delay);
		this.player = player;
	}

	@Override
	public void execute() {
		if (!player.inWilderness() || TargetSystem.getInstance().playerHasTarget(player)) {
			stop();
			return;
		}
		if (player.inWilderness()) {
			TargetSystem.getInstance().assignTarget(player);
			stop();
			return;
		}
	}

	@Override
	public void onStop() {
		player.getAttributes().remove("gainTarget");
	}
}