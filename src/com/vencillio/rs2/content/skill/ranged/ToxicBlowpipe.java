package com.vencillio.rs2.content.skill.ranged;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

public class ToxicBlowpipe {

	private static final int FULL = 16_383;

	private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");

	private Item blowpipeAmmo;
	private int blowpipeCharge;

	public ToxicBlowpipe(Item blowpipeAmmo, int blowpipeCharge) {
		this.blowpipeAmmo = blowpipeAmmo;
		this.blowpipeCharge = blowpipeCharge;
		FORMATTER.setRoundingMode(RoundingMode.FLOOR);
	}

	public Item getBlowpipeAmmo() {
		return blowpipeAmmo;
	}
	
	public int getBlowpipeCharge() {
		return blowpipeCharge;
	}

	public static boolean itemOnItem(Player player, Item itemUsed, Item usedWith) {
		if (itemUsed.getId() == 12924 || itemUsed.getId() == 12926 || usedWith.getId() == 12924 || usedWith.getId() == 12926) {
			if (usedWith.getId() == 12934) {
				if (player.getToxicBlowpipe().blowpipeCharge/ 3 == FULL) {
					return true;
				}
				
				int added = 0;
				if (player.getToxicBlowpipe().blowpipeCharge / 3 + usedWith.getAmount() > FULL) {
					added = FULL - player.getToxicBlowpipe().blowpipeCharge / 3;
				} else {
					added = usedWith.getAmount();
				}
				player.getToxicBlowpipe().blowpipeCharge += added * 3;
				player.getInventory().remove(usedWith.getId(), added);
				int slot = player.getInventory().getItemSlot(itemUsed.getId());
				player.getInventory().get(slot).setId(12926);
				check(player);
				return true;
			} else if (itemUsed.getId() == 12934) {
				if (player.getToxicBlowpipe().blowpipeCharge/ 3 == FULL) {
					return true;
				}
				int added = 0;
				if (player.getToxicBlowpipe().blowpipeCharge/ 3 + itemUsed.getAmount() > FULL) {
					added = FULL - player.getToxicBlowpipe().blowpipeCharge / 3;
				} else {
					added = itemUsed.getAmount();
				}
				player.getToxicBlowpipe().blowpipeCharge += added * 3;
				player.getInventory().remove(itemUsed.getId(), added);
				int slot = player.getInventory().getItemSlot(usedWith.getId());
				player.getInventory().get(slot).setId(12926);
				check(player);
				return true;
			}
		}
		
		if (player.getToxicBlowpipe().blowpipeAmmo != null && player.getToxicBlowpipe().blowpipeAmmo.getAmount() == FULL) {
			return true;
		}

		Item dart = null;

		switch (itemUsed.getId()) {
		case 806:
		case 807:
		case 808:
		case 809:
		case 810:
		case 811:
		case 11230:
			dart = new Item(itemUsed);
		}

		if (dart == null) {
			switch (usedWith.getId()) {
			case 806:
			case 807:
			case 808:
			case 809:
			case 810:
			case 811:
			case 11230:
				dart = new Item(usedWith);
			}
		}

		if (dart == null) {
			return false;
		}
		
		if (usedWith.getId() == 12924) {
			int slot = player.getInventory().getItemSlot(usedWith.getId());
			player.getInventory().get(slot).setId(12926);
		} else if (itemUsed.getId() == 12924) {
			int slot = player.getInventory().getItemSlot(itemUsed.getId());
			player.getInventory().get(slot).setId(12926);
		}
		
		if (usedWith.getId() == 12924 || usedWith.getId() == 12926 || itemUsed.getId() == 12924 || itemUsed.getId() == 12926) {
			if (player.getToxicBlowpipe().blowpipeAmmo != null) {
				if (dart.getAmount() + player.getToxicBlowpipe().blowpipeAmmo.getAmount() > FULL) {
					dart.setAmount((dart.getAmount() + player.getToxicBlowpipe().blowpipeAmmo.getAmount()) - FULL);
				}
				player.getToxicBlowpipe().blowpipeAmmo.add(dart.getAmount());
			} else if (dart.getAmount() > FULL) {
				dart.setAmount(FULL);
				player.getToxicBlowpipe().blowpipeAmmo = dart;
			} else {
				player.getToxicBlowpipe().blowpipeAmmo = dart;
			}
		}
		
		player.getInventory().remove(dart);
		check(player);
		return false;
	}

	public static void check(Player player) {
		String ammo = "None";
		if (player.getToxicBlowpipe().blowpipeAmmo != null) {
			ammo = player.getToxicBlowpipe().blowpipeAmmo.getDefinition().getName() + " x " + player.getToxicBlowpipe().blowpipeAmmo.getAmount();
		}
		String scales = FORMATTER.format((player.getToxicBlowpipe().blowpipeCharge/ 3.0) * 100.0 / (double) FULL) + "%";
		player.send(new SendMessage("Darts: <col=007F00>" + ammo + "</col>. Scales: <col=007F00>" + scales));
	}

	public static boolean hasBlowpipe(Player player) {
		return player.getEquipment().isWearingItem(12926, EquipmentConstants.WEAPON_SLOT);
	}

	public static void unload(Player player) {
		if (player.getToxicBlowpipe().blowpipeCharge > 0) {
			player.getInventory().addOrCreateGroundItem(12934, player.getToxicBlowpipe().blowpipeCharge/ 3, true);
		}
		if (player.getToxicBlowpipe().blowpipeAmmo != null) {
			player.getInventory().addOrCreateGroundItem(player.getToxicBlowpipe().blowpipeAmmo.getId(), player.getToxicBlowpipe().blowpipeAmmo.getAmount(), true);
		}
		player.getToxicBlowpipe().blowpipeCharge = 0;
		player.getToxicBlowpipe().blowpipeAmmo = null;
		player.getInventory().get(player.getInventory().getItemSlot(12926)).setId(12924);
	}

	public static boolean itemOption(Player player, int i, int itemId) {
		if (itemId != 12926) {
			return false;
		}
		switch (i) {
		case 1:
		case 2:
			check(player);
			return true;
		case 3:
			if (player.getToxicBlowpipe().blowpipeAmmo != null) {
				player.getInventory().addOrCreateGroundItem(player.getToxicBlowpipe().blowpipeAmmo.getId(), player.getToxicBlowpipe().blowpipeAmmo.getAmount(), true);
			}
			player.getToxicBlowpipe().blowpipeAmmo = null;
			return true;
		case 4:
			ask(player, 12926);
			player.getAttributes().set("ASK_KEY", 0);
			return true;
		}
		return false;
	}
	
	public static void ask(Player player, int itemId) {
		ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);
		String[][] info = { { "Are you sure you want to destroy this object?", "14174" }, { "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" }, { "", "14182" }, { "If you uncharge the blowpipe, all scales and darts will fall out.", "14183" }, { itemDef.getName(), "14184" } };
		player.send(new SendUpdateItemsAlt(14171, itemId, 1, 0));
		for (int i = 0; i < info.length; i++) {
			player.send(new SendString(info[i][0], Integer.parseInt(info[i][1])));
		}
		player.send(new SendChatBoxInterface(14170));
	}

	public static void degrade(Player player) {
		ToxicBlowpipe blowpipe = player.getToxicBlowpipe();
		blowpipe.blowpipeCharge -= 2;

		Item cape = player.getEquipment().getItems()[1];
		if (cape != null && (cape.getId() == 10499 || cape.getId() == 10498)) {
			if (Math.random() > 1 - 1/4.0) {
				if (player.getCombat().getAttacking().getLocation() != null && Math.random() > 1 - 1/3.0) {
					player.getGroundItems().drop(blowpipe.blowpipeAmmo.getSingle(), player.getCombat().getAttacking().getLocation());
				}
				blowpipe.blowpipeAmmo.remove(1);
			}
		} else {
			if (player.getCombat().getAttacking().getLocation() != null && Math.random() > 1 - 1/3.0) {
				player.getGroundItems().drop(blowpipe.blowpipeAmmo.getSingle(), player.getCombat().getAttacking().getLocation());
			}
			blowpipe.blowpipeAmmo.remove(1);
		}
		if (blowpipe.blowpipeCharge == 0 || blowpipe.blowpipeAmmo.getAmount() == 0) {
			if (blowpipe.blowpipeAmmo.getAmount() == 0) {
				blowpipe.blowpipeAmmo = null;
			}
			player.send(new SendMessage("The blowpipe needs to be charged with Zulrah's scales and loaded with darts."));
		}
		if (blowpipe.blowpipeCharge == 0 && blowpipe.blowpipeAmmo == null) {
			player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].setId(12924);
		}
	}
}