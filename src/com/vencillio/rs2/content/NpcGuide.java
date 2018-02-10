package com.vencillio.rs2.content;

import com.vencillio.core.definitions.ItemDropDefinition;
import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendNpcDisplay;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * A helper class for instantiating the Npc Guide interface.
 * 
 * @author Daniel
 * @author Chris
 */
public class NpcGuide {

	/**
	 * The integer configuration representing the interface.
	 */
	private static final int MONSTER_GUIDE_INTERFACE_ID = 59800;

	/**
	 * The starting index for the Item container in the interface.
	 */
	private static final int INTERFACE_ITEM_CONTAINER = 59806;

	/**
	 * The integer configuration for the title's string placement.
	 */
	private static final int INTERFACE_TITLE_ID = 59805;

	/**
	 * The starting index for the String container in the interface.
	 */
	private static final int INTERFACE_STRING_CONTAINER = 59821;

	/**
	 * Validates the drop table for the specified npc id and returns a generated
	 * interface based on the results.
	 * 
	 * @param player
	 *            The player to send the interface to.
	 * @param npcId
	 *            The npc id.
	 */
	public static void open(Player player, final int npcId) {

		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

		if (npcDef == null) {
			player.send(new SendMessage("@red@The ID " + Utility.format(npcId) + " does not exist in our database! Please check the NPC list."));
			return;
		}

		ItemDropDefinition table = GameDefinitionLoader.getItemDropDefinition(npcId);
		if (table == null) {
			clear(player);

			player.send(new SendInterface(MONSTER_GUIDE_INTERFACE_ID));
			player.send(new SendNpcDisplay(npcId, npcDef.getSize() > 1 ? 40 : 100));
			player.send(new SendString("@or1@Monster Information | @gre@" + npcDef.getName(), INTERFACE_TITLE_ID));
			for (int i = 0; i <= 3; i++) {
				player.send(new SendString(getInfo(npcDef, i), INTERFACE_STRING_CONTAINER + i));
			}
			return;
		}

		Item[] drops = table.getMostExpensiveDrops(8);

		if (drops == null) {
			DialogueManager.sendStatement(player, "Comparable returned null array.");
			return;
		}

		clear(player);

		player.send(new SendInterface(MONSTER_GUIDE_INTERFACE_ID));
		player.send(new SendNpcDisplay(npcId, npcDef.getSize() > 1 ? 40 : 100));
		player.send(new SendString("@or1@Monster Information | @gre@" + npcDef.getName(), INTERFACE_TITLE_ID));

		player.getClient().queueOutgoingPacket(new SendUpdateItems(INTERFACE_ITEM_CONTAINER, drops));

		for (int i = 0; i <= 3; i++) {
			player.send(new SendString(getInfo(npcDef, i), INTERFACE_STRING_CONTAINER + i));
		}

	}

	/**
	 * Sends configurations to the client to erase the interface.
	 * 
	 * @param player
	 *            The player that the configurations are being sent to.
	 */
	private static void clear(Player player) {
		// player.getPacketBuilder().sendMobHeadModel(0, INTERFACE_NPC_HEAD_ID);
		for (int loop = 0; loop < 3; loop++) {
			player.send(new SendString("", INTERFACE_STRING_CONTAINER + loop));
		}
		player.getClient().queueOutgoingPacket(new SendUpdateItems(INTERFACE_ITEM_CONTAINER, null));
	}

	/**
	 * Generates a string based on the {@link NpcDefinition} supplied.
	 * 
	 * @param npcDef
	 *            The {@link NpcDefinition}.
	 * @param index
	 *            The index identifier which decides which string to return.
	 * @return The generated string.
	 */
	private static String getInfo(NpcDefinition npcDef, int index) {
		switch (index) {
		case 0:
			return "@or1@".concat(npcDef.getName() + ":");
		case 1:
			return "@or1@ID: @gre@".concat(String.valueOf(npcDef.getId()));
		case 2:
			return "@or1@Level: @gre@".concat(String.valueOf(npcDef.getLevel()));
		case 3:
			return "@or1@Can attack: @gre@".concat(String.valueOf(npcDef.isAttackable()));
		default:
			return "Error";
		}
	}
}