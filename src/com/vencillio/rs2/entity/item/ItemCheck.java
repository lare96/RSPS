package com.vencillio.rs2.entity.item;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.skill.cooking.CookingData;
import com.vencillio.rs2.content.skill.herblore.FinishedPotionData;
import com.vencillio.rs2.entity.player.Player;

public class ItemCheck {
	
	public static boolean hasAnyItem(Player p) {
		for (Item i : p.getInventory().getItems()) {
			if (i != null) {
				return true;
			}
		}

		for (Item i : p.getEquipment().getItems()) {
			if (i != null) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean hasIronArmour(Player player) {
		if (player.getInventory().hasItemId(12810) || player.getInventory().hasItemId(12811) || player.getInventory().hasItemId(12812)) {
			return true;
		}
		if (player.getBank().hasItemId(12810) || player.getBank().hasItemId(12811) || player.getBank().hasItemId(12812)) {
			return true;
		}
		if (player.getEquipment().contains(12810) || player.getEquipment().contains(12811) || player.getEquipment().contains(12812)) {
			return true;
		}	
		return false;
	}
	
	public static boolean hasUltimateArmour(Player player) {
		if (player.getInventory().hasItemId(12813) || player.getInventory().hasItemId(12814) || player.getInventory().hasItemId(12815)) {
			return true;
		}
		if (player.getBank().hasItemId(12813) || player.getBank().hasItemId(12814) || player.getBank().hasItemId(12815)) {
			return true;
		}
		if (player.getEquipment().contains(12813) || player.getEquipment().contains(12814) || player.getEquipment().contains(12815)) {
			return true;
		}	
		return false;
	}
	
	public static boolean hasGodCape(Player player) {
		if (player.getInventory().hasItemId(2412) || player.getInventory().hasItemId(2413) || player.getInventory().hasItemId(2414)) {
			return true;
		}
		if (player.getBank().hasItemId(2412) || player.getBank().hasItemId(2413) || player.getBank().hasItemId(2414)) {
			return true;
		}
		if (player.getEquipment().contains(2412) || player.getEquipment().contains(2413) || player.getEquipment().contains(2414)) {
			return true;
		}
		return false;
	}

	public static boolean hasBNeckAndObbyMaulCombo(Player p) {
		Item w = p.getEquipment().getItems()[3];
		Item n = p.getEquipment().getItems()[2];

		return (w != null) && (w.getId() == 6528) && (n != null) && (n.getId() == 11128);
	}

	public static boolean hasConsumables(Player p) {
		for (Item i : p.getInventory().getItems()) {
			if (i != null) {
				int id = i.getId();
				if ((GameDefinitionLoader.getFoodDefinition(id) != null) || (GameDefinitionLoader.getPotionDefinition(id) != null)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasDFireShield(Player p) {
		return (p.getEquipment().getItems()[5] != null) && (p.getEquipment().getItems()[5].getId() == 11283);
	}

	public static final boolean hasEquipmentOn(Player p) {
		for (Item i : p.getEquipment().getItems()) {
			if (i != null) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasHerbloreIngredients(Player p) {
		for (Item i : p.getInventory().getItems()) {
			if (i != null) {
				for (FinishedPotionData k : FinishedPotionData.values()) {
					if (i.getId() == k.getItemNeeded()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean hasRawFood(Player p) {
		for (Item i : p.getInventory().getItems()) {
			if ((i != null) && (CookingData.forId(i.getId()) != null)) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Gets if the item is a dyed whip
	 * 
	 * @param i
	 *            The item being checked
	 * @return
	 */
	public static final boolean isItemDyedWhip(Item i) {
		if (i != null) {
			return (i.getId() == 12773) || (i.getId() == 12774);
		}
		return false;
	}

	/**
	 * Gets if the player is using a balmung
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isUsingBalmung(Player p) {
		return (p.getEquipment().getItems()[3] != null) && (p.getEquipment().getItems()[3].getId() == 15403);
	}

	/**
	 * Gets if the player is using a crossbow
	 * 
	 * @param p
	 *            The player using a crossbow
	 * @return
	 */
	public static boolean isUsingCrossbow(Player p) {
		Item weapon = p.getEquipment().getItems()[3];

		if (weapon != null) {
			int i = weapon.getId();
			return (i == 837) || (i == 4734) || (i == 9174) || (i == 9178) || (i == 9180) || (i == 9182) || (i == 9184) || (i == 9185) || (i == 11785);
		}

		return false;
	}

	/**
	 * Gets if the player is wearing an anti-dragonfire shield
	 * 
	 * @param p
	 *            The player wearing the shield
	 * @return
	 */
	public static final boolean isWearingAntiDFireShield(Player p) {
		Item shield = p.getEquipment().getItems()[EquipmentConstants.SHIELD_SLOT];
		if (shield != null) {
			int index = shield.getId();
			return (index == 11283) || (index == 1540);
		}
		return false;
	}

	public static boolean wearingFullBarrows(Player player, String check) {
		int[] slots = { 0, 4, 7, 3 };
		Item[] equip = player.getEquipment().getItems();

		for (int i = 0; i < slots.length; i++) {
			if (equip[slots[i]] == null) {
				return false;
			}
			if (!equip[slots[i]].getDefinition().getName().contains(check)) {
				return false;
			}

		}

		return true;
	}

	public static boolean wearingFullVoidMagic(Player player) {
		int[] slots = { 0, 4, 7, 9 };
		int[] ids = { 11663, 8839, 8840, 8842 };
		Item[] equip = player.getEquipment().getItems();

		for (int i = 0; i < slots.length; i++) {
			if (equip[slots[i]] == null) {
				return false;
			}
			if (equip[slots[i]].getId() != ids[i]) {
				return false;
			}

		}

		return true;
	}

	public static boolean wearingFullVoidMelee(Player player) {
		int[] slots = { 0, 4, 7, 9 };
		int[] ids = { 11665, 8839, 8840, 8842 };
		Item[] equip = player.getEquipment().getItems();

		for (int i = 0; i < slots.length; i++) {
			if (equip[slots[i]] == null) {
				return false;
			}
			if (equip[slots[i]].getId() != ids[i]) {
				return false;
			}

		}

		return true;
	}

	public static boolean wearingFullVoidRanged(Player player) {
		int[] slots = { 0, 4, 7, 9 };
		int[] ids = { 11664, 8839, 8840, 8842 };
		Item[] equip = player.getEquipment().getItems();

		for (int i = 0; i < slots.length; i++) {
			if (equip[slots[i]] == null) {
				return false;
			}
			if (equip[slots[i]].getId() != ids[i]) {
				return false;
			}

		}

		return true;
	}
}
