package com.vencillio.rs2.content.skill.crafting;

import java.util.HashMap;
import java.util.Map;

public enum HideTanData {
	COWHIDE_LEATHER(1739, new int[] { 0, 2 }, 1741, 1, new int[0]),
	COWHIDE_HARDLEATHER(1739, new int[] { 3, 5 }, 1743, 28, new int[0]),
	SNAKEHIDE(6287, new int[] { 15, 25 }, 6289, 45, new int[0]),
	SNAKEHIDE2(7801, new int[] { 20, 45 }, 6289, 45, new int[0]),
	GREEN_LEATHER(1753, new int[] { 15, 25 }, 1745, 57, new int[] { 1135, 1065, 1099 }),

	BLUE_LEATHER(1751, new int[] { 20, 45 }, 2505, 66, new int[] { 2499, 2487, 2493 }),

	RED_LEATHER(1749, new int[] { 20, 45 }, 2507, 73, new int[] { 2501, 2489, 2495 }),

	BLACK_LEATHER(1747, new int[] { 20, 45 }, 2509, 79, new int[] { 2503, 2491, 2497 });

	private short itemId;
	private int[] coins;
	private short outcome;
	private short requiredLevel;
	private int[] craftableOutcomes;
	private static Map<Short, HideTanData> hideRewards = new HashMap<Short, HideTanData>();

	public static final void declare() {
		for (HideTanData hide : values())
			hideRewards.put(Short.valueOf(hide.getOutcome()), hide);
	}

	public static HideTanData forReward(short id) {
		return hideRewards.get(Short.valueOf(id));
	}

	private HideTanData(int itemId, int[] coins, int outcome, int requiredLevel, int[] craftableOutcomes) {
		this.itemId = ((short) itemId);
		this.coins = coins;
		this.outcome = ((short) outcome);
		this.requiredLevel = ((short) requiredLevel);
		this.craftableOutcomes = craftableOutcomes;
	}

	public int[] getCoins() {
		return coins;
	}

	public int[] getCraftableOutcomes() {
		return craftableOutcomes;
	}

	public short getItemId() {
		return itemId;
	}

	public short getOutcome() {
		return outcome;
	}

	public short getRequiredLevel() {
		return requiredLevel;
	}
}
