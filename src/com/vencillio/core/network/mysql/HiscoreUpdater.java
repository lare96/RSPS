package com.vencillio.core.network.mysql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.player.Player;

/**
 * Performs an upload to the MySQL server containing highscores information.
 */
public final class HiscoreUpdater {

	private static ExternalDatabase database = null;

	private static ExecutorService executorService = null;

	private static boolean prepared = false;

	private static long getTotalExperience(Player player) {
		return player.getSkill().getTotalExperience();
	}

	private static int getTotalLevel(Player player) {
		return player.getSkill().getTotalLevel();
	}

	public static void prepare() {
		database = new ExternalDatabase("vdaniel_ipb", "PBDQC7UqKdDa", "108.167.182.37/vdaniel_highscores", 3);
		database.initialise();
		executorService = Executors.newSingleThreadExecutor();
		prepared = true;
	}

	public static void shutdown() {
		database.shutdown();
		executorService.shutdown();
	}

	public static void update(Player player) {
		if (!prepared) {
			throw new IllegalStateException("unprepared");
		}
		
		if (database.getPooledConnection() == null) {
			return;
		}

		executorService.submit(() -> {
			final int totalLevel = getTotalLevel(player);
			final long totalExp = getTotalExperience(player);
			int totalPrestige = 0;
			for (int prestige : player.getSkillPrestiges()) {
				if (prestige > 0) {
					totalPrestige += prestige;
				}
			}
			
			StringBuilder bldr = new StringBuilder(1256);

			bldr.append("INSERT INTO highscores (");
			bldr.append("`username`");
			bldr.append(",`rights`");
			bldr.append(",`overall_prestige`");
			bldr.append(",`overall_level`");
			bldr.append(",`overall_xp`");

			for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
				if (skill == Skills.CONSTRUCTION || skill == Skills.DUNGEONEERING || skill == Skills.SUMMONING) {
					continue;
				}
				String skillName = Skills.SKILL_NAMES[skill];
				bldr.append(",`" + skillName + "_xp`");
				bldr.append(",`" + skillName + "_prestige`");
			}

			bldr.append(") VALUES (");
			bldr.append("'" + Utility.formatPlayerName(player.getUsername().toLowerCase()) + "'");
			bldr.append("," + player.getRights());
			bldr.append("," + totalPrestige);
			bldr.append("," + totalLevel);
			bldr.append("," + totalExp);

			for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
				if (skill == Skills.CONSTRUCTION || skill == Skills.DUNGEONEERING || skill == Skills.SUMMONING) {
					continue;
				}
				bldr.append("," + player.getSkill().getExperience()[skill]);
				bldr.append("," + player.getSkillPrestiges()[skill]);
			}
			bldr.append(")");
			bldr.append(" ON DUPLICATE KEY UPDATE ");
			bldr.append("`rights`=" + player.getRights());
			bldr.append(",`overall_prestige`=" + totalPrestige);
			bldr.append(",`overall_level`=" + totalLevel);
			bldr.append(",`overall_xp`=" + totalExp);

			for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
				if (skill == Skills.CONSTRUCTION || skill == Skills.DUNGEONEERING || skill == Skills.SUMMONING) {
					continue;
				}
				String skillName = Skills.SKILL_NAMES[skill];
				bldr.append(",`" + skillName + "_xp`=" + player.getSkill().getExperience()[skill]);
				bldr.append(",`" + skillName + "_prestige`=" + player.getSkillPrestiges()[skill]);
			}

			database.executeQuery(bldr.toString());
		});
	}
}