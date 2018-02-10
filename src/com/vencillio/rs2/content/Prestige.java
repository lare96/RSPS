package com.vencillio.rs2.content;

import java.util.HashMap;

import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the prestige system
 * 
 * @author Daniel
 *
 */
public class Prestige {

	/**
	 * Prestige sendMessage color
	 */
	private static final String PRESTIGE_COLOR = "<col=CC0066>";

	/**
	 * Holds the max number of prestige tiers
	 */
	private static final int MAX_PRESTIGES = 5;

	/**
	 * Prestige Data
	 * 
	 * @author Daniel
	 *
	 */
	public enum PrestigeData {
		ATTACK(199088, "Attack", 0, 51010, 600_000),	
		DEFENCE(199094, "Defence", 1, 51012, 600_000),		
		STRENGTH(199091, "Strength", 2, 51011, 600_000),
		HITPOINTS(199109, "Hitpoints", 3, 51017, 600_000),	
		RANGE(199097, "Range", 4, 51013, 600_000),
		PRAYER(199100, "Prayer", 5, 51014, 600_000),
		MAGIC(199103, "Magic", 6, 51015, 600_000),		
		COOKING(199139, "Cooking", 7, 51027, 1_500_000),		
		WOODCUTTING(199145, "Woodcutting", 8, 51029, 1_500_000),	
		FLETCHING(199124, "Fletching", 9, 51022, 1_500_000),	
		FISHING(199136, "Fishing", 10, 51026, 1_500_000),	
		FIREMAKING(199142, "Firemaking", 11, 51028, 1_500_000),		
		CRAFTING(199121, "Crafting", 12, 51021, 1_500_000),		
		SMITHING(199133, "Smithing", 13, 51025, 1_500_000),		
		MINING(199130, "Mining", 14, 51024, 1_500_000),	
		HERBLORE(199115, "Herblore", 15, 51019, 1_500_000),		
		AGILITY(199112, "Agility", 16, 51018, 1_500_000),		
		THIEVING(199118, "Thieving", 17, 51020, 1_500_000),		
		SLAYER(199127, "Slayer", 18, 51023, 1_500_000),	
		FARMING(199148, "Farming", 19, 51030, 1_500_000),	
		RUNECRAFTING(199106, "Runecraft", 20, 51016, 1_500_000),	
		HUNTER(199151, "Hunter", 21, 51031, 1_500_000);

		String name;
		int buttonId, skill, frame, money;

		private PrestigeData(int buttonId, String name, int skill, int frame, int money) {
			this.buttonId = buttonId;
			this.name = name;
			this.skill = skill;
			this.frame = frame;
			this.money = money;
		}

		public String getName() {
			return name;
		}

		public int getButton() {
			return buttonId;
		}

		public int getSkill() {
			return skill;
		}

		public int getFrame() {
			return frame;
		}
		
		public int getMoney() {
			return money;
		}

		public static PrestigeData forSkill(int id) {
			for (PrestigeData data : PrestigeData.values())
				if (data.skill == id)
					return data;
			return null;
		}

		public static HashMap<Integer, PrestigeData> prestige = new HashMap<Integer, PrestigeData>();

		static {
			for (final PrestigeData prestige : PrestigeData.values()) {
				PrestigeData.prestige.put(prestige.buttonId, prestige);
			}
		}
	}

	/**
	 * Handles the clicking buttons for interface
	 * 
	 * @param player
	 * @param actionButtonId
	 */
	public static boolean handleActionButtons(Player player, int buttonId) {
		PrestigeData data = PrestigeData.prestige.get(buttonId);

		if (data == null) {
			return false;
		}
		
		if (player.getInterfaceManager().main != 51000) {
			player.send(new SendRemoveInterfaces());
			player.send(new SendMessage("That interface does not exist!"));
			return false;
		}
		
		if (prestigeSkill(player, data.getSkill())) {
			player.send(new SendString(data.getName() + " (" + skillTierColor(player, data.getSkill()) + "" + player.getSkillPrestiges()[data.getSkill()] + "@lre@)", data.getFrame()));
			player.getInventory().add(new Item(995, data.getMoney()));
		}
		return true;
	}

	/**
	 * Checks if player can prestige
	 * 
	 * @param player
	 * @param skillId
	 * @return
	 */
	public static boolean canPrestige(Player player, int skillId) {
		if (player.getMaxLevels()[skillId] < 99) {
			player.send(new SendMessage("You can only prestige your " + getSkillName(skillId) + " when you have reached 99!"));
			return false;
		}
		if (player.getSkillPrestiges()[skillId] >= MAX_PRESTIGES) {
			player.send(new SendMessage("You may only prestige your skill " + MAX_PRESTIGES + " times."));
			return false;
		}
		if (player.getInventory().getFreeSlots() < 1) {
			player.send(new SendMessage("Please make sure you have at least 1 free slot in your inventory before prestiging!"));
			return false;
		}
		if (player.getEquipment().getEquipmentCount() != 0) {
			player.send(new SendMessage("Remove all equipment before prestiging."));
			return false;
		}
		return true;
	}

	/**
	 * Prestiges the skill if all requirements are met
	 * 
	 * @param player
	 * @param skillId
	 */
	public static boolean prestigeSkill(Player player, int skillId) {
		if (!canPrestige(player, skillId)) {
			return false;
		}

		if (skillId == 3) {
			player.getLevels()[skillId] = ((byte) 10);
			player.getMaxLevels()[skillId] = ((byte) 10);
			player.getSkill().getExperience()[skillId] = player.getSkill().getXPForLevel(skillId, 10);
			player.getSkill().update(skillId);
		} else {
			player.getLevels()[skillId] = ((byte) 1);
			player.getMaxLevels()[skillId] = ((byte) 1);
			player.getSkill().getExperience()[skillId] = player.getSkill().getXPForLevel(skillId, 1);
			player.getSkill().update(skillId);
		}

		player.getSkillPrestiges()[skillId] += 1;
		player.setTotalPrestiges(player.getTotalPrestiges() + 1);
		player.setPrestigePoints(player.getPrestigePoints() + 1);
		player.send(new SendMessage("[ " + PRESTIGE_COLOR + "Prestige</col> ] You've successfully prestiged " + PRESTIGE_COLOR + "" + getSkillName(skillId) + "</col>!"));
		World.sendGlobalMessage("<img=8> " + PRESTIGE_COLOR + player.getUsername() + " </col>has just prestiged their " + PRESTIGE_COLOR + "" + getSkillName(skillId) + "</col> skill to tier " + PRESTIGE_COLOR + "" + player.getSkillPrestiges()[skillId] + "</col>!");
		AchievementHandler.activateAchievement(player, AchievementList.PRESTIGE_5_TIMES, 1);
		player.getSkill().restore();
		update(player);
		return true;
	}

	/**
	 * Updates the interface
	 * 
	 * @param player
	 */
	public static void update(Player player) {
		player.send(new SendString("@gre@" + player.determineIcon(player) + "  " + player.getUsername(), 51007));
		player.send(new SendString("</col>Total Prestiges: @gre@" + player.getTotalPrestiges(), 51008));
		player.send(new SendString("</col>Prestige Points: @gre@" + player.getPrestigePoints(), 51009));
		for (int i = 0; i < player.getSkillPrestiges().length; i++) {
			PrestigeData data = PrestigeData.forSkill(i);
			if (data == null) {
				continue;
			}
			player.send(new SendString(data.getName() + " (<col=" + skillTierColor(player, data.getSkill()) + ">" + player.getSkillPrestiges()[data.getSkill()] + "@lre@)", data.getFrame()));
		}
	}

	/**
	 * Skill names
	 */
	public static String[] skillName = { "Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter" };

	/**
	 * Gets the skillname
	 * @param i
	 * @return
	 */
	public static String getSkillName(int i) {
		return skillName[i];
	}

	/**
	 * The skill colors
	 * @param player
	 * @param skillId
	 * @return
	 */
	public static int skillTierColor(Player player, int skillId) {
		switch (player.getSkillPrestiges()[skillId]) {
		case 1:
			return 0xE100FF;
		case 2:
			return 0xFF6A00;
		case 3:
			return 0x11BF0B;
		case 4:
			return 0x0D96D1;
		case 5:
			return 0xED0909;
		default:
			return 0xFFFF00;
		}
	}
}