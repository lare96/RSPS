package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler.AchievementDifficulty;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendColor;

/**
 * Handles the achievement tab
 * 
 * @author Daniel
 *
 */
public class AchievementTab extends InterfaceHandler {

	private String[] text = new String[AchievementList.values().length + 8];

	public AchievementTab(Player player) {
		super(player);
		int shift = 0;
		text[shift++] = "";
		text[shift++] = "@lre@ Total achievements: @gre@" + AchievementList.values().length;
		text[shift++] = "@lre@ Completed: @gre@" + player.getPA().achievementCompleted();
		text[shift++] = "@lre@ Points: @gre@" + player.getAchievementsPoints();
		text[shift++] = "";
		AchievementDifficulty last = null;
		for (AchievementList achievement : AchievementList.values()) {
			if (last != achievement.getDifficulty()) {
				last = achievement.getDifficulty();
				text[shift++] = "@or2@[@lre@ " + Utility.capitalize(last.name().toLowerCase()) + " @or2@]";
			}
			int completed = player.getPlayerAchievements().get(achievement);
			if (completed > achievement.getCompleteAmount()) {
				completed = achievement.getCompleteAmount();
			}

			int color = completed == achievement.getCompleteAmount() ? 0x00FF00 : completed > 0 ? 0xFFFF00 : 0xFF0000;
			player.send(new SendColor(startingLine() + shift, color));
			text[shift++] = " " + achievement.getName();
		}
	}

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 31006;
	}

}