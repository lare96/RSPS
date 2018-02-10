package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.player.Player;

public class PointsInterface extends InterfaceHandler {
	
	public PointsInterface(Player player) {
		super(player);
	}

	private final String[] text = {
		"@dre@Credits: @blu@" + Utility.format(player.getCredits()),
		"@dre@Achievement: @blu@" + Utility.format(player.getAchievementsPoints()),
		"@dre@Vote: @blu@" + Utility.format(player.getVotePoints()),
		"@dre@Bounty: @blu@" + Utility.format(player.getBountyPoints()),
		"@dre@Slayer: @blu@" + Utility.format(player.getSlayerPoints()),
		"@dre@Prestige: @blu@" + Utility.format(player.getPrestigePoints()),
		"@dre@Pest Control: @blu@" + Utility.format(player.getPestPoints()),
		"@dre@Mage Arena: @blu@" + Utility.format(player.getArenaPoints()),
		"@dre@Weapon Game: @blu@" + Utility.format(player.getWeaponPoints()),
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 8145;
	}

}

