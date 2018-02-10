package com.vencillio.rs2.content.minigames.godwars;

import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.WalkThroughDoorTask;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.minigames.godwars.GodWarsData.Allegiance;
import com.vencillio.rs2.content.minigames.godwars.GodWarsData.GodWarsNpc;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.following.Following;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobConstants;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

/**
 * 
 * @author Michael | Chex
 *
 */
public class GodWars {

	/**
	 * Points required to enter the room
	 */
	public static final int POINTS_TO_ENTER = 25;

	/**
	 * Godwars Key
	 */
	public static final String GWD_ALTAR_KEY = "GWD_ALTAR_KEY";
	
	/**
	 * Ecumencial Key Identification
	 */
	public static final int ECUMENICAL_KEY = 11942;

	/**
	 * Handles clicing object for Godwars
	 * @param player
	 * @param id
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static boolean clickObject(Player player, int id, int x, int y, int z) {
		switch (id) {

		/**
		 * Bandos dungeon
		 */
		case 26461:
			if (player.getX() >= 2852) {
				TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x - 1, y, z)));
			} else {
				if (player.getInventory().hasItemId(ECUMENICAL_KEY)) {
					player.getInventory().remove(ECUMENICAL_KEY, 1);
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x + 1, y, z)));
					player.send(new SendMessage("You have used your Ecumencial key to enter the room."));
					return true;
				}
				if (player.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()] >= POINTS_TO_ENTER) {
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x + 1, y, z)));
					player.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.BANDOS);
					for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.BANDOS)) {
						mob.getCombat().setAttacking(player);
						mob.getFollowing().setFollow(player, Following.FollowType.COMBAT);
					}
				} else {
					int req = 40 - player.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()];
					DialogueManager.sendStatement(player, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
				}
			}
			return true;

		/**
		 * Armadyl room
		 */
		case 26502:
			if (player.getY() > 5294) {
				TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y - 1, z)));
			} else {
				if (player.getInventory().hasItemId(ECUMENICAL_KEY)) {
					player.getInventory().remove(ECUMENICAL_KEY, 1);
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y + 1, z)));
					player.send(new SendMessage("You have used your Ecumencial key to enter the room."));
					return true;
				}
				if (player.getMinigames().getGWKC()[Allegiance.ARMADYL.ordinal()] >= POINTS_TO_ENTER) {
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y + 1, z)));
					player.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.ARMADYL);
					for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.ARMADYL)) {
						mob.getCombat().setAttacking(player);
						mob.getFollowing().setFollow(player, Following.FollowType.COMBAT);
					}
				} else {
					int req = 40 - player.getMinigames().getGWKC()[Allegiance.ARMADYL.ordinal()];
					DialogueManager.sendStatement(player, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
				}
			}
			return true;

		/**
		 * General Gaardor room
		 */
		case 26503:
			if (player.getX() <= 2862) {
				if (player.getInventory().hasItemId(ECUMENICAL_KEY)) {
					player.getInventory().remove(ECUMENICAL_KEY, 1);
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x + 1, y, z)));
					player.send(new SendMessage("You have used your Ecumencial key to enter the room."));
					return true;
				}
				if (player.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()] >= POINTS_TO_ENTER) {
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x + 1, y, z)));
					player.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.BANDOS);
					for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.BANDOS)) {
						mob.getCombat().setAttacking(player);
						mob.getFollowing().setFollow(player, Following.FollowType.COMBAT);
					}
				} else {
					int req = 40 - player.getMinigames().getGWKC()[Allegiance.BANDOS.ordinal()];
					DialogueManager.sendStatement(player, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
				}
			} else {
				TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x - 1, y, z)));
			}
			return true;

		/**
		 * Saradomin room
		 */
		case 26504:
			if (player.getX() >= 2909) {
				if (player.getInventory().hasItemId(ECUMENICAL_KEY)) {
					player.getInventory().remove(ECUMENICAL_KEY, 1);
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x - 1, y, z)));
					player.send(new SendMessage("You have used your Ecumencial key to enter the room."));
					return true;
				}
				if (player.getMinigames().getGWKC()[Allegiance.SARADOMIN.ordinal()] >= POINTS_TO_ENTER) {
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x - 1, y, z)));
					player.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.SARADOMIN);
					for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.SARADOMIN)) {
						mob.getCombat().setAttacking(player);
						mob.getFollowing().setFollow(player, Following.FollowType.COMBAT);
					}
				} else {
					int req = 40 - player.getMinigames().getGWKC()[Allegiance.SARADOMIN.ordinal()];
					DialogueManager.sendStatement(player, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
				}
			} else {
				TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x + 1, y, z)));
			}
			return true;

		/**
		 * Zamorak room
		 */
		case 26505:
			if (player.getY() >= 5333) {
				if (player.getInventory().hasItemId(ECUMENICAL_KEY)) {
					player.getInventory().remove(ECUMENICAL_KEY, 1);
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y - 1, z)));
					player.send(new SendMessage("You have used your Ecumencial key to enter the room."));
					return true;
				}
				if (player.getMinigames().getGWKC()[Allegiance.ZAMORAK.ordinal()] >= POINTS_TO_ENTER) {
					TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y - 1, z)));
					player.getMinigames().changeGWDKills(-POINTS_TO_ENTER, Allegiance.ZAMORAK);
					for (Mob mob : MobConstants.getGodWarsBossMob(Allegiance.BANDOS)) {
						mob.getCombat().setAttacking(player);
						mob.getFollowing().setFollow(player, Following.FollowType.COMBAT);
					}
				} else {
					int req = 40 - player.getMinigames().getGWKC()[Allegiance.ZAMORAK.ordinal()];
					DialogueManager.sendStatement(player, "You need " + req + " more kill" + (req > 1 ? "s" : "") + " to enter this room.");
				}
			} else {
				TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y + 1, z)));
			}
			return true;

		/**
		 * Zamorak entrance
		 */
		case 26518:
			if (player.getY() == 5332) {
				player.teleport(new Location(2885, 5345, 2));
			} else {
				player.teleport(new Location(2885, 5332, 2));
			}
			return true;

		/**
		 * Saradomin entrance
		 */
		case 26561:
			player.teleport(new Location(2918, 5300, 1));
			return true;

		/**
		 * Saradomin entrance 2
		 */
		case 26562:
			player.teleport(new Location(2919, 5274, 0));
			return true;

		/**
		 * Armadyl entrance
		 */
		case 26380:
			if (player.getY() == 5269) {
				player.teleport(new Location(2871, 5279, 2));
			} else {
				player.teleport(new Location(2871, 5269, 2));
			}
			return true;

		/**
		 * All altars
		 */
		case 26366:
		case 26365:
		case 26364:
		case 26363:
			if (player.getCombat().inCombat()) {
				player.getClient().queueOutgoingPacket(new SendMessage("You cannot use this while in combat!"));
				return true;
			}

			if ((player.getAttributes().get(GWD_ALTAR_KEY) == null) || (((Long) player.getAttributes().get(GWD_ALTAR_KEY)).longValue() < System.currentTimeMillis())) {
				player.getAttributes().set(GWD_ALTAR_KEY, Long.valueOf(System.currentTimeMillis() + 600_000));
				player.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
				player.getClient().queueOutgoingPacket(new SendMessage("You recharge your Prayer points at the altar."));
				player.getUpdateFlags().sendAnimation(645, 5);
				player.getLevels()[5] = player.getMaxLevels()[5];
				player.getSkill().update(5);
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You cannot use this yet!"));
			}
			return true;

		}
		return false;
	}

	/**
	 * Handles killing Godwars npc
	 * @param player
	 * @param id
	 */
	public static void onGodwarsKill(Player player, int id) {
		GodWarsNpc npc = GodWarsData.forId(id);

		if (npc == null) {
			return;
		}

		player.getMinigames().changeGWDKills(1, npc.getAllegiance());
	}

	/**
	 * Use item on object
	 * @param player
	 * @param id
	 * @param obj
	 * @return
	 */
	public static final boolean useItemOnObject(Player player, int id, int obj) {
		return false;
	}
}
