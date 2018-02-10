package com.vencillio.rs2.content.minigames.clanwars;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.GenericMinigameController;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerOption;

public class ClanWarsFFAController extends GenericMinigameController {

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
		return false;
	}

	@Override
	public boolean canAttackPlayer(Player paramPlayer1, Player paramPlayer2) {
		if (!paramPlayer1.inClanWarsFFA() || !paramPlayer2.inClanWarsFFA()) {
			return false;
		}
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
		return ClanWarsConstants.FFA_PORTAL;
	}

	@Override
	public boolean isSafe(Player player) {
		return true;
	}

	@Override
	public void onControllerInit(Player player) {
		player.send(new SendMessage("@dre@Pass the line enter combat zone."));
	}

	@Override
	public void onDeath(Player paramPlayer) {
		paramPlayer.teleport(new Location(3327, 4754, paramPlayer.getZ()));
	}

	@Override
	public void onDisconnect(Player paramPlayer) {
		paramPlayer.teleport(ClanWarsConstants.CLAN_WARS_ARENA);
	}

	@Override
	public void tick(Player paramPlayer) {
		if (paramPlayer.inClanWarsFFA()) {
			paramPlayer.getClient().queueOutgoingPacket(new SendPlayerOption("Attack", 3));
		} else {
			paramPlayer.getClient().queueOutgoingPacket(new SendPlayerOption("null", 3));
		}
	}

	@Override
	public String toString() {
		return "Clan Wars FFA";
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
	public void onTeleport(Player p) {
	}


}
