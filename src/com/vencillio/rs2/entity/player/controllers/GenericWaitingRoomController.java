package com.vencillio.rs2.entity.player.controllers;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public abstract class GenericWaitingRoomController extends
		GenericMinigameController {
	@Override
	public boolean allowMultiSpells() {
		return false;
	}

	@Override
	public boolean allowPvPCombat() {
		return false;
	}

	@Override
	public boolean canAttackNPC() {
		return false;
	}

	@Override
	public boolean canAttackPlayer(Player p, Player p2) {
		return false;
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
	public boolean canSave() {
		return false;
	}

	@Override
	public boolean canUseCombatType(Player p, CombatTypes type) {
		return false;
	}

	@Override
	public boolean canUsePrayer(Player p, int id) {
		return false;
	}

	@Override
	public boolean canUseSpecialAttack(Player p) {
		return false;
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return null;
	}

	@Override
	public boolean isSafe(Player player) {
		return true;
	}

	@Override
	public abstract void onControllerInit(Player paramPlayer);

	@Override
	public void onDeath(Player p) {
	}

	@Override
	public abstract void onDisconnect(Player paramPlayer);

	@Override
	public abstract String toString();
}
