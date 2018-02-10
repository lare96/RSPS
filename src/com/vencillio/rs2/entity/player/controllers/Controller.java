package com.vencillio.rs2.entity.player.controllers;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public abstract class Controller {
	
	public abstract boolean allowMultiSpells();

	public abstract boolean allowPvPCombat();

	public abstract boolean canAttackNPC();

	public abstract boolean canAttackPlayer(Player player1, Player player2);

	public abstract boolean canClick();

	public abstract boolean canDrink(Player player);

	public abstract boolean canEat(Player player);

	public abstract boolean canEquip(Player player, int int1, int int2);
	
	public abstract boolean canUnequip(Player player);
	
	public abstract boolean canDrop(Player player);

	public abstract boolean canLogOut();

	public abstract boolean canMove(Player player);

	public abstract boolean canSave();

	public abstract boolean canTalk();

	public abstract boolean canTeleport();

	public abstract boolean canTrade();

	public abstract boolean canUseCombatType(Player player, CombatTypes paramCombatTypes);

	public abstract boolean canUsePrayer(Player player, int id);

	public abstract boolean canUseSpecialAttack(Player player);

	public abstract Location getRespawnLocation(Player player);

	public abstract boolean isSafe(Player player);

	public abstract void onControllerInit(Player player);

	public abstract void onDeath(Player player);

	public abstract void onKill(Player player, Entity killed);
	
	public abstract void onDisconnect(Player player);

	public abstract void onTeleport(Player player);

	public void throwException(Player player, String action) {
		System.out.println("||||||||||||||||||||||||||");
		System.out.println("UNABLE TO " + action + " FOR PLAYER " + player.getUsername() + "!");
		System.out.println("CONTROLLER: " + player.getController().toString());
		System.out.println("||||||||||||||||||||||||||");
	}

	public abstract void tick(Player player);

	@Override
	public abstract String toString();

	public abstract boolean transitionOnWalk(Player player);

}
