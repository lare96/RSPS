package com.vencillio.rs2.content.skill.prayer;

import java.util.HashMap;
import java.util.Map;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.Task.BreakType;
import com.vencillio.core.task.Task.StackType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

public class BoneBurying {
	
	public static enum Bones {
		NORMAL_BONES(526, 4.5D),
		WOLF_BONES(2859, 4.5D),
		BAT_BONES(530, 4.5D),
		BIG_BONES(532, 18.0D),
		BABYDRAGON_BONES(534, 30.0D),
		DRAGON_BONES(536, 72.0D),
		DAGG_BONES(6729, 75.0D),
		OURG_BONES(4834, 100.0D),
		LONG_BONE(10976, 100.0D),
		SKELETAL_WYVERN_BONES(6812, 95.0D),
		LAVA_DRAGON_BONES(11943, 85.0D);

		private int id;
		private double experience;
		private static Map<Integer, Bones> bones = new HashMap<Integer, Bones>();

		public static final void declare() {
			for (Bones b : values())
				bones.put(Integer.valueOf(b.getId()), b);
		}

		public static Bones forId(int id) {
			return bones.get(Integer.valueOf(id));
		}

		private Bones(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public int getId() {
			return id;
		}
	}

	public static final String USING_ON_ALTAR_KEY = "boneonaltarkey";

	public static final int BURYING_ANIMATION = 827;

	public static boolean bury(Player player, int id, int slot) {
		Bones bones = Bones.forId(id);

		if (bones == null) {
			return false;
		}

		if (player.getSkill().locked()) {
			return true;
		}

		player.getSkill().lock(4);
		player.getCombat().reset();
		player.getUpdateFlags().sendAnimation(827, 0);
		player.getClient().queueOutgoingPacket(new SendMessage("You bury the " + Item.getDefinition(bones.id).getName() + "."));
		player.getInventory().clear(slot);
		player.getSkill().addExperience(5, bones.experience);
		AchievementHandler.activateAchievement(player, AchievementList.BURY_150_BONES, 1);
		AchievementHandler.activateAchievement(player, AchievementList.BURY_1000_BONES, 1);

		return true;
	}

	public static void finishOnAltar(Player player, int amount) {
		if (player.getAttributes().get("boneonaltarkey") == null) {
			return;
		}

		int item = player.getAttributes().getInt("boneonaltarkey");

		Bones bones = Bones.forId(item);

		if (bones == null) {
			return;
		}

		int invAmount = player.getInventory().getItemAmount(item);

		if (invAmount == 0)
			return;
		if (invAmount < amount) {
			amount = invAmount;
		}

		player.getSkill().lock(2);

		player.getClient().queueOutgoingPacket(new SendMessage("You sacrifice the " + Item.getDefinition(bones.id).getName() + " at the altar."));

		player.getUpdateFlags().sendAnimation(645, 5);
		player.getInventory().remove(new Item(item, amount));
		player.getSkill().addExperience(5, (bones.experience * 2.0D) * amount);
		AchievementHandler.activateAchievement(player, AchievementList.BURY_150_BONES, 1);
		AchievementHandler.activateAchievement(player, AchievementList.BURY_1000_BONES, 1);

	}

	public static boolean useBonesOnAltar(Player p, int item, int object) {
		if (object == 2640 || object == 409) {
			Bones bones = Bones.forId(item);

			if (bones == null) {
				return false;
			}
			
			TaskQueue.queue(new Task(p, 3, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.BONES_ON_ALTER) {
				@Override
				public void execute() {
					if (!p.getInventory().hasItemId(item)) {
						stop();
						return;
					}
					
					if (p.getSkill().locked()) {
						return;
					}
					
					p.getSkill().lock(2);
					
					p.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
					p.getClient().queueOutgoingPacket(new SendMessage("You sacrifice the " + Item.getDefinition(bones.id).getName() + " at the altar."));
					
					p.getUpdateFlags().sendAnimation(645, 5);
					p.getInventory().remove(item);
					p.getSkill().addExperience(5, bones.experience * 2.0);
				}

				@Override
				public void onStop() {
				}
			});


			return true;
		}

		return false;
	}
}
