package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class SpawnTab extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		int id = in.readShort();
		int amount = in.readInt();
		int bankedOrNoted = in.readByte();
		
		Item item = new Item(id, amount);
		
		String name = item.getDefinition().getName();
		
		String[] UNSPAWNABLES = { "armadyl godsword" };
		
		if (!PlayerConstants.isOwner(player)) {
			for (int i = 0; i < UNSPAWNABLES.length; i ++) {
				if (name.equalsIgnoreCase(UNSPAWNABLES[i])) {
					DialogueManager.sendItem1(player, "@red@" + name + " @bla@may not be spawned!", item.getId());
					return;
				}
			}
		}
		
		if (bankedOrNoted == 1) {
			if (item.getDefinition().isStackable()) {
				player.send(new SendMessage("This item cannot be noted!"));
				return;
			} else {
				item.unNote();
			}
		}
		
		if (bankedOrNoted == 2) {
			if (player.getBank().depositFromNoting(id, amount, 0, true) <= 0) {
				player.send(new SendMessage("Your bank is full!"));
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			
			if (freeSlots == 0) {
				return;
			}
			
			if (amount > freeSlots) {
				amount = freeSlots;
			}
			
			if (player.getInventory().add(item) <= 0) {
				player.send(new SendMessage("Your inventory is full!"));
				return;
			}
		}
		
		player.send(new SendMessage(name + " (ID: " + item.getId() + ") x " + amount + " to " + (bankedOrNoted == 2 ? "bank." : "inventory.")));
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}