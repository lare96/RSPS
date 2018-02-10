package com.vencillio.rs2.content.skill.woodcutting;

import com.vencillio.core.cache.map.ObjectDef;

public enum WoodcuttingTreeData {
	NORMAL_FREE(1, 1511, 1342, 22, 25.0D),
	DEAD_TREE(1, 1511, 1342, 22, 25.0D),
	OAK_TREE(15, 1521, 1356, 30, 37.5D),
	WILLOW_TREE(30, 1519, 7399, 38, 67.5D),
	MAPLE_TREE(45, 1517, 1343, 45, 100.0D),
	YEW_TREE(60, 1515, 7402, 50, 175.0D),
	MAGIC_TREE(75, 1513, 7401, 55, 250.0D);

	int levelRequired;
	int reward;
	int replacementId;
	int respawnTimer;
	double experience;

	public static WoodcuttingTreeData forId(int objectId) {
		ObjectDef def = ObjectDef.getObjectDef(objectId);

		if (def == null || def.name == null) {
			return null;
		}

		String name = def.name.toLowerCase().trim();

		switch (name) {
		case "dead tree":
			return DEAD_TREE;
		case "oak tree":
		case "oak":
			return OAK_TREE;
		case "willow tree":
		case "willow":
			return WILLOW_TREE;
		case "maple tree":
		case "maple":
			return MAPLE_TREE;
		case "yew tree":
		case "yew":
			return YEW_TREE;
		case "magic tree":
		case "magic":
			return MAGIC_TREE;
		case "tree":
			return NORMAL_FREE;
		default:
			return null;
		}
	}

	private WoodcuttingTreeData(int level, int reward, int replacement, int respawnTimer, double experience) {
		levelRequired = level;
		this.reward = reward;
		replacementId = replacement;
		this.respawnTimer = respawnTimer;
		this.experience = experience;
	}

	public double getExperience() {
		return experience;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public int getReplacement() {
		return replacementId;
	}

	public int getRespawnTimer() {
		return respawnTimer;
	}

	public int getReward() {
		return reward;
	}
}
