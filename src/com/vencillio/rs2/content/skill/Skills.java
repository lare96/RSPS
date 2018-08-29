package com.vencillio.rs2.content.skill;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.player.Player;

public class Skills {

	public static final int LEVEL_99_SONG = 99;
	public static final int[] LEVEL_UP_SOUNDS = { 112, 107, 104, 10, 21, 24 };
	public static final int SKILL_COUNT = 25;
	public static final Graphic LEVELUP_GRAPHIC = Graphic.highGraphic(199, 0);
	public static final int[] EXPERIENCE_RATES = new int[SKILL_COUNT];
	public static final int MAX_EXPERIENCE = 200000000;
	public static final String[] SKILL_NAMES = { "attack", "defence", "strength", "hitpoints", "ranged", "prayer", "magic", "cooking", "woodcutting", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblore", "agility", "thieving", "slayer", "farming", "runecrafting", "hunter", "summoning", "construction", "dungeoneering" };
	public static final int ATTACK = 0;
	public static final int DEFENCE = 1;
	public static final int STRENGTH = 2;
	public static final int HITPOINTS = 3;
	public static final int RANGED = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUTTING = 8;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLORE = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
	public static final int SLAYER = 18;
	public static final int FARMING = 19;
	public static final int RUNECRAFTING = 20;
	public static final int SUMMONING = 22;
	public static final int HUNTER = 21;
	public static final int CONSTRUCTION = 23;
	public static final int DUNGEONEERING = 24;
	public static final int[][] CHAT_INTERFACES = { { 0, 6247 }, { 1, 6253 }, { 2, 6206 }, { 3, 6216 }, { 4, 4443, 5453, 6114 }, { 5, 6242 }, { 6, 6211 }, { 7, 6226 }, { 8, 4272 }, { 9, 6231 }, { 10, 6258 }, { 11, 4282 }, { 12, 6263 }, { 13, 6221 }, { 14, 4416, 4417, 4438 }, { 15, 6237 }, { 16, 4277 }, { 17, 4261, 4263, 4264 }, { 18, 12122 }, { 19, 4887, 4890, 4891 }, { 20, 4267 }, { 21, 4268 }, { 22, 4268 } };
	public static final int[][] REFRESH_DATA = { { 4004, 4005 } , { 4008, 4009 }, { 4006, 4007 }, { 4016, 4017 }, { 4010, 4011 }, { 4012, 4013 }, { 4014, 4015 }, { 4034, 4035 }, { 4038, 4039 }, { 4026, 4027 }, { 4032, 4033 }, { 4036, 4037 }, { 4024, 4025 }, { 4030, 4031 }, { 4028, 4029 }, { 4020, 4021 }, { 4018, 4019 }, { 4022, 4023 }, { 12166, 12167 }, { 13926, 13927 }, { 4152, 4153 }, { -1, -1 }, { 24134, 24135 }, { -1, -1 }, { -1, -1 } };
	public static final int CURRENT_HEALTH_UPDATE_ID = 4016;
	public static final int MAX_HEALTH_UPDATE_ID = 4017;
	public static final int CURRENT_PRAYER_UPDATE_ID = 4012;
	public static final int MAX_PRAYER_UPDATE_ID = 4013;


	public static void declare() { //Skill rate multipliers
		EXPERIENCE_RATES[ATTACK] = 1;
		EXPERIENCE_RATES[DEFENCE] = 1;
		EXPERIENCE_RATES[STRENGTH] = 1;
		EXPERIENCE_RATES[HITPOINTS] = 1;
		EXPERIENCE_RATES[RANGED] = 1;
		EXPERIENCE_RATES[PRAYER] = 35;
		EXPERIENCE_RATES[MAGIC] = 1;
		EXPERIENCE_RATES[COOKING] = 17;
		EXPERIENCE_RATES[WOODCUTTING] = 28;
		EXPERIENCE_RATES[FLETCHING] = 25;
		EXPERIENCE_RATES[FISHING] = 16;
		EXPERIENCE_RATES[FIREMAKING] = 20;
		EXPERIENCE_RATES[CRAFTING] = 30;
		EXPERIENCE_RATES[SMITHING] = 25;
		EXPERIENCE_RATES[MINING] = 30;
		EXPERIENCE_RATES[HERBLORE] = 30;
		EXPERIENCE_RATES[AGILITY] = 42;
		EXPERIENCE_RATES[THIEVING] = 15;
		EXPERIENCE_RATES[SLAYER] = 28;
		EXPERIENCE_RATES[HUNTER] = 26;
		EXPERIENCE_RATES[FARMING] = 35;
		EXPERIENCE_RATES[RUNECRAFTING] = 35;
		EXPERIENCE_RATES[DUNGEONEERING] = 25;
		EXPERIENCE_RATES[SUMMONING] = 27;
	}

	public static final boolean isSuccess(int skill, int levelRequired) {
		double successChance = Math.ceil(((double) skill * 50.0D - (double) levelRequired * 15.0D) / (double) levelRequired / 3.0D * 4.0D);
		int roll = Utility.randomNumber(99);

		return successChance >= roll;

	}

	public static final boolean isSuccess(Player p, int skillId, int levelRequired) {
		double level = p.getMaxLevels()[skillId];
		double req = levelRequired;
		double successChance = Math.ceil((level * 50.0D - req * 15.0D) / req / 3.0D * 4.0D);
		int roll = Utility.randomNumber(99);

		if (successChance >= roll) {
			return true;
		}

		return false;
	}

	public static final boolean isSuccess(Player p, int skillId, int levelRequired, int toolLevelRequired) {
		double level = (p.getMaxLevels()[skillId] + toolLevelRequired) / 2.0D;
		double req = levelRequired;
		double successChance = Math.ceil((level * 50.0D - req * 15.0D) / req / 3.0D * 4.0D);
		int roll = Utility.randomNumber(99);

		if (successChance >= roll) {
			return true;
		}

		return false;
	}
}
