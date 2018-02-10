package com.vencillio.rs2.entity.player;

import com.vencillio.rs2.content.achievements.AchievementList;

/**
 * Handles miscellaneous methods for player
 * @author Daniel
 *
 */
public class PlayerAssistant {

	private Player player;
	
	public PlayerAssistant(Player player) {
		this.player = player;
	}
	
	/**
	 * Gets the total amount of achievements completed
	 * @return
	 */
	public int achievementCompleted() {
		int completed = 0;
		for (AchievementList achievement : player.getPlayerAchievements().keySet()) {
			if (achievement != null && player.getPlayerAchievements().get(achievement) == achievement.getCompleteAmount()) {
				completed++;
			}
		}
		return completed;
	}

	
}
