package com.vencillio.rs2.entity.player.controllers;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.wilderness.GainTarget;
import com.vencillio.rs2.content.wilderness.TargetSystem;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerOption;

public class WildernessController extends Controller {

	public static final String LEVEL_ATTRIBUTE = "wildlvlattr";

	@Override
	public boolean allowMultiSpells() {
		return true;
	}

	@Override
	public boolean allowPvPCombat() {
		return true;
	}

	@Override
	public boolean canAttackNPC() {
		return true;
	}

	@Override
	public boolean canAttackPlayer(Player p, Player p2) {
		if (p2.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)) {
			int difference = Math.abs(p.getSkill().getCombatLevel() - p2.getSkill().getCombatLevel());

			if (difference > p.getWildernessLevel()) {
				p.getClient().queueOutgoingPacket(new SendMessage("You must move deeper in the Wilderness to attack this player."));
				return false;
			}
			if (difference > p2.getWildernessLevel()) {
				p.getClient().queueOutgoingPacket(new SendMessage("This player must move deeper in the Wilderness for you to attack him."));
				return false;
			}
		} else {
			p.getClient().queueOutgoingPacket(new SendMessage("This player is busy or they are not in the Wilderness."));
			return false;
		}

		return true;
	}

	@Override
	public boolean canClick() {
		return true;
	}

	@Override
	public boolean canDrink(Player p) {
		return true;
	}

	@Override
	public boolean canEat(Player p) {
		return true;
	}

	@Override
	public boolean canEquip(Player p, int id, int slot) {
		return true;
	}
	
	@Override
	public boolean canUnequip(Player player) {		
		return true;
	}

	@Override
	public boolean canDrop(Player player) {
		return true;
	}

	@Override
	public boolean canLogOut() {
		return true;
	}

	@Override
	public boolean canMove(Player p) {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public boolean canTalk() {
		return true;
	}

	@Override
	public boolean canTeleport() {
		return true;
	}

	@Override
	public boolean canTrade() {
		return true;
	}

	@Override
	public boolean canUseCombatType(Player p, CombatTypes type) {
		return true;
	}

	@Override
	public boolean canUsePrayer(Player p, int id) {
		return true;
	}

	@Override
	public boolean canUseSpecialAttack(Player p) {
		return true;
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return new Location(PlayerConstants.HOME);
	}

	@Override
	public boolean isSafe(Player player) {
		return false;
	}

	@Override
	public void onControllerInit(Player player) {		
		player.getClient().queueOutgoingPacket(new SendPlayerOption("Attack", 3));
		TargetSystem.getInstance().update(player);
		if (player.getAttributes().get("gainTarget") == null && !TargetSystem.getInstance().playerHasTarget(player)) {
			Task task = new GainTarget(player, (byte) 1);
			player.getAttributes().set("gainTarget", task);
			TaskQueue.queue(task);
		}
	}

	@Override
	public void onDeath(Player p) {
		p.getAttributes().remove("gainTarget");
		if (TargetSystem.getInstance().playerHasTarget(p)) {
			TargetSystem.getInstance().resetTarget(p, false);
		}
	}

	@Override
	public void onDisconnect(Player p) {
	}

	@Override
	public void onTeleport(Player p) {
		if (p.getAttributes().get("gainTarget") != null) {
			((Task) p.getAttributes().get("gainTarget")).stop();
		}
		if (TargetSystem.getInstance().playerHasTarget(p)) {
			TargetSystem.getInstance().resetTarget(p, false);
		}
	}

	@Override
	public void tick(Player player) {
		TargetSystem.getInstance().update(player);
	}

	@Override
	public String toString() {
		return "WILDERNESS";
	}

	@Override
	public boolean transitionOnWalk(Player p) {
		return true;
	}

	@Override
	public void onKill(Player player, Entity killed) {
	// TODO Auto-generated method stub
	
	}
}
