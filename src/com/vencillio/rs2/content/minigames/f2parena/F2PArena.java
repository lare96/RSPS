package com.vencillio.rs2.content.minigames.f2parena;

import java.util.ArrayDeque;
import java.util.Queue;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the F2P Arena Minigame
 * Arena where players may only use F2P weapon, armour, and gear.
 * 
 * @author Daniel
 *
 */
public class F2PArena {
	/**
	 * Holds the game players
	 */
	public static Queue<Player> gamePlayers = new ArrayDeque<Player>();
	
	/**
	 * Handles entering minigame
	 * @param player
	 */
	public static void enterGame(Player player) {
		if (player.getBossPet() != null) {
			DialogueManager.sendStatement(player, "You can't bring a pet into this game!");
			return;
		}
		if (gamePlayers.contains(player)) {
			return;
		}
		DialogueManager.sendInformationBox(player, "F2P Arena", "Welcome to the @blu@F2P Arena@bla@!", "There are currently @blu@" + gamePlayers.size() + " @bla@members playing.", "Objective: @blu@Kill as many players as possible@bla@.", "To leave click on the @blu@portal@bla@.");
		player.setController(ControllerManager.F2P_ARENA_CONTROLLER);
		player.teleport(Utility.randomElement(F2PArenaConstants.RESPAWN_LOCATIONS));
		gamePlayers.add(player);
	}
	
	/**
	 * Handles leaving minigame
	 * @param player
	 */
	public static void leaveGame(Player player) {
		if (!gamePlayers.contains(player)) {
			return;
		}
		gamePlayers.remove(player);
	}
	
	/**
	 * Sends message to all game players
	 * @param message
	 */
	public static void messagePlayers(String message) {
		for (Player players : gamePlayers) {
			players.send(new SendMessage(message));
		}
	}
	
}
