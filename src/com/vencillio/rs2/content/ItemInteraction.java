package com.vencillio.rs2.content;

import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;


public class ItemInteraction {
	
	public static boolean clickPouch(Player p, int id, int option) {
		if (id == 5509 || id == 5510 || id == 5512 || id == 5514) {
			if (option == 1) {
				int index = id == 5509 ? 0 : id == 5510 ? 1 : id == 5512 ? 2 : id == 5514 ? 3 : 0;
				int ess = (index + 1) * 3;
				if (p.getPouches()[index] == ess) {
					p.getClient().queueOutgoingPacket(new SendMessage("Your pouch is already full."));
					return true;
				}
				if ((index > 0) && (p.getMaxLevels()[20] < index * 25)) {
					p.getClient().queueOutgoingPacket(new SendMessage("You need a Runecraft level of " + index * 25 + " to use this pouch."));
					return true;
				}
				int amount = ess - p.getPouches()[index];
				int invAmount = p.getInventory().getItemAmount(1436);
				if (invAmount == 0) {
					p.getClient().queueOutgoingPacket(new SendMessage("You do not have any essence to fill your pouch with."));
					return true;
				}
				if (amount > invAmount) {
					amount = invAmount;
				}
				p.getInventory().remove(1436, amount, true);
				p.getPouches()[index] = ((byte) (p.getPouches()[index] + amount));
				if (p.getPouches()[index] == ess)
					p.getClient().queueOutgoingPacket(new SendMessage("You fill your pouch."));
			} else if (option == 2) {
				int index = id == 5509 ? 0 : id == 5510 ? 1 : id == 5512 ? 2 : id == 5514 ? 3 : 0;
				if (p.getPouches()[index] == 0) {
					p.getClient().queueOutgoingPacket(new SendMessage("Your pouch is empty."));
					return true;
				}
				int add = p.getInventory().add(1436, p.getPouches()[index]);
				p.getPouches()[index] = ((byte) (p.getPouches()[index] - add));

				if (p.getPouches()[index] == 0)
					p.getClient().queueOutgoingPacket(new SendMessage("You empty your pouch."));
			} else if (option == 3) {
				int index = id == 5509 ? 0 : id == 5510 ? 1 : id == 5512 ? 2 : id == 5514 ? 3 : 0;
				if (p.getPouches()[index] == 0) {
					p.getClient().queueOutgoingPacket(new SendMessage("Your pouch is empty."));
					return true;
				}
				p.getClient().queueOutgoingPacket(new SendMessage("There is " + p.getPouches()[index] + " essence is your pouch."));
			}

			return true;
		}
		return false;
	}
	
	
	
}
