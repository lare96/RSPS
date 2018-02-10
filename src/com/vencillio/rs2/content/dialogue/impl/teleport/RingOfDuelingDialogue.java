package com.vencillio.rs2.content.dialogue.impl.teleport;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.magic.MagicSkill;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class RingOfDuelingDialogue extends Dialogue {

	private int itemId;
	private boolean operate = false;

	public RingOfDuelingDialogue(Player player, boolean operate, int itemId) {
		this.player = player;
		this.operate = operate;
		this.itemId = itemId;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case 9157:
			getPlayer().getMagic().teleport(2659, 2661, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId + 2 != 2568) {
					player.getEquipment().getItems()[12].setId(1639);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId + 2 != 2568) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId + 2, 1);
				}
			}
			player.getClient().queueOutgoingPacket(
					new SendMessage("@pur@You use up a charge from your "
							+ Item.getDefinition(itemId).getName()
							+ " and teleport away."));
			break;
		case 9158:
			getPlayer().getMagic().teleport(3356, 3268, 0,
					MagicSkill.TeleportTypes.SPELL_BOOK);
			if (operate == true) {
				if (itemId + 2 != 2568) {
					player.getEquipment().getItems()[12].setId(itemId + 2);
					player.getEquipment().update();
				}
			}
			if (operate == false) {
				if (itemId + 2 != 2568) {
					player.getInventory().remove(itemId);
					player.getInventory().add(itemId + 2, 1);
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
		DialogueManager.sendOption(player, new String[] { "Pest Control",
				"Duel Arena" });
	}
}
