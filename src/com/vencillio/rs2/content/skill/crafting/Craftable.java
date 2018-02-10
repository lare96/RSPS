package com.vencillio.rs2.content.skill.crafting;

import java.util.HashMap;
import java.util.Map;

public enum Craftable {
	
	LEATHERGLOVES(1741, 1059, 1, 13.800000000000001D, 1),
	LEATHERBOOTS(1741, 1061, 7, 16.25D, 1),
	LEATHERCOWL(1741, 1167, 9, 18.5D, 1),
	LEATHERVAMBS(1741, 1063, 11, 22.0D, 1),
	LEATHERBODY(1741, 1129, 14, 25.0D, 1),
	LEATHERCHAPS(1741, 1095, 18, 27.0D, 1),
	COIF(1741, 1169, 38, 37.0D, 1);

	private int itemId;
	private int outcome;
	private int requiredLevel;
	private int requiredAmount;
	private double experience;

	private static Map<Integer, Craftable> craftableRewards = new HashMap<Integer, Craftable>();

	public static final void declare() {
		for (Craftable craftable : values())
			craftableRewards.put(Integer.valueOf(craftable.getOutcome()), craftable);
	}

	public static Craftable forReward(int id) {
		return craftableRewards.get(Integer.valueOf(id));
	}

	private Craftable(int itemId, int outcome, int requiredLevel, double experience, int requiredAmount) {
		this.itemId = itemId;
		this.outcome = outcome;
		this.requiredLevel = requiredLevel;
		this.experience = experience;
		this.requiredAmount = requiredAmount;
	}

	public double getExperience() {
		return experience;
	}

	public int getItemId() {
		return itemId;
	}

	public int getOutcome() {
		return outcome;
	}

	public int getRequiredAmount() {
		return requiredAmount;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}
}
