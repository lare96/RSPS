package com.vencillio.rs2.content.skill.runecrafting;

import java.util.HashMap;
import java.util.Map;

public enum RunecraftingData {
	
	AIR(14897, 556, 6.0D, new int[] { 1, 11, 22, 33, 44, 55, 66, 77, 88, 99 }),
	MIND(14898, 558, 6.5D, new int[] { 1, 14, 28, 42, 56, 70, 84, 98 }),
	WATER(14899, 555, 7.0D, new int[] { 5, 19, 38, 57, 76, 95 }),
	EARTH(14900, 557, 7.5D, new int[] { 9, 26, 52, 78 }),
	FIRE(14901, 554, 8.0D, new int[] { 14, 35, 70 }),
	BODY(14902, 559, 8.5D, new int[] { 20, 46, 92 }),
	COSMIC(14903, 564, 10.0D, new int[] { 27, 59 }),
	CHAOS(14906, 562, 10.5D, new int[] { 35, 74 }),
	NATURE(14905, 561, 11.0D, new int[] { 44, 91 }),
	LAW(14904, 563, 10.5D, new int[] { 54 }),
	DEATH(14907, 560, 12.0D, new int[] { 65 }),
	BLOOD(4090, 565, 12.5D, new int[] { 77 });

	public static final void declare() {
		for (RunecraftingData data : values())
			altars.put(data.getAltarId(), data);
	}

	public static RunecraftingData forId(int id) {
		return altars.get(id);
	}

	private int altarId;
	private int runeId;
	private double xp;

	private int[] multiplier;

	private static Map<Integer, RunecraftingData> altars = new HashMap<Integer, RunecraftingData>();

	private RunecraftingData(int altarId, int runeId, double xp, int[] multiplier) {
		this.altarId = altarId;
		this.runeId = runeId;
		this.xp = xp;
		this.multiplier = multiplier;
	}

	public int getAltarId() {
		return altarId;
	}

	public int getLevel() {
		return multiplier[0];
	}

	public int[] getMultiplier() {
		return multiplier;
	}

	public int getRuneId() {
		return runeId;
	}

	public double getXp() {
		return xp;
	}
}
