package com.vencillio.rs2.content.minigames.weapongame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendEquipment;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerHint;
import com.vencillio.rs2.entity.player.net.out.impl.SendPlayerOption;

/**
 * Handles the Weapon Game Minigame
 * @author Daniel
 *
 */
public class WeaponGame {
	
	/**
	 * Holds the lobby players
	 */
	public static Queue<Player> lobbyPlayers = new ArrayDeque<Player>();
	
	/**
	 * Holds the game players
	 */
	public static Queue<Player> gamePlayers = new ArrayDeque<Player>();
	
	/**
	 * Holds the crates
	 */
	private static List<GameObject> crates = new ArrayList<>();
	
	/**
	 * The current leader of minigame
	 */
	public static Player leader;
	
	/**
	 * Check if a game is active
	 */
	private static boolean started = false;
	
	/**
	 * Lobby time before game starter
	 */

	/**
	 * Handles player joining lobby
	 * @param player
	 */
	public static void joinLobby(Player player) {
		if (player.ironPlayer()) {
			DialogueManager.sendStatement(player, "<img=13>@gry@Iron man@bla@ accounts are restricted!");
			return;
		}
		if (player.getSkill().getCombatLevel() < WeaponGameConstants.COMBAT_REQUIRED) {
			DialogueManager.sendStatement(player, "Combat level of " + WeaponGameConstants.COMBAT_REQUIRED + " is required!");
			return;
		}
		if (player.getInventory().getFreeSlots() != 28) {
			DialogueManager.sendStatement(player, "You can't bring any items into the game!");
			return;
		}
		if (player.getEquipment().getEquipmentCount() != 0) {
			DialogueManager.sendStatement(player, "You can't be wearing any items!");
			return;
		}
		if (player.getBossPet() != null) {
			DialogueManager.sendStatement(player, "You can't bring a pet into this game!");
			return;
		}
		if (!player.getController().equals(ControllerManager.WEAPON_LOBBY_CONTROLLER)) {
			player.teleport(new Location(player.getX(), player.getY() - 2, player.getZ()));
			player.setController(ControllerManager.WEAPON_LOBBY_CONTROLLER);
			if (!lobbyPlayers.contains(player)) {
				lobbyPlayers.add(player);
			}
			player.send(new SendMessage("@dre@There are currently " + lobbyPlayers.size() + " players in the lobby."));
		}	
	}
	
	/**
	 * Handles player leaving lobby
	 * @param player
	 * @param portal
	 */
	public static void leaveLobby(Player player, boolean barrier) {
		if (lobbyPlayers.contains(player)) {
			lobbyPlayers.remove(player);
			player.setController(ControllerManager.DEFAULT_CONTROLLER);
		}
		if (barrier) {
			player.send(new SendMessage("@dre@You have left the Weapon Game Lobby."));
			player.teleport(new Location(player.getX(), player.getY() + 2, player.getZ()));
		}
	}
	
	/**
	 * Handles starting the game
	 */
	public static void startGame() {
		if (lobbyPlayers.size() < WeaponGameConstants.MINIMUM_PLAYERS) {
			messagePlayers("@dre@There were not enough players to start the game!", true);
			return;
		}
		if (started) {
			messagePlayers("@dre@There is currently a game in session.", true);
			return;
		}
		World.sendGlobalMessage("[ <col=0079AD>Weapon Game</col> ] Game has just begun!");
		WeaponGameConstants.METEOR_TIME = 120;
		WeaponGameConstants.CRATE_TIME = 90;
		WeaponGameConstants.GAME_TIME = 900;
		for (Player players : lobbyPlayers) {
			players.getSkill().resetCombatStats();
			players.getPrayer().disable();
			players.setWeaponKills(0);
			players.teleport(Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS));
			DialogueManager.sendInformationBox(players, "@dre@Weapon Game", "@dre@Objective: @bla@Be the first to reach 10 kills",  "@dre@Note: @bla@Each kill will upgrade your weapon",  "@dre@Hint: @bla@Click on scattered crates for supplies ",  "Good luck!");
			players.send(new SendPlayerOption("Attack", 3));
			lobbyPlayers.remove(players);
			gamePlayers.add(players);
			started = true;
			players.setController(ControllerManager.WEAPON_GAME_CONTROLLER);
			AchievementHandler.activateAchievement(players, AchievementList.PARTICIPATE_IN_WEAPON_GAME, 1);
			AchievementHandler.activateAchievement(players, AchievementList.PARTICIPATE_IN_WEAPON_GAME_10_TIMES, 1);
		}
	}
	
	public static void meteors() {
		messagePlayers("@dre@Beware meteors are inbound!", false);
		for (int i = 0; i < Utility.random(10); i++) {
			Location location = Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS);
			World.sendStillGraphic(659, 100, location);
			for (Player players : gamePlayers) {
				TaskQueue.queue(new Task(players, 3, false) {
					@Override
					public void execute() {
						stop();
					}

					@Override
					public void onStop() {
						if (players.getLocation() == location) {
							players.hit(new Hit(Utility.random(35)));
							players.send(new SendMessage("@dre@A meteor came crashing down on your head!"));
						}
					}
				});
			}
		}
		WeaponGameConstants.METEOR_TIME = 120;
	}
	
	/**
	 * Handles spawning crates
	 */
	public static void spawnCrates() {
		messagePlayers("@dre@A crate has spawned! Find it for some gear and supplies!", false);
		for (int index = 0; index < 5; index++) {
			Location location = Utility.randomElement(WeaponGameConstants.CRATE_LOCATIONS);
			GameObject object = new GameObject(2072, location, 10, 0);
			ObjectManager.register(object);
			crates.add(object);			
			World.sendStillGraphic(776, 10, location);
		}
		WeaponGameConstants.CRATE_TIME = 60;
	}
	
	/**
	 * Handles opening crate
	 * @param player
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void handleCrate(Player player, int x, int y, int z) {
		if (gamePlayers.contains(player)) {
			player.send(new SendMessage("You start searching the crate..."));
			player.getUpdateFlags().sendAnimation(new Animation(832));
			TaskQueue.queue(new Task(3) {
				@Override
				public void execute() {
					crateLoot(player, x, y, z);
					stop();
				}
				@Override
				public void onStop() {
				}
			});
		}		
	}
	
	/**
	 * Handles crate loot
	 * @param player
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void crateLoot(Player player, int x, int y, int z) {
		int random = Utility.random(5);
		GameObject object = ObjectManager.getGameObject(x, y, z);
		
		if (object == null) {
			return;
		}
		
		switch (random) {
		case 0:
		case 1:
		case 2:
		case 3:
			ObjectManager.remove(object);
			crates.remove(object);
			Item weapon = Utility.randomElement(WeaponGameConstants.CRATE_DATA);
			player.getInventory().addItems(weapon);
			player.send(new SendMessage("@dre@...You have found some " + weapon.getDefinition().getName() + " inside the chest!"));
			break;
			
		case 4:
			player.hit(new Hit(Utility.random(10)));
			player.send(new SendMessage("@dre@...While searching you cut your hand on a sharp object!"));
			ObjectManager.remove(object);
			crates.remove(object);
			break;
			
		case 5:
			player.teleport(Utility.randomElement(WeaponGameConstants.SPAWN_LOCATIONS));
			player.send(new SendMessage("@dre@...While searching you feel a mysterious force move you!"));
			ObjectManager.remove(object);
			crates.remove(object);
			break;
		}
	}

	/**
	 * Handles upgrading weapons
	 * @param player
	 */
	public static void upgrade(Player player) {
		if (player.getWeaponKills() >= 10) {
			leader = player;
			endGame(false);
			return;
		}
		if (!player.inWGGame() || !gamePlayers.contains(player)) {
			return;
		}
		for (int index = 0; index < WeaponGameConstants.TIER_DATA.length; index++) {
			if (player.getWeaponKills() != index) {
				continue;
			}
			Item weapon = Utility.randomElement(WeaponGameConstants.TIER_DATA[index]);
			player.getEquipment().getItems()[3] = weapon;
			player.send(new SendEquipment(3, weapon.getId(), weapon.getAmount()));
			player.setAppearanceUpdateRequired(true);
    		player.getCombat().reset();
    		player.getUpdateFlags().setUpdateRequired(true);
    		player.getEquipment().onLogin();
			DialogueManager.sendItem1(player, "You have advanced to the next tier!", weapon.getId());
		}
		for (Player players : gamePlayers) {
			if (player.getWeaponKills() > players.getWeaponKills()) {
				leader = player;
				players.send(new SendPlayerHint(true, player.getIndex()));
			}
		}
	}
	
	/**
	 * Handles ending game
	 * @param force
	 */
	public static void endGame(boolean force) {
		for (GameObject object : crates) {
			ObjectManager.remove(object);
		}
		crates.clear();
		if (force) {
			World.sendGlobalMessage("[ <col=0079AD>Weapon Game</col> ] Game has just ended with no victor!");
		} else {
			World.sendGlobalMessage("[ <col=0079AD>Weapon Game</col> ] Game has been won by <col=0079AD>" + leader.determineIcon(leader) + " " + leader.getUsername() + " </col>!");			
			for (Player players : gamePlayers) {
				leaveGame(players, true);			
			}
		}
		
		leader = null;
		started = false;
	}
	
	/**
	 * Handles leaving game
	 * @param player
	 */
	public static void leaveGame(Player player, boolean game) {
		player.getInventory().clear();
		player.getEquipment().clear();
		player.teleport(WeaponGameConstants.LOBBY_COODINATES);
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		player.send(new SendPlayerHint(true, -1));
		player.getSkill().restore();
		if (game) {
			if (leader !=  null) {
				if (leader == player) {
					player.getInventory().add(995, 500_000);
					player.setWeaponPoints(player.getWeaponPoints() + WeaponGameConstants.LEADER_REWARD);
					AchievementHandler.activateAchievement(leader, AchievementList.WIN_10_WEAPON_GAMES, 1);
					DialogueManager.sendStatement(leader, "Congratulations! You have won the Weapon Game and was rewarded.");	
				} else {
					if (player.getWeaponKills() < WeaponGameConstants.KILLS_FOR_REWARD) {
						DialogueManager.sendStatement(leader, "You needed a minimum of " + WeaponGameConstants.KILLS_FOR_REWARD + " kills to get a reward.");	
					} else {
						player.setWeaponPoints(player.getWeaponPoints() + WeaponGameConstants.PLAYER_REWARD);	
						DialogueManager.sendStatement(leader, "You lost! But was rewarded for your efforts!");	
					}
				}		
			}
		}
		if (gamePlayers.contains(player)) {
			gamePlayers.remove(player);			
		}
	}
	
	/**
	 * Gets amount of players in lobby
	 * @return
	 */
	public static int lobbyCount() {
		return lobbyPlayers.size();
	}
	
	/**
	 * Gets amount of players in game
	 * @return
	 */
	public static int gameCount() {
		return gamePlayers.size();
	}
	
	/**
	 * Sends messages to players in game or lobby
	 * @param message
	 * @param lobby
	 * @return
	 */
	public static String messagePlayers(String message, boolean lobby) {
		if (lobby) {
			for (Player player : lobbyPlayers) {
				player.send(new SendMessage(message));
			}			
		} else {
			for (Player player : gamePlayers) {
				player.send(new SendMessage(message));
			}		
		}
		return null;
	}
	
	/**
	 * Handles clicking on object
	 * @param player
	 * @param id
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static boolean objectClick(Player player, int id, int x, int y, int z) {
		switch (id) {
		case 2072:
			handleCrate(player, x, y, z);
			break;
		case 11005:
			if (!player.inWGLobby()) {
				joinLobby(player);				
			} else {
				leaveLobby(player, true);
			}
			break;
		}
		return false;
	}
	
	/**
	 * Game tick
	 */
	public static void tick() {
		if (lobbyCount() > 0) {
			WeaponGameConstants.LOBBY_TIME--;
			if (WeaponGameConstants.LOBBY_TIME == 0 || lobbyCount() == WeaponGameConstants.MAXIMUM_PLAYERS) {
				startGame();
				WeaponGameConstants.LOBBY_TIME = 180;
			}
		}
		if (started) {
			WeaponGameConstants.GAME_TIME--;
			if (WeaponGameConstants.GAME_TIME == 0 || gamePlayers.size() == 1 || gamePlayers.size() == 0) {
				endGame(true);
				return;
			}
			WeaponGameConstants.CRATE_TIME--;
			if (WeaponGameConstants.CRATE_TIME == 0) {
				spawnCrates();
			}
			WeaponGameConstants.METEOR_TIME--;
			if (WeaponGameConstants.METEOR_TIME == 0) {
				meteors();
			}
		}
	}

}
