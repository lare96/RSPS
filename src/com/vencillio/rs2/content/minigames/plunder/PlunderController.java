package com.vencillio.rs2.content.minigames.plunder;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.Controller;

public class PlunderController extends Controller {

	@Override
	public boolean allowMultiSpells() {
		return true;
	}

	@Override
	public boolean allowPvPCombat() {
		return false;
	}

	@Override
	public boolean canAttackNPC() {
		return true;
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
		return true;
	}

	@Override
	public boolean canEat(Player paramPlayer) {
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
	public boolean canEquip(Player paramPlayer, int paramInt1, int paramInt2) {
		return true;
	}

	@Override
	public boolean canLogOut() {
		return true;
	}

	@Override
	public boolean canMove(Player paramPlayer) {
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
		return false;
	}

	@Override
	public boolean canTrade() {
		return false;
	}

	@Override
	public boolean canUseCombatType(Player paramPlayer, CombatTypes paramCombatTypes) {
		return true;
	}

	@Override
	public boolean canUsePrayer(Player paramPlayer, int id) {
		return true;
	}

	@Override
	public boolean canUseSpecialAttack(Player paramPlayer) {
		return true;
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return new Location(3087, 3500, 0);
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
		paramPlayer.teleport(new Location(3087, 3500, 0));
	}

	@Override
	public void onTeleport(Player paramPlayer) {
	}

	@Override
	public void tick(Player paramPlayer) {
	}

	@Override
	public String toString() {
		return "Pyramid Plunder Controller";
	}

	@Override
	public boolean transitionOnWalk(Player paramPlayer) {
		return false;
	}

	@Override
	public void onKill(Player player, Entity killed) {
	// TODO Auto-generated method stub
	
	}
}