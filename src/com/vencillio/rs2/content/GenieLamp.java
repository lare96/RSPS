package com.vencillio.rs2.content;

import java.util.HashMap;

import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Class that handles genie lamp
 * @author Daniel
 *
 */
public class GenieLamp {
	
	/**
	 * Holds all the genie lamp data
	 * @author Daniel
	 *
	 */
	public enum GenieData {
		ATTACK(10252, 0),
		STRENGTH(10253, 2),
		RANGE(10254, 4),
		MAGIC(10255, 6),
		DEFENCE(11000, 1),
		HITPOINTS(11001, 3),
		PRAYER(11002, 5),
		AGILITY(11003, 16),
		HERBLORE(11004, 15),
		THIEVING(11005, 17),
		CRAFTING(11006, 12),
		RUNECRAFTING(11007, 20),
		SLAYER(47002, 18),
		FARMING(54090, 19),
		MINING(11008, 14),
		SMITHING(11009, 13),
		FISHING(11010, 10),
		COOKING(11011, 7),
		FIREMAKING(11012, 11),
		WOODCUTTING(11013, 8),
		FLETCHING(11014, 9);
		
		int buttonId, skillId;
		private GenieData(int buttonId, int skillId) {
			this.buttonId = buttonId;
			this.skillId = skillId;
		}
		
		public int getButton() {
			return buttonId;
		}
		
		public int getSkill() {
			return skillId;
		}
		
		public static HashMap<Integer, GenieData> genie = new HashMap<Integer, GenieData>();

		static {
			for (final GenieData genie : GenieData.values()) {
				GenieData.genie.put(genie.buttonId, genie);
			}
		}
		
	}
	
	/**
	 * Handles the clicking of interface
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean handle(Player player, int buttonId) {
		GenieData genie = GenieData.genie.get(buttonId);
		
		if (genie == null || player.getDelay().elapsed() < 1000 || !player.getInventory().hasItemId(2528)) {	
			return false;
		}
		
		if (player.getInterfaceManager().main != 2808) {
			player.send(new SendRemoveInterfaces());
			player.send(new SendMessage("That interface does not exist!"));
			return false;
		}

		player.getDelay().reset();
		player.send(new SendRemoveInterfaces());
		player.getInventory().remove(new Item(2528));
		player.getSkill().addExperience(genie.getSkill(), 1_000);
		player.send(new SendMessage("You rub on the lamp... and were given experience in "+Prestige.getSkillName(genie.getSkill())+"."));
		return true;
	}
}
