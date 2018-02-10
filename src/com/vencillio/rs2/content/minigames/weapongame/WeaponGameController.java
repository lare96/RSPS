package com.vencillio.rs2.content.minigames.weapongame;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.GenericMinigameController;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

public class WeaponGameController extends GenericMinigameController {

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
	public boolean canAttackPlayer(Player paramPlayer1, Player paramPlayer2) {
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
	public boolean canEquip(Player paramPlayer, int paramInt1, int paramInt2) {
		return true;
	}
	
	@Override
	public boolean canUnequip(Player player) {		
		return true;
	}

	@Override
	public boolean canDrop(Player player) {
		return false;
	}

	@Override
	public boolean canUseCombatType(Player paramPlayer, CombatTypes paramCombatTypes) {
		return true;
	}

	@Override
	public boolean canUsePrayer(Player paramPlayer, int id) {
		return false;
	}

	@Override
	public boolean canUseSpecialAttack(Player paramPlayer) {
		return true;
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS);
	}

	@Override
	public boolean isSafe(Player player) {
		return true;
	}

	@Override
	public void onControllerInit(Player paramPlayer) {
	// TODO Auto-generated method stub
	
	}

	@Override
	public void onDeath(Player paramPlayer) {
	
		Entity killer = paramPlayer.getCombat().getDamageTracker().getKiller();
		
		if (killer != null && !killer.isNpc()) {
			killer.getPlayer().setWeaponKills(killer.getPlayer().getWeaponKills() + 1);
			WeaponGame.upgrade(killer.getPlayer());
		}
	
	}
	
	@Override
	public void onKill(Player player, Entity killed) {
	
	}

	@Override
	public void onDisconnect(Player paramPlayer) {
		WeaponGame.leaveGame(paramPlayer, false);
	}

	@Override
	public void tick(Player paramPlayer) {
		paramPlayer.send(new SendString("" + Utility.getFormattedTime(WeaponGameConstants.GAME_TIME), 41274));
		paramPlayer.send(new SendString("" + WeaponGame.gameCount(), 41276));
		if (WeaponGame.leader != null) {
			paramPlayer.send(new SendString("" + WeaponGame.leader.getUsername(), 41278));			
		} else {
			paramPlayer.send(new SendString("None", 41278));	
		}
		paramPlayer.send(new SendString("" + Utility.getFormattedTime(WeaponGameConstants.CRATE_TIME) , 41280));
		
		paramPlayer.send(new SendString("" + paramPlayer.getWeaponKills(), 41282));
		if (paramPlayer.getWeaponKills() == 0) {
			paramPlayer.send(new SendUpdateItemsAlt(41283, 0, 0, 0));		
		} else {
			paramPlayer.send(new SendUpdateItemsAlt(41283, WeaponGameConstants.TIER_DATA[paramPlayer.getWeaponKills()][0].getId(), 1, 0));			
		}
		if (paramPlayer.getWeaponKills() != 9) {
			paramPlayer.send(new SendUpdateItemsAlt(41283, WeaponGameConstants.TIER_DATA[paramPlayer.getWeaponKills() + 1][0].getId(), 1, 1));			
		} else {
			paramPlayer.send(new SendUpdateItemsAlt(41283, 995, 500_000, 1));			
		}
	}

	@Override
	public String toString() {
	return "WEAPON_GAME_CONTROLLER";
	}

	@Override
	public boolean canLogOut() {
		return false;
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
		return false;
	}

	@Override
	public boolean canTrade() {
		return false;
	}

	@Override
	public void onTeleport(Player p) {
	}

}
