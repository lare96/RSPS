package com.vencillio.rs2.entity.following;

import com.vencillio.rs2.GameConstants;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.pathfinding.RS317PathFinder;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class PlayerFollowing extends Following {
	private final Player player;

	public PlayerFollowing(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public void findPath(Location location) {
		if (type == Following.FollowType.COMBAT) {
			if (player.getCombat().getCombatType() == CombatTypes.MELEE)
				RS317PathFinder.findRoute(player, location.getX(), location.getY(), false, 0, 0);
			else
				RS317PathFinder.findRoute(player, location.getX(), location.getY(), true, 16, 16);
		} else
			RS317PathFinder.findRoute(player, location.getX(), location.getY(), true, 16, 16);
	}

	@Override
	public void onCannotReach() {
		reset();

		if (type == Following.FollowType.COMBAT) {
			player.getCombat().reset();
		}

		player.getClient().queueOutgoingPacket(new SendMessage("I can't reach that!"));
	}

	@Override
	public boolean pause() {
		if (type == Following.FollowType.COMBAT) {
			if (GameConstants.withinBlock(following.getLocation().getX(), following.getLocation().getY(), following.getSize(), player.getLocation().getX(), player.getLocation().getY())) {
				return false;
			}

			if (following.isNpc()) {
				CombatTypes c = player.getCombat().getCombatType();

				if ((c == CombatTypes.MAGIC) || (c == CombatTypes.RANGED)) {
					Mob mob = com.vencillio.rs2.entity.World.getNpcs()[following.getIndex()];

					if (mob == null) {
						return false;
					}

					if (!mob.withinMobWalkDistance(player)) {
						return false;
					}
				}
			}

			if ((!player.getLocation().equals(following.getLocation())) && (player.getCombat().withinDistanceForAttack(player.getCombat().getCombatType(), true))) {
				return true;
			}

		}

		return false;
	}
}
