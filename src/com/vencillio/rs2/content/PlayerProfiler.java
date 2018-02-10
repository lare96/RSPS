package com.vencillio.rs2.content;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
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
			player.send(new SendMessage("@dre@Please click on the 'My Profile' button."));
			return;
		}

		if (viewing == null) {
			player.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(string) + " either does not exist or is not online!"));
			return;
		}

		TaskQueue.queue(new Task(2) {
			@Override
			public void execute() {

				int deltaX = viewing.getLocation().getX() - (player.getCurrentRegion().getRegionX() << 3);
				int deltaY = viewing.getLocation().getY() - (player.getCurrentRegion().getRegionY() << 3);

				if ((deltaX < 16) || (deltaX >= 88) || (deltaY < 16) || (deltaY > 88)) {
					player.send(new SendMessage("@dre@You can only view profiles of players that are in your region."));
					stop();
					return;
				}

				if (viewing.getProfilePrivacy()) {
					player.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(viewing.getUsername()) + " has disabled profile viewing."));
					stop();
					return;
				}

				if (player.getProfilePrivacy()) {
					player.send(new SendMessage("@dre@You cannot view profiles while your profile privacy is off!"));
					stop();
					return;
				}

				displayProfile(player, viewing);
				stop();
			}

			@Override
			public void onStop() {
			}
		});

	}

	/**
	 * If a player is found, display the profile
	 * 
	 * @param player
	 * @param viewing
	 */
	public static void displayProfile(Player player, Player viewing) {

		viewing.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(player.getUsername()) + " is viewing your profile!"));

		viewing.setProfileViews(+1);

		player.send(new SendPlayerProfilerIndex(viewing.getIndex()));

		player.send(new SendString("Player Profiler", 51802));

		for (int i = 0; i < 20; i++) {
			player.send(new SendString(Utility.capitalizeFirstLetter(Skills.SKILL_NAMES[i]) + " level: " + viewing.getSkill().getLevels()[i] + "/" + viewing.getSkill().getLevelForExperience(i, viewing.getSkill().getExperience()[i]) + "\\nPrestige level: " + viewing.getSkillPrestiges()[i], 51832 + i));
		}

		player.send(new SendString("</col>Name: @gre@" + Utility.capitalizeFirstLetter(viewing.getUsername()), 51807));
		player.send(new SendString("</col>Rank: @gre@" + viewing.determineIcon(viewing) + " " + viewing.determineRank(viewing), 51808));
		player.send(new SendString("</col>Combat: @gre@" + viewing.getSkill().getCombatLevel(), 51809));

		player.send(new SendString("</col>Likes: @whi@" + viewing.getLikes(), 51881));
		player.send(new SendString("</col>Dislikes @whi@" + viewing.getDislikes(), 51882));
		player.send(new SendString("</col>Views @whi@" + viewing.getProfileViews(), 51883));
		player.send(new SendString("</col>Money Spent: $@whi@" + viewing.getMoneySpent(), 51884));
		player.send(new SendString("</col>Credits: @whi@" + viewing.getCredits(), 51885));
		player.send(new SendString("</col>Kills: @whi@" + viewing.getKills(), 51886));
		player.send(new SendString("</col>Deaths: @whi@" + viewing.getDeaths(), 51887));
		player.send(new SendString("</col>KDR: @whi@" + "Nan", 51888));
		player.send(new SendString("</col>Task: @whi@" + viewing.getSlayer().getTask() + "</col>( @whi@" + viewing.getSlayer().getAmount() + "</col> )", 51889));
		player.send(new SendString("</col>Slayer Points: @whi@" + viewing.getSlayerPoints(), 51890));
		player.send(new SendString("</col>PC Points: @whi@" + viewing.getPestPoints(), 51891));
		player.send(new SendString("", 51892));

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
			player.send(new SendString(Utility.capitalizeFirstLetter(Skills.SKILL_NAMES[i]) + " level: " + player.getSkill().getLevels()[i] + "/" + player.getSkill().getLevelForExperience(i, player.getSkill().getExperience()[i]) + "\\nPrestige level: " + player.getSkillPrestiges()[i], 51632 + i));
		}

		player.send(new SendString("</col>Name: @gre@" + Utility.capitalizeFirstLetter(player.getUsername()), 51607));
		player.send(new SendString("</col>Rank: @gre@" + player.determineIcon(player) + " " + player.determineRank(player), 51608));
		player.send(new SendString("</col>Combat: @gre@" + player.getSkill().getCombatLevel(), 51609));

		player.send(new SendString("", 51681));
		player.send(new SendString("</col>Likes: @whi@" + player.getLikes(), 51682));
		player.send(new SendString("</col>Dislikes @whi@" + player.getDislikes(), 51683));
		player.send(new SendString("</col>Views @whi@" + player.getProfileViews(), 51684));
		player.send(new SendString("</col>Money Spent: $@whi@" + player.getMoneySpent(), 51685));
		player.send(new SendString("</col>Credits: @whi@" + player.getCredits(), 51686));
		player.send(new SendString("</col>Kills: @whi@" + player.getKills(), 51687));
		player.send(new SendString("</col>Deaths: @whi@" + player.getDeaths(), 51688));
		player.send(new SendString("</col>KDR: @whi@" + "Nan", 51689));
		player.send(new SendString("</col>Task: @whi@" + player.getSlayer().getTask() + "</col>(" + player.getSlayer().getAmount() + "</col>)", 51690));
		player.send(new SendString("</col>Slayer Points: @whi@" + player.getSlayerPoints(), 51691));
		player.send(new SendString("</col>PC Points: @whi@" + player.getPestPoints(), 51692));
		player.send(new SendString("", 51693));

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
