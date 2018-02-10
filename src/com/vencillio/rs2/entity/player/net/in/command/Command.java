package com.vencillio.rs2.entity.player.net.in.command;

import com.vencillio.rs2.entity.player.Player;

/**
 * Command
 * @author Vencillio Team
 *
 */
public abstract interface Command {
	
	/**
	 * Handles the commands
	 * @param player
	 * @param parser
	 */
	public abstract boolean handleCommand(Player player, CommandParser parser) throws Exception;

	/**
	 * Checks if player meets requirement(s)
	 * @param player
	 * @return
	 */
	public abstract boolean meetsRequirements(Player player);
}
