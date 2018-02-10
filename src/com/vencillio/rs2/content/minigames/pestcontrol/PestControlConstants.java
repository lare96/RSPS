package com.vencillio.rs2.content.minigames.pestcontrol;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.minigames.pestcontrol.monsters.Portal;
import com.vencillio.rs2.content.minigames.pestcontrol.monsters.Shifter;
import com.vencillio.rs2.content.minigames.pestcontrol.monsters.Spinner;
import com.vencillio.rs2.content.minigames.pestcontrol.monsters.Splatter;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;

/**
 * Pest Control constants
 * @author Daniel
 *
 */
public class PestControlConstants {

	/**
	 * Starting location coordinates
	 */
	public static final int START_X = 2656, START_Y = 2609, START_X_MOD = 4, START_Y_MOD = 6;
	
	/**
	 * Void Knight spawn location
	 */
	public static final Location VOID_KNIGHT_SPAWN = new Location(2656, 2592);

	/**
	 * Portal IDs
	 */
	public static final int[] PORTAL_IDS = { 6144, 6145, 6146, 6145 };

	/**
	 * Portal spawn locations
	 */
	public static final Location[] PORTAL_SPAWN_LOCATIONS = { new Location(2628, 2591), new Location(2680, 2588), new Location(2669, 2570), new Location(2645, 2569), };

	/**
	 * Splatter IDs
	 */
	public static final int[] SPLATTERS = { 1689, 1690, 1691, 1692, 1693, };

	/**
	 * Shifters IDs
	 */
	public static final int[] SHIFTERS = { 1694, 1695, 1696, 1697, 1698, 1699, 1700, 1701, 1702, 1703, };

	/**
	 * Ravagers IDs
	 */
	public static final int[] RAVAGERS = { 1704, 1705, 1706, 1707, 1708, };

	/**
	 * Spinners IDs
	 */
	public static final int[] SPINNERS = { 1709, 1710, 1711, 1712, 1713 };

	/**
	 * Torchers IDs
	 */
	public static final int[] TORCHERS = { 1714, 1715, 1716, 1717, 1718, 1719, 1720, 1721, 1722, 1723, };

	/**
	 * Defilers IDs
	 */
	public static final int[] DEFILERS = { 1724, 1725, 1726, 1727, 1728, 1729, 1730, 1731, 1732, 1733, };

	/**
	 * Brawlers IDs
	 */
	public static final int[] BRAWLERS = { 1734, 1735, 1736, 1737, 1738, };

	/**
	 * Pests
	 */
	public static final int[][] PESTS = { BRAWLERS, DEFILERS, TORCHERS, RAVAGERS, SPLATTERS, SPINNERS };

	/**
	 * Gets a random boat location
	 * @param z
	 * @return
	 */
	public static Location getRandomBoatLocation(int z) {
		return new Location(START_X + Utility.randomNumber(START_X_MOD), START_Y + Utility.randomNumber(START_Y_MOD));
	}

	/**
	 * Gets random pests
	 * @param l
	 * @param game
	 * @param portal
	 * @return
	 */
	public static Mob getRandomPest(Location l, PestControlGame game, Portal portal) {
		int r = Utility.randomNumber(PESTS.length);
		final int id = PESTS[r][Utility.randomNumber(PESTS[r].length)];

		for (int i : SPLATTERS) {
			if (id == i) {
				return new Splatter(l, game);
			}
		}

		for (int i : SHIFTERS) {
			if (id == i) {
				return new Shifter(l, game);
			}
		}

		for (int i : SPINNERS) {
			if (id == i) {
				return new Spinner(l, game, portal);
			}
		}

		return new Pest(game, id, l) {

			@Override
			public void tick() {
			}

		};
	}

	/**
	 * Sets pest levels
	 * @param mob
	 */
	public static void setLevels(Mob mob) {
		int HP = mob.getDefinition().getLevel();
		int level = 180;
		
		mob.getLevels()[Skills.ATTACK] = (short) level;
		mob.getLevels()[Skills.STRENGTH] = (short) level;
		mob.getLevels()[Skills.DEFENCE] = (short) level;
		mob.getLevels()[Skills.MAGIC] = (short) level;
		mob.getLevels()[Skills.RANGED] = (short) level;
		mob.getLevels()[Skills.HITPOINTS] = (short) (HP * 2);

		mob.getMaxLevels()[Skills.ATTACK] = (short) level;
		mob.getMaxLevels()[Skills.STRENGTH] = (short) level;
		mob.getMaxLevels()[Skills.DEFENCE] = (short) level;
		mob.getMaxLevels()[Skills.MAGIC] = (short) level;
		mob.getMaxLevels()[Skills.RANGED] = (short) level;
		mob.getMaxLevels()[Skills.HITPOINTS] = (short) (HP * 2);
	}
}
