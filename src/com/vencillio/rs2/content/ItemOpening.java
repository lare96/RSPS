package com.vencillio.rs2.content;

import java.util.HashMap;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles opening items
 * 
 * @author Daniel
 */
public enum ItemOpening {
	
	/* Regular Armour Sets */
	BRONZE_ARMOUR_LG(12960, new Item(1155), new Item(1117), new Item(1075), new Item(1189)),
	BRONZE_ARMOUR_SK(12962, new Item(1155), new Item(1117), new Item(1087), new Item(1189)),
	IRON_ARMOUR_LG(12972, new Item(1153), new Item(1115), new Item(1067), new Item(1191)),
	IRON_ARMOUR_SK(12974, new Item(1153), new Item(1115), new Item(1081), new Item(1191)),
	STEEL_ARMOUR_LG(12984, new Item(1157), new Item(1119), new Item(1069), new Item(1193)),
	STEEL_ARMOUR_SK(12986, new Item(1157), new Item(1119), new Item(1083), new Item(1193)),
	BLACK_ARMOUR_LG(12988, new Item(1165), new Item(1125), new Item(1077), new Item(1195)),
	BLACK_ARMOUR_SK(12990, new Item(1165), new Item(1125), new Item(1089), new Item(1195)),
	MITHRIL_ARMOUR_LG(13000, new Item(1159), new Item(1121), new Item(1071), new Item(1197)),
	MITHRIL_ARMOUR_SK(13002, new Item(1159), new Item(1121), new Item(1085), new Item(1197)),
	ADAMANT_ARMOUR_LG(13012, new Item(1161), new Item(1123), new Item(1073), new Item(1199)),
	ADAMANT_ARMOUR_SK(13014, new Item(1161), new Item(1123), new Item(1091), new Item(1199)),
	RUNE_ARMOUR_LG(13024, new Item(1163), new Item(1127), new Item(1079), new Item(1201)),
	RUNE_ARMOUR_SK(13026, new Item(1163), new Item(1127), new Item(1093), new Item(1201)),
	PROSELYTE_M(9666, new Item(9672), new Item(9674), new Item(9676)),
	PROSELYTE_F(9670, new Item(9672), new Item(9674), new Item(9678)),
	GREEN_DHIDE(12865, new Item(1065), new Item(1099), new Item(1135)),
	BLUE_DHIDE(12867, new Item(2487), new Item(2493), new Item(2499)),
	RED_DHIDE(12869, new Item(2489), new Item(2495), new Item(2501)),
	BLACK_DHIDE(12871, new Item(2491), new Item(2497), new Item(2503)),
	
	/* Trimmed Armour sets */
	BRONZE_TRIMMED_LG(12964, new Item(12215), new Item(12217), new Item(12221), new Item(12223)),
	BRONZE_TRIMMED_SK(12966, new Item(12215), new Item(12219), new Item(12221), new Item(12223)),
	BRONZE_GOLD_TRIMMED_LG(12968, new Item(12205), new Item(12207), new Item(12211), new Item(12213)),
	BRONZE_GOLD_TRIMMED_SK(12970, new Item(12205), new Item(12209), new Item(12211), new Item(12213)),
	IRON_TRIMMED_LG(12976, new Item(12225), new Item(12227), new Item(12231), new Item(12233)),
	IRON_TRIMMED_SK(12978, new Item(12225), new Item(12229), new Item(12231), new Item(12233)),
	IRON_GOLD_TRIMMED_LG(12980, new Item(12235), new Item(12237), new Item(12241), new Item(12243)),
	IRON_GOLD_TRIMMED_SK(12982, new Item(12235), new Item(12239), new Item(12241), new Item(12243)),
	BLACK_TRIMMED_LG(12992, new Item(2583), new Item(2585), new Item(2587), new Item(2589)),
	BLACK_TRIMMED_SK(12994, new Item(2583), new Item(3472), new Item(2587), new Item(2589)),
	BLACK_GOLD_TRIMMED_LG(12996, new Item(2591), new Item(2593), new Item(2595), new Item(2597)),
	BLACK_GOLD_TRIMMED_SK(12998, new Item(2591), new Item(3473), new Item(2595), new Item(2597)),
	MITRHIL_TRIMMED_LG(13004, new Item(12287), new Item(12289), new Item(12291), new Item(12293)),
	MITRHIL_TRIMMED_SK(13006, new Item(12287), new Item(12295), new Item(12291), new Item(12293)),
	MITRHIL_GOLD_TRIMMED_LG(13008, new Item(12277), new Item(12279), new Item(12281), new Item(12283)),
	MITRHIL_GOLD_TRIMMED_SK(13010, new Item(12277), new Item(12285), new Item(12281), new Item(12283)),
	ADAMANT_TRIMMED_LG(13016, new Item(2259), new Item(2601), new Item(2603), new Item(2605)),
	ADAMANT_TRIMMED_SK(13018, new Item(2259), new Item(3474), new Item(2603), new Item(2605)),
	ADAMANT_GOLD_TRIMMED_LG(13020, new Item(2607), new Item(2609), new Item(2611), new Item(2613)),
	ADAMANT_GOLD_TRIMMED_SK(13022, new Item(2607), new Item(3475), new Item(2611), new Item(2613)),
	RUNE_TRIMMED_LG(13028, new Item(2623), new Item(2625), new Item(2627), new Item(2629)),
	RUNE_TRIMMED_SK(13030, new Item(2623), new Item(3477), new Item(2627), new Item(2629)),
	RUNE_GOLD_TRIMMED_LG(13032, new Item(2615), new Item(2617), new Item(2619), new Item(2621)),
	RUNE_GOLD_TRIMMED_SK(13034, new Item(2615), new Item(3476), new Item(2619), new Item(2621)),
	GILDED_ARMOUR_LG(13036, new Item(3481), new Item(3483), new Item(3486), new Item(3488)),
	GILDED_ARMOUR_SK(13038, new Item(3481), new Item(3485), new Item(3486), new Item(3488)),	
	
	/* Packs */
	EMPTY_VIAL_PACK(11877, new Item(230, 100)),
	WATER_FILLED_VIAL_PACK(11879, new Item(228, 100)),
	FEATHER_PACK(11881, new Item(314, 100)),
	BAIT_PACK(11883, new Item(313, 100)),
	SOFT_CLAY_PACK(12009, new Item(1762, 100)),
	BROAD_ARROWHEAD_PACK(11885, new Item(11874, 100)),
	AIR_RUNE_PACK(12728, new Item(556, 100)),
	WATER_RUNE_PACK(12730, new Item(555, 100)),
	EARTH_RUNE_PACK(12732, new Item(557, 100)),
	FIRE_RUNE_PACK(12734, new Item(554, 100)),
	MIND_RUNE_PACK(12736, new Item(558, 100)),
	CHAOS_RUNE_PACK(12738, new Item(562, 100)),
	BIRD_SNARE_PACK(12740, new Item(10007, 100)),
	BOX_SNARE_PACK(12742, new Item(10009, 100)),
	MAGIC_BOX_PACK(12744, new Item(10026, 100)),
	EYE_OF_NEW_PACK(12859, new Item(222, 100)),
	
	/* Achievement Armour */
	ARMOUR_SET_1(6828, new Item(13137, 1), new Item(13104, 1), new Item(13112, 1), new Item(13117, 1), new Item(13108, 1), new Item(13121, 1), new Item(13129, 1)),
	ARMOUR_SET_2(6829, new Item(13138, 1), new Item(13105, 1), new Item(13113, 1), new Item(13118, 1), new Item(13109, 1), new Item(13122, 1) ,new Item(13130, 1)),
	ARMOUR_SET_3(6830, new Item(13139, 1), new Item(13106, 1), new Item(13114, 1), new Item(13119, 1), new Item(13110, 1), new Item(13123, 1) ,new Item(13131, 1)),
	ARMOUR_SET_4(6831, new Item(13140, 1), new Item(13107, 1), new Item(13115, 1), new Item(13120, 1), new Item(13111, 1), new Item(13124, 1) ,new Item(13132, 1)),
	
	/* Magic Box */
	MAGIC_BOX(10025, new Item(554, 250), new Item(555, 250), new Item(556, 250), new Item(557, 250), new Item(558, 250), new Item(563, 50), new Item(561, 30), new Item(562, 100)),

	/* Herb Box */
	HERBLORE_BOX(11738, new Item(200, 25), new Item(202, 25), new Item(204, 25), new Item(206, 25), new Item(208, 10), new Item(210, 25), new Item(212, 20), new Item(214, 10), new Item(216, 10), new Item(218, 10), new Item(220, 10));
	
	private final int itemID;
	private final Item items[];

	private ItemOpening(int itemID, Item...items) {
		this.itemID = itemID;
		this.items = items;
	}

	private static final HashMap<Integer, ItemOpening> PACKS = new HashMap<Integer, ItemOpening>();

	static {
		for (final ItemOpening packs : ItemOpening.values()) {
			ItemOpening.PACKS.put(packs.itemID, packs);
		}
	}

	/**
	 * Handles opening the armour set
	 * 
	 * @param player
	 * @param item
	 * @return
	 */
	public static boolean openSet(Player player, int item) {
		//Get the item
		ItemOpening data = ItemOpening.PACKS.get(item);

		//If item is nulled; return
		if (data == null) {
			return false;
		}

		//Return is player does not have enough inventory space 
		if (player.getInventory().getFreeSlots() <= data.items.length) {
			player.send(new SendMessage("You need at least " + data.items.length + " available inventory spaces to do this!"));
			return false;
		}

		//Remove the item set from inventory
		player.getInventory().remove(data.itemID);

		//Add all the item pieces to inventory
		player.getInventory().addItems(data.items);			
		
		//Send message of successful opening
		player.send(new SendMessage("You successfully open the " + GameDefinitionLoader.getItemDef(data.itemID).getName() + "."));
		
		//Send the achievement
		AchievementHandler.activateAchievement(player, AchievementList.OPEN_50_ITEM_SETS, 1);
		
		return true;
	}
}