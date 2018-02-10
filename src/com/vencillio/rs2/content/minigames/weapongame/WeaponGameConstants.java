package com.vencillio.rs2.content.minigames.weapongame;

import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;

/**
 * Holds the constants for Weapon Game Minigame
 * @author Daniel
 *
 */
public class WeaponGameConstants {
	
	/**
	 * The lobby coordinates
	 */
	public static final Location LOBBY_COODINATES = new Location(1864, 5329, 0);
	
	/**
	 * Minimum amount of players required to start a game
	 */
	public static final int MINIMUM_PLAYERS = 5;
	
	/**
	 * The maximum amount of players that can participate in the game
	 */
	public static final int MAXIMUM_PLAYERS = 20;

	/**
	 * The combat level required to play this game
	 */
	public static final int COMBAT_REQUIRED = 75;
	
	public static final int KILLS_FOR_REWARD = 3;
	
	public static final int PLAYER_REWARD = 1_000;
	
	public static final int LEADER_REWARD = 1_500;
	
	/**
	 * Holds the lobby time
	 */
	public static int LOBBY_TIME = 180;
	
	/**
	 * Holds the game time
	 */
	public static int GAME_TIME = 900;
	
	/**
	 * Holds the crate time
	 */
	public static int CRATE_TIME = 90;
	
	/**
	 * Holds the meteor time
	 */
	public static int METEOR_TIME = 120;
	
	/**
	 * Holds the weapon tier data
	 */
	public static Item[][] TIER_DATA = { 
		{ new Item(1217) }, //TIER 1
		{ new Item(1209) }, //TIER 2
		{ new Item(1211) }, //TIER 3
		{ new Item(1213) }, //TIER 4
		{ new Item(1215) }, //TIER 5
		{ new Item(4151) }, //TIER 6
		{ new Item(11838), new Item(12809) }, //TIER 7
		{ new Item(11806), new Item(11808), new Item(11804) }, //TIER 8
		{ new Item(12006), }, //TIER 9
		{ new Item(11802) }, //TIER 10
	};
	
	/**
	 * Holds the respawn locations for game
	 */
	public static Location[] SPAWN_LOCATIONS = { new Location (2139, 5103, 0), new Location (2139, 5095, 0), new Location (2140, 5093, 0), new Location (2144, 5095, 0), new Location (2143, 5101, 0), new Location (2150, 5104, 0), new Location (2149, 5099, 0), new Location (2148, 5093, 0), new Location (2155, 5095, 0), new Location (2163, 5093, 0), new Location (2162, 5096, 0), new Location (2161, 5099, 0), new Location (2162, 5103, 0), new Location (2160, 5105, 0), new Location (2157, 5099, 0), new Location (2153, 5101, 0), new Location (2150, 5099, 0), new Location (2148, 5094, 0), new Location (2141, 5092, 0), new Location (2140, 5094, 0) };

	/**
	 * Holds the item founds inside the crate
	 */
	public static Item[] CRATE_DATA = { new Item(1183, 1), new Item(1123, 1), new Item(1073, 1), new Item(4730, 1), new Item(4732, 1), new Item(4757, 1), new Item(4759, 1), new Item(1434, 1), new Item(6585, 1), new Item(7461, 1), new Item(13078, 1), new Item(10828, 1), new Item(11836, 1), new Item(11840, 1), new Item(1079, 1), new Item(391, 5), new Item(1725, 1), new Item(6570, 1), new Item(7462, 1), new Item(6737, 1), new Item(10551, 1), new Item(8850, 1), new Item(2436, 1), new Item(2440, 1), new Item(2442, 1), new Item(3024, 1), new Item(6685, 1), new Item(2550, 1), new Item(1149, 1), new Item(4585, 1), new Item(4087, 1), new Item(1113, 1), new Item(1093, 1), new Item(10548, 1), new Item(4751, 1), new Item(4722, 1), new Item(4724, 1) };
	
	/**
	 * Holds the crate spawn location
	 */
	public static Location[] CRATE_LOCATIONS =  { new Location (2138, 5099, 0), new Location (2138, 5103, 0), new Location (2138, 5094, 0), new Location (2144, 5096, 0), new Location (2149, 5091, 0), new Location (2152, 5106, 0), new Location (2160, 5106, 0), new Location (2160, 5101, 0), new Location (2156, 5098, 0), new Location (2161, 5091, 0) };

	
}
