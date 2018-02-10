package com.vencillio.rs2.content.skill.thieving;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class ThievingNpcTask extends Task {

	public static boolean attemptThieving(Player player, Mob mob) {
		if (player.getSkill().locked()) {
			return false;
		}
		ThievingNpcData data = ThievingNpcData.getNpcById(mob.getId());
		if (data == null)
			return false;
		if (System.currentTimeMillis() - player.getCurrentStunDelay() < player.getSetStunDelay()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You are stunned!"));
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have any inventory space."));
			return true;
		}

		if (!meetsRequirements(player, data)) {
			return false;
		}
		player.getClient().queueOutgoingPacket(new SendMessage("You attempt to pick the " + mob.getDefinition().getName() + "'s pocket."));
		player.getUpdateFlags().sendAnimation(new Animation(881));
		TaskQueue.queue(new ThievingNpcTask(3, player, data, mob));
		return true;
	}

	private static void failedAttempt(Player player, ThievingNpcData data, Mob mob) {
		mob.getUpdateFlags().sendForceMessage("What are you doing in me pockets?");
		mob.face(player);
		mob.getUpdateFlags().sendAnimation(new Animation(422));
		player.getClient().queueOutgoingPacket(new SendMessage("You fail to pick the " + mob.getDefinition().getName() + "'s pocket."));
		player.getUpdateFlags().sendGraphic(Graphic.highGraphic(80, 0));
		player.hit(new Hit(1));
		player.getUpdateFlags().sendAnimation(new Animation(404));
		player.setCurrentStunDelay(System.currentTimeMillis() + data.getStunTime() * 1000);
		player.setSetStunDelay(6000L);
		player.getCombat().setInCombat(null);
	}

	private static boolean meetsRequirements(Player player, ThievingNpcData data) {
		if (player.getMaxLevels()[17] < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a Thieving level of " + data.getLevelRequired() + " to pickpocket this mob."));
			return false;
		}

		return true;
	}

	private static void successfulAttempt(Player player, ThievingNpcData data, Mob mob) {
		player.getSkill().addExperience(17, data.getExperience());

		Item stolen = null;

		if (data == ThievingNpcData.MASTER_FARMER || data == ThievingNpcData.MASTER_FARMER2) {
			int roll = Utility.randomNumber(SEEDS[0].length);

			if (SEEDS[0][roll] == -1)
				stolen = new Item(SEEDS[1][Utility.randomNumber(SEEDS[1].length)]);
			else
				stolen = new Item(SEEDS[0][roll]);
		} else {
			int roll = Utility.randomNumber(data.getItems().length);
			stolen = new Item(data.getItems()[roll][0], data.getItems()[roll][1]);
		}

		if (!player.getInventory().hasSpaceFor(stolen)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to carry that."));
			return;
		}

		player.getInventory().add(stolen);

		player.getClient().queueOutgoingPacket(new SendMessage("You successfully pick the " + mob.getDefinition().getName() + "'s pocket."));
	}

	private static boolean successfulAttemptChance(Player player, ThievingNpcData data) {
		return Skills.isSuccess(player, 17, (int) (data.levelRequired * 0.7D));
	}

	private Player player;

	private ThievingNpcData data;

	private Mob mob;

	public static final int[][] SEEDS = { { 5291, 5292, 5293, -1, 5294, 5318, 5319, 5324, 5322, 5320, 5096, 5097, 5098, -1, 5318, 5318, 5318, 5318, -1, 5318, 5319, 5319, 5319, 5319, -1, 5324, 5324, 5324 }, { 5295, 5296, 5297, 5298, 14870, 5299, 5300, 5301, 5302, 5303, 5304, 5323, 5321, 5099, 5100, 14589 } };

	public ThievingNpcTask(int delay, Player player, ThievingNpcData data, Mob mob) {
		super(player, delay, false, Task.StackType.NEVER_STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.data = data;
		this.mob = mob;
	}

	@Override
	public void execute() {
		if (successfulAttemptChance(player, data))
			successfulAttempt(player, data, mob);
		else {
			failedAttempt(player, data, mob);
		}
		stop();
	}

	@Override
	public void onStop() {
	}
}
