package com.vencillio.rs2.content.skill.thieving;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class ThievingStallTask extends Task {
	
	public static final int[][] THEIVING_ITEMS = { { 1277, 1 }, { 1281, 1 }, { 1211, 1 }, { 1285, 1 }, { 1331, 1 }, { 1289, 1 } };

	public static void attemptStealFromStall(Player player, int id, Location location) {
		ThievingStallData data = ThievingStallData.getObjectById(id);

		if (data == null) {
			return;
		}

		if (player.getSkill().locked()) {
			return;
		}
		if (!meetsRequirements(player, data)) {
			return;
		}
		player.getSkill().lock(3);
		player.getUpdateFlags().sendAnimation(new Animation(832));

		TaskQueue.queue(new ThievingStallTask(1, player, data));
	}

	private static boolean meetsRequirements(Player player, ThievingStallData data) {
		if (player.getInventory().getFreeSlots() == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have enough inventory spaces left to hold this."));
			return false;
		}
		if (player.getSkill().getLevels()[17] <= data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a thieving level of " + data.getLevelRequired() + " to steal from this stall."));
			return false;
		}
		return true;
	}

	private Player player;

	private ThievingStallData data;

	public ThievingStallTask(int delay, Player player, ThievingStallData data) {
		super(player, delay, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, data)) {
			stop();
			return;
		}
		successfulAttempt(player, data);
		stop();
	}

	@Override
	public void onStop() {
	}

	private void successfulAttempt(Player player, ThievingStallData data) {
		int randomItem = Utility.randomNumber(data.getRewards().length);
		player.getInventory().add(new Item(data.getRewards()[randomItem][0], data.getRewards()[randomItem][1]));

		if (data.getRewards()[randomItem][0] == 995) {
			player.getClient().queueOutgoingPacket(new SendMessage("You steal " + data.getRewards()[randomItem][1] + "gp from the stall."));
		}
		
		player.getSkill().addExperience(17, data.getExperience());
	}
}
