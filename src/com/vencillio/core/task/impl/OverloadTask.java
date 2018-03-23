package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Created by Tanner on 3/22/2018.
 */
public class OverloadTask extends Task {

	private int cycles;

	private final Player player;

	private boolean success;

	public OverloadTask(Player player) {
		super(player, 1, false, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.cycles = 500;
		this.player = player;
		this.success = true;

		if (player.getAttributes().get("overload_potion_task") != null) {
			// cancels this task when starting another one
			((OverloadTask) player.getAttributes().get("overload_potion_task")).cycles = 500;
			success = false;
			return;
		}

		player.getAttributes().set("overload_potion_task", this);
	}

	public void execute() {
		int amount;
		int[] skillIds = {0, 1, 2, 4, 6};
		if (player.isDead() || !success) {
			this.stop();
			return;
		}

		if (cycles > 0) {
			cycles--;

			if (cycles % 25 == 0) {

				for (int i = 0; i < skillIds.length; i++) {
					if (player.getMaxLevels()[skillIds[i]] + 5 + (int) (player.getMaxLevels()[skillIds[i]] * 0.15) > player.getLevels()[skillIds[i]]) {
						if (player.getMaxLevels()[skillIds[i]] + 5 + (int) (player.getMaxLevels()[skillIds[i]] * 0.15) < player.getLevels()[skillIds[i]] + 5 + (int) (player.getLevels()[skillIds[i]] * 0.15)) {
							amount = player.getMaxLevels()[skillIds[i]] + 5 + (int) (player.getMaxLevels()[skillIds[i]] * 0.15);
						} else {
							amount = player.getLevels()[skillIds[i]] + 5 + (int) (player.getLevels()[skillIds[i]] * 0.15);
						}
						player.getSkill().setLevel(skillIds[i], amount);
					}
				}
			}

			if (cycles == 100) {
				player.getClient().queueOutgoingPacket(new SendMessage("@red@Your overload potion is about to run out."));
			}

			if (cycles == 0) {
				player.getClient().queueOutgoingPacket(new SendMessage("@red@Your overload potion has run out."));
				this.stop();
				return;
			}
		}
	}

	public void onStop() {
		if (success) {
			player.getAttributes().remove("overload_potion_task");
		}
	}
}
