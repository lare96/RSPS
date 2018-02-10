package com.vencillio.rs2.content.minigames.pestcontrol;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Pest Control minigame
 * @author Daniel
 *
 */
public class PestControl {
	
	/**
	 * List of games
	 */
	private static final List<PestControlGame> games = new LinkedList<PestControlGame>();

	/**
	 * List of players waiting
	 */
	private static final Queue<Player> waiting = new ArrayDeque<Player>();

	/**
	 * Time
	 */
	private static short time = 120;

	/**
	 * Handles object clicking
	 * @param player
	 * @param id
	 * @return
	 */
	public static boolean clickObject(Player player, int id) {
		switch (id) {
		case 14315:
			if (!player.getController().equals(ControllerManager.PEST_WAITING_ROOM_CONTROLLER)) {
				player.setController(ControllerManager.PEST_WAITING_ROOM_CONTROLLER);
				player.teleport(new Location(2661, 2639));

				if (!waiting.contains(player)) {
					waiting.add(player);
				}
			}
			return true;
		case 14314:
			if (!player.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
				player.setController(ControllerManager.DEFAULT_CONTROLLER);
				player.teleport(new Location(2657, 2639));
				waiting.remove(player);
			}
			return true;
		}

		return false;
	}

	/**
	 * Get the minutes till depart
	 * @return
	 */
	public static int getMinutesTillDepart() {
		return time;
	}

	/**
	 * Gets the ready players
	 * @return
	 */
	public static int getPlayersReady() {
		return waiting.size();
	}

	/**
	 * Game ending
	 * @param game
	 */
	public static void onGameEnd(PestControlGame game) {
		games.remove(game);
	}

	/**
	 * Sends message to players waiting
	 * @param message
	 */
	public static void sendMessageToWaiting(String message) {
		for (Player p : waiting) {
			p.getClient().queueOutgoingPacket(new SendMessage(message));
		}
	}

	/**
	 * Starts the game
	 */
	public static void startGame() {

		if (waiting.size() < 2) {
			sendMessageToWaiting("There are not enough required players to start.");
			return;
		}

		if (games.size() == 3) {
			sendMessageToWaiting("There are too many active pest control games right now, Please wait.");
			return;
		}

		List<Player> toPlay = new LinkedList<Player>();

		int playing = 0;
		Player p;

		while ((playing < 25) && ((p = waiting.poll()) != null)) {
			toPlay.add(p);
			playing++;
		}

		if (waiting.size() > 0) {
			for (Player k : waiting) {
				k.getClient().queueOutgoingPacket(new SendMessage("You couldn't be added to the last game, you've moved up in priority for the next game."));
			}
		}

		games.add(new PestControlGame(toPlay, toPlay.get(0).getIndex() << 2));
	}

	/**
	 * Process
	 */
	public static void tick() {
		if (waiting.size() > 0) {
			time--;

			if (time == 0 || waiting.size() == 25) {
				startGame();
				time = 120;
			}
		} else if (time != 120) {
			time = 120;
		}

		if (games.size() > 0) {
			for (Iterator<PestControlGame> i = games.iterator(); i.hasNext();) {
				i.next().process();
			}
		}
	}
	
}
