package com.vencillio.rs2.content.wilderness;

import com.vencillio.rs2.entity.player.Player;

public class PlayerKilling {

	/**
	 * Adds the host of the killed player.
	 *
	 * @param player
	 *            Player that saves the host.
	 * @param host
	 *            Host address of the killed player.
	 * @return True if the host is added to the players array.
	 */

	public static boolean addHostToList(Player player, String host) {
		if (player != null && player.getLastKilledPlayers() != null) {
			return player.getLastKilledPlayers().add(host);
		}
		return false;
	}

	/**
	 * Checks if the host is already on the players array.
	 * 
	 * @param player
	 *            Player that is adding the killed players host.
	 * @param host
	 *            Host address of the killed player.
	 * @return True if the host is on the players array.
	 */

	public static boolean hostOnList(Player player, String host) {
		if (player != null && player.getLastKilledPlayers() != null) {
			if (player.getLastKilledPlayers().lastIndexOf(host) >= 3) {
				removeHostFromList(player, host);
				return false;
			}
			return player.getLastKilledPlayers().contains(host);
		}
		return false;
	}

	/**
	 * Removes the host from the players array.
	 * 
	 * @param player
	 *            Player that is removing the host.
	 * @param host
	 *            Host that is being removed.
	 * @return True if host is successfully removed.
	 */

	public static boolean removeHostFromList(Player player, String host) {
		if (player != null && player.getLastKilledPlayers() != null) {
			return player.getLastKilledPlayers().remove(host);
		}
		return false;
	}

}