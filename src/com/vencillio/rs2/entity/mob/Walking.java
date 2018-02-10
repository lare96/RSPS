package com.vencillio.rs2.entity.mob;

import com.vencillio.core.cache.map.Region;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.GameConstants;
import com.vencillio.rs2.content.skill.summoning.FamiliarMob;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.pathfinding.SimplePathWalker;

public class Walking {

	private static Region region = null;

	public static boolean canMoveTo(Mob mob, int direction) {
		if (direction == -1) {
			return false;
		}

		int x = mob.getLocation().getX();
		int y = mob.getLocation().getY();
		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();

		boolean virtual = mob.inVirtualRegion();
		VirtualMobRegion region = mob.getVirtualRegion();

		int x5 = mob.getLocation().getX() + GameConstants.DIR[direction][0];
		int y5 = mob.getLocation().getY() + GameConstants.DIR[direction][1];

		int size = mob.getSize();

		boolean familiar = mob instanceof FamiliarMob;

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < GameConstants.SIZES[i].length; k++) {
				int x3 = x + GameConstants.SIZES[i][k][0];
				int y3 = y + GameConstants.SIZES[i][k][1];

				int x2 = x5 + GameConstants.SIZES[i][k][0];
				int y2 = y5 + GameConstants.SIZES[i][k][1];

				if (GameConstants.withinBlock(x, y, size, x2, y2)) {
					continue;
				}

				if ((familiar) && (mob.getOwner().getX() == x2) && (mob.getOwner().getY() == y2)) {
					return false;
				}

				Region r = getRegion(x3, y3);

				if (r == null) {
					mob.remove();
					return false;
				}

				if (!r.canMove(x3, y3, z, direction)) {
					return false;
				}

				if (!virtual) {
					if (getRegion(x2, y2).isNpcOnTile(x2, y2, z))
						return false;
				} else {
					if (region.isMobOnTile(x2, y2, z))
						return false;
				}

				for (int j = 0; j < 8; j++) {
					if ((GameConstants.withinBlock(x5, y5, size, x2 + GameConstants.DIR[j][0], x2 + GameConstants.DIR[j][1])) && (!Region.getRegion(x2, x2).canMove(x2, x2, z, j))) {
						return false;
					}
				}
			}
		}

		if (GameConstants.DIR[direction][0] != 0 && GameConstants.DIR[direction][1] != 0) {
			return canMoveTo(mob, GameConstants.getDirection(GameConstants.DIR[direction][0], 0)) && canMoveTo(mob, GameConstants.getDirection(0, GameConstants.DIR[direction][1]));
		}

		return true;
	}

	public static boolean canMoveTo(Mob mob, int x, int y, int direction, int size) {
		if (direction == -1) {
			return false;
		}

		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();

		int x5 = mob.getLocation().getX() + GameConstants.DIR[direction][0];
		int y5 = mob.getLocation().getY() + GameConstants.DIR[direction][1];

		int x4 = 0;
		int y4 = 0;

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < GameConstants.SIZES[i].length; k++) {
				int x3 = x + GameConstants.SIZES[i][k][0];
				int y3 = y + GameConstants.SIZES[i][k][1];

				int x2 = x5 + GameConstants.SIZES[i][k][0];
				int y2 = y5 + GameConstants.SIZES[i][k][1];

				if (!GameConstants.withinBlock(x, y, size, x2, y2)) {
					if (!getRegion(x3, y3).canMove(x3, y3, z, direction)) {
						return false;
					}

					if ((x2 == x4) && (y2 == y4)) {
						return false;
					}

					for (int j = 0; j < 8; j++) {
						int x6 = x3 + GameConstants.DIR[j][0];
						int y6 = y3 + GameConstants.DIR[j][1];

						if ((GameConstants.withinBlock(x5, y5, size, x6, y6)) && (!getRegion(x3, y3).canMove(x3, y3, z, j))) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public static int getRandomDirection(Mob mob) {
		int count = 0;

		byte[] walkable = new byte[8];

		for (byte i = 0; i < 8; i = (byte) (i + 1)) {
			if ((withinMaxDistance(mob, i)) && (mob.getMovementHandler().canMoveTo(i))) {
				walkable[count] = i;
				count++;
			}
		}

		if (count > 0) {
			return walkable[Utility.randomNumber(count)];
		}

		return -1;
	}

	public static Region getRegion(int x, int y) {
		if ((region == null) || (!region.withinRegion(x, y))) {
			region = Region.getRegion(x, y);
		}

		return region;
	}

	public static void randomWalk(Mob mob) {
		if (mob.getId() == 6064 || mob.getId() == 6063 || mob.getId() == 1035 || mob.getId() == 1034 || mob.getId() == 1033 || mob.getId() == 1032 || mob.getId() == 1031 || mob.getId() == 1030 || mob.getId() == 1029 || mob.getId() == 1028) {
			if (Utility.randomNumber(3) == 1) {
				int dir = getRandomDirection(mob);
				if (dir != -1)
					SimplePathWalker.walkToNextTile(mob, new Location(mob.getX() + GameConstants.DIR[dir][0], mob.getY() + GameConstants.DIR[dir][1]));
			}
		} else if (Utility.randomNumber(15) == 0) {
			int dir = getRandomDirection(mob);
			if (dir != -1)
				SimplePathWalker.walkToNextTile(mob, new Location(mob.getX() + GameConstants.DIR[dir][0], mob.getY() + GameConstants.DIR[dir][1]));
		}
	}

	public static void setNpcOnTile(Mob mob, boolean set) {
		int x = mob.getLocation().getX();
		int y = mob.getLocation().getY();
		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();

		boolean virtual = mob.inVirtualRegion();
		VirtualMobRegion region = mob.getVirtualRegion();

		int size = mob.getSize();

		for (int i = 1; i < size + 1; i++)
			for (int k = 0; k < GameConstants.SIZES[i].length; k++) {
				int x2 = x + GameConstants.SIZES[i][k][0];
				int y2 = y + GameConstants.SIZES[i][k][1];
				if (!virtual) {
					Region r = getRegion(x2, y2);

					if (r == null) {
						return;
					}

					r.setNpcOnTile(set, x2, y2, z);
				} else {
					region.setMobOnTile(x2, y2, z, set);
				}
			}
	}

	public static void walk(Mob mob, int dir) {
		if ((dir == -1) || (mob.isPlacement()) || (mob.isFrozen()) || (mob.isStunned()) || (mob.getMovementHandler().getPrimaryDirection() != -1)) {
			return;
		}

		setNpcOnTile(mob, false);

		mob.getMovementHandler().setPrimaryDirection(dir);

		mob.getMovementHandler().getLastLocation().setAs(mob.getLocation());

		mob.getLocation().move(GameConstants.DIR[dir][0], GameConstants.DIR[dir][1]);

		setNpcOnTile(mob, true);

		mob.getUpdateFlags().setUpdateRequired(true);

		mob.getMovementHandler().setPrimaryDirection(dir);
	}

	public static boolean withinMaxDistance(Mob mob, int direction) {
		int yMax;
		int xMax;
		int yMin;
		int xMin;
		if (mob.getId() == 6064 || mob.getId() == 6063 || mob.getId() == 1035 || mob.getId() == 1034 || mob.getId() == 1033 || mob.getId() == 1032 || mob.getId() == 1031 || mob.getId() == 1030 || mob.getId() == 1029 || mob.getId() == 1028) {
			xMax = mob.getSpawnLocation().getX() + 15;
			yMax = mob.getSpawnLocation().getY() + 15;
			xMin = mob.getSpawnLocation().getX() - 15;
			yMin = mob.getSpawnLocation().getY() - 15;
		} else {
			xMax = mob.getSpawnLocation().getX() + 2;
			yMax = mob.getSpawnLocation().getY() + 2;
			xMin = mob.getSpawnLocation().getX() - 2;
			yMin = mob.getSpawnLocation().getY() - 2;
		}
		int newX = mob.getLocation().getX() + GameConstants.DIR[direction][0];
		int newY = mob.getLocation().getY() + GameConstants.DIR[direction][1];
		return (newX <= xMax) && (newY <= yMax) && (newX >= xMin) && (newY >= yMin);
	}
}
