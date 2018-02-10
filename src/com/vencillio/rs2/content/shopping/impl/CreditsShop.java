package com.vencillio.rs2.content.shopping.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for pest credits
 * 
 * @author Daniel
 */
public class CreditsShop extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 94;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 2528:
			return 10;
		case 12439:
		case 12397:
		case 12393:
		case 12395:
		case 12319:
		case 12351:
		case 12441:
		case 12443:
			return 25;
		case 4566:
		case 4565:
			return 50;
		case 11990:
		case 7144:
			return 15;
			
		case 12363:
			return 15;
		case 12365:
			return 20;
		case 12367:
			return 25;
		case 12369:
			return 30;
		case 12518:
			return 35;
		case 12520:
			return 40;
		case 12522:
			return 45;
		case 12371:
		case 12524:
		case 12373:
		case 12335:
		case 12337:
		case 12432:
			return 50;
		case 12357:
			return 100;
			
		case 9472:
		case 9945:
			return 35;
		case 9946:
			return 65;
		}

		return 150;
	}

	/**
	 * Items in shop
	 */
	public CreditsShop() {
		super(SHOP_ID, new Item[] { 
				new Item(2528),
				new Item(7144),
				new Item(4566),
				new Item(11990),
				new Item(12319),
				new Item(12439), 	
				new Item(12397),
				new Item(12395),
				new Item(12393),				
				new Item(12351),
				new Item(12441),
				new Item(12443),
				new Item(4565),			
				new Item(12363),
				new Item(12365),
				new Item(12367),
				new Item(12369),
				new Item(12518),
				new Item(12520),
				new Item(12522),
				new Item(12371),
				new Item(12524),
				new Item(12373),
				new Item(12335),
				new Item(12337),
				new Item(12432),
				new Item(12357),
				
				new Item(9472),
				new Item(9945),
				new Item(9946),
				
		}, false, "Credits Store");
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

		if (player.getCredits() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Credits to buy that."));
			return;
		}

		player.setCredits(player.getCredits() - (amount * getPrice(id)));

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
		return "Credits";
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
