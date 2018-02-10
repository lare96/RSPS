package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.VencillioConstants;
import com.vencillio.Server;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendColor;

/**
 * Handles the quest tab text
 * @author Daniel
 *
 */
public class QuestTab extends InterfaceHandler {
	
	public QuestTab(Player player) {
		super(player);
		color(16, 0xC71C1C);
		color(17, 0xC71C1C);
	}
	
	public void color(int id, int color) {
		player.send(new SendColor(startingLine() + id, color));
	}
	
	private final String[] text = {
			"@or1@        [ @lre@Game Information @or1@]",
			"@or2@Online Player(s): @whi@" + World.getActivePlayers(),		
			"@or2@Online Staff(s): @whi@" + World.getStaff(),
			"@or2@Online Player Record: @whi@" + VencillioConstants.MOST_ONLINE,
			"@or2@Time: @whi@"+ Utility.getCurrentServerTime(),
			"@or2@Date: @whi@"+ Server.vencillioDate(),
			"@or2@Uptime: @whi@"+ Server.getUptime(),
			"@or2@Votes: @whi@" + Utility.format(VencillioConstants.CURRENT_VOTES),
			"@or2@Last Voter: @whi@" + Utility.formatPlayerName(VencillioConstants.LAST_VOTER) ,
			"@or2@Vesion: @whi@" + VencillioConstants.VERSION,
			"",
			"@or1@        [ @lre@Player Information @or1@]",		
			"@or2@Username: @whi@" + Utility.capitalizeFirstLetter(player.getUsername()),
			"@or2@Rank: " + player.determineIcon(player) + player.determineRank(player) ,
			"@or2@Money spent: @whi@$" + Utility.format(player.getMoneySpent()),
			"@or2@Credits: @whi@" + Utility.format(player.getCredits()),			
			"@or2@Log Panel [</col>View@or2@]",
			"",
			"",
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 29501;
	}

}

