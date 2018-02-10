package com.vencillio.rs2.entity.player.controllers;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public abstract class GenericMinigameController extends Controller {
	
	@Override
	public abstract boolean allowMultiSpells();

	@Override
	public abstract boolean allowPvPCombat();

	@Override
	public abstract boolean canAttackNPC();

	@Override
	public abstract boolean canAttackPlayer(Player paramPlayer1, Player paramPlayer2);

	@Override
	public boolean canClick() {
		return true;
	}

	@Override
	public abstract boolean canDrink(Player paramPlayer);

	@Override
	public abstract boolean canEat(Player paramPlayer);

	@Override
	public abstract boolean canEquip(Player paramPlayer, int paramInt1, int paramInt2);

	@Override
	public abstract boolean canLogOut();

	@Override
	public abstract boolean canMove(Player p);

	@Override
	public abstract boolean canSave();

	@Override
	public abstract boolean canTalk();

	@Override
	public abstract boolean canTeleport();

	@Override
	public abstract boolean canTrade();

	@Override
	public abstract boolean canUseCombatType(Player paramPlayer, CombatTypes paramCombatTypes);

	@Override
	public abstract boolean canUsePrayer(Player paramPlayer, int id);

	@Override
	public abstract boolean canUseSpecialAttack(Player paramPlayer);

	@Override
	public abstract Location getRespawnLocation(Player player);

	@Override
	public abstract boolean isSafe(Player player);

	@Override
	public abstract void onControllerInit(Player paramPlayer);
	
	@Override
	public void onKill(Player player, Entity killed) {
	}

	@Override
	public abstract void onDeath(Player paramPlayer);

	@Override
	public abstract void onDisconnect(Player paramPlayer);

	@Override
	public abstract void onTeleport(Player p);

	@Override
	public abstract void tick(Player paramPlayer);

	@Override
	public abstract String toString();

	@Override
	public boolean transitionOnWalk(Player p) {
		return false;
	}
}
