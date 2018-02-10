package com.vencillio.rs2.content.achievements;

import com.vencillio.rs2.content.achievements.AchievementHandler.AchievementDifficulty;

/**
 * Holds all the achievements and their requirements to complete. When adding a
 * new achievement, add it to the end of the list to prevent bugs with
 * saving/loading achievements.

 * @author Daniel
 * @author Michael
 */
public enum AchievementList {
	
	/* Easy Achievements */
	CHANGE_APPEARANCE("Change your appearance once", 1, AchievementDifficulty.EASY),
	EAT_100_FOODS("Eat 100 of any food", 100, AchievementDifficulty.EASY),
	DRINK_100_POTIONS("Drink 100 of any potion", 100, AchievementDifficulty.EASY),
	KILL_75_COWS("Kill 75 cows", 75, AchievementDifficulty.EASY),
	SPEND_5000000_ON_SHOPS("Spend 5m coins on shops", 5000000, AchievementDifficulty.EASY),
	THIEVE_300_TIMES_FROM_STALLS("Thieve 300 stalls", 300, AchievementDifficulty.EASY),
	FAIL_50_TIMES_THIEVING_STALLS("Fail 50 times thieving stalls", 50, AchievementDifficulty.EASY),
	WIN_20_DUELS("Win 20 duels", 20, AchievementDifficulty.EASY),
	STRING_100_AMULETS("String 100 amulets", 100, AchievementDifficulty.EASY),
	VIEW_15_PLAYER_PROFILES("View 15 player profiles", 15, AchievementDifficulty.EASY),
	CHOP_DOWN_150_TREES("Chop down 150 trees", 150, AchievementDifficulty.EASY),
	COOK_250_FOODS("Cook 250 foods", 250, AchievementDifficulty.EASY),
	ANSWER_15_TRIVIABOTS_CORRECTLY("Answer 15 TriviaBots correctly", 15, AchievementDifficulty.EASY),
	KILL_1_PLAYER("Kill one player", 1, AchievementDifficulty.EASY),
	DIE_1_TIME("Die 1 time", 1, AchievementDifficulty.EASY),
	COMPLETE_100_GNOME_COURSES("Complete 100 gnome courses", 100, AchievementDifficulty.EASY),
	HARVEST_100_WATERMERLONS("Harvest 100 watermelons", 100, AchievementDifficulty.EASY),
	PARTICIPATE_IN_WEAPON_GAME("Participate in the Weapon Game", 1, AchievementDifficulty.EASY),
	SHEAR_10_SHEEPS("Shear 10 Sheeps", 10, AchievementDifficulty.EASY),
	SETUP_A_BANK_PIN("Setup a bank pin", 1, AchievementDifficulty.EASY),
	SET_YOUR_ACCOUNT_DETAILS("Set your account security details", 4, AchievementDifficulty.EASY),
	DO_A_SKILLCAPE_EMOTE("Do a skillcape emote", 1, AchievementDifficulty.EASY),
	ENTER_THE_LOTTERY_5_TIMES("Enter the lottery 5 times", 5, AchievementDifficulty.EASY),
	USE_THE_SUPER_HEATING_SPELL_25_TIMES("Use the super heating spell 25 times", 25, AchievementDifficulty.EASY),
	
	/* Medium Achievements */
	KILL_ROCK_CRABS("Kill 100 Rock crabs", 100, AchievementDifficulty.MEDIUM),
	PRESTIGE_5_TIMES("Prestige 5 times", 5, AchievementDifficulty.MEDIUM),
	EXCHANGE_1000_ITEMS_PILES("Exchange 1,000 items to Piles", 1000, AchievementDifficulty.MEDIUM),
	COMPLETE_10_SLAYER_TASKS("Complete 10 slayer tasks", 10, AchievementDifficulty.MEDIUM),
	OBTAIN_1_BOSS_PET("Obtain 1 boss pet", 1, AchievementDifficulty.MEDIUM),
	HIGH_ALCH_250_ITEMS("High alch 250 items", 250, AchievementDifficulty.MEDIUM),
	OBTAIN_10_FIRECAPES("Obtain 10 firecapes", 10, AchievementDifficulty.MEDIUM),
	WIN_A_DUEL_WORTH_OVER_10M("Win a duel worth over 10m", 1, AchievementDifficulty.MEDIUM),
	WIN_30_PEST_CONTROL_GAMES("Win 30 Pest Control games", 30, AchievementDifficulty.MEDIUM),
	CHOP_DOWN_750_TREES("Chop down 750 trees", 750, AchievementDifficulty.MEDIUM),
	BURN_500_LOGS("Burn 500 logs", 500, AchievementDifficulty.MEDIUM),
	OPEN_50_ITEM_SETS("Open 50 item sets", 50, AchievementDifficulty.MEDIUM),
	RESET_5_STATISTICS("Reset 5 statistics", 5, AchievementDifficulty.MEDIUM),
	KILL_15_PLAYER("Kill 15 players", 15, AchievementDifficulty.MEDIUM),
	DIE_10_TIME("Die 10 times", 10, AchievementDifficulty.MEDIUM),
	COMPLETE_250_BARB_COURSES("Complete 250 barb courses", 250, AchievementDifficulty.MEDIUM),
	CUT_250_GEMS("Cut 250 gems", 250, AchievementDifficulty.MEDIUM),
	KILL_25_KRAKENS("Kill 25 Krakens", 25, AchievementDifficulty.MEDIUM),
	EARN_100_MAGE_ARENA_POINTS("Earn 100 Mage Arena Points", 100, AchievementDifficulty.MEDIUM),
	PARTICIPATE_IN_WEAPON_GAME_10_TIMES("Participate in the Weapon Game 10 times", 10, AchievementDifficulty.MEDIUM),
	SHEAR_150_SHEEPS("Shear 150 Sheeps", 150, AchievementDifficulty.MEDIUM),
	ENCHANT_1000_BOLTS("Enchant 1,000 bolts", 1000, AchievementDifficulty.MEDIUM),
	BURY_150_BONES("Bury or use on altar 150 bones", 150, AchievementDifficulty.MEDIUM),
	USE_THE_SUPER_HEATING_SPELL_135_TIMES("Use the super heating spell 135 times", 135, AchievementDifficulty.MEDIUM),

	
	/* Hard Achievements */
	KILL_KING_BLACK_DRAGON("Kill King Black dragon 50 times", 50, AchievementDifficulty.HARD),
	KILL_250_SKELETAL_WYVERNS("Kill 250 Skeletal wyverns", 250, AchievementDifficulty.HARD),
	CRAFT_1500_BLOOD_RUNES("Craft 1,500 blood runes", 1500, AchievementDifficulty.HARD),
	COMPLETE_100_SLAYER_TASKS("Complete 100 slayer tasks", 100, AchievementDifficulty.HARD),
	OBTAIN_10_BOSS_PET("Obtain 10 boss pets", 10, AchievementDifficulty.HARD),
	SPEND_100000000_ON_SHOPS("Spend 100m coins on shops", 100000000, AchievementDifficulty.HARD),
	OBTAIN_50_FIRECAPES("Obtain 50 firecapes", 50, AchievementDifficulty.HARD),
	CAST_VENGEANCE_350_TIMES("Cast vengeance 350 times", 350, AchievementDifficulty.HARD),
	BURN_1250_LOGS("Burn 1,250 logs", 1250, AchievementDifficulty.HARD),
	COOK_1000_FOODS("Cook 1,000 foods", 1000, AchievementDifficulty.HARD),
	ANSWER_80_TRIVIABOTS_CORRECTLY("Answer 80 TriviaBots correctly", 80, AchievementDifficulty.HARD),
	OPEN_70_CRYSTAL_CHESTS("Open 70 Crystal chests", 70, AchievementDifficulty.HARD),
	KILL_50_PLAYER("Kill 50 players", 50, AchievementDifficulty.HARD),
	DIE_50_TIME("Die 50 times", 50, AchievementDifficulty.HARD),
	COMPLETE_500_WILD_COURSES("Complete 500 barb courses", 500, AchievementDifficulty.HARD),
	MINE_1000_ROCKS("Mine 1,000 rocks", 1000, AchievementDifficulty.HARD),
	EARN_500_MAGE_ARENA_POINTS("Earn 500 Mage Arena points", 500, AchievementDifficulty.HARD),
	KILL_100_ZULRAHS("Kill 100 Zulrahs", 100, AchievementDifficulty.HARD),
	KILL_150_KRAKENS("Kill 150 Krakens", 150, AchievementDifficulty.HARD),
	KILL_100_CALLISTO("Kill 100 Callisto", 100, AchievementDifficulty.HARD),
	WIN_10_WEAPON_GAMES("Win 10 Weapon Games", 10, AchievementDifficulty.HARD),
	OBTAIN_10_RARE_DROPS("Obtain 10 rare drops", 10, AchievementDifficulty.HARD),
	BURY_1000_BONES("Bury or use on altar 1,000 bones", 1000, AchievementDifficulty.HARD),
	WIN_THE_LOTTERY_3_TMES("Win the lottery 3 times", 3, AchievementDifficulty.HARD),
	;

	private final String name;
	private final int completeAmount;
	private final AchievementDifficulty difficulty;

	private AchievementList(String name, int completeAmount, AchievementDifficulty difficulty) {
		this.name = name;
		this.completeAmount = completeAmount;
		this.difficulty = difficulty;
	}

	public String getName() {
		return name;
	}

	public int getCompleteAmount() {
		return completeAmount;
	}
	
	public int getReward() {
		switch (difficulty) {
		case MEDIUM:
			return 2;
		case HARD:
			return 3;
		case EASY:
		default:
			return 1;
		}
	}

	public AchievementDifficulty getDifficulty() {
		return difficulty;
	}
}