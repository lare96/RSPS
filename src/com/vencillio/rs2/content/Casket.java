package com.vencillio.rs2.content;

import com.vencillio.core.util.Utility;
import com.vencillio.core.util.chance.Chance;
import com.vencillio.core.util.chance.WeightedChance;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.util.Arrays;

/**
 * Created by Tanner on 6/16/2018.
 */
public class Casket {

	private final static int SKILLING_CASKET = 2726;
	private final static int PVM_CASKET = 2728;
	private final static int SLAYER_CASKET = 2730;
	private final static int RARES_CASKET = 2732;

	public static Chance<Item> RARE_LOOTS = new Chance<Item>(Arrays.asList(
			//Common Items
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2513, 1)), //Dragon chainbody
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7462, 1)), //Barrows gloves
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6731, 1)), //Seers ring
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6733, 1)), //Archers ring
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6735, 1)), //Warrior ring
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6737, 1)), //Berserker ring
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6918, 1)), //Infinity hat
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6916, 1)), //Infinity top
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6924, 1)), //Infinity bottom
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6922, 1)), //Infinity gloves
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6920, 1)), //Infinity boots
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7158, 1)), //D2h
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11920, 1)), //Dragon pick
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6739, 1)), //Dragon axe
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6889, 1)), //Mage's book
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12934, 2500)), //Zulrah scales
			//Uncommon Items
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4708, 1)), //Ahrim's hood
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4710, 1)), //Ahrim's staff
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4712, 1)), //Ahrim's robetop
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4714, 1)), //Ahrim's robeskirt
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4724, 1)), //Guthan's helm
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4726, 1)), //Guthan's warspear
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4728, 1)), //Guthan's platebody
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4730, 1)), //Guthan's chainskirt
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4745, 1)), //Torag's helm
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4747, 1)), //Torag's hammers
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4749, 1)), //Torag's platebody
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4751, 1)), //Torag's platelegs
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4732, 1)), //Karil's coif
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4734, 1)), //Karil's crossbow
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4736, 1)), //Karil's leathertop
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4738, 1)), //Karil's leatherskirt
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4753, 1)), //Verac's helm
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4755, 1)), //Verac's flail
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4757, 1)), //Verac's brassard
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4759, 1)), //Verac's plateskirt
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4716, 1)), //Dharok's helm
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4718, 1)), //Dharok's greataxe
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4720, 1)), //Dharok's platebody
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4722, 1)), //Dharok's platelegs
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11840, 1)), //Dragon boots
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11836, 1)), //Bandos boots
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11818, 1)), //GS Shard 1
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11820, 1)), //GS Shard 2
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11822, 1)), //GS Shard 3
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11787, 1)), //Steam battlestaff
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(10833, 1)), //Tax bag
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(13116, 1)), //Bonecrusher
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12798, 1)), //Steam staff upgrade kit
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12800, 1)), //Dragon pick upgrade kit
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12802, 1)), //Ward upgrade kit


			//Rare Items
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11810, 1)), //Armadyl hilt
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11812, 1)), //Bandos hilt
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11814, 1)), //Sara hilt
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11816, 1)), //Zammy hilt
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11838, 1)), //Sara sword
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11824, 1)), //Zammy spear
			new WeightedChance<Item>(WeightedChance.RARE, new Item(10887, 1)), //Barrelchest anchor
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12002, 1)), //Occult necklace
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12922, 1)), //Tanzanite fang
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12932, 1)), //Magic fang
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12804, 1)), //Saradomin's tear
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1038, 1)), //Red partyhat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1040, 1)), //Yellow partyhat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1042, 1)), //Blue partyhat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1044, 1)), //Green partyhat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1046, 1)), //Purple partyhat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1048, 1)), //White partyhat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1053, 1)), //Green h mask
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1055, 1)), //Blue h mask
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1057, 1)), //Red h mask

			//Very Rare Items
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11832, 1)), //Bandos chestplate
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11834, 1)), //Bandos tassets
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11826, 1)), //Armadyl helm
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12470, 1)), //Armadyl platebody
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12472, 1)), //Armadyl platelegs
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11785, 1)), //Armadyl crossbow
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(13188, 1)), //D claws
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12819, 1)), //Elysian sigil
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12823, 1)), //Spectral sigil
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12827, 1)), //Arcane sigil
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2577, 1)), //Ranger boots
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10350, 1)), //3rd age helm
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10348, 1)), //3rd age platebody
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10346, 1)), //3rd age platelegs
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10352, 1)), //3rd age kiteshield
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10342, 1)), //3rd age mage hat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10338, 1)), //3rd age robe top
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10340, 1)), //3rd age robe bottom
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10334, 1)), //3rd age coif
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10330, 1)), //3rd age range top
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10332, 1)), //3rd age range legs
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(10336, 1)), //3rd age vambraces
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12424, 1)), //3rd age bow
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(13195, 1)) //200 credit voucher


	));

	public static Chance<Item> SKILLING_LOOTS = new Chance<Item>(Arrays.asList(
			//Common Items
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2359, 500)), //Mith bar
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2361, 500)), //Addy bar
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2363, 500)), //Rune bar
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1515, 500)), //Yew log
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1513, 500)), //Magic log
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1745, 500)), //green leather
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2595, 500)), //blue leather
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2507, 500)), //red leather
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2509, 500)), //black leather
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(257, 500)), //Grimy ranarr
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3051, 500)), //Grimy snapdragon
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(371, 500)), //Raw swordfish
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(389, 500)), //Raw manta
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(383, 500)), //Raw shark
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11934, 500)), //Raw dark crab
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11849, 75)), //mark of grace
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5304, 250)), //torstol seeds
			//Uncommon Items
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2359, 1000)), //Mith bar
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2361, 1000)), //Addy bar
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2363, 1000)), //Rune bar
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1515, 1000)), //Yew log
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1513, 1000)), //Magic log
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1745, 1000)), //green leather
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2595, 1000)), //blue leather
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2507, 1000)), //red leather
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2509, 1000)), //black leather
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(257, 1000)), //Grimy ranarr
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(3051, 1000)), //Grimy snapdragon
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(371, 1000)), //Raw swordfish
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(389, 1000)), //Raw manta
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(383, 1000)), //Raw shark
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11934, 1000)), //Raw dark crab
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11849, 125)), //mark of grace
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5304, 500)), //torstol seeds


			//Rare Items
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2359, 1500)), //Mith bar
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2361, 1500)), //Addy bar
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2363, 1500)), //Rune bar
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1515, 1500)), //Yew log
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1513, 1500)), //Magic log
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1745, 1500)), //green leather
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2595, 1500)), //blue leather
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2507, 1500)), //red leather
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2509, 1500)), //black leather
			new WeightedChance<Item>(WeightedChance.RARE, new Item(257, 1500)), //Grimy ranarr
			new WeightedChance<Item>(WeightedChance.RARE, new Item(3051, 1500)), //Grimy snapdragon
			new WeightedChance<Item>(WeightedChance.RARE, new Item(371, 1500)), //Raw swordfish
			new WeightedChance<Item>(WeightedChance.RARE, new Item(389, 1500)), //Raw manta
			new WeightedChance<Item>(WeightedChance.RARE, new Item(383, 1500)), //Raw shark
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11934, 1500)), //Raw dark crab
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11849, 175)), //mark of grace
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5304, 750)), //torstol seeds

			//Very Rare Items
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2359, 2500)), //Mith bar
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2361, 2500)), //Addy bar
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2363, 2500)), //Rune bar
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1515, 2500)), //Yew log
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1513, 2500)), //Magic log
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1745, 2500)), //green leather
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2595, 2500)), //blue leather
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2507, 2500)), //red leather
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2509, 2500)), //black leather
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(257, 2500)), //Grimy ranarr
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(3051, 2500)), //Grimy snapdragon
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(371, 2500)), //Raw swordfish
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(389, 2500)), //Raw manta
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(383, 2500)), //Raw shark
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11934, 2500)), //Raw dark crab
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(5304, 1000)), //torstol seeds
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11920, 1)), //d pick
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6739, 1)) //d axe


	));

	public static Chance<Item> PVM_LOOTS = new Chance<Item>(Arrays.asList(
			//Common Items
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(362, 250)), //Tuna
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(380, 250)), //Lobster
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(374, 250)), //Swordfish
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(392, 250)), //Mantas
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(386, 250)), //Shark
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7061, 250)), //Tuna potato
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2441, 150)), //Super str
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2437, 150)), //Super att
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2443, 150)), //Super def
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3025, 75)), //Super restore
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2453, 75)), //Antifires
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(892, 500)), //rune arrows
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(9144, 300)), //rune bolts
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11726, 150)), //Super mage
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11722, 150)), //Super range

			//Uncommon Items
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(362, 500)), //Tuna
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(380, 500)), //Lobster
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(374, 500)), //Swordfish
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(392, 500)), //Mantas
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(386, 500)), //Shark
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(7061, 500)), //Tuna potato
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2441, 250)), //Super str
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2437, 250)), //Super att
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2443, 250)), //Super def
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(3025, 125)), //Super restore
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2453, 125)), //Antifires
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(892, 1000)), //rune arrows
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(9144, 750)), //rune bolts
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11726, 250)), //Super mage
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11722, 250)), //Super range


			//Rare Items
			new WeightedChance<Item>(WeightedChance.RARE, new Item(362, 1500)), //Tuna
			new WeightedChance<Item>(WeightedChance.RARE, new Item(380, 1500)), //Lobster
			new WeightedChance<Item>(WeightedChance.RARE, new Item(374, 1500)), //Swordfish
			new WeightedChance<Item>(WeightedChance.RARE, new Item(392, 1500)), //Mantas
			new WeightedChance<Item>(WeightedChance.RARE, new Item(386, 1500)), //Shark
			new WeightedChance<Item>(WeightedChance.RARE, new Item(7061, 1500)), //Tuna potato
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2441, 300)), //Super str
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2437, 300)), //Super att
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2443, 300)), //Super def
			new WeightedChance<Item>(WeightedChance.RARE, new Item(3025, 175)), //Super restore
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2453, 175)), //Antifires
			new WeightedChance<Item>(WeightedChance.RARE, new Item(892, 1500)), //rune arrows
			new WeightedChance<Item>(WeightedChance.RARE, new Item(9144, 1000)), //rune bolts
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11726, 300)), //Super mage
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11722, 300)), //Super range

			//Very Rare Items
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(362, 2500)), //Tuna
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(380, 2500)), //Lobster
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(374, 2500)), //Swordfish
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(392, 2500)), //Mantas
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(386, 2500)), //Shark
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(7061, 2500)), //Tuna potato
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2441, 400)), //Super str
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2437, 400)), //Super att
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2443, 400)), //Super def
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(3025, 225)), //Super restore
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(2453, 200)), //Antifires
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(892, 5000)), //rune arrows
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11726, 400)), //Super mage
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11722, 400)) //Super range


	));


	public static void open(Player player, int casket) {
		switch(casket) {
			case SKILLING_CASKET:
			case PVM_CASKET:
			case RARES_CASKET:
				Item reward = casket == SKILLING_CASKET ? SKILLING_LOOTS.nextObject().get() : casket == PVM_CASKET ? PVM_LOOTS.nextObject().get() : RARE_LOOTS.nextObject().get();
				String name = reward.getDefinition().getName();
				String formatted_name = Utility.getAOrAn(name) + " " + name;
				player.getInventory().remove(casket);
				if(reward.getDefinition().getId() != reward.getDefinition().getNoteId())
					player.getInventory().add(new Item(reward.getDefinition().getNoteId()));
				else
					player.getInventory().add(reward);
				player.send(new SendMessage("You have opened the casket and were rewarded with " + reward.getAmount() + "x " + formatted_name + " ."));
				if (reward.getDefinition().getGeneralPrice() >= 500_000) {
					World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved " + formatted_name + " from a Mystery box!");
				}
				break;
			case SLAYER_CASKET:
				int randPoints = Utility.randomNumber(675) + 75;
				player.getSlayer().addSlayerExperience(randPoints*100);
				player.addSlayerPoints(randPoints);
				player.getInventory().remove(casket);
				player.send(new SendMessage("You have opened the and were rewarded with " + randPoints + " slayer points."));
				break;
		}

	}
}
