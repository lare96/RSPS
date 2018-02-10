package com.vencillio.rs2.content.skill.melee;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles Serpentine Helment
 * @author Daniel
 *
 */
public class SerpentineHelmet {
	
	/**
	 * Limit of charges
	 */
	private static final int FULL = 11_000;
	
	/**
	 * Current charges
	 */
	private int charges;
	
	/**
	 * SerpentineHelmet
	 * @param charges
	 */
	public SerpentineHelmet(int charges) {
		this.charges = charges;
	}
	
	/**
	 * Grabs the charges
	 * @return
	 */
	public int getCharges() {
		return charges;
	}
	
	/**
	 * Checks if player has helmet equipped
	 * @param player
	 * @return
	 */
	public static boolean hasHelmet(Player player) {
		return player.getEquipment().isWearingItem(12931, EquipmentConstants.HELM_SLOT);
	}
	
	/**
	 * Handles creating helmet & chraging
	 * @param player
	 * @param itemUsed
	 * @param usedWith
	 * @return
	 */
	public static boolean itemOnItem(Player player, Item itemUsed, Item usedWith) {
		if ((itemUsed.getId() == 12929 && usedWith.getId() == 12934) || (usedWith.getId() == 12929 && itemUsed.getId() == 12934)) {
			int amount = player.getInventory().getItemAmount(12934);
			if (amount >= FULL) {
				amount = FULL;
			}
			player.getInventory().remove(12934, amount);
			player.getSerpentineHelment().charges += amount;
			if (player.getSerpentineHelment().getCharges() > 0) {
				player.getInventory().remove(12929, 1);
				player.getInventory().add(12931, 1);
			}
			check(player);
			return true;
		}
		
		if ((itemUsed.getId() == 12931 && usedWith.getId() == 12934) || (usedWith.getId() == 12931 && itemUsed.getId() == 12934)) {
			int amount = player.getInventory().getItemAmount(12934);
			if (player.getSerpentineHelment().getCharges() >= FULL || player.getSerpentineHelment().getCharges() + amount >= FULL) {
				amount = FULL - player.getSerpentineHelment().getCharges();
			}
			
			if (amount <= 0) {
				return true;
			}
			
			player.getInventory().remove(12934, amount);
			player.getSerpentineHelment().charges += amount;
			check(player);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Handles helmet options
	 * @param player
	 * @param i
	 * @param itemId
	 * @return
	 */
	public static boolean itemOption(Player player, int i, int itemId) {
		if (itemId != 12931) {
			return false;
		}
		
		switch (i) {
		case 1:
			check(player);
			return true;
			
		case 4:
			uncharge(player);
			return true;
			
		}
	
		return true;
	}
	
	/**
	 * Checks the current charges
	 * @param player
	 */
	public static void check(Player player) {
		int percent = player.getSerpentineHelment().getCharges() * 100 / FULL;
		player.send(new SendMessage("Please not the helment effect has not been added yet!"));
		player.send(new SendMessage("Charges: <col=007F00>" + Utility.format(player.getSerpentineHelment().getCharges()) + " </col>(<col=007F00>" + percent + "%</col>)"));
	}
	
	/**
	 * Uncharges the helmet
	 * @param player
	 */
	public static void uncharge(Player player) {
		if (player.getSerpentineHelment().getCharges() == 0) {
			player.send(new SendMessage("You do not have any charges!"));
			return;
		}
		int amount = player.getSerpentineHelment().getCharges();
		
		player.getSerpentineHelment().charges = 0;
		player.getInventory().remove(12931, 1);
		player.getInventory().addOrCreateGroundItem(12934, amount, true);
		player.getInventory().addOrCreateGroundItem(12929, 1, true);
	
	}
	
}