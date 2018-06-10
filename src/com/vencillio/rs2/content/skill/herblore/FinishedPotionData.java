package com.vencillio.rs2.content.skill.herblore;

import java.util.HashMap;
import java.util.Map;

public enum FinishedPotionData {
	
	ATTACK_POTION(121, 91, 221, 1, 25), //Eye of newt
	RANGING_POTION(169, 91, 1951, 3, 30), //Redberries
	MAGIC_POTION(3042, 95, 1470, 5, 35), //Red beads
	STRENGTH_POTION(115, 95, 225, 7, 40), //Limpwurt
	DEFENCE_POTION(133, 99, 948, 9, 45), //Bear fur
	ANTIPOISON(175, 93, 235, 13, 50), //Unicorn horn dust
	RESTORE_POTION(127, 97, 223, 22, 63), //Red spiders eggs
	ENERGY_POTION(3010, 97, 1975, 26, 68), //Chocolate dust
	AGILITY_POTION(3034, 3002, 2152, 34, 80), // Toad's Legs
	COMBAT_POTION(9741, 97, 9736, 36, 84), //Goat horn dust
	PRAYER_POTION(139, 99, 231, 38, 88), //snape grass
	SUPER_ATTACK(145, 101, 221, 45, 100), //Eye of newt
	VIAL_OF_STENCH(18661, 101, 1871, 46, 0),
	FISHING_POTION(181, 101, 235, 50, 112), //Unicorn horn dust
	SUPER_ENERGY(3018, 103, 2970, 52, 118), //mort myre fungus
	SUPER_STRENGTH(157, 105, 225, 55, 125), //limpwurt root
	SUPER_RESTORE(3026, 3004, 223, 63, 143), //Red spiders' eggs
	SUPER_DEFENCE(163, 107, 239, 66, 150), //white berries
	ANTIFIRE(2454, 2483, 241, 69, 158), //Dragonscale dust
	SUPER_RANGE_POTION(11723, 109, 245, 72, 163), //Wine of zamorak
	WEAPON_POISON(187, 105, 223, 73, 165), //Red spiders' eggs
	SUPER_MAGIC_POTION(11727, 2483, 3138, 76, 173), //Potato Cactus
	ZAMORAK_BREW(189, 111, 247, 78, 175), //Jangerberries
	SARADOMIN_BREW(6687, 3002, 6693, 81, 180); //Crushed nest

	public static final void declare() {
		for (FinishedPotionData data : values())
			potions.put(Integer.valueOf(data.itemNeeded), data);
	}

	public static FinishedPotionData forIds(int id1, int id2) {
		for (FinishedPotionData i : values()) {
			if (((id1 == i.getItemNeeded()) && (id2 == i.getUnfinishedPotion())) || ((id2 == i.getItemNeeded()) && (id1 == i.getUnfinishedPotion()))) {
				return i;
			}
		}

		return null;
	}

	private int finishedPotion;
	private int unfinishedPotion;
	private int itemNeeded;
	private int levelReq;

	private int expGained;

	private static Map<Integer, FinishedPotionData> potions = new HashMap<Integer, FinishedPotionData>();

	public static FinishedPotionData forId(int id) {
		return potions.get(Integer.valueOf(id));
	}

	private FinishedPotionData(int finishedPotion, int unfinishedPotion, int itemNeeded, int levelReq, int expGained) {
		this.finishedPotion = finishedPotion;
		this.unfinishedPotion = unfinishedPotion;
		this.itemNeeded = itemNeeded;
		this.levelReq = levelReq;
		this.expGained = expGained;
	}

	public int getExpGained() {
		return expGained;
	}

	public int getFinishedPotion() {
		return finishedPotion;
	}

	public int getItemNeeded() {
		return itemNeeded;
	}

	public int getLevelReq() {
		return levelReq;
	}

	public int getUnfinishedPotion() {
		return unfinishedPotion;
	}
}
