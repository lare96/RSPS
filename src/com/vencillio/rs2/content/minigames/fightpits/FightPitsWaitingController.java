package com.vencillio.rs2.content.minigames.fightpits;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerOption;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendWalkableInterface;

public class FightPitsWaitingController extends Controller {
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
		return true;
	}

	@Override
	public boolean canAttackPlayer(Player p, Player p2) {
		return false;
	}

	@Override
	public boolean canClick() {
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
	public boolean canLogOut() {
		return true;
	}

	@Override
	public boolean canMove(Player p) {
		return true;
	}

	@Override
	public boolean canSave() {
		return false;
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
		return new Location(PlayerConstants.EDGEVILLE);
	}

	@Override
	public boolean isSafe(Player player) {
		return true;
	}

	@Override
	public void onControllerInit(Player p) {
		FightPits.updateInterface(p);
		p.getClient().queueOutgoingPacket(new SendPlayerOption("null", 3));
		for (int i = 2; i < FightPitsConstants.FIGHT_PITS_INTERFACE_STRINGS.length; i++) {
			p.getClient().queueOutgoingPacket(new SendString("", FightPitsConstants.FIGHT_PITS_INTERFACE_STRINGS[i]));
		}
		p.getClient().queueOutgoingPacket(new SendWalkableInterface(17600));
	}

	@Override
	public void onDeath(Player p) {
		p.setController(ControllerManager.DEFAULT_CONTROLLER);
		FightPits.removeFromWaitingRoom(p);
	}

	@Override
	public void onDisconnect(Player p) {
		FightPits.removeFromWaitingRoom(p);
	}

	@Override
	public void onTeleport(Player p) {
		FightPits.removeFromWaitingRoom(p);
	}

	@Override
	public void tick(Player p) {
		FightPits.updateInterface(p);
	}

	@Override
	public String toString() {
		return "FIGHT PITS WAITING ROOM";
	}

	@Override
	public boolean transitionOnWalk(Player p) {
		return false;
	}

	@Override
	public void onKill(Player player, Entity killed) {
	}
}
