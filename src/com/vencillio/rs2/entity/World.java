package com.vencillio.rs2.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.core.util.MobUpdateList;
import com.vencillio.rs2.content.combat.CombatConstants;
import com.vencillio.rs2.content.dwarfcannon.DwarfCannon;
import com.vencillio.rs2.content.gambling.Lottery;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControl;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGame;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobUpdateFlags;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.PlayerUpdateFlags;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.impl.SendGameUpdateTimer;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendNPCUpdate;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerUpdate;
import com.vencillio.rs2.entity.player.net.out.impl.SendProjectile;
import com.vencillio.rs2.entity.player.net.out.impl.SendStillGraphic;

/**
 * Handles the in-game world
 * 
 * @author Michael Sasse
 * 
 */
public class World {

	/**
	 * The maximum amount of players that can be processed
	 */
	public static final short MAX_PLAYERS = 2048;

	/**
	 * The maximum amount of mobs available in the in-game world
	 */
	public static final short MAX_MOBS = 8192;

	/**
	 * A list of players registered into the game world
	 */
	private static final Player[] players = new Player[MAX_PLAYERS];

	/**
	 * A list of mobs registered into the game world
	 */
	private static final Mob[] mobs = new Mob[MAX_MOBS];

	/**
	 * The servers cycles?
	 */
	private static long cycles = 0L;

	/**
	 * A list of updated mobs
	 */
	private static MobUpdateList mobUpdateList = new MobUpdateList();

	/**
	 * A list of cannons in-game
	 */
	private static List<DwarfCannon> cannons = new ArrayList<DwarfCannon>();

	/**
	 * The current server update timer
	 */
	private static short updateTimer = -1;

	/**
	 * The server is being updated
	 */
	private static boolean updating = false;

	/**
	 * is the tick ignored
	 */
	private static boolean ignoreTick = false;

	/**
	 * Is the world Updating
	 */
	public static boolean worldUpdating = false;

	/**
	 * Adds a cannon to the list
	 * 
	 * @param cannon
	 */
	public static void addCannon(DwarfCannon cannon) {
		cannons.add(cannon);
	}

	/**
	 * Gets the active amount of players online
	 * 
	 * @return
	 */
	public static int getActivePlayers() {
		int r = 0;

		for (Player p : players) {
			if (p != null) {
				r++;
			}
		}

		return r;
	}

	/**
	 * Gets the cycles
	 * 
	 * @return
	 */
	public static long getCycles() {
		return cycles;
	}

	/**
	 * Gets the list of in-game mobs
	 * 
	 * @return
	 */
	public static Mob[] getNpcs() {
		return mobs;
	}

	/**
	 * Gets a player by their name as a long
	 * 
	 * @param n
	 *            The players username as a long
	 * @return
	 */
	public static Player getPlayerByName(long n) {
		for (Player p : players) {
			if ((p != null) && (p.isActive()) && (p.getUsernameToLong() == n)) {
				return p;
			}
		}

		return null;
	}

	/**
	 * Gets a player by their username
	 * 
	 * @param username
	 *            The players username
	 * @return
	 */
	public static Player getPlayerByName(String username) {
		if (username == null) {
			return null;
		}

		long n = Utility.nameToLong(username.toLowerCase());

		for (Player p : players) {
			if ((p != null) && (p.isActive()) && (p.getUsernameToLong() == n)) {
				return p;
			}
		}

		return null;
	}

	/**
	 * Gets the list of players
	 * 
	 * @return
	 */
	public static Player[] getPlayers() {
		return players;
	}

	/**
	 * Initiates an in-game update
	 */
	public static void initUpdate(int time, boolean reboot) {
//		try {
//			ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "Run Server.bat");
//			processBuilder.directory(new File("./"));
//			processBuilder.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		Lottery.draw();
		worldUpdating = true;
		for (Player p : players) {
			if (p != null) {
				p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(time));
			}
		}
		TaskQueue.queue(new Task((int) Math.ceil((time * 5) / 3.0)) {
			@Override
			public void execute() {
				for (Player p : players)
					if (p != null) {
						p.logout(true);
						PlayerSave.save(p);
					}
				stop();
			}

			@Override
			public void onStop() {
				if (reboot) {
					try {
						ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "Run Server.bat");
						processBuilder.directory(new File("./"));
						processBuilder.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
	}

	/**
	 * Is the tick ignored
	 * 
	 * @return
	 */
	public static boolean isIgnoreTick() {
		return ignoreTick;
	}

	/**
	 * Checks if a mobs index is within range
	 * 
	 * @param mobIndex
	 * @return
	 */
	public static boolean isMobWithinRange(int mobIndex) {
		return (mobIndex > -1) && (mobIndex < mobs.length);
	}

	/**
	 * Checks if a player is within range to be registered
	 * 
	 * @param playerIndex
	 *            The index of the player
	 * @return
	 */
	public static boolean isPlayerWithinRange(int playerIndex) {
		return (playerIndex > -1) && (playerIndex < players.length);
	}

	/**
	 * Is the server being updated
	 * 
	 * @return
	 */
	public static boolean isUpdating() {
		return updating;
	}

	/**
	 * The amount of npcs registered into the game world
	 * 
	 * @return
	 */
	public static int npcAmount() {
		int amount = 0;
		for (int i = 1; i < mobs.length; i++) {
			if (mobs[i] != null) {
				amount++;
			}
		}
		return amount;
	}

	/**
	 * Handles processing the main game world
	 */
	public static void process() {

		PlayerUpdateFlags[] pFlags = new PlayerUpdateFlags[players.length];
		MobUpdateFlags[] nFlags = new MobUpdateFlags[mobs.length];
		try {
			//FightPits.tick();
			PestControl.tick();
			WeaponGame.tick();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (DwarfCannon c : cannons) {
			c.tick();
		}

		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			try {
				if (player != null) {
					if (!player.isActive()) {
						if (player.getClient().getStage() == Client.Stages.LOGGED_IN) {
							player.setActive(true);
							player.start();

							player.getClient().resetLastPacketReceived();
						} else if (getCycles() - player.getClient().getLastPacketTime() > 30) {
							player.logout(true);
						}
					}

					player.getClient().processIncomingPackets();

					player.process();

					player.getClient().reset();

					for (DwarfCannon c : cannons) {
						if (c.getLoc().isViewableFrom(player.getLocation())) {
							c.rotate(player);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				if (player != null) {
					player.logout(true);
				}
			}

		}

		for (int i = 0; i < mobs.length; i++) {
			Mob mob = mobs[i];
			if (mob != null) {
				try {
					mob.process();
				} catch (Exception e) {
					e.printStackTrace();
					mob.remove();
				}
			}
		}

		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			if ((player == null) || (!player.isActive()))
				pFlags[i] = null;
			else {
				try {
					player.getMovementHandler().process();
					pFlags[i] = new PlayerUpdateFlags(player);
				} catch (Exception ex) {
					ex.printStackTrace();
					player.logout(true);
				}
			}
		}
		for (int i = 0; i < mobs.length; i++) {
			Mob mob = mobs[i];
			if (mob != null) {
				try {
					mob.processMovement();
					nFlags[mob.getIndex()] = new MobUpdateFlags(mob);
				} catch (Exception e) {
					e.printStackTrace();
					mob.remove();
				}
			}
		}

		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			if ((player != null) && (pFlags[i] != null) && (player.isActive())) {
				try {
					player.getClient().queueOutgoingPacket(new SendPlayerUpdate(pFlags));
					player.getClient().queueOutgoingPacket(new SendNPCUpdate(nFlags, pFlags[i]));
				} catch (Exception ex) {
					ex.printStackTrace();
					player.logout(true);
				}
			}
		}
		for (int i = 1; i < 2048; i++) {
			Player player = players[i];
			if ((player != null) && (player.isActive())) {
				try {
					player.reset();
				} catch (Exception ex) {
					ex.printStackTrace();
					player.logout(true);
				}
			}
		}
		for (int i = 0; i < mobs.length; i++) {
			Mob mob = mobs[i];
			if (mob != null) {
				try {
					mob.reset();
				} catch (Exception e) {
					e.printStackTrace();
					mob.remove();
				}
			}
		}

		if ((updateTimer > -1) && ((World.updateTimer = (short) (updateTimer - 1)) == 0)) {
			update();
		}

		if (ignoreTick) {
			ignoreTick = false;
		}

		cycles += 1L;
	}

	/**
	 * Registers a mob into the game world
	 * 
	 * @param mob
	 *            The mob to register into the game world
	 * @return
	 */
	public static int register(Mob mob) {
		for (int i = 1; i < mobs.length; i++) {
			if (mobs[i] == null) {
				mobs[i] = mob;
				mob.setIndex(i);
				return i;
			}
		}

		return -1;
	}

	/**
	 * Registers a player into the in-game world
	 * 
	 * @param player
	 *            The player to register into the game world
	 * @return
	 */
	public static int register(Player player) {
		int[] ids = new int[players.length];

		int c = 0;

		for (int i = 1; i < players.length; i++) {
			if (players[i] == null) {
				ids[c] = i;
				c++;
			}
		}

		if (c == 0) {
			return -1;
		}
		int index = ids[Utility.randomNumber(c)];

		players[index] = player;

		player.setIndex(index);

		for (int k = 1; k < players.length; k++) {
			if ((players[k] != null) && (players[k].isActive())) {
				players[k].getPrivateMessaging().updateOnlineStatus(player, true);
			}
		}
		if (updateTimer > -1) {
			player.getClient().queueOutgoingPacket(new SendGameUpdateTimer(updateTimer));
		}

		return c;
	}

	public static void remove(List<Mob> local) {
	}

	/**
	 * Removes a cannon from the list
	 * 
	 * @param cannon
	 */
	public static void removeCannon(DwarfCannon cannon) {
		cannons.remove(cannon);
	}

	/**
	 * Resets an in-game update
	 */
	public static void resetUpdate() {
		updateTimer = -1;

		synchronized (players) {
			for (Player p : players)
				if (p != null)
					p.getClient().queueOutgoingPacket(new SendGameUpdateTimer(0));
		}
	}

	/**
	 * Sends a global message to all players online
	 * 
	 * @param message
	 *            The message to send to all players
	 * @param format
	 *            Should the message beformatted
	 */
	public static void sendGlobalMessage(String message, boolean format) {
		message = (format ? "<col=255>" : "") + message + (format ? "</col>" : "");

		for (Player p : players)
			if ((p != null) && (p.isActive()))
				p.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	public static void sendGlobalMessage(String message) {
		for (Player i : World.getPlayers()) {
			if (i != null) {
				i.getClient().queueOutgoingPacket(new SendMessage(message));
			}
		}
	}
	
	public static void sendGlobalMessage(String message, Player exceptions) {
		for (Player i : World.getPlayers()) {
			if (i != null) {
				if (i != exceptions)
					i.getClient().queueOutgoingPacket(new SendMessage(message));
			}
		}
	}

	public static void sendProjectile(Projectile p, Entity e1, Entity e2) {
		int lockon = e2.isNpc() ? e2.getIndex() + 1 : -e2.getIndex() - 1;
		byte offsetX = (byte) ((e1.getLocation().getY() - e2.getLocation().getY()) * -1);
		byte offsetY = (byte) ((e1.getLocation().getX() - e2.getLocation().getX()) * -1);
		sendProjectile(p, CombatConstants.getOffsetProjectileLocation(e1), lockon, offsetX, offsetY);
	}

	/**
	 * Sends a projectile
	 * 
	 * @param projectile
	 *            The id of the graphic
	 * @param pLocation
	 *            The location to send the graphic too
	 * @param lockon
	 *            The lockon index
	 * @param offsetX
	 *            The x offset of the projectile
	 * @param offsetY
	 *            The y offset of the projectile
	 */
	public static void sendProjectile(Projectile projectile, Location pLocation, int lockon, byte offsetX, byte offsetY) {
		for (Player player : players)
			if (player != null) {
				if (pLocation.isViewableFrom(player.getLocation()))
					player.getClient().queueOutgoingPacket(new SendProjectile(player, projectile, pLocation, lockon, offsetX, offsetY));
			}
	}

	/**
	 * Sets a still graphic to a location
	 * 
	 * @param id
	 *            The id of the graphic
	 * @param delay
	 *            The delay of the graphic
	 * @param location
	 *            The location of the graphic
	 */
	public static void sendStillGraphic(int id, int delay, Location location) {
		for (Player player : players)
			if ((player != null) && (location.isViewableFrom(player.getLocation())))
				player.getClient().queueOutgoingPacket(new SendStillGraphic(id, location, delay));
	}

	/**
	 * Sends message to region players
	 * @param message
	 * @param location
	 */
	public static void sendRegionMessage(String message, Location location) {
		for (Player player : players) {
			if (player != null && location.isViewableFrom(player.getLocation())) {
				player.send(new SendMessage(message));

			}
		}
	}
	
	/**
	 * Sets the tick to be ignored
	 * 
	 * @param ignore
	 *            Should the tick be ignored
	 */
	public static void setIgnoreTick(boolean ignore) {
		ignoreTick = ignore;
	}

	/**
	 * Unregisters a mob from the game world
	 * 
	 * @param mob
	 *            The mob to unregister from the game world
	 */
	public static void unregister(Mob mob) {
		if (mob.getIndex() == -1) {
			return;
		}
		mobs[mob.getIndex()] = null;
		mobUpdateList.toRemoval(mob);
	}

	/**
	 * Unregisters a player from the game world
	 * 
	 * @param player
	 *            The player to unregister into the game world
	 */
	public static void unregister(Player player) {
		if ((player.getIndex() == -1) || (players[player.getIndex()] == null)) {
			return;
		}

		players[player.getIndex()] = null;

		for (int i = 0; i < players.length; i++)
			if ((players[i] != null) && (players[i].isActive())) {
				players[i].getPrivateMessaging().updateOnlineStatus(player, false);
			}
	}

	/**
	 * Updates the server by disconnecting all players
	 */
	public static void update() {
		updating = true;
		for (Player p : players)
			if (p != null)
				p.logout(true);
	}
	
	public static int getStaff() {
		int amount = 0;
		for (Player players : World.getPlayers()) {
			if (players != null) {
				if (PlayerConstants.isStaff(players)) {
					amount++;
				}
			}
		}
		return amount;
		
	}
}
