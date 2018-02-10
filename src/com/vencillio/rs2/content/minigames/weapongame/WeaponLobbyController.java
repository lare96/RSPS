package com.vencillio.rs2.content.minigames.weapongame;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class WeaponLobbyController extends Controller {

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
	public boolean canUnequip(Player player) {		
		return false;
	}

	@Override
	public boolean canDrop(Player player) {
		return false;
	}

	@Override
	public boolean canEquip(Player paramPlayer, int paramInt1, int paramInt2) {
		return false;
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
		return true;
	}

	@Override
	public void onControllerInit(Player player) {
		TaskQueue.queue(new Task(player, 1) {
			@Override
			public void execute() {
				DialogueManager.sendStatement(player, "Welcome to the @dre@Weapon Game Lobby@bla@!", "Game will start when there are a minimum of @dre@5@bla@ players.", "Click on the @dre@portal@bla@ to @dre@exit@bla@.");
				stop();
			}
	
			@Override
			public void onStop() {

			}
		});
	
	}

	@Override
	public void onDeath(Player player) {
	
	}

	@Override
	public void onDisconnect(Player player) {
		WeaponGame.leaveLobby(player, false);
	}

	@Override
	public void onTeleport(Player player) {
		player.send(new SendMessage("@dre@If you would like to exit, please enter the portal."));
	}

	@Override
	public void tick(Player player) {
		player.send(new SendString("Players ready: " + WeaponGame.lobbyCount(), 41252));
		player.send(new SendString("(Need " + WeaponGameConstants.MINIMUM_PLAYERS + " to " + WeaponGameConstants.MAXIMUM_PLAYERS + ")", 41253));
		player.send(new SendString("Next Departure: " + Utility.getFormattedTime(WeaponGameConstants.LOBBY_TIME), 41254));
	}

	@Override
	public String toString() {
		return "WEAPON_GAME_LOBBY_CONTROLLER";
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
