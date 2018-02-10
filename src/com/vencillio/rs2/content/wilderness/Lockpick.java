package com.vencillio.rs2.content.wilderness;

import com.vencillio.core.cache.map.Door;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.WalkThroughDoorTask;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Lockpick extends Task {

	private Player player;
	private int x;
	private int y;
	private int z;
	private boolean success = false;
	private boolean insideExiting = false;

	public Lockpick(Player player, byte delay, int doorId, int x, int y, int z) {
		super(delay);
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void execute() {
		player.getUpdateFlags().sendAnimation(new Animation(2246));
		Door door = Region.getDoor(x, y, z);
		if (door.getX() == 3041 && door.getY() == 3959) {
			if (player.getX() != door.getX()) {
				player.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
				return;
			}
		}
		if (door.getX() == 3191 && door.getY() == 3963) {
			if (player.getX() != door.getX()) {
				player.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
				return;
			}
		}
		if (door.getX() == 3190 && door.getY() == 3957) {
			if (player.getX() != door.getX()) {
				player.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
				return;
			}
		}
		if (door.getX() == 3038 || door.getX() == 3044 && door.getY() == 3956) {
			if (player.getY() != door.getY()) {
				player.getMovementHandler().addToPath(new Location(door.getX(), door.getY(), door.getZ()));
				return;
			}
		}
		if (player.getX() == 3038 && player.getY() == 3956 || player.getX() == 3044 && player.getY() == 3956 || player.getX() == 3041 && player.getY() == 3959 || player.getX() == 3190 && player.getY() == 3957 || player.getX() == 3191 && player.getY() == 3963) {
			insideExiting = true;
			stop();
			return;
		}
		int chance = Utility.randomNumber(4);
		if (chance == 3 || chance == 1 || chance == 2) {
			player.getClient().queueOutgoingPacket(new SendMessage("You successfully pick the lock.."));
			success = true;
			stop();
			return;
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("You fail to pick the lock on the door.."));
			stop();
			return;
		}
	}

	@Override
	public void onStop() {
		if (success) {
			player.getSkill().addExperience(17, 1350 + Utility.randomNumber(763));
			Task task = new WalkThroughDoorTask(player, x, y, z, new Location(x, y, z));
			player.getAttributes().set("lockPick", task);
			TaskQueue.queue(task);
			return;
		} else if (insideExiting && x == 3038) {
			Task task = new WalkThroughDoorTask(player, x, y, z, new Location(x - 1, y, z));
			player.getAttributes().set("lockPick", task);
			TaskQueue.queue(task);
		} else if (insideExiting && x == 3044) {
			Task task = new WalkThroughDoorTask(player, x, y, z, new Location(x + 1, y, z));
			player.getAttributes().set("lockPick", task);
			TaskQueue.queue(task);
		} else if (insideExiting && x == 3041) {
			Task task = new WalkThroughDoorTask(player, x, y, z, new Location(x, y + 1, z));
			player.getAttributes().set("lockPick", task);
			TaskQueue.queue(task);
		} else if (insideExiting && x == 3190) {
			Task task = new WalkThroughDoorTask(player, x, y, z, new Location(x, y + 1, z));
			player.getAttributes().set("lockPick", task);
			TaskQueue.queue(task);
		} else if (insideExiting && x == 3191) {
			Task task = new WalkThroughDoorTask(player, x, y, z, new Location(x, y - 1, z));
			player.getAttributes().set("lockPick", task);
			TaskQueue.queue(task);
		}
	}

}