package com.vencillio.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vencillio.tools.ChexDropParser.ItemDrop.Rarity;

public class ChexDropParser {

	public static void main(String[] args) throws IOException {
		int npcId = 6619;
		
		List<ItemDrop> drops = new ArrayList<>();
		
		
		
		drops.add(new ItemDrop(526, 1, 1, Rarity.ALWAYS));
		drops.add(new ItemDrop(4697, 30, 30, Rarity.COMMON)); //Smoke rune
		drops.add(new ItemDrop(995, 600, 4000, Rarity.COMMON)); //Coins
		drops.add(new ItemDrop(1624, 4, 4, Rarity.COMMON)); //Uncut sapphire
		drops.add(new ItemDrop(1622, 6, 6, Rarity.COMMON)); //Uncut emerald
		drops.add(new ItemDrop(2298, 8, 8, Rarity.COMMON)); //Anchovy pizza
		drops.add(new ItemDrop(7946, 1, 1, Rarity.COMMON)); //Monkfish
		drops.add(new ItemDrop(7946, 1, 1, Rarity.COMMON)); //Monkfish
		drops.add(new ItemDrop(7946, 1, 1, Rarity.COMMON)); //Monkfish
		drops.add(new ItemDrop(385, 1, 1, Rarity.COMMON)); //Shark
		drops.add(new ItemDrop(1452, 1, 1, Rarity.COMMON)); //Chaos talisman
		drops.add(new ItemDrop(1462, 1, 1, Rarity.COMMON)); //Nature talisman
		drops.add(new ItemDrop(7937, 250, 250, Rarity.COMMON)); //Pure essence
		drops.add(new ItemDrop(2434, 1, 1, Rarity.COMMON)); //Prayer potion(4)
		drops.add(new ItemDrop(246, 10, 10, Rarity.COMMON)); //Wine of zamorak

		
		
		drops.add(new ItemDrop(995, 3000, 3000, Rarity.COMMON));//Coins
		drops.add(new ItemDrop(1623, 1, 1, Rarity.COMMON));//Uncut sapphire
		drops.add(new ItemDrop(1619, 1, 1, Rarity.UNCOMMON));//Uncut ruby
		drops.add(new ItemDrop(1621, 1, 1, Rarity.UNCOMMON));//Uncut emerald
		drops.add(new ItemDrop(1462, 1, 1, Rarity.UNCOMMON));//Nature talisman
		drops.add(new ItemDrop(1452, 1, 1, Rarity.UNCOMMON));//Chaos talisman
		drops.add(new ItemDrop(561, 67, 67, Rarity.UNCOMMON));//Nature rune
		drops.add(new ItemDrop(2363, 1, 1, Rarity.UNCOMMON));//Runite bar
		drops.add(new ItemDrop(1247, 1, 1, Rarity.RARE));//Rune spear
		drops.add(new ItemDrop(1319, 1, 1, Rarity.RARE));//Rune 2h sword
		drops.add(new ItemDrop(830, 5, 5, Rarity.RARE));//Rune javelin
		drops.add(new ItemDrop(1201, 1, 1, Rarity.RARE));//Rune kiteshield
		drops.add(new ItemDrop(892, 42, 42, Rarity.RARE));//Rune arrow
		drops.add(new ItemDrop(1373, 1, 1, Rarity.RARE));//Rune battleaxe
		drops.add(new ItemDrop(1617, 1, 1, Rarity.RARE));//Uncut diamond
		drops.add(new ItemDrop(443, 100, 100, Rarity.RARE));//Silver ore
		drops.add(new ItemDrop(829, 20, 20, Rarity.RARE));//Adamant javelin
		drops.add(new ItemDrop(1185, 1, 1, Rarity.RARE));//Rune sq shield
		drops.add(new ItemDrop(886, 150, 150, Rarity.RARE));//Steel arrow
		drops.add(new ItemDrop(563, 45, 45, Rarity.RARE));//Law rune
		drops.add(new ItemDrop(560, 45, 45, Rarity.RARE));//Death rune
		drops.add(new ItemDrop(1615, 1, 1, Rarity.RARE));//Dragonstone
		drops.add(new ItemDrop(1149, 1, 1, Rarity.RARE));//Dragon med helm
		drops.add(new ItemDrop(1249, 1, 1, Rarity.RARE));//Dragon spear
		drops.add(new ItemDrop(2366, 1, 1, Rarity.RARE));//Shield left half
		drops.add(new ItemDrop(2368, 1, 1, Rarity.RARE));//Shield right half
		drops.add(new ItemDrop(1033, 1, 1, Rarity.UNCOMMON)); //Zamorak robe
		drops.add(new ItemDrop(1035, 1, 1, Rarity.UNCOMMON)); //Zamorak robe
		drops.add(new ItemDrop(1391, 1, 1, Rarity.UNCOMMON)); //Battlestaff
		drops.add(new ItemDrop(2570, 1, 1, Rarity.UNCOMMON)); //Ring of life
		drops.add(new ItemDrop(3387, 1, 1, Rarity.UNCOMMON)); //Splitbark body
		drops.add(new ItemDrop(3389, 1, 1, Rarity.UNCOMMON)); //Splitbark legs
		drops.add(new ItemDrop(554, 250, 250, Rarity.UNCOMMON)); //Fire rune
		drops.add(new ItemDrop(562, 175, 175, Rarity.UNCOMMON)); //Chaos rune
		drops.add(new ItemDrop(565, 50, 50, Rarity.UNCOMMON)); //Blood rune
		drops.add(new ItemDrop(2482, 4, 4, Rarity.UNCOMMON)); //Lantadyme
		drops.add(new ItemDrop(993, 1, 1, Rarity.UNCOMMON)); //Sinister key
		drops.add(new ItemDrop(12746, 1, 1, Rarity.UNCOMMON)); //Mysterious emblem
		drops.add(new ItemDrop(4675, 1, 1, Rarity.RARE)); //Ancient staff
		drops.add(new ItemDrop(11928, 1, 1, Rarity.RARE)); //Odium shard 1
		drops.add(new ItemDrop(11931, 1, 1, Rarity.RARE)); //Malediction shard 1
		drops.add(new ItemDrop(11995, 1, 1, Rarity.RARE)); //Pet chaos elemental

		

		List<ItemDrop> always = drops.stream().filter(item -> item.rarity == Rarity.ALWAYS).collect(Collectors.toList());
		List<ItemDrop> common = drops.stream().filter(item -> item.rarity == Rarity.COMMON).collect(Collectors.toList());
		List<ItemDrop> uncommon = drops.stream().filter(item -> item.rarity == Rarity.UNCOMMON).collect(Collectors.toList());
		List<ItemDrop> rare = drops.stream().filter(item -> item.rarity == Rarity.RARE).collect(Collectors.toList());

		System.out.println("	<ItemDropDefinition>");
		System.out.println("		<id>" + npcId + "</id>");
		if (always.isEmpty()) {
			System.out.println("		<constant>null</constant>");
		} else {
			System.out.println("		<constant>");
			System.out.println("			<scrolls>null</scrolls>");
			System.out.println("			<charms>null</charms>");
			System.out.println("			<drops>");
			for (ItemDrop drop : always) {
				System.out.println("				<itemDrop>");
				System.out.println("					<id>" + drop.itemId + "</id>");
				System.out.println("					<min>" + drop.min + "</min>");
				System.out.println("					<max>" + drop.max + "</max>");
				System.out.println("				</itemDrop>");
			}
			System.out.println("			</drops>");
			System.out.println("		</constant>");
		}
		if (common.isEmpty()) {
			System.out.println("		<common>null</common>");
		} else {
			System.out.println("		<common>");
			System.out.println("			<scrolls>null</scrolls>");
			System.out.println("			<charms>null</charms>");
			System.out.println("			<drops>");
			for (ItemDrop drop : common) {
				System.out.println("				<itemDrop>");
				System.out.println("					<id>" + drop.itemId + "</id>");
				System.out.println("					<min>" + drop.min + "</min>");
				System.out.println("					<max>" + drop.max + "</max>");
				System.out.println("				</itemDrop>");
			}
			System.out.println("			</drops>");
			System.out.println("		</common>");
		}
		if (uncommon.isEmpty()) {
			System.out.println("		<uncommon>null</uncommon>");
		} else {
			System.out.println("		<uncommon>");
			System.out.println("			<scrolls>null</scrolls>");
			System.out.println("			<charms>null</charms>");
			System.out.println("			<drops>");
			for (ItemDrop drop : uncommon) {
				System.out.println("				<itemDrop>");
				System.out.println("					<id>" + drop.itemId + "</id>");
				System.out.println("					<min>" + drop.min + "</min>");
				System.out.println("					<max>" + drop.max + "</max>");
				System.out.println("				</itemDrop>");
			}
			System.out.println("			</drops>");
			System.out.println("		</uncommon>");
		}
		if (rare.isEmpty()) {
			System.out.println("		<rare>null</rare>");
		} else {
			System.out.println("		<rare>");
			System.out.println("			<scrolls>null</scrolls>");
			System.out.println("			<charms>null</charms>");
			System.out.println("			<drops>");
			for (ItemDrop drop : rare) {
				System.out.println("				<itemDrop>");
				System.out.println("					<id>" + drop.itemId + "</id>");
				System.out.println("					<min>" + drop.min + "</min>");
				System.out.println("					<max>" + drop.max + "</max>");
				System.out.println("				</itemDrop>");
			}
			System.out.println("			</drops>");
			System.out.println("		</rare>");
		}
		System.out.println("		<useRareTable>" + !rare.isEmpty() + "</useRareTable>");
		System.out.println("	</ItemDropDefinition>");

	}

	static class ItemDrop {
		static enum Rarity {
			ALWAYS,
			COMMON,
			UNCOMMON,
			RARE;

			public static Rarity get(String rarity) {
				for (Rarity r : values()) {
					if (r.name().equals(rarity)) {
						return r;
					}
				}
				return UNCOMMON;
			}
		}

		public final int itemId;
		public final int min, max;
		public final Rarity rarity;

		public ItemDrop(int itemId, int min, int max, Rarity rarity) {
			this.itemId = itemId;
			this.min = min;
			this.max = max;
			this.rarity = rarity;
		}

		@Override
		public String toString() {
			return "[" + itemId + ", " + min + ", " + max + ", " + rarity.name() + "]";
		}
	}
}