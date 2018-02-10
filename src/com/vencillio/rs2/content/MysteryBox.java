package com.vencillio.rs2.content;

import java.util.Arrays;

import com.vencillio.core.util.Utility;
import com.vencillio.core.util.chance.Chance;
import com.vencillio.core.util.chance.WeightedChance;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles Mystery Box rewards
 * @author Daniel
 *
 */
public class MysteryBox {
	/**
	 * Mystery box Identification
	 */
	private final static Item MYSTERY_BOX = new Item(6199);
	
	/**
	 * All possible loots from Mystery Box
	 */
	public static Chance<Item> LOOTS = new Chance<Item>(Arrays.asList(
			//Common Items
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1079, 1)), //Rune platelegs
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1093, 1)), //Rune plateskirt
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1113, 1)), //Rune chainbody
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1127, 1)), //Rune platebody
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1147, 1)), //Rune med helm
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1163, 1)), //Rune full helm
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1185, 1)), //Rune sq shield
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1201, 1)), //Rune kiteshield
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1213, 1)), //Rune dagger
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4131, 1)), //Rune boots
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3476, 1)), //Rune plateskirt (g)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3477, 1)), //Rune plateskirt (t)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7336, 1)), //Rune shield (h1)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7342, 1)), //Rune shield (h2)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7348, 1)), //Rune shield (h3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7354, 1)), //Rune shield (h4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7360, 1)), //Rune shield (h5)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10286, 1)), //Rune helm (h1)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10288, 1)), //Rune helm (h2)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10290, 1)), //Rune helm (h3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10292, 1)), //Rune helm (h4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10294, 1)), //Rune helm (h5)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(560, 350)), //Death rune
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(565, 350)), //Blood rune
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(561, 500)), //Nature rune
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(892, 500)), //Rune arrow
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(868, 175)), //Rune knife
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2491, 1)), //Black d'hide vamb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2497, 1)), //Black d'hide chaps
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2503, 1)), //Black d'hide body
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12871, 1)), //Black dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12869, 1)), //Red dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12867, 1)), //Blue dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12865, 1)), //Green dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1725, 1)), //Amulet of strength
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1712, 1)), //Amulet of glory(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10602, 1)), //Mystic hat (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10601, 1)), //Mystic hat
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4091, 1)), //Mystic robe top
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4105, 1)), //Mystic gloves (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4103, 1)), //Mystic robe bottom (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4113, 1)), //Mystic robe bottom (light)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4111, 1)), //Mystic robe top (light)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4101, 1)), //Mystic robe top (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(392, 55)), //Manta ray
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11937, 25)), //Dark crab
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(158, 5)), //Super strength(3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2441, 5)), //Super strength(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2437, 5)), //Super attack(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2443, 5)), //Super defence(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(166, 5)), //Super defence(2)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3027, 5)), //Super restore(3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3025, 5)), //Super restore(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(13066, 1)), //Super potion set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6688, 5)), //Saradomin brew(3)
			
			//Uncommon Items
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1149, 1)), //Dragon med helm
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1187, 1)), //Dragon sq shield
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1215, 1)), //Dragon dagger
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1231, 1)), //Dragon dagger(p)
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1249, 1)), //Dragon spear
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1305, 1)), //Dragon longsword
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1377, 1)), //Dragon battleaxe
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1434, 1)), //Dragon mace
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1615, 1)), //Dragonstone
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1631, 1)), //Uncut dragonstone
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1645, 1)), //Dragonstone ring
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4087, 1)), //Dragon platelegs
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4585, 1)), //Dragon plateskirt
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(7158, 1)), //Dragon 2h sword
			
			//Rare Items
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4980, 1)), //Verac's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4986, 1)), //Verac's flail 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4998, 1)), //Verac's plateskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4992, 1)), //Verac's brassard 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4956, 1)), //Torag's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4962, 1)), //Torag's hammers 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4968, 1)), //Torag's platebody 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4974, 1)), //Torag's platelegs 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4932, 1)), //Karil's coif 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4938, 1)), //Karil's crossbow 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4944, 1)), //Karil's leathertop 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4950, 1)), //Karil's leatherskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4908, 1)), //Guthan's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4914, 1)), //Guthan's warspear 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4920, 1)), //Guthan's platebody 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4926, 1)), //Guthan's chainskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4860, 1)), //Ahrim's hood 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4866, 1)), //Ahrim's staff 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4872, 1)), //Ahrim's robetop 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4878, 1)), //Ahrim's robeskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4884, 1)), //Dharok's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4890, 1)), //Dharok's greataxe 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4896, 1)), //Dharok's platebody 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4902, 1)), //Dharok's platelegs 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11840, 1)), //Dragon boots
			
			//Very Rare Items
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6585, 1)), //Amulet of fury
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11283, 1)), //Dragonfire shield
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11335, 1)), //Dragon full helm
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(3140, 1)), //Dragon chainbody
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12829, 1)), //Spirit shield
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6731, 1)), //Seers ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6733, 1)), //Archers ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6735, 1)), //Warrior ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6737, 1)), //Berserker ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12922, 1)), //Tanzanite fang
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12932, 1)), //Magic fang
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6570, 1)), //Fire cape
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12795, 1)) //Steam battlestaff
	));
	
	/**
	 * Handles opening the Mystery Box
	 * @param player
	 */
	public static void open(Player player) {
		Item reward = LOOTS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(MYSTERY_BOX);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You have opened the and were rewarded with " + formatted_name + " ."));
		if (reward.getDefinition().getGeneralPrice() >= 500_000) {
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved " + formatted_name + " from a Mystery box!");
		}
	}

}
