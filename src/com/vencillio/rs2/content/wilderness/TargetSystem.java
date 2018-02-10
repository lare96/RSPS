package com.vencillio.rs2.content.wilderness;

import java.math.BigInteger;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerHint;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class TargetSystem {

	/**
	 * The class instance.
	 */
	private static final TargetSystem instance = new TargetSystem();

	/**
	 * Returns a visible encapsulation of the class instance.
	 * 
	 * @return The returned encapsulated instance.
	 */
	public final static TargetSystem getInstance() {
		return instance;
	}

	/**
	 * Manages Assigning the Target to the user if they meet the requirements to
	 * receive one.
	 * 
	 * @param player
	 */
	public void assignTarget(Player player) {
		for (Player players : World.getPlayers()) {
			if (players != null && players.isActive()) {
				Player p = players;
				if (p.inWilderness() && player.inWilderness() && p.isActive() && player.isActive() && p != player) {
					if (p.getClient().getHostId() == player.getClient().getHostId()) {
						return;
					}
					if (p.isDead() || player.isDead()) {
						return;
					}
					if (playerHasTarget(p) || playerHasTarget(player)) {
						return;
					}
					if (PlayerKilling.hostOnList(p, player.getClient().getHost()) || PlayerKilling.hostOnList(player, p.getClient().getHost())) {
						return;
					}
					if (!inCombatRange(player, p)) {
						return;
					}
					setTarget(player, p.getIndex(), p.getUsername());
					setTarget(p, player.getIndex(), player.getUsername());
				}
			}
		}
	}

	/**
	 * Determines if the chosen user is applicable combat level for the other
	 * user to be assigned a target to.
	 * 
	 * @param player
	 * @param target
	 * @return
	 */
	public boolean inCombatRange(Player player, Player target) {
		if (Math.abs(player.getSkill().getCombatLevel() - target.getSkill().getCombatLevel()) > 10) {
			return false;
		}
		return true;
	}

	/**
	 * Determines if the users target is null.
	 * 
	 * @param targetName
	 * @return
	 */
	public boolean isNull(String targetName) {
		for (Player p : World.getPlayers())
			if (p != null && p.getUsername().equalsIgnoreCase(targetName))
				return false;
		return true;
	}

	/**
	 * Returns whether or not the user has a target.
	 * 
	 * @param player
	 * @return
	 */
	public boolean playerHasTarget(Player player) {
		return player.targetIndex != 0 && (player.targetName != "None" || player.targetName != null);
	}

	/**
	 * Manages resetting the users target for whatever reason may be called for.
	 * 
	 * Ex: Logging out, Dying etc.
	 * 
	 * @param player
	 */
	public void resetTarget(Player player, boolean logout) {
		Player target = World.getPlayers()[player.targetIndex];
		if (target == null || player == null) {
			return;
		}
		target.targetIndex = 0;
		target.targetName = "None";
		if (logout) {
			target.getClient().queueOutgoingPacket(new SendMessage("@dre@Your target has left the wilderness. You will be assigned a new one shortly."));
			if (target.inWilderness()) {
				if (player.getAttributes().get("gainTarget") == null) {
					Task task = new GainTarget(player, (byte) 1);
					player.getAttributes().set("gainTarget", task);
					TaskQueue.queue(task);
				}
			}
		}
		target.getClient().queueOutgoingPacket(new SendPlayerHint(true, -1));
		target.getClient().queueOutgoingPacket(new SendString("None", 23307));
		player.targetIndex = 0;
		player.targetName = "None";
		player.getClient().queueOutgoingPacket(new SendPlayerHint(true, -1));
	}

	/**
	 * Manages setting the target to the player.
	 * 
	 * @param player
	 * @param targetPlayerId
	 * @param targetName
	 */
	public void setTarget(Player player, int targetPlayerId, String targetName) {
		player.targetIndex = targetPlayerId;
		player.targetName = targetName;
		player.getClient().queueOutgoingPacket(new SendMessage("@dre@You have been assigned the user " + "'" + targetName + "'" + " as your target!"));
		if (World.getPlayers()[targetPlayerId] != null) {
			player.getClient().queueOutgoingPacket(new SendPlayerHint(true, player.targetIndex));
		}
	}

	/**
	 * Updates the interfaces
	 * 
	 * @param player
	 */
	public void update(Player player) {
		if (!player.inWilderness()) {
			return;
		}
		player.send(new SendString(player.getRogueKills() + "", 23310));
		player.send(new SendString(player.getRogueRecord() + "", 23311));
		player.send(new SendString(player.getHunterKills() + "", 23312));
		player.send(new SendString(player.getHunterRecord() + "", 23313));
		player.send(new SendString(player.targetIndex == 0 ? "None" : player.targetName, 23307));
		player.send(new SendString(calculateTargetWealth(player), 23305));
		player.send(new SendString(getTargetInformation(player), 23308));
		player.send(new SendString("@yel@Level: " + player.getWildernessLevel(), 199));
	}
	
	public String calculateTargetWealth(Player player) {
		if (player.targetIndex == 0) {
			return "";
		}
	
		Player target = World.getPlayers()[player.targetIndex];
		BigInteger carried_wealth = target.getInventory().getContainerNet().add(target.getEquipment().getContainerNet());

		if (carried_wealth.intValue() > 10_000_000) {
			player.send(new SendConfig(881, 1));
			return "Wealth: V. High";
		} else if ((carried_wealth.intValue() >= 1_000_000) && (carried_wealth.intValue() < 10_000_000)) {
			player.send(new SendConfig(880, 1));
			return "Wealth: High";
		} else if ((carried_wealth.intValue() >= 250_000) && (carried_wealth.intValue() < 1_000_000)) {
			player.send(new SendConfig(879, 1));
			return "Wealth: Medium";
		} else if ((carried_wealth.intValue() >= 50_000) && (carried_wealth.intValue() < 250_000)) {
			player.send(new SendConfig(878, 1));
			return "Wealth: Low";
		} else {
			player.send(new SendConfig(877, 1));
			return "Wealth: V. Low";
		}
	}
	
	public String getTargetInformation(Player player) {
		if (player.targetIndex == 0) {
			return "Level: -----";
		}
		
		Player target = World.getPlayers()[player.targetIndex];	
	
		String location = "Safe";
		String color = "@gr2@";
	
		if (target.inWilderness()) {
			int level = target.getWildernessLevel();
			location = "Lvl " + (level <= 3 ? "1" : level - 3) + "-" + (level + 3);
	
			if ((target.getSkill().getCombatLevel() > (player.getSkill().getCombatLevel() + target.getWildernessLevel())) || (player.getSkill().getCombatLevel() > (target.getSkill().getCombatLevel() + target.getWildernessLevel()))) {
				color = "@red@";
			}
		}
	
		location += ", Cmb " + target.getSkill().getCombatLevel();
		return color + location;
	}


}