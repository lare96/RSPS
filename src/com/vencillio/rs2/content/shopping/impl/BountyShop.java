package com.vencillio.rs2.content.shopping.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Bounty store
 * 
 * @author Daniel
 */
public class BountyShop extends Shop {

	/**
	 * Id of Bounty shop
	 */
	public static final int SHOP_ID = 7;

	/**
	 * Price of items in Bounty store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 4587:
		case 1305:
		case 12800:
			return 300_000;
		case 1215:
			return 90_000;
		case 1377:
			return 600_000;
		case 1434:
			return 150_000;
		case 3204:
			return 900_000;
		case 10828:
		case 11941:
			return 150_000;
		case 3751:
		case 3753:
		case 3749:
		case 3755:
			return 234_000;
		case 4091:
			return 360_000;
		case 4093:
			return 240_000;
		case 4089:
			return 45_000;
		case 4095:
		case 4097:
			return 30_000;
		case 1127:
			return 255_000;
		case 1079:
		case 1093:
			return 192_000;
		case 12759:
		case 12761:
		case 12763:
		case 12757:
		case 12771:
		case 12769:
			return 500_000;
		case 12798:
			return 250_000;
		case 12802:
			return 350_000;
		case 12804:
			return 25_000_000;
		case 4740:
			return 360;
		case 892:
			return 600;
		case 890:
			return 240;
		case 12846:
			return 8_000_000;
		case 12855:
		case 12856:
			return 2_500_000;
		case 12786:
			return 350_000;

		}
		return 2147483647;
	}

	/**
	 * All items in Bounty store
	 */
	public BountyShop() {
		super(SHOP_ID, new Item[] { new Item(4587), new Item(1305), new Item(1215), new Item(1377), new Item(1434), new Item(3204), new Item(10828), new Item(3751), new Item(3753), new Item(3749), new Item(3755), new Item(4091), new Item(4093), new Item(4089), new Item(4097), new Item(1127), new Item(1079), new Item(1093), new Item(12759), new Item(12761), new Item(12763), new Item(12757), new Item(12771),new Item(12769), new Item(12798), new Item(12800), new Item(12802), new Item(12804), new Item(11941), new Item(4740), new Item(892), new Item(890), new Item(12846), new Item(12855), new Item(12856), new Item(12786) 
		}, false, "Bounty Store");
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

		if (player.getBountyPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Bounty points to buy that."));
			return;
		}

		player.setBountyPoints(player.getBountyPoints() - amount * getPrice(id));

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
		return "Bounty points";
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
