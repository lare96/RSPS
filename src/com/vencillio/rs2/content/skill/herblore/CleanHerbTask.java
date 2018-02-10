package com.vencillio.rs2.content.skill.herblore;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class CleanHerbTask extends Task {
	public static void attemptHerbCleaning(Player player, int slot) {
		if (player.getInventory().get(slot) == null) {
			return;
		}

		GrimyHerbData data = GrimyHerbData.forId(player.getInventory().get(slot).getId());

		if (data == null) {
			return;
		}
		if (!meetsRequirements(player, data)) {
			return;
		}

		TaskQueue.queue(new CleanHerbTask(player, slot, data));
	}

	private static boolean meetsRequirements(Player player, GrimyHerbData data) {
		if (player.getSkill().getLevels()[15] < data.getLevelReq()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need an herblore level of " + data.getLevelReq() + " to clean this herb."));
			return false;
		}
		return true;
	}

	private final Player player;

	private int slot;

	private GrimyHerbData data;

	public CleanHerbTask(Player player, int slot, GrimyHerbData data) {
		super(player, 0, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.slot = slot;
		this.data = data;
	}

	private void cleanHerb() {
		player.getInventory().getItems()[slot] = null;
		player.getInventory().getItems()[slot] = new Item(data.getCleanHerb(), 1);
		player.getInventory().update();
		player.getSkill().addExperience(15, data.getExp());
		player.getClient().queueOutgoingPacket(new SendMessage("You clean the dirt off the herb."));
	}

	@Override
	public void execute() {
		cleanHerb();
		stop();
	}

	@Override
	public void onStop() {
	}
}
