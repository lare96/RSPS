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
				return 550_000;
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
				return 550_000;
			case 12802:
				return 550_000;
			case 12804:
				return 850_000;
			case 4740:
				return 360;
			case 892:
				return 600;
			case 890:
				return 240;
			case 12846:
				return 300_000;
			case 12786:
				return 150_000;
			case 4153:
				return 7_500;
			case 7462:
				return 15_000;
			case 5698:
				return 5_000;
			case 11791:
				return 750_000;
			case 11840:
				return 75_000;
			case 6585:
				return 100_000;
			case 12765:
				return 1_500_000;
			case 12002:
				return 1_250_000;
			case 4151:
				return 1_750_000;


		}
		return 2147483647;
	}

	/**
	 * All items in Bounty store
	 */
	public BountyShop() {
		super(SHOP_ID, new Item[]{ new Item(12002), new Item(11840), new Item(6585), new Item(11791), new Item(5698), new Item(4153), new Item(7462), new Item(12798), new Item(12800), new Item(12802), new Item(12804), new Item(12765), new Item(4151), new Item(12846), new Item(12786)
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
