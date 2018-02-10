package com.vencillio.rs2.content.skill.runecrafting;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

public class RunecraftingTask extends Task {

	public static boolean attemptRunecrafting(Player player, GameObject object) {
		RunecraftingData data = RunecraftingData.forId(object.getId());

		if (data == null) {
			return true;
		}

		if (getEssenceId(player) == -1) {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have any essence to craft runes with."));
			return true;
		}

		if (!meetsRequirements(player, data, object)) {
			return true;
		}

		TaskQueue.queue(new RunecraftingTask(player, data, getEssenceId(player)));
		return true;
	}

	private static int getEssenceId(Player player) {
		if (player.getInventory().hasItemId(1436)) {
			return 1436;
		}
		if (player.getInventory().hasItemId(7936)) {
			return 7936;
		}
		return -1;
	}

	private static boolean meetsRequirements(Player player, RunecraftingData data, GameObject object) {
		if (player.getSkill().getLevels()[20] < data.getLevel()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a runecrafting level of " + data.getLevel() + " to craft this rune."));
			return false;
		}
		return true;
	}

	private final Player player;

	private final RunecraftingData data;

	private final int essenceId;

	public static final int PURE_ESSENCE = 7936;

	public RunecraftingTask(Player player, RunecraftingData data, int essenceId) {
		super(player, 1, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
		this.essenceId = essenceId;
	}

	@Override
	public void execute() {
		player.getClient().queueOutgoingPacket(new SendSound(481, 1, 0));
		player.getUpdateFlags().sendAnimation(new Animation(791));
		player.getUpdateFlags().sendGraphic(Graphic.highGraphic(186, 0));
		
		int amount = player.getInventory().getItemAmount(essenceId);
		
		if (essenceId == PURE_ESSENCE) {
			amount *= 2;
		}

		player.getSkill().addExperience(20, amount * data.getXp());

		player.getInventory().remove(new Item(essenceId, amount / (essenceId == PURE_ESSENCE ? 2 : 1)));
		player.getInventory().add(new Item(data.getRuneId(), amount * getMultiplier()));

		if (Utility.random(5) == 0) {
			if (!player.getInventory().hasItemId(5509) && !player.getBank().hasItemId(5509)) {
				player.getInventory().add(5509, 1);
				player.send(new SendMessage("You find a small pouch while runecrafting!"));
			} else if (!player.getInventory().hasItemId(5510) && !player.getBank().hasItemId(5510)) {
				player.getInventory().add(5510, 1);
				player.send(new SendMessage("You find a medium pouch while runecrafting!"));
			} else if (!player.getInventory().hasItemId(5512) && !player.getBank().hasItemId(5512)) {
				player.getInventory().add(5512, 1);
				player.send(new SendMessage("You find a large pouch while runecrafting!"));
			} else if (!player.getInventory().hasItemId(5514) && !player.getBank().hasItemId(5514)) {
				player.getInventory().add(5514, 1);
				player.send(new SendMessage("You find a giant pouch while runecrafting!"));
			}
		}

		if (data == RunecraftingData.BLOOD) {
			AchievementHandler.activateAchievement(player, AchievementList.CRAFT_1500_BLOOD_RUNES, amount);
		}

		stop();
	}

	private int getMultiplier() {
		int multiplier = 1;
		for (int i = 1; i < data.getMultiplier().length; i++) {
			if (player.getMaxLevels()[20] >= data.getMultiplier()[i]) {
				multiplier = i;
			}
		}

		return multiplier;
	}

	@Override
	public void onStop() {
	}
}
