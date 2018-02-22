package com.vencillio.rs2.content.vencilliobot;

/**
 * Holds the VencillioBot Data
 * @author Daniel
 *
 */
public enum VencillioBotData {	
	  DATA_1("Who is the CEO of Tannerscape?", "Tanner"),
	  DATA_2("What color is prestige tier 5?", "red"),
	  DATA_3("How many times can you prestige a skill?", "5", "five"),
	  DATA_4("The barrows brother Verac hits through which prayer?", "Protect from melee"),
	  DATA_5("How many developers are there of Tannerscape?", "1", "one"),
	  DATA_6("Which MOB has four enourmous tentacles?", "Kraken", "kraken"),
	  DATA_7("How many crystal keys do you get for voting?", "3", "Three"),
	  DATA_8("I am a rock that turns into a crab what am I called?", "Rock crab", "Rock crabs"),
	  DATA_9("What is the max level possible reached in any skill?", "99"),
	  DATA_10("Which NPC allows you set bank pin?", "Tannerscape guard", "guard"),
	  DATA_11("IHow many NPCs drop the Chaos Elemental pet?", "2", "two"),
	  DATA_12("How many times can you prestige a skill?", "5", "five"),
	  DATA_13("Corporeal Beast drops how many onyx bolts(e)?", "175"),
	  DATA_14("Tanzanite fang turns into what when cut with chisel?", "toxic Blowpipe", "blowpipe"),
	  DATA_15("What is the name of the NPC that drops Wyvern bones?", "skeletal Wyvern", "skeletal", "wyverns"),
	  DATA_16("How many colors of infinity are there?", "3", "Three"),
	  DATA_17("What is the max combat level you can achieve?", "126"),
	  DATA_18("How many times can you prestige a skill?", "5", "five"),
	  DATA_19("Which of the barrows warriors is based on magic?","Ahrim", "Ahrim"),
	  DATA_20("What is the required fishing level for sharks?","76", "76"),
	  DATA_21("What is the smithing level required to create a DFS?", "90"),
	  DATA_22("Purple is prestige color for what prestige level?","2nd", "second", "2"),
	  DATA_23("How many waves in fight caves?","fifteen", "15"),
	  DATA_24("What is the minimum required amount to bet against the Gambler?", "500k"),
	  DATA_25("How much special attack does Magic Short Bow require?", "55%", "55"),
	  DATA_26("Berserker ring is dropped by which NPC?","Dagannoth King Rex", "Dagannoth Rex", "Rex"),
	  DATA_27("How many friends fit on the friendslist?", "200"),
	  DATA_28("What is the short term used for 'Staff Of The Dead'?", "Sotd"),
	  DATA_29("Anagram - odsanb", "bandos"),
	  DATA_30("Where are spiritual mages found?", "Godwars", "Godwars dungeon"),
	  DATA_31("What is the required level for Smite?", "52"),
	  DATA_32("Toxic blowpipe is a drop from what NPC?", "Zulrah"),
	  DATA_33("What is the name of the first teleport in the teleport interface?", "Rock Crabs"),
	  DATA_34("Which npc teleports you to rune essence mine?", "Mage of zamorak", "Zamorak mage"),
	  DATA_35("What is the required level for Smite?", "52"),
	  DATA_36("How many times can you prestige one stat?", "5", "five"),
	  DATA_37("Which gamemode cannot use banks?", "Ultimate Ironman", "Ult"),
	  DATA_38("What does the npc 'Big Mo' Sell?", "loyalty titles", "titles", "player titles"),
	  DATA_39("How many bank booths are in edgeville bank?", "4", "four"),
	  DATA_40("What slayer level is required to kill Kraken?", "87", "87"),
	  DATA_41("What is the required attack level to wield a godsword?", "75"),
	  DATA_42("How many barrows brothers are there?", "6"),
	  DATA_43("Type this backwards; tannerscapeisthebestForsure", "erusroftsebehtsiepacsrennat"),
	  DATA_44("How much does regular membership cost?", "$5", "5 dollars", "5 dollar"),
	  DATA_45("How many tiers of membership are there?", "4", "four"),
	  DATA_46("What game is wildly addictive?", "tannerscape", "this game"),
	  DATA_47("What NPC helps Iron accounts?", "adam"),
	  DATA_48("Name one of the moderators.", "saga", "grim", "narcan"), //Mod names here
	  DATA_49("Who is the community manager?", "synergy"), //CM here
	  DATA_50("Name one of the developers", "tanner", "chex", "zion", "complex"),
	  DATA_51("How many total achievements are there?", "70", "seventy"),
	  DATA_52("How many prayers are there?", "26", "twenty six", "twenty-six"),
	  DATA_53("What is the maximum amount of friends allowed?", "200", "two hundred", "two-hundred"),
	  DATA_54("What is the Woodcutting level required to wield a dragon hatchet?", "61", "sixty one", "sixty-one"),	  
	  DATA_55("Who can you talk to if you want to see the NPC drop tables?", "hari"),
	  DATA_56("Where is home?", "edgeville"),
	  DATA_57("What is the name of the NPC you can get skillcapes from?", "wise old man"),
	  DATA_58("What color party hat does the wise old man wear?", "blue"),
	  DATA_59("What is the name of the NPC used to travel around Tannerscape?", "sailor"),
	  DATA_60("What skill involves buring logs?", "firemaking"),
	  DATA_61("What is the required defence level to wear dragon armour?", "60", "sixty"),
	  DATA_62("What NPC allows you to reset combat stats?", "genie"),
	  DATA_63("What minigame offers void armour as a reward?", "pest control"),
	  DATA_64("What NPC can you talk to if you want to claim a donation?", "credits master", "membership"),
	  DATA_65("How many Thieving stalls are there at home?", "5", "five"),
	  DATA_66("Who is the website developer?", "tanner"),
	  DATA_67("What is the best F2P armour?", "rune", "rune armour"),	  
	  DATA_68("How much money does normal membership cost?", "$5", "5 dollars"),  
	  DATA_69("What is the best crossbow in game?", "armadyl crossbow", "acb"),	  
	  DATA_70("How many posts do you need to post an application?", "50", "fifty"),
	  DATA_71("How many letters are in the word 'tannerscape'?", "11", "eleven"),
	  DATA_72("What level magic does the spell High Alchemy require?", "55", "fifty-five", "fifty five"),
	  
	  ;
	
	private final String question;
	private final String[] answers;
	
	private VencillioBotData(String question, String... answers) {
		this.question = question;
		this.answers = answers;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String[] getAnswers() {
		return answers;
	}
	
}
