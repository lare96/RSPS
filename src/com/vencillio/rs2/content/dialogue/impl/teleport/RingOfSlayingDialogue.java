package com.vencillio.rs2.content.dialogue.impl.teleport;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.magic.MagicSkill;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class RingOfSlayingDialogue extends Dialogue {

	private int itemId;
	private boolean operate = false;

	public RingOfSlayingDialogue(Player player, boolean operate, int itemId) {
		this.player = player;
		this.operate = operate;
		this.itemId = itemId;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case 9178:
			getPlayer().getMagic().teleport(3086, 3489, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId - 2 != 1702) {
					player.getEquipment().getItems()[2].setId(itemId - 2);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId - 2 != 1702) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId - 2, 1);
				}
			}
			player.getClient().queueOutgoingPacket(
					new SendMessage("@pur@You use up a charge from your "
							+ Item.getDefinition(itemId).getName()
							+ " and teleport away."));
			break;
		case 9179:
			getPlayer().getMagic().teleport(3093, 3244, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId - 2 != 1702) {
					player.getEquipment().getItems()[2].setId(itemId - 2);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId - 2 != 1702) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId - 2, 1);
				}
			}
			player.getClient().queueOutgoingPacket(
					new SendMessage("@pur@You use up a charge from your "
							+ Item.getDefinition(itemId).getName()
							+ " and teleport away."));
			break;
		case 9180:
			getPlayer().getMagic().teleport(2909, 3151, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId - 2 != 1702) {
					player.getEquipment().getItems()[2].setId(itemId - 2);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId - 2 != 1702) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId - 2, 1);
				}
			}
			player.getClient().queueOutgoingPacket(
					new SendMessage("@pur@You use up a charge from your "
							+ Item.getDefinition(itemId).getName()
							+ " and teleport away."));
			break;
		case 9181:
			getPlayer().getMagic().teleport(3356, 3268, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId - 2 != 1702) {
					player.getEquipment().getItems()[2].setId(itemId - 2);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId - 2 != 1702) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId - 2, 1);
				}
			}
			player.getClient().queueOutgoingPacket(
					new SendMessage("@pur@You use up a charge from your "
							+ Item.getDefinition(itemId).getName()
							+ " and teleport away."));
			break;
		}
		return false;
	}

	@Override
	public void execute() {
		DialogueManager.sendOption(player, new String[] { "Vannaka",
				"Slayer Tower", "Gold Member Slayer Dungeon",
				"Ancient Cavern Slayer Dungeon" });
	}
}