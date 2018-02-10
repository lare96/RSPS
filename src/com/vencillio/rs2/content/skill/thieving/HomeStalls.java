package com.vencillio.rs2.content.skill.thieving;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles stalls for thieving class
 * 
 * @author Daniel
 *
 */
public class HomeStalls extends Task {
	
	private Player player;

	private stallData data;

	public HomeStalls(int delay, Player player, stallData data) {
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
		successfull(player, data);
		stop();
	}

	@Override
	public void onStop() {
	}

	private enum stallData {
		FOOD("food", 4875, 3162, 1500, 1, 30),
		GENERAL("general", 4876, 1887, 2500, 25, 50),
		CRAFT("crafting", 4874, 1635, 5500, 50, 70),
		MAGIC("magic", 4877, 8788, 1, 75, 90),
		SCIMITAR("scimitar", 4878, 6721, 12000, 85, 125);

		private String name;
		private int objectId;
		private int itemId;
		private int levelReq;
		private int xpGained;

		private stallData(String name, int objectId, int itemId, int itemAmount, int levelReq, int xpGained) {
			this.name = name;
			this.objectId = objectId;
			this.itemId = itemId;
			this.levelReq = levelReq;
			this.xpGained = xpGained;
		}

		public static stallData getObjectById(int id) {
			for (stallData data : stallData.values())
				if (data.objectId == id)
					return data;
			return null;
		}
	}
	
	public static void attempt(Player player, int id, Location location) {
		stallData data = stallData.getObjectById(id);

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

		TaskQueue.queue(new HomeStalls(4, player, data));
	}
	
	public static boolean meetsRequirements(Player player, stallData stall) {
		if (stall == null) {
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have enough inventory spaces left to hold this."));
			return false;
		}
		if (player.getSkill().getLevels()[17] < stall.levelReq) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a thieving level of " + stall.levelReq + " to do this!"));
			return false;
		}
		return true;
	}

	public static void successfull(Player player, stallData stall) {
			player.getUpdateFlags().sendAnimation(new Animation(832));
			player.getInventory().add(new Item(stall.itemId, 1));
			player.getSkill().addExperience(17, player.inMemberZone() ? stall.xpGained * 2: stall.xpGained);
			player.getClient().queueOutgoingPacket(new SendMessage("You manage to steal some loot from the " + stall.name + " stall."));
			AchievementHandler.activateAchievement(player, AchievementList.THIEVE_300_TIMES_FROM_STALLS, 1);
			handleRandom(player);
	}

	public static void handleRandom(Player player) {
		int random = Utility.randomNumber(100);
		if (random == 1) {
			if (player.inMemberZone()) {
				player.teleport(new Location(2854, 3337, 0));
			} else {
				player.teleport(new Location(3087, 3492, 0));
			}
			player.getUpdateFlags().sendAnimation(new Animation(4367));
			player.getClient().queueOutgoingPacket(new SendMessage("Some mystical force drops you from the sky causing damage."));
			player.hit(new Hit(2, Hit.HitTypes.NONE));
			player.getUpdateFlags().sendForceMessage(Utility.randomElement(FORCED_CHAT));
			AchievementHandler.activateAchievement(player, AchievementList.FAIL_50_TIMES_THIEVING_STALLS, 1);
		}
	}

	public final static String FORCED_CHAT[] = { "Ow!", "Ouch!", "What the-?", "This is racist!", "Noooo!", "I want to live!", "Somebody help me!", "Only in Tannerscape -.-", };

}