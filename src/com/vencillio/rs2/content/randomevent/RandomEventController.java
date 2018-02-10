package com.vencillio.rs2.content.randomevent;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.Controller;

public class RandomEventController extends Controller {

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
	public boolean canAttackPlayer(Player paramPlayer1, Player paramPlayer2) {
		return false;
	}

	@Override
	public boolean canClick() {
		return true;
	}

	@Override
	public boolean canDrink(Player paramPlayer) {
		return false;
	}

	@Override
	public boolean canEat(Player paramPlayer) {
		return false;
	}

	@Override
	public boolean canEquip(Player paramPlayer, int paramInt1, int paramInt2) {
		return false;
	}
	
	@Override
	public boolean canUnequip(Player player) {		
		return false;
	}

	@Override
	public boolean canDrop(Player player) {
		return false;
	}

	@Override
	public boolean canLogOut() {
		return false;
	}

	@Override
	public boolean canMove(Player paramPlayer) {
		return false;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public boolean canTalk() {
		return false;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canTrade() {
		return false;
	}

	@Override
	public boolean canUseCombatType(Player paramPlayer, CombatTypes paramCombatTypes) {
		return false;
	}

	@Override
	public boolean canUsePrayer(Player paramPlayer, int id) {
		return false;
	}

	@Override
	public boolean canUseSpecialAttack(Player paramPlayer) {
		return false;
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return null;
	}

	@Override
	public boolean isSafe(Player player) {
		return false;
	}

	@Override
	public void onControllerInit(Player paramPlayer) {
	}

	@Override
	public void onDeath(Player paramPlayer) {
	}

	@Override
	public void onDisconnect(Player paramPlayer) {
	}

	@Override
	public void onTeleport(Player paramPlayer) {
	}

	@Override
	public void tick(Player paramPlayer) {
	}

	@Override
	public String toString() {
		return "Random Event Controller";
	}

	@Override
	public boolean transitionOnWalk(Player paramPlayer) {
		return false;
	}

	@Override
	public void onKill(Player player, Entity killed) {
	}
}