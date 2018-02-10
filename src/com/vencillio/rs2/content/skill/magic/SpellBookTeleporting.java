package com.vencillio.rs2.content.skill.magic;

import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * SpellBookTeleportation
 * 
 * @author Reece - http://www.rune-server.org/members/Valiant
 * @since April 28th, 2015
 * 
 */
public final class SpellBookTeleporting {

	/* Teleportation Rune Constants */
	private final static int LAW_RUNE = 563;
	private final static int AIR_RUNE = 556;
	private final static int FIRE_RUNE = 554;
	private final static int EARTH_RUNE = 557;
	private final static int WATER_RUNE = 555;
	private final static int SOUL_RUNE = 566;
	private final static int BLOOD_RUNE = 565;

	/**
	 * Teleportation Enumeration
	 */
	enum TeleportationData {

		HOME_TELEPORT(3086, 3489, 75010, 1, new int[] { AIR_RUNE, 3, FIRE_RUNE, 1, LAW_RUNE, 1 }),

		VARROCK(3210, 3424, 4140, 25, new int[] { AIR_RUNE, 3, FIRE_RUNE, 1, LAW_RUNE, 1 }),

		LUMBRIDGE(3222, 3218, 4143, 31, new int[] { AIR_RUNE, 3, EARTH_RUNE, 1, LAW_RUNE, 1 }),

		FALADOR(2964, 3378, 4146, 37, new int[] { AIR_RUNE, 3, WATER_RUNE, 1, LAW_RUNE, 1 }),

		CAMELOT(2757, 3477, 4150, 45, new int[] { AIR_RUNE, 3, LAW_RUNE, 1, LAW_RUNE, 1 }),

		ARDOUGNE(2662, 3305, 6004, 51, new int[] { WATER_RUNE, 2, LAW_RUNE, 2, LAW_RUNE, 2 }),

		WATCH_TOWER(3087, 3500, 6005, 58, new int[] { EARTH_RUNE, 2, LAW_RUNE, 2, LAW_RUNE, 2 }),

		TROLLHEIM(3243, 3513, 29031, 61, new int[] { FIRE_RUNE, 2, LAW_RUNE, 2, LAW_RUNE, 2 }),

		HOME_TELEPORT_ANCIENT(3086, 3489, 84237, 1, new int[] { AIR_RUNE, 3, FIRE_RUNE, 1, LAW_RUNE, 1 }),

		PADDEWWA(3097, 9882, 50235, 54, new int[] { AIR_RUNE, 1, FIRE_RUNE, 1, LAW_RUNE, 2 }),

		SENNTISTEN(3322, 3336, 50245, 60, new int[] { LAW_RUNE, 2, SOUL_RUNE, 1, SOUL_RUNE, 1 }),

		KHARYLL(3492, 3471, 50253, 66, new int[] { LAW_RUNE, 2, BLOOD_RUNE, 1, BLOOD_RUNE, 1 });

		// The teleport location
		private final int x, y, req;

		// the runes required to teleport
		private final int[] runes;

		// the spellbook button
		private final int button;

		// Enum Constructment
		TeleportationData(final int x, final int y, final int button, final int req, final int[] runes) {
			this.x = x;
			this.y = y;
			this.button = button;
			this.req = req;
			this.runes = runes;
		}

		// gets the required runes to teleport
		private int[] getRunes() {
			return runes;
		}

		// gets the X axis teleport location
		private int getX() {
			return x;
		}

		// gets the Y axis teleport location
		private int getY() {
			return y;
		}

		// gets the magic requirement to teleport
		private int getReq() {
			return req;
		}

		// gets the button clicked on for teleport
		private int getButton() {
			return button;
		}

		/** Returns the data for the clicking button id recieved */
		private static final TeleportationData forId(int button) {
			for (TeleportationData data : TeleportationData.values()) {
				if (button == data.getButton()) {
					return data;
				}
			}
			return null;
		}
	}

	/**
	 * Manages the actual teleport action for the player
	 * 
	 * @param player
	 *            the player teleporting
	 * @param button
	 *            the button recieved
	 */
	public static final void teleport(final Player player, final int button) {
		final TeleportationData data = TeleportationData.forId(button);
		if (data == null) {
			return;
		}
		if (button == 75010 || button == 84237 || button == 117048) {
			player.getMagic().teleport(data.getX(), data.getY(), 0, TeleportTypes.SPELL_BOOK);
			return;
		}
		if (PlayerConstants.isOwner(player)) {
			player.getMagic().teleport(data.getX(), data.getY(), 0, TeleportTypes.SPELL_BOOK);
			return;
		}
		if (player.getInventory().hasItemId(new Item(data.getRunes()[0], data.getRunes()[1])) && player.getInventory().hasItemId(new Item(data.getRunes()[2], data.getRunes()[3])) && player.getInventory().hasItemId(new Item(data.getRunes()[4], data.getRunes()[5]))) {
			if (player.getSkill().getLevels()[Skills.MAGIC] >= data.getReq()) {
				player.getMagic().teleport(data.getX(), data.getY(), 0, TeleportTypes.SPELL_BOOK);
				player.getInventory().remove(new Item(data.getRunes()[0], data.getRunes()[1]));
				player.getInventory().remove(new Item(data.getRunes()[2], data.getRunes()[3]));
				if (data.getRunes()[2] == data.getRunes()[4] && data.getRunes()[3] == data.getRunes()[5]) {
					return;
				} else {
					player.getInventory().remove(new Item(data.getRunes()[4], data.getRunes()[5]));
				}
				return;
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You don't have a high enough magic level to cast this spell."));
				return;
			}
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have the required runes to cast this spell."));
			return;
		}
	}
}
