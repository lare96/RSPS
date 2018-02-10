package com.vencillio.rs2.content.skill.herblore;

import java.util.ArrayList;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles making Super combat potion
 * @author Daniel
 *
 */
public class SuperCombatPotion {
	
	/**
	 * The Super combat potion identification
	 */
	private final static int POTION_ID = 12695;
	
	/**
	 * All the required items to make the potion
	 */
	private final static int[] ITEMS = { 2436, 2440, 2442, 269 };
	
	/**
	 * The Herblore level required to make the potion
	 */
	private final static short LEVEL = 90;
	
	/**
	 * The experience given for making the potion
	 */
	private final static short EXPERIENCE = 150;
	
	/**
	 * Handles item on item
	 * @param player
	 * @param itemUsed
	 * @param usedWith
	 * @return
	 */
	public static boolean itemOnItem(Player player, Item itemUsed, Item usedWith) {
	
		//Check if item is being used on the same item 
		if (itemUsed.getId() == usedWith.getId()) {
			return false;
		}
		
		//Creates item if items being used are correct
		for (int index = 0; index < ITEMS.length; index++) {
			if (itemUsed.getId() == ITEMS[index] || usedWith.getId() == ITEMS[index]) {
				create(player);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates the potion
	 * @param player
	 */
	private static void create(Player player) {
	
		//Checks if player meets the level requirement
		if (player.getLevels()[Skills.HERBLORE] < LEVEL) {
			player.send(new SendMessage("You need a Herblore level of " + LEVEL + " to do this!"));
			return;
		}
		
		//Checks if player has all items
		boolean hasItems = true;
		
		//List of all required items
		ArrayList<String> required = new ArrayList<String>();
		
		//Checks the inventory to see if player has all items
		for (int index = 0; index < ITEMS.length; index++) {
			if (!player.getInventory().hasItemId(ITEMS[index])) {
				String name = GameDefinitionLoader.getItemDef(ITEMS[index]).getName();
				hasItems = false;
				required.add(name);
				continue;
			}
		}
		
		//Send message of missing items if player does not have all items
		if (!hasItems) {
			player.send(new SendMessage("@dre@The following items are required: " + required + "."));
			return;
		}
		
		//Removes all the items from inventory
		for (int index = 0; index < ITEMS.length; index++) {
			player.getInventory().remove(ITEMS[index]);			
		}
		
		//Adds the potion to inventory
		player.getInventory().add(POTION_ID, 1);
		
		//Sends dialogue message of success
		DialogueManager.sendItem1(player, "You have combined all the ingredients!", POTION_ID);
		
		//Adds experience to the sill
		player.getSkill().addExperience(Skills.HERBLORE, EXPERIENCE);
	}

}
