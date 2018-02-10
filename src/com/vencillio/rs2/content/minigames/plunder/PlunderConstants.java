package com.vencillio.rs2.content.minigames.plunder;

import java.util.HashMap;

import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;

public class PlunderConstants {
	
	public enum DoorBitPosition {
		DOOR_1(26618, 9),
		DOOR_2(26619, 10),
		DOOR_3(26620, 11),
		DOOR_4(26621, 12);
		
		private static final HashMap<Integer, DoorBitPosition> door = new HashMap<>();
		
		public static void declare() {
			for (DoorBitPosition urn : values()) {
				door.put(urn.object, urn);
			}
		}

		private final int object;
		private final int bitPosition;
		
		private DoorBitPosition(int object, int bitPosition) {
			this.object = object;
			this.bitPosition = bitPosition;
		}
		
		public int getConfig() {
			return 1 << bitPosition;
		}
		
		public static DoorBitPosition get(int object) {
			return door.get(Integer.valueOf(object));
		}
	}
	
	public enum UrnBitPosition {
		URN_1(26610, 22, 23, 0),
	    URN_2(26601, 4, 5, 0),
	    URN_3(26606, 14, 15, 0),
	    URN_4(26611, 24, 25, 0),
	    URN_5(26607, 16, 17, 0),
	    URN_6(26612, 26, 27, 0),
	    URN_7(26603, 8, 9, 0),
	    URN_8(26613, 28, 29, 0),
	    URN_9(26608, 18, 19, 0),
	    URN_10(26580, 0, 1, 0),
	    URN_11(26604, 10, 11, 0),
	    URN_12(26609, 20, 21, 0),
	    URN_13(26600, 2, 3, 0),
	    URN_14(26605, 12, 13, 0),
	    URN_15(26602, 6, 7, 0);
		
		private final int object;
		private final int openedBitPosition;
		private final int snakedBitPosition;
		private final int charmedBitPosition;
		
		private static final HashMap<Integer, UrnBitPosition> urns = new HashMap<>();
		
		public static void declare() {
			for (UrnBitPosition urn : values()) {
				urns.put(urn.object, urn);
			}
		}
		
		private UrnBitPosition(int object, int openedBitPosition, int snakedBitPosition, int charmedBitPosition) {
			this.object = object;
			this.openedBitPosition = openedBitPosition;
			this.snakedBitPosition = snakedBitPosition;
			this.charmedBitPosition = charmedBitPosition;
		}

		public int getConfig(int type) {
			switch (type) {
			case 0:
				return 1 << openedBitPosition;
			case 1:
				return 1 << snakedBitPosition;
			case 2:
				return 1 << charmedBitPosition;
			default:
				return 0;
			}
		}
		
		public static UrnBitPosition get(int object) {
			return urns.get(Integer.valueOf(object));
		}
	}
	
	public static final String URNS_CONFIG_KEY = "URNS_CONFIG";
	public static final String DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY = "DOORS_CHEST_SARCOPHAGUS_CONFIG";
	
	public static final int URNS_CONFIG = 820;
	public static final int DOORS_CHEST_SARCOPHAGUS_CONFIG = 821;

	public static final Location FLOOR_1 = new Location(1927, 4477, 0);
	public static final Location FLOOR_2 = new Location(1954, 4477, 0);
	public static final Location FLOOR_3 = new Location(1925, 4452, 0);
	public static final Location FLOOR_4 = new Location(1965, 4443, 0);
	public static final Location FLOOR_5 = new Location(1927, 4453, 0);
	public static final Location FLOOR_6 = new Location(1941, 4422, 0);
	public static final Location FLOOR_7 = new Location(1974, 4420, 0);
	
	public static final Animation ATTEMPT_LOOT = new Animation(4340);
	public static final Animation UNSUCCESSFUL_LOOT = new Animation(4341);
	public static final Animation SUCCESSFUL_LOOT = new Animation(4342);
	
	public static final Animation SPEAR_TRAP = new Animation(2246);
	
	public static final Animation GOLD_CHEST_SEARCH = new Animation(4238);
	
	public static final Animation SARCOPHAGUS_SEARCH_START = new Animation(4344);
	public static final Animation SARCOPHAGUS_SEARCH_END = new Animation(4345);

	public static final int DOOR_ANIMATION = 4338;
	public static final int SPEAR_TRAP_ANIMATION = 459;

}