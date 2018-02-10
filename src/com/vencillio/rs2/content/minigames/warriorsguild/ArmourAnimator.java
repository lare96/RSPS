package com.vencillio.rs2.content.minigames.warriorsguild;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles creating armours
 * @author Daniel
 *
 */
public final class ArmourAnimator {

	/**
	 * Locations of animators
	 */
	public static final int[][] ANIMATOR_LOCATIONS = { { 2851, 3536 }, { 2857, 3536 } };

	/**
	 * Array of armour sets
	 */
	public static final int[][] ARMOR_SETS = { { 1155, 1117, 1075 }, { 1153, 1115, 1067 }, { 1157, 1119, 1069 }, { 1165, 1125, 1077 }, { 1159, 1121, 1071 }, { 1161, 1123, 1073 }, { 1163, 1127, 1079 } };

	/**
	 * Token amounts to reward
	 */
	public static final int[] TOKENS = { 5, 10, 15, 20, 25, 30, 40 };

	/**
	 * Animated armour Ids
	 */
	public static final int[] ANIMATED_ARMOR = { 2450, 2451, 2452, 2453, 2454, 2455, 2456 };

	/**
	 * Armour types
	 */
	public static final String[] ARMOR_TYPE = { "Bronze", "Iron", "Steel", "Black", "Mithril", "Adamant", "Rune" };

	/**
	 * Checks if id is an armour
	 * @param id
	 * @return
	 */
	public static boolean isAnimatedArmour(int id) {
		for (int i : ANIMATED_ARMOR) {
			if (id == i)
				return true;
		}
		return false;
	}

	/**
	 * Handles putting armour on animator
	 * @param player
	 * @param itemId
	 * @param object
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean armorOnAnimator(Player player, int itemId, GameObject object, int x, int y) {
		if (object.getId() != 23955) {
			return false;
		}

		if (player.getAttributes().get("warriorGuildAnimator") != null) {
			return true;
		}

		int objectX = object.getLocation().getX();
		int objectY = object.getLocation().getY();

		int animatorIndex = -1;

		for (int i = 0; i < ANIMATOR_LOCATIONS.length; i++) {
			if ((objectX == ANIMATOR_LOCATIONS[i][0]) && (objectY == ANIMATOR_LOCATIONS[i][1])) {
				animatorIndex = i;
				break;
			}
		}

		if (animatorIndex == -1) {
			return false;
		}

		player.getAttributes().set("warriorGuildAnimator", Integer.valueOf(animatorIndex));

		int armorIndex = hasArmor(player, itemId);

		if (armorIndex != -1) {
			createAnimatedArmor(player, armorIndex);
		}

		return true;
	}

	/**
	 * Creaters the animated armour
	 * @param player
	 * @param index
	 */
	protected static void createAnimatedArmor(final Player player, final int index) {
		if (!player.getAttributes().isSet("warriorGuildAnimator")) {
			return;
		}
		player.getUpdateFlags().sendAnimation(new Animation(827));
		player.getAttributes().set("stopMovement", Boolean.valueOf(true));
		for (int i = 0; i < ARMOR_SETS[index].length; i++) {
			player.getInventory().remove(ARMOR_SETS[index][i]);
		}
		final int animatorIndex = ((Integer) player.getAttributes().get("warriorGuildAnimator")).intValue();
		TaskQueue.queue(new Task(2) {
			int i = 0;
			Mob mob = null;

			@Override
			public void execute() {
				if (i == 0) {
					setTaskDelay(1);
					player.send(new SendMessage("You place the armour pieces inside the animator..."));
				} else if (i == 1) {
					player.send(new SendMessage("The animator begins to hum, something appears to be working..."));
					
					int x = player.getLocation().getX();
					int y = player.getLocation().getY();
					
					player.getMovementHandler().addToPath(new Location(x, y + 3));

					player.getMovementHandler().finish();
					
					player.getUpdateFlags().sendFaceToDirection(x, y);

					setTaskDelay(3);
					
				} else if (i == 2) {
					setTaskDelay(1);
					mob = new Mob(player, ANIMATED_ARMOR[index], false, false, false, new Location(ANIMATOR_LOCATIONS[animatorIndex][0], ANIMATOR_LOCATIONS[animatorIndex][1], 0));
				} else if (i == 3) {
					player.send(new SendMessage("The animated armour comes to life!"));
					mob.getUpdateFlags().sendForceMessage("I'M ALIVE!");

					mob.getCombat().setAttack(player);
					mob.getFollowing().setIgnoreDistance(true);

					stop();
				}
				i += 1;
			}

			@Override
			public void onStop() {
			}
		});
	}

	/**
	 * Handles item droping for the animated armour
	 * 
	 * @param player
	 * @param mob
	 */
	public static void dropForAnimatedArmour(Player player, Mob mob) {
		int index = -1;
	
		for (int i = 0; i < ANIMATED_ARMOR.length; i++) {
			if (mob.getId() == ANIMATED_ARMOR[i]) {
				index = i;
				break;
			}
		}
	
		if (index == -1) {
			return;
		}
	
		for (int i : ARMOR_SETS[index]) {
			GroundItemHandler.add(new Item(i), mob.getLocation(), player, player.ironPlayer() ? player : null);
		}
	
		GroundItemHandler.add(new Item(8851, TOKENS[index]), mob.getLocation(), player, player.ironPlayer() ? player : null);
	
		player.getAttributes().remove("warriorGuildAnimator");
	}

	/**
	 * Checks if player has armour
	 * @param player
	 * @param itemId
	 * @return
	 */
	protected static int hasArmor(Player player, int itemId) {
		int itemIndex = -1;
		for (int i = 0; i < ARMOR_SETS.length; i++) {
			for (int j = 0; j < ARMOR_SETS[i].length; j++) {
				if (itemId == ARMOR_SETS[i][j]) {
					itemIndex = i;
					for (int k = 0; k < ARMOR_SETS[i].length; k++) {
						if (!player.getInventory().hasItemId(ARMOR_SETS[i][k])) {
							player.send(new SendMessage("You need a complete armour set to do this!"));
							return -1;
						}
					}
					break;
				}
			}
		}
		return itemIndex;
	}
}
