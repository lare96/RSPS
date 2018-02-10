package com.vencillio.rs2.content;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * This class handles the reseting of all combat skills
 * 
 * @author Daniel
 *
 */
public class GenieReset {

	/**
	 * Genie data for skill reseting
	 * 
	 * @author Daniel
	 *
	 */
	public enum GenieData {
		ATTACK(232114, "Attack", 0),
		STRENGTH(232117, "Strength", 2),
		DEFENCE(232120, "Defence", 1),
		RANGE(232123, "Range", 4),
		MAGIC(232126, "Magic", 6),
		PRAYER(232129, "Prayer", 5),
		HITPOINTS(232132, "Hitpoints", 3);

		private int buttonId, skillId;
		private String skillName;

		private GenieData(int buttonId, String skillName, int skillId) {
			this.buttonId = buttonId;
			this.skillName = skillName;
			this.skillId = skillId;
		}

		public int getButton() {
			return buttonId;
		}

		public String getSkillName() {
			return skillName;
		}

		public int getSkillId() {
			return skillId;
		}

		public static GenieData forId(int buttonId) {
			for (GenieData data : GenieData.values())
				if (data.buttonId == buttonId)
					return data;
			return null;
		}
	}

	/**
	 * Handles the skill reseting
	 * 
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean handle(Player player, int buttonId) {
		// Grabs the genie data
		GenieData genie = GenieData.forId(buttonId);

		// Return if genie data is null
		if (genie == null) {
			return false;
		}

		// Return if player does not have required amount of coins
		if (!player.getInventory().hasItemAmount(new Item(995, 1_000_000))) {
			DialogueManager.sendNpcChat(player, 409, Emotion.ANGRY_1, "Reseting a skill costs " + Utility.format(1_000_000) + " coins!");
			return false;
		}

		// Return if player is wearing items
		if (player.getEquipment().getEquipmentCount() != 0) {
			DialogueManager.sendNpcChat(player, 409, Emotion.ANGRY_2, "Remove all equipment before prestiging.");
			return false;
		}

		// Closes the interface
		player.send(new SendRemoveInterfaces());

		// Deletes the coins from player's inventory
		player.getInventory().remove(new Item(995, 1_000_000));

		// Resets the player's skill
		if (genie.getSkillId() == 3) {
			player.getLevels()[genie.getSkillId()] = ((byte) 10);
			player.getMaxLevels()[genie.getSkillId()] = ((byte) 10);
			player.getSkill().getExperience()[genie.getSkillId()] = player.getSkill().getXPForLevel(genie.getSkillId(), 10);
			player.getSkill().update(genie.getSkillId());
		} else {
			player.getLevels()[genie.getSkillId()] = ((byte) 1);
			player.getMaxLevels()[genie.getSkillId()] = ((byte) 1);
			player.getSkill().getExperience()[genie.getSkillId()] = player.getSkill().getXPForLevel(genie.getSkillId(), 1);
			player.getSkill().update(genie.getSkillId());
		}
		// Refreshes the player's skills
		player.getSkill().update();

		// Sends Genie dialogue
		DialogueManager.sendNpcChat(player, 326, Emotion.HAPPY, "You have successfully reset your " + genie.getSkillName() + " to "+player.getSkill().getLevels()[genie.getSkillId()]+"!");

		// Reset dialogue
		player.getDialogue().setNext(-1);
		
		// Sends the Achievement
		AchievementHandler.activateAchievement(player, AchievementList.RESET_5_STATISTICS, 1);
		player.send(new SendMessage("Yes"));
		
		// Return true
		return true;
	}

}
