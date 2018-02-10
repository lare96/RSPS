package com.vencillio.rs2.content.profiles;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerProfilerIndex;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the Player Profiler
 * 
 * @author Daniel
 *
 */
public class PlayerProfiler {

	/**
	 * Searches for a player
	 * 
	 * @param player
	 * @param string
	 */
	public static void search(Player player, String string) {
		player.send(new SendMessage("@dre@Searching account '" + Utility.capitalizeFirstLetter(string) + "' for profile..."));
		Player viewing = World.getPlayerByName(string);
		player.viewing = string;

		if (player == viewing && !PlayerConstants.isOwner(player)) {
			myProfile(player);
			return;
		}

		if (viewing == null) {
			player.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(string) + " either does not exist or is not online!"));
			return;
		}

		if (viewing.getProfilePrivacy()) {
			player.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(viewing.getUsername()) + " has disabled profile viewing."));
			return;
		}

		if (player.getProfilePrivacy()) {
			player.send(new SendMessage("@dre@You cannot view profiles whilst your profile privacy is off!"));
			return;
		}
		
		int deltaX = viewing.getLocation().getX() - (player.getCurrentRegion().getRegionX() << 3);
		int deltaY = viewing.getLocation().getY() - (player.getCurrentRegion().getRegionY() << 3);

		if ((deltaX < 16) || (deltaX >= 88) || (deltaY < 16) || (deltaY > 88)) {
			player.send(new SendMessage("@dre@Viewing character models is disabled while not in same region."));
			displayProfile(player, viewing, false);
		} else {
			displayProfile(player, viewing, true);			
		}
	}

	/**
	 * If a player is found, display the profile
	 * 
	 * @param player
	 * @param viewing
	 */
	public static void displayProfile(Player player, Player viewing, boolean inRegion) {
		viewing.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(player.getUsername()) + " is viewing your profile!"));
		AchievementHandler.activateAchievement(player, AchievementList.VIEW_15_PLAYER_PROFILES, 1);
		viewing.setProfileViews(+1);

		if (inRegion) {
			player.send(new SendPlayerProfilerIndex(viewing.getIndex()));			
		} else {
			player.send(new SendPlayerProfilerIndex(-1));		
		}

		player.send(new SendString("Player Profiler", 51802));

		for (int i = 0; i < 20; i++) {
			player.send(new SendString(Utility.capitalizeFirstLetter(Skills.SKILL_NAMES[i]) + " level: " + viewing.getSkill().getLevels()[i] + "/" + viewing.getSkill().getLevelForExperience(i, viewing.getSkill().getExperience()[i]) + "\\nPrestige level: " + viewing.getSkillPrestiges()[i], 51832 + i));
		}

		player.send(new SendString("</col>Name: @gre@" + Utility.capitalizeFirstLetter(viewing.getUsername()), 51807));
		player.send(new SendString("</col>Rank: @gre@" + viewing.determineIcon(viewing) + " " + viewing.determineRank(viewing), 51808));
		player.send(new SendString("</col>Combat: @gre@" + viewing.getSkill().getCombatLevel(), 51809));

				
		String[] STRINGS = { 
			"",
			"</col>Likes: @whi@" + viewing.getLikes(),
			"</col>Dislikes: @whi@" + viewing.getDislikes(),
			"</col>Views: @whi@" + viewing.getProfileViews(),
			"</col>Money Spent: $@whi@" + viewing.getMoneySpent(),
			"</col>Credits: @whi@" + viewing.getCredits(),		
			"</col>Achievements Completed: @whi@" + viewing.getPA().achievementCompleted(),
			"</col>Achievement Points: @whi@" + viewing.getAchievementsPoints(),
			"</col>Total Prestiges: @whi@" + viewing.getTotalPrestiges(),
			"</col>Prestige Points: @whi@" + viewing.getPrestigePoints(),			
			"</col>Task: @whi@" + viewing.getSlayer().getTask() + "</col>(@whi@" + viewing.getSlayer().getAmount() + "</col>)",		
			"</col>Rogue Kills: @whi@" + viewing.getRogueKills(),
			"</col>Rogue Record: @whi@" + viewing.getRogueRecord(),		
			"</col>Hunter Kills: @whi@" + viewing.getHunterKills(),
			"</col>Hunter Record: @whi@" + viewing.getHunterRecord(),		
			"</col>Deaths: @whi@" + viewing.getDeaths(),		
			"</col>Bounty Points: @whi@" + viewing.getBountyPoints(),
			"</col>Vote Points: @whi@" + viewing.getVotePoints(),
			"</col>PC Points: @whi@" + viewing.getPestPoints(),
			"</col>Slayer Points: @whi@" + viewing.getSlayerPoints(),
			"</col>Mage Arena Points: @whi@" + viewing.getArenaPoints(),
			"</col>WG Points: @whi@" + viewing.getWeaponPoints(),
		};

		for (int i = 0; i < STRINGS.length; i++) {
			player.send(new SendString(STRINGS[i], 51881 + i));
		}		

		player.send(new SendInterface(51800));
	}

	/**
	 * Display player's own profile
	 * 
	 * @param player
	 */
	public static void myProfile(Player player) {

		player.send(new SendMessage("@dre@You are now viewing your own profile."));

		player.send(new SendString("My Profile", 51602));


		for (int i = 0; i < 20; i++) {
			player.send(new SendString(Utility.capitalizeFirstLetter(Skills.SKILL_NAMES[i]) + " level: @dre@" + player.getSkill().getLevels()[i] + "@bla@/@dre@" + player.getSkill().getLevelForExperience(i, player.getSkill().getExperience()[i]) + "\\nPrestige level: @dre@" + player.getSkillPrestiges()[i], 51632 + i));
		}
		
		String[] STRINGS = { 
			"",
			"</col>Likes: @whi@" + player.getLikes(),
			"</col>Dislikes: @whi@" + player.getDislikes(),
			"</col>Views: @whi@" + player.getProfileViews(),
			"</col>Money Spent: $@whi@" + player.getMoneySpent(),
			"</col>Credits: @whi@" + player.getCredits(),		
			"</col>Achievements Completed: @whi@" + player.getPA().achievementCompleted(),
			"</col>Achievement Points: @whi@" + player.getAchievementsPoints(),
			"</col>Total Prestiges: @whi@" + player.getTotalPrestiges(),
			"</col>Prestige Points: @whi@" + player.getPrestigePoints(),			
			"</col>Task: @whi@" + player.getSlayer().getTask() + "</col>(@whi@" + player.getSlayer().getAmount() + "</col>)",		
			"</col>Rogue Kills: @whi@" + player.getRogueKills(),
			"</col>Rogue Record: @whi@" + player.getRogueRecord(),		
			"</col>Hunter Kills: @whi@" + player.getHunterKills(),
			"</col>Hunter Record: @whi@" + player.getHunterRecord(),		
			"</col>Deaths: @whi@" + player.getDeaths(),		
			"</col>Bounty Points: @whi@" + player.getBountyPoints(),
			"</col>Vote Points: @whi@" + player.getVotePoints(),
			"</col>PC Points: @whi@" + player.getPestPoints(),
			"</col>Slayer Points: @whi@" + player.getSlayerPoints(),
			"</col>Mage Arena Points: @whi@" + player.getArenaPoints(),
			"</col>WG Points: @whi@" + player.getWeaponPoints(),
		};

		for (int i = 0; i < STRINGS.length; i++) {
			player.send(new SendString(STRINGS[i], 51681 + i));
		}
		
		player.send(new SendString("</col>Name: @gre@" + Utility.capitalizeFirstLetter(player.getUsername()), 51607));
		player.send(new SendString("</col>Rank: @gre@" + player.determineIcon(player) + " " + player.determineRank(player), 51608));
		player.send(new SendString("</col>Combat: @gre@" + player.getSkill().getCombatLevel(), 51609));


		player.send(new SendInterface(51600));
	}

	/**
	 * Manages reputations
	 * 
	 * @param player
	 * @param name
	 * @param button
	 */
	public static void manageReputation(Player player, String name, int button) {
		Player viewing = World.getPlayerByName(name);

		if (player == viewing) {
			return;
		}

		if (!player.canLike()) {
			player.send(new SendMessage("@dre@You may only give out 3 reputations per day."));
			return;
		}

		switch (button) {

		case 203022:
			player.addLike();
			player.send(new SendMessage("@dre@You have liked " + Utility.capitalizeFirstLetter(viewing.getUsername()) + "'s profile!"));
			if (player.getLikesGiven() == 3) {
				player.setLastLike(System.currentTimeMillis());
				player.send(new SendMessage("You have given your last reputation; please wait another 24 hours to give more."));
			}
			viewing.setLikes(+1);
			viewing.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(player.getUsername()) + " has liked your profile."));
			break;

		case 203025:
			player.addLike();
			player.send(new SendMessage("@dre@You have disliked " + Utility.capitalizeFirstLetter(viewing.getUsername()) + "'s profile!"));
			if (player.getLikesGiven() == 3) {
				player.setLastLike(System.currentTimeMillis());
				player.send(new SendMessage("You have given your last reputation; please wait another 24 hours to give more."));
			}
			viewing.setDislikes(+1);
			viewing.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(player.getUsername()) + " has disliked your profile."));
			break;

		}
	}

}
