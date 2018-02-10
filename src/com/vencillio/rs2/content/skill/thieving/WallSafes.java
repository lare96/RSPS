package com.vencillio.rs2.content.skill.thieving;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class WallSafes extends Task {

	private Player player;

	public WallSafes(int delay, Player player) {
		super(player, delay, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
	}

	public static Item ITEMS[] = { new Item(1617), new Item(1619), new Item(1621), new Item(1623), new Item(1623), new Item(995, 20), new Item(995, 40) };

	public static Item RANDOM() {
		return ITEMS[(int) (Math.random() * ITEMS.length)];
	}

	public static int timer(Player player) {
		if (player.getInventory().hasItemAmount(new Item(5560))) {
			return (10 - (int) Math.floor(player.getSkill().getLevels()[17] / 10) + Utility.random(5));
		} else {
			return (10 - (int) Math.floor(player.getSkill().getLevels()[17] / 10) + Utility.random(11)) + 20;
		}
	}

	public static int chance(Player player) {
		return (Utility.random((int) Math.floor(player.getSkill().getLevels()[17] / 10) + 1));
	}

	public static boolean can(Player player) {
		if (player.isCracking) {
			player.send(new SendMessage("You are currently cracking a safe!"));
			return false;
		}
		if (player.getSkill().getLevels()[17] < 50) {
			player.send(new SendMessage("You need a thieving level atleast 50 to crack safes!"));
			return false;
		}
		if (player.getInventory().getFreeSlots() < 1) {
			player.send(new SendMessage("You do not have any space left in your inventory."));
			return false;
		}
		return true;
	}

	public static void crack(Player player) {
		if (!can(player) || player.getDelay().elapsed() < 1000) {
			return;
		}
		player.isCracking = true;
		player.send(new SendMessage("You attempt to crack the safe... "));
		player.getUpdateFlags().sendAnimation(new Animation(881));
		player.getMovementHandler().reset();
		TaskQueue.queue(new WallSafes(timer(player), player));

	}

	@Override
	public void execute() {
		if (chance(player) == 0) {
			player.send(new SendMessage("You slip and trigger a trap!"));
			if (player.getSkill().getLevels()[17] == 99) {
				player.hit(new Hit(1));
			} else if (player.getSkill().getLevels()[17] > 79) {
				player.hit(new Hit(2));
			} else if (player.getSkill().getLevels()[17] > 49) {
				player.hit(new Hit(3));
			} else {
				player.hit(new Hit(4));
			}
			this.stop();
			player.getUpdateFlags().sendAnimation(new Animation(404));
			player.isCracking = false;
			return;
		}
		player.send(new SendMessage("You get some loot."));
		player.getInventory().add(RANDOM());
		player.getSkill().addExperience(17, 100);
		player.isCracking = false;
		player.getDelay().reset();
		this.stop();
	}

	@Override
	public void onStop() {
	}

}
