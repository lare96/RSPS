package com.vencillio.rs2.content.shopping.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Prestige store
 * 
 * @author Daniel
 */
public class PrestigeShop extends Shop {

	/**
	 * Id of Bounty shop
	 */
	public static final int SHOP_ID = 93;

	/**
	 * Price of items in Bounty store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 1419:
			return 15;
		case 9472:
			return 10;
		}
		return 2;
	}

	/**
	 * All items in Bounty store
	 */
	public PrestigeShop() {
		super(SHOP_ID, new Item[] {
				new Item(1419),
				new Item(6656),
				new Item(6654),
				new Item(6655),		
				new Item(11919), 
				new Item(12956), 
				new Item(12957), 
				new Item(12958), 
				new Item(12959), 			
				new Item(9472),			
				new Item(6182),
				new Item(6180),
				new Item(6181),		
				new Item(12887), 
				new Item(12888), 
				new Item(12889), 
				new Item(12890), 
				new Item(12891), 
				new Item(12892), 
				new Item(12893), 
				new Item(12894), 
				new Item(12895), 
				new Item(12896), 				
				new Item(3057), 
				new Item(11282), 
				new Item(11280), 
				new Item(2631), 	
				new Item(7594), 
				new Item(7592), 
				new Item(7593), 
				new Item(7595), 
				new Item(7596), 
				
				
		}, false, "Prestige Store");
	}

	@Override
	public void buy(Player player, int slot, int id, int amount) {
		if (!hasItem(slot, id))
			return;
		if (get(slot).getAmount() == 0)
			return;
		if (amount > get(slot).getAmount()) {
			amount = get(slot).getAmount();
		}

		Item buying = new Item(id, amount);

		if (!player.getInventory().hasSpaceFor(buying)) {
			if (!buying.getDefinition().isStackable()) {
				int slots = player.getInventory().getFreeSlots();
				if (slots > 0) {
					buying.setAmount(slots);
					amount = slots;
				} else {
					player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to buy this item."));
				}
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to buy this item."));
				return;
			}
		}

		if (player.getPrestigePoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Prestige points to buy that."));
			return;
		}

		player.setPrestigePoints(player.getPrestigePoints() - amount * getPrice(id));

		InterfaceHandler.writeText(new QuestTab(player));

		player.getInventory().add(buying);
		update();
	}

	@Override
	public int getBuyPrice(int id) {
		return 0;
	}

	@Override
	public String getCurrencyName() {
		return "Prestige points";
	}

	@Override
	public int getSellPrice(int id) {
		return getPrice(id);
	}

	@Override
	public boolean sell(Player player, int id, int amount) {
		player.getClient().queueOutgoingPacket(new SendMessage("You cannot sell items to this shop."));
		return false;
	}
}
