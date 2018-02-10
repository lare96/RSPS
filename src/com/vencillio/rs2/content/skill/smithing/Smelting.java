package com.vencillio.rs2.content.skill.smithing;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class Smelting extends Task {

	private final Player player;
	private final SmeltingData data;
	private final int amount;
	private int smelted = 0;
	private final String name;
	public static final Animation SMELTING_ANIMATION = new Animation(899, 0);
	public static final String A = "You smelt ";
	public static final String B = ".";
	public static final String IRON_FAILURE = "You fail to refine the iron ore.";

	public Smelting(Player player, int amount, SmeltingData data) {
		super(player, 2, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
		this.amount = amount;
		name = data.getResult().getDefinition().getName();

		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

		if (!canSmelt(player, data, false)) {
			stop();
		}
	}

	public boolean canSmelt(Player player, SmeltingData data, boolean taskRunning) {
		if (player.getMaxLevels()[13] < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a Smithing level of " + data.getLevelRequired() + " to smelt this bar."));
			return false;
		}

		for (Item i : data.getRequiredOres()) {
			if (!player.getInventory().hasItemAmount(i.getId(), i.getAmount())) {
				player.getClient().queueOutgoingPacket(new SendMessage(taskRunning ? "You have run out of " + i.getDefinition().getName() + "." : "You don't not have any " + i.getDefinition().getName().toLowerCase() + " to smelt."));
				return false;
			}
		}

		return true;
	}

	@Override
	public void execute() {
		if (!canSmelt(player, data, true)) {
			stop();
			return;
		}

		player.getUpdateFlags().sendAnimation(SMELTING_ANIMATION);

		player.getInventory().remove(data.getRequiredOres(), false);

		if (data == SmeltingData.IRON_BAR) {
			if (Skills.isSuccess(player, 13, data.getLevelRequired())) {
				player.getInventory().add(data.getResult(), false);
				player.getClient().queueOutgoingPacket(new SendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + "."));
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You fail to refine the iron ore."));
			}
		} else {
			player.getInventory().add(data.getResult(), false);
			player.getClient().queueOutgoingPacket(new SendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + "."));
		}

		player.getInventory().update();

		player.getSkill().addExperience(13, data.getExp());

		if (++smelted == amount)
			stop();
	}

	public boolean isSuccess(Player player, SmeltingData data) {
		return Skills.isSuccess(player, 13, data.levelRequired);
	}

	@Override
	public void onStop() {
	}
}
