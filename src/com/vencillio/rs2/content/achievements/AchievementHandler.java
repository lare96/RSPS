package com.vencillio.rs2.content.achievements;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.AchievementTab;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the achievements
 * @author Daniel
 * 
 */
public class AchievementHandler {

	/**
	 * Holds the types of achievements
	 * 
	 */
	public enum AchievementDifficulty {
		EASY, MEDIUM, HARD
	}

	/**
	 * Activates the achievement for the individual player. Increments the
	 * completed amount for the player. If the player has completed the
	 * achievement, they will receive their reward.
	 * 
	 * @param player The player activating the achievement.
	 * @param achievement The achievement for activation.
	 */
	public static void activateAchievement(Player player, AchievementList achievement, int increase) {
		if (player.getPlayerAchievements().get(achievement) == achievement.getCompleteAmount()) {
			return;
		}
		
		int current = player.getPlayerAchievements().get(achievement);
		
		player.getPlayerAchievements().put(achievement, current + increase);

		InterfaceHandler.writeText(new AchievementTab(player));

		if (player.getPlayerAchievements().put(achievement, current + increase) == achievement.getCompleteAmount()) {
			AchievementInterface.sendCompleteInterface(player, achievement);
			player.addAchievementPoints(player.getAchievementsPoints() + achievement.getReward());
			player.send(new SendMessage("[ <col=297A29>Achievements</col> ] You have completed an achievement!"));
			InterfaceHandler.writeText(new AchievementTab(player));
		}
	}

}