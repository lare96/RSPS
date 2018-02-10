package com.vencillio.rs2.content;

import java.util.HashMap;

import com.vencillio.rs2.entity.player.Player;

/**
 * Handles pressing a skill in skilltab
 * @author Daniel
 *
 */
public class SkillsChat {
	
	/**
	 * Chat data
	 * @author Daniel
	 *
	 */
	public enum ChatData {
		ATTACK(94147, 0),
		STRENGTH(94150, 2),
		RANGE(94156, 4),
		MAGIC(94162, 6),
		DEFENCE(94153, 1),
		HITPOINTS(94148, 3),
		PRAYER(94159, 5),
		AGILITY(94151, 16),
		HERBLORE(94154, 15),
		THIEVING(94157, 17),
		CRAFTING(94160, 12),
		RUNECRAFTING(94165, 20),
		SLAYER(94166, 18),
		FARMING(94167, 19),
		MINING(94149, 14),
		SMITHING(94152, 13),
		FISHING(94155, 10),
		COOKING(94158, 7),
		FIREMAKING(94161, 11),
		WOODCUTTING(94164, 8),
		FLETCHING(94163, 9),
		HUNTER(94168, 21),
		TOTAL_LEVEL(94144, 69);
		
		public int skill, button;
		
		private ChatData(int button, int skill) {
			this.skill = skill;
			this.button = button;
		}
		
		public int getSkill() {
			return skill;
		}
		
		public int getButton() {
			return button;
		}
		
        public static ChatData forskill(int id) {
            for (ChatData data: ChatData.values())
                if (data.button == id)
                    return data;
            return null;
        }
        
        
		public static HashMap<Integer, ChatData> chat = new HashMap<Integer, ChatData>();

		static {
			for (final ChatData chat : ChatData.values()) {
				ChatData.chat.put(chat.button, chat);
			}
		}
		
	}
	
	/**
	 * Skill names
	 */
	public static String[] skillName = { 
		"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", 
		"Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", 
		"Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter"
	};

	/**
	 * Gets the skill name
	 * @param i
	 * @return
	 */
	public static String getSkillName(int i) {
		return skillName[i];
	}
	
	
	/**
	 * Handles the quick chat
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean handle(Player player, int buttonId) {
		ChatData chat = ChatData.chat.get(buttonId);
		if (chat == null) {
			return false;
		}
		
		if (chat.getButton() == 94144) {
			player.getUpdateFlags().sendForceMessage("[QC] My total level is " + player.getSkill().getTotalLevel() + ".");
			return false;
		}

		player.getUpdateFlags().sendForceMessage("[QC] My "+getSkillName(chat.skill)+" level is "+player.getMaxLevels()[chat.skill]+".");		
		return true;
	}

}
