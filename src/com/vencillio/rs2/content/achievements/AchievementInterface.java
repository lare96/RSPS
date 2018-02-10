package com.vencillio.rs2.content.achievements;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendBanner;

/**
 * Handles the achievement interfaces
 * 
 * @author Daniel
 * @author Michael
 */
public class AchievementInterface {
	
	/**
	 * Sends the achievement information interface
	 * 
	 * @param player
	 * @param achievement
	 */
	public static void sendInterfaceForAchievement(final Player player, AchievementList achievement) {
		String difficulty = Utility.formatPlayerName(achievement.getDifficulty().name().toLowerCase());
		int completed = player.getPlayerAchievements().get(achievement);
		int progress = (int) (completed * 100 / (double) achievement.getCompleteAmount());
		DialogueManager.sendInformationBox(player, "<u>Achievement System", "@dre@" + achievement.getName(), "@bla@Difficulty: @blu@" + difficulty, "@bla@Point(s): @blu@" + achievement.getReward(), "Progress: @blu@" + completed + "@bla@/@blu@" + achievement.getCompleteAmount() + " @bla@(@blu@" + progress + "%@bla@)");
	}

	/**
	 * Sends the achievement completion interface
	 * 
	 * @param player
	 * @param achievement
	 */
	public static void sendCompleteInterface(final Player player, final AchievementList achievement) {
		int color = 0;

		switch (achievement.getDifficulty()) {
		case EASY:
			color = 0x1C889E;
			break;
		case MEDIUM:
			color = 0xD9750B;
			break;
		case HARD:
			color = 0xC41414;
			break;
		}

		player.send(new SendBanner("Achievement complete: '" + achievement.getName() + "'", color));

	}

}