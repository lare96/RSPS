package com.vencillio.rs2.content.skill.magic.weapons;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

public class TridentOfTheSeas {

	private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");
	
	private static final int FULL = 2_500;

	private int tridentCharge;
	
	public int getCharges() {
		return tridentCharge;
	}
	
	/*
	 * 1251 - trident player gfx
	 * 1252 - trident projectile
	 * 
	 * 1040 - swamp projectile
	 * 1042 - swamp spell contact
	 * 665 - player spell gfx
	 */

	public TridentOfTheSeas(int tridentCharge) {
		this.tridentCharge = tridentCharge;
		FORMATTER.setRoundingMode(RoundingMode.FLOOR);
	}

	public static boolean itemOnItem(Player player, Item itemUsed, Item usedWith) {
		Item used = null;
		Item with = null;
		
		if (itemUsed.getId() == 11907 || itemUsed.getId() == 11908) {
			used = itemUsed.getSingle();
			with = usedWith.getSingle();
		} else if (usedWith.getId() == 11907 || usedWith.getId() == 11908) {
			used = usedWith.getSingle();
			with = itemUsed.getSingle();
		}

		if (used == null || with == null) {
			return false;
		}
		
		switch (with.getId()) {
		case 554:
		case 560:
		case 562:
		case 995:
			if (player.getInventory().hasAllItems(new Item(554, 5), new Item(560), new Item(562), new Item(995, 500))) {
				int min = Integer.MAX_VALUE;
				for (Item item : player.getInventory().getItems()) {
					if (item != null) {
						switch (item.getId()) {
						case 554:
						case 560:
						case 562:
						case 995:
							if (item.getAmount() / (item.getId() == 995 ? 500 : item.getId() == 554 ? 5 : 1) < min) {
								min = item.getAmount() / (item.getId() == 995 ? 500 : item.getId() == 554 ? 5 : 1);
							}
							break;
						}
					}
				}
				
				if (min + player.getSeasTrident().tridentCharge > FULL) {
					min = FULL - player.getSeasTrident().tridentCharge;
				}
				
				if (min > 0) {
					if (usedWith.getId() == 11908) {
						int slot = player.getInventory().getItemSlot(usedWith.getId());
						player.getInventory().get(slot).setId(11907);
					} else if (itemUsed.getId() == 11908) {
						int slot = player.getInventory().getItemSlot(itemUsed.getId());
						player.getInventory().get(slot).setId(11907);
					}
					
					player.getInventory().remove(554, 5 * min);
					player.getInventory().remove(560, min);
					player.getInventory().remove(562, min);
					player.getInventory().remove(995, 500 * min);
					player.getSeasTrident().tridentCharge = min + player.getSeasTrident().tridentCharge;
					player.getUpdateFlags().sendAnimation(new Animation(1979));
					player.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
					DialogueManager.sendItem1(player, "You infuse the trident with @dre@" + player.getSeasTrident().tridentCharge + "</col> charge" + (player.getSeasTrident().tridentCharge > 1 ? "s" : "") + ".", 11907);
					check(player);
				}
			}
			return true;
		}

		return false;
	}

	public static void check(Player player) {
		TridentOfTheSeas trident = player.getSeasTrident();

		if (trident.tridentCharge > 0) {
			player.send(new SendMessage("Your trident has " + trident.tridentCharge + " charge" + (trident.tridentCharge > 1 ? "s" : "") + "."));
		} else {
			player.send(new SendMessage("Your trident has no charges."));
		}
	}

	public static boolean hasTrident(Player player) {
		return player.getEquipment().isWearingItem(11907, EquipmentConstants.WEAPON_SLOT);
	}

	public static void unload(Player player) {
		player.getInventory().addOrCreateGroundItem(554, player.getSeasTrident().tridentCharge * 5, true);
		player.getInventory().addOrCreateGroundItem(560, player.getSeasTrident().tridentCharge, true);
		player.getInventory().addOrCreateGroundItem(562, player.getSeasTrident().tridentCharge, true);
		player.getSeasTrident().tridentCharge = 0;
		int slot = player.getInventory().getItemSlot(11907);
		player.getInventory().get(slot).setId(11908);
	}

	public static boolean itemOption(Player player, int i, int itemId) {
		if (itemId != 11907 && itemId != 11908) {
			return false;
		}
		switch (i) {
		case 1:
		case 2:
			check(player);
			return true;
		case 3:
			unload(player);
			return true;
		case 4:
			ask(player, itemId);
			player.getAttributes().set("ASK_KEY", 1);
			return true;
		}
		return false;
	}

	public static void ask(Player player, int itemId) {
		ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);
		String[][] info = { { "Are you sure you want to destroy this object?", "14174" }, { "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" }, { "", "14182" }, { "If you uncharge the trident, all runes will fall out.", "14183" }, { itemDef.getName(), "14184" } };
		player.send(new SendUpdateItemsAlt(14171, itemId, 1, 0));
		for (int i = 0; i < info.length; i++) {
			player.send(new SendString(info[i][0], Integer.parseInt(info[i][1])));
		}
		player.send(new SendChatBoxInterface(14170));
	}

	public static void degrade(Player player) {
		player.getSeasTrident().tridentCharge--;
		
		if (player.getSeasTrident().tridentCharge == 0) {
			player.send(new SendMessage("The trident needs to be charged with 500 coins, 1 death, 1 chaos, and 5 fire runes."));
			player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].setId(11908);
		}
	}
}