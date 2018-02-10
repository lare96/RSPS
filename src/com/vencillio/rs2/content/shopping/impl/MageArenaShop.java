package com.vencillio.rs2.content.shopping.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Mage Arena
 * 
 * @author Daniel
 */
public class MageArenaShop extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 95;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 6918: 
		case 6916: 
		case 6924: 
		case 6922: 
		case 6920: 
			return 50;
		case 6908:  
			return 25;
		case 6910: 
			return 30;
		case 6912: 
			return 35;
		case 6914: 
			return 85;
		case 12528: 
			return 150;
		case 12530: 
			return 150;
		case 6889:  
			return 100;
		case 8015: 
			return 3;
		case 560: 
			return 1;
		case 565: 
			return 1;
		}

		return 2147483647;
	}

	/**
	 * Items in shop
	 */
	public MageArenaShop() {
		super(SHOP_ID, new Item[] { 
			new Item(6918), 
			new Item(6916), 
			new Item(6924), 
			new Item(6922), 
			new Item(6920), 
			new Item(6908), 
			new Item(6910), 
			new Item(6912), 
			new Item(6914), 
			new Item(12528), 
			new Item(12530), 
			new Item(6889), 
			new Item(8015), 
			new Item(560), 
			new Item(565), 
		}, false, "Mage Arena Store");
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

		if (player.getArenaPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Mage Arena points to buy that."));
			return;
		}

		player.setArenaPoints(player.getArenaPoints() - (amount * getPrice(id)));

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
		return "Mage Arena points";
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
