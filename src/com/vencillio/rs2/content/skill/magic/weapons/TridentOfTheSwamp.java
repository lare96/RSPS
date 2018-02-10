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

public class TridentOfTheSwamp {
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");
		
	private static final int FULL = 2_500;

	private int tridentCharge;
	
	public int getCharges() {
		return tridentCharge;
	}
	
	public TridentOfTheSwamp(int tridentCharge) {
		this.tridentCharge = tridentCharge;
		FORMATTER.setRoundingMode(RoundingMode.FLOOR);
	}
	
	public static boolean itemOnItem(Player player, Item itemUsed, Item usedWith) {
		if (itemUsed.getId() == 12899) {
			switch (usedWith.getId()) {
			case 554:
			case 560:
			case 562:
			case 12934:
				if (player.getInventory().hasAllItems(new Item(554, 5), new Item(560, 1), new Item(562, 1), new Item(12934, 1))) {
					int min = Integer.MAX_VALUE;
					for (Item item : player.getInventory().getItems()) {
						if (item != null) {
							switch (item.getId()) {
							case 554:
							case 560:
							case 562:
							case 12934:
								if (item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1) < min) {
									min = item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1);
								}
								break;
							}
						}
					}
					if (min + player.getSwampTrident().tridentCharge > FULL) {
						min = FULL - player.getSwampTrident().tridentCharge;
					}
					player.getInventory().remove(554, 5 * min);
					player.getInventory().remove(560, min);
					player.getInventory().remove(562, min);
					player.getInventory().remove(12934, 1 * min);
					player.getSwampTrident().tridentCharge = min + player.getSwampTrident().tridentCharge;
					player.getUpdateFlags().sendAnimation(new Animation(1979));
					player.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
					DialogueManager.sendItem1(player, "You infuse the trident with @dre@" + player.getSwampTrident().tridentCharge + "</col> charge" + (player.getSwampTrident().tridentCharge > 1 ? "s" : "") + ".", 12899);
					check(player);
				}
				break;
			}			
		} else if (usedWith.getId() == 12899) {
			switch (itemUsed.getId()) {
			case 554:
			case 560:
			case 562:
			case 12934:
				if (player.getInventory().hasAllItems(new Item(554, 5), new Item(560, 1), new Item(562, 1), new Item(12934, 1))) {
					int min = Integer.MAX_VALUE;
					for (Item item : player.getInventory().getItems()) {
						if (item != null) {
							switch (item.getId()) {
							case 554:
							case 560:
							case 562:
							case 12934:
								if (item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1) < min) {
									min = item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1);
								}
								break;
							}
						}
					}
					if (min + player.getSwampTrident().tridentCharge > FULL) {
						min = FULL - player.getSwampTrident().tridentCharge;
					}
					player.getInventory().remove(554, 5 * min);
					player.getInventory().remove(560, min);
					player.getInventory().remove(562, min);
					player.getInventory().remove(12934, 1 * min);
					player.getSwampTrident().tridentCharge = min + player.getSwampTrident().tridentCharge;
					player.getUpdateFlags().sendAnimation(new Animation(1979));
					player.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
					DialogueManager.sendItem1(player, "You infuse the trident with @dre@" + player.getSwampTrident().tridentCharge + "</col> charge" + (player.getSwampTrident().tridentCharge > 1 ? "s" : "") + ".", 12899);
					check(player);
				}
				break;		
			}			
		} else {
			return false;
		}
		return false;
	}
	
	public static boolean itemOption(Player player, int i, int itemId) {
		if (itemId != 12899) {
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
	
	public static void check(Player player) {
		TridentOfTheSwamp trident = player.getSwampTrident();
	
		if (trident.tridentCharge > 0) {
			player.send(new SendMessage("Your trident has " + trident.tridentCharge + " charge" + (trident.tridentCharge > 1 ? "s" : "") + "."));
		} else {
			player.send(new SendMessage("Your trident has no charges."));
		}
	}
	
	public static boolean hasTrident(Player player) {
		return player.getEquipment().isWearingItem(12899, EquipmentConstants.WEAPON_SLOT);
	}

	public static void unload(Player player) {
		player.getInventory().addOrCreateGroundItem(554, player.getSwampTrident().tridentCharge * 5, true);
		player.getInventory().addOrCreateGroundItem(560, player.getSwampTrident().tridentCharge, true);
		player.getInventory().addOrCreateGroundItem(562, player.getSwampTrident().tridentCharge, true);
		player.getSwampTrident().tridentCharge = 0;
		player.send(new SendMessage("You have unloaded your trident."));
	}

	public static void degrade(Player player) {
		player.getSwampTrident().tridentCharge--;
		
		if (player.getSwampTrident().tridentCharge == 0) {
			player.send(new SendMessage("You do not have any charges left to use this."));
		}
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
	
}
