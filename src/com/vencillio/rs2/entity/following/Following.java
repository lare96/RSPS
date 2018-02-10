package com.vencillio.rs2.entity.following;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.pathfinding.RS317PathFinder;
import com.vencillio.rs2.entity.pathfinding.StraightPathFinder;

public abstract class Following {

	public static enum FollowType {
		DEFAULT,
		COMBAT,
		FOLLOW_TO;
	}

	public static final int[] NON_DIAGONAL_DIRECTIONS = { 1, 3, 4, 6 };
	protected final Entity entity;

	protected Entity following = null;
	protected Location waypoint = new Location();

	protected Location newWaypoint = new Location();
	protected int lastX = 0;

	protected int lastY = 0;
	protected boolean flag = false;

	protected boolean ignoreDistance = false;

	protected FollowType type = FollowType.DEFAULT;

	public Following(Entity entity) {
		this.entity = entity;
	}

	public abstract void findPath(Location paramLocation);

	public boolean flag() {
		return flag;
	}

	public void follow() {
		entity.face(following);

		if (!following.isActive()) {
			reset();
			return;
		}

		if (entity.isFrozen() || entity.isStunned()) {
			return;
		}

		if (!pause()) {
			if ((entity.isNpc()) || (following.getLocation().getX() != lastX) || (following.getLocation().getY() != lastY)) {
				lastX = following.getLocation().getX();
				lastY = following.getLocation().getY();
				findPath(newWaypoint);
				waypoint.setAs(newWaypoint);
			}
		} else {
			entity.getMovementHandler().reset();
		}

		if (outOfRange()) {
			onCannotReach();
		}
	}

	public Location getCombatFollowLocation() {
		if ((entity.getCombat().getCombatType() == CombatTypes.MELEE) && (entity.getSize() == 1) && (following.getSize() == 1)) {
			return getMeleeLocation();
		}

		int x = entity.getLocation().getX();
		int y = entity.getLocation().getY();
		int z = entity.getLocation().getZ();
		int x2 = following.getLocation().getX();
		int y2 = following.getLocation().getY();
		boolean projectile = (entity.getCombat().getCombatType() == CombatTypes.RANGED) || (entity.getCombat().getCombatType() == CombatTypes.MAGIC);

		double lowDist = 9999.0D;
		int lowX = 0;
		int lowY = 0;

		int x3 = x2;
		int y3 = y2 - 1;

		int loop = following.getSize();

		if (RS317PathFinder.accessable(x2, y2, z, x3, y3) && (!projectile && StraightPathFinder.isInteractionPathClear(x3, y3, z, x2, y2)) || (projectile && StraightPathFinder.isProjectilePathClear(x3, y3, z, x2, y2))) {
			lowDist = Utility.getManhattanDistance(x3, y3, x, y);
			lowX = x3;
			lowY = y3;
		}

		for (int k = 0; k < 4; k++) {
			for (int i = 0; i < loop - (k == 0 ? 1 : 0); i++) {
				if (k == 0) {
					x3++;
				} else if (k == 1) {
					if (i == 0) {
						x3++;
					}
					y3++;
				} else if (k == 2) {
					if (i == 0) {
						y3++;
					}
					x3--;
				} else if (k == 3) {
					if (i == 0) {
						x3--;
					}
					y3--;
				}
				double d;
				if ((d = Utility.getManhattanDistance(x3, y3, x, y)) < lowDist) {
					if (RS317PathFinder.accessable(x2, y2, z, x3, y3) && (!projectile && StraightPathFinder.isInteractionPathClear(x3, y3, z, x2, y2)) || (projectile && StraightPathFinder.isProjectilePathClear(x3, y3, z, x2, y2))) {
						lowDist = d;
						lowX = x3;
						lowY = y3;
					}
				}
			}
		}

		return new Location(lowX, lowY, entity.getLocation().getZ());
	}

	public Location getFollowToLocation() {
		int x = following.getLocation().getX();
		int y = following.getLocation().getY();
		int z = following.getLocation().getZ();

		int[][] nodes = { { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[0]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[0]][1] }, { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[1]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[1]][1] }, { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[2]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[2]][1] }, { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[3]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[3]][1] } };

		int bestX = 0;
		int bestY = 0;
		double bestDist = 99999.0D;

		for (int i = 0; i < nodes.length; i++) {
			double dist = Math.sqrt(Math.pow(entity.getLocation().getX() - nodes[i][0], 2.0D) + Math.pow(entity.getLocation().getY() - nodes[i][1], 2.0D));
			if ((dist < bestDist) && (following.getMovementHandler().canMoveTo(NON_DIAGONAL_DIRECTIONS[i]))) {
				bestDist = dist;
				bestX = nodes[i][0];
				bestY = nodes[i][1];
			}
		}

		return new Location(bestX, bestY, z);
	}

	public Entity getInteracting() {
		return following;
	}

	public Location getMeleeLocation() {
		int x = following.getLocation().getX();
		int y = following.getLocation().getY();
		int z = following.getLocation().getZ();

		int[][] nodes = { { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[0]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[0]][1] }, { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[1]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[1]][1] }, { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[2]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[2]][1] }, { x + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[3]][0], y + com.vencillio.rs2.GameConstants.DIR[NON_DIAGONAL_DIRECTIONS[3]][1] } };

		int bestX = 0;
		int bestY = 0;
		double bestDist = 99999.0D;

		for (int i = 0; i < nodes.length; i++) {
			if (StraightPathFinder.isInteractionPathClear(nodes[i][0], nodes[i][1], z, x, y)) {
				double dist = Math.sqrt(Math.pow(entity.getLocation().getX() - nodes[i][0], 2.0D) + Math.pow(entity.getLocation().getY() - nodes[i][1], 2.0D));
				if ((dist < bestDist) && (following.getMovementHandler().canMoveTo(NON_DIAGONAL_DIRECTIONS[i]))) {
					bestDist = dist;
					bestX = nodes[i][0];
					bestY = nodes[i][1];
				}
			}
		}

		if (bestX == 0 && bestY == 0) {
			return null;
		}

		return new Location(bestX, bestY, z);
	}

	public boolean isFollowing() {
		return following != null;
	}

	public boolean isIgnoreDistance() {
		return ignoreDistance;
	}

	public boolean isWithinDistance() {
		int x1 = entity.getLocation().getX();
		int x2 = following.getLocation().getX();

		int y1 = entity.getLocation().getY();
		int y2 = following.getLocation().getY();
		
		return (Math.abs(x1 - x2) < 16) && (Math.abs(y1 - y2) < 16);
	}

	public abstract void onCannotReach();

	public boolean outOfRange() {
		return ignoreDistance ? false : entity.getZ() != following.getZ() || !isWithinDistance();
	}

	public abstract boolean pause();

	public void process() {
		if (following != null)
			follow();
	}

	public void reset() {
		if (following != null) {
			following = null;

			waypoint = new Location();
			newWaypoint = new Location();

			lastX = 0;
			lastY = 0;
		}
	}

	public void setFollow(Entity entity) {
		setFollow(entity, FollowType.DEFAULT);
	}

	public void setFollow(Entity entity, FollowType type) {
		following = entity;
		this.type = type;
		updateWaypoint();
		waypoint.setAs(newWaypoint);
		lastX = 0;
		lastY = 0;
	}

	public void setIgnoreDistance(boolean ignoreDistance) {
		this.ignoreDistance = ignoreDistance;
	}

	public void updateWaypoint() {
		if (following == null) {
			return;
		}

		if (flag) {
			flag = false;
		}

		if ((!following.getFollowing().flag()) && (following.getSize() == entity.getSize())) {
			Entity otherF = following.getFollowing().getInteracting();
			if ((otherF != null) && (otherF.equals(entity)) && (entity.getLocation().equals(following.getLocation()))) {
				flag = true;
				return;
			}

		}

		if (type == FollowType.DEFAULT) {
			newWaypoint.setAs(following.getMovementHandler().getLastLocation());
		} else if (type == FollowType.COMBAT) {
			Location location;
			if ((location = getCombatFollowLocation()) != null) {
				newWaypoint.setAs(location);
			} else if (!entity.isNpc()) {
				entity.getCombat().reset();
			} else {
				newWaypoint.setAs(following.getLocation());
			}
		} else if (type == FollowType.FOLLOW_TO) {
			newWaypoint.setAs(getFollowToLocation());
		}
	}
}
