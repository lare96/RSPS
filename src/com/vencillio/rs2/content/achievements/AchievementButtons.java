package com.vencillio.rs2.content.achievements;

import java.util.HashMap;

import com.vencillio.rs2.content.achievements.AchievementHandler.AchievementDifficulty;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the achievement buttons
 * @author Daniel
 * @author Michael
 */
public class AchievementButtons {

	private static final HashMap<Integer, AchievementList> BUTTONS = new HashMap<Integer, AchievementList>();
	
	static {
		int button = 121035;
		AchievementDifficulty last = null;
		for (AchievementList achievement : AchievementList.values()) {
			if (last != achievement.getDifficulty()) {
				button++;
				last = achievement.getDifficulty();
			}
			BUTTONS.put(button++, achievement);
		}
	}

	public static boolean handleButtons(Player player, int buttonId) {
		if (BUTTONS.containsKey((Integer) buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS.get((Integer) buttonId));
			return true;
		}
		return false;
	}

}