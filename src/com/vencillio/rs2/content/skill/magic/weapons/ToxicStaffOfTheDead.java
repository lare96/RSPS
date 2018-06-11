package com.vencillio.rs2.content.skill.magic.weapons;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

/**
 * Created by Tanner on 6/9/2018.
 */
public class ToxicStaffOfTheDead {
	private static final int FULL = 11_000;

	public static boolean itemOnItem(Player player, Item itemUsed, Item usedWith) {
		if (itemUsed.getId() == 12904) {
			switch (usedWith.getId()) {
				case 12934:
					if (player.getInventory().hasAllItems(new Item(12934, 1))) {
						int min = Integer.MAX_VALUE;
						for (Item item : player.getInventory().getItems()) {
							if (item != null) {
								switch (item.getId()) {
									case 12934:
										if (item.getAmount() < min) {
											min = item.getAmount();
										}
										break;
								}
							}
						}
						if (min + player.getToxicStaffOfTheDead() > FULL) {
							min = FULL - player.getToxicStaffOfTheDead();
						}
						player.getInventory().remove(12934, min);
						player.setToxicStaffOfTheDead(min + player.getToxicStaffOfTheDead());
						player.getUpdateFlags().sendAnimation(new Animation(1979));
						player.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
						DialogueManager.sendItem1(player, "You infuse the staff of the dead with @dre@" + player.getToxicStaffOfTheDead() + "</col> charge" + (player.getToxicStaffOfTheDead() > 1 ? "s" : "") + ".", 12904);
						check(player);
					}
					break;
			}
		} else if (usedWith.getId() == 12904) {
			switch (itemUsed.getId()) {
				case 12934:
					if (player.getInventory().hasAllItems(new Item(12934, 1))) {
						int min = Integer.MAX_VALUE;
						for (Item item : player.getInventory().getItems()) {
							if (item != null) {
								switch (item.getId()) {
									case 12934:
										if (item.getAmount() < min) {
											min = item.getAmount();
										}
										break;
								}
							}
						}
						if (min + player.getToxicStaffOfTheDead() > FULL) {
							min = FULL - player.getToxicStaffOfTheDead();
						}
						player.getInventory().remove(12934, min);
						player.setToxicStaffOfTheDead(min + player.getToxicStaffOfTheDead());
						player.getUpdateFlags().sendAnimation(new Animation(1979));
						player.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
						DialogueManager.sendItem1(player, "You infuse the staff of the dead with @dre@" + player.getToxicStaffOfTheDead() + "</col> charge" + (player.getToxicStaffOfTheDead() > 1 ? "s" : "") + ".", 12904);
						check(player);
					}
					break;
			}
		} else {
			return false;
		}
		return false;
	}

	public static void check(Player player) {
		if (player.getToxicStaffOfTheDead() > 0) {
			player.send(new SendMessage("Your staff has " + player.getToxicStaffOfTheDead() + " charge" + (player.getToxicStaffOfTheDead() > 1 ? "s" : "") + "."));
		} else {
			player.send(new SendMessage("Your staff has no charges."));
		}
	}

	public static boolean itemOption(Player player, int i, int itemId) {
		if (itemId != 12904) {
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

	public static void unload(Player player) {
		player.getInventory().addOrCreateGroundItem(12934, player.getToxicStaffOfTheDead(), true);
		player.setToxicStaffOfTheDead(0);
		player.getInventory().get(player.getInventory().getItemSlot(12904)).setId(12902);
		player.send(new SendMessage("You have unloaded your staff."));
	}

	public static void ask(Player player, int itemId) {
		ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);
		String[][] info = { { "Are you sure you want to destroy this object?", "14174" }, { "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" }, { "", "14182" }, { "Currently unavailable", "14183" }, { itemDef.getName(), "14184" } };
		player.send(new SendUpdateItemsAlt(14171, itemId, 1, 0));
		for (int i = 0; i < info.length; i++) {
			player.send(new SendString(info[i][0], Integer.parseInt(info[i][1])));
		}
		player.send(new SendChatBoxInterface(14170));
	}
}
