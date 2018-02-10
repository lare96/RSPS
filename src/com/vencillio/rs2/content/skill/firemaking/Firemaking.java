package com.vencillio.rs2.content.skill.firemaking;

import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItem;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Firemaking extends Task {
	
	public static boolean attemptFiremaking(Player player, Item used, Item usedWith) {
		if (LogData.getLogById(used.getId()) != null && LogData.getLogById(usedWith.getId()) != null) {
			return false;
		}
		
		Item log = usedWith.getId() != 590 ? usedWith : used.getId() != 590 ? used : null;
		LogData logData = LogData.getLogById(log.getId());

		if (logData == null || used.getId() == 946 || usedWith.getId() == 946) {
			return false;
		}

		if (!meetsRequirements(player, log)) {
			return true;
		}

		player.getUpdateFlags().sendAnimation(new Animation(733));
		groundLog = new GroundItem(new Item(log.getId(), 1), new Location(player.getLocation()), player.getUsername());
		GroundItemHandler.add(groundLog);
		player.getInventory().remove(log);
		TaskQueue.queue(new Firemaking(1, player, log, logData));

		return true;
	}

	private static boolean meetsRequirements(Player player, Item log) {
		int skillLevel = player.getSkill().getLevels()[11];
		LogData data = LogData.getLogById(log.getId());

		int x = player.getLocation().getX();
		int y = player.getLocation().getY();

		if ((x >= 3090) && (y >= 3488) && (x <= 3098) && (y <= 3500)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot light fires here."));
			return false;
		}

		if (skillLevel < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a firemaking level of " + data.getLevelRequired() + " to light this log."));
			return false;
		}

		if (ObjectManager.objectExists(player.getLocation())) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot light a fire here."));
			return false;
		}

		if (!player.getInventory().hasItemId(590)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a tinderbox to light this log."));
			return false;
		}

		return true;
	}

	public static boolean success(Player player, Item log) {
		return Skills.isSuccess(player, 11, LogData.getLogById(log.getId()).getLevelRequired());
	}

	private Player player;
	private LogData logData;

	private Item log;

	private int animationCycle;

	private static GroundItem groundLog;

	public Firemaking(int delay, Player entity, Item log, LogData logData) {
		super(entity, delay, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		player = entity;
		this.log = log;
		this.logData = logData;

		player.getMovementHandler().reset();
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, log)) {
			stop();
			return;
		}
		if (success(player, log)) {
			successfullFiremake(player);
			stop();
			return;
		}
		if (animationCycle < 6) {
			animationCycle += 1;
		} else {
			player.getUpdateFlags().sendAnimation(new Animation(733));
			animationCycle = 0;
		}
	}

	@Override
	public void onStop() {
	}

	private void successfullFiremake(Player player) {
		GroundItemHandler.remove(groundLog);

		player.getUpdateFlags().sendAnimation(65535, 0);

		GameObject object = new GameObject(FireColor.FIRE, new Location(player.getLocation()), 10, 0);

		TaskQueue.queue(new FireTask(player, 50 + logData.ordinal() * 15, object));

		player.getSkill().addExperience(11, logData.getExperience());
		AchievementHandler.activateAchievement(player, AchievementList.BURN_500_LOGS, 1);
		AchievementHandler.activateAchievement(player, AchievementList.BURN_1250_LOGS, 1);
		walk(player);

	}

	private void walk(Player player) {
		if (!Region.getRegion(player.getLocation().getX(), player.getLocation().getY()).blockedWest(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())) {
			player.getMovementHandler().walkTo(-1, 0);
		} else if (!Region.getRegion(player.getLocation().getX(), player.getLocation().getY()).blockedEast(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
			player.getMovementHandler().walkTo(1, 0);
	}
}
