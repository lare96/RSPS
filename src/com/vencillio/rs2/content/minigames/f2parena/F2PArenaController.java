package com.vencillio.rs2.content.minigames.f2parena;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.GenericMinigameController;

/**
 * F2P Arena Controller
 * @author Daniel
 *
 */
public class F2PArenaController extends GenericMinigameController {
	@Override
	public boolean allowMultiSpells() {
		return false;
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
		for (int i = 0; i < F2PArenaConstants.ALLOWED_PRAYERS.length; i++) {
			if (id == F2PArenaConstants.ALLOWED_PRAYERS[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canUseSpecialAttack(Player paramPlayer) {
		return false;
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return Utility.randomElement(F2PArenaConstants.RESPAWN_LOCATIONS);
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
	public void tick(Player paramPlayer) {

	}

	@Override
	public String toString() {
		return "F2P Arena";
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
