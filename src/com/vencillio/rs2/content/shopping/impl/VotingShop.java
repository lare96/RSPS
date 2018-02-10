package com.vencillio.rs2.content.shopping.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Voting store
 * 
 * @author Daniel
 */
public class VotingShop extends Shop {

	/**
	 * Id of Bounty shop
	 */
	public static final int SHOP_ID = 92;

	/**
	 * Price of items in Bounty store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 2528:
		case 7144:
			return 3;
		case 12964:
		case 12966:
		case 12968:
		case 12970:
		case 12976:
		case 12978:
		case 12980:
		case 12982:
		case 12992:
		case 12994:
		case 12996:
		case 12998:
		case 13004:
		case 13006:
		case 13008:
		case 13010:
		case 13016:
		case 13018:
		case 13020:
		case 13022:
		case 13028:
		case 13030:
		case 13032:
		case 13034:
			return 20;
		case 13036:
		case 13038:
			return 30;
		case 1837:
		case 5607:
			return 15;
		case 5608:
		case 5609:
		case 9470:
		case 9472:
			return 25;
		}
		return 2147483647;
	}

	/**
	 * All items in Bounty store
	 */
	public VotingShop() {
		super(SHOP_ID, new Item[] {
				new Item(2528), 
				new Item(7144),
				new Item(12964),
				new Item(12966), 
				new Item(12968), 
				new Item(12970), 
				new Item(12976),
				new Item(12978), 
				new Item(12980), 
				new Item(12982), 
				new Item(12992), 
				new Item(12994), 
				new Item(12996), 
				new Item(12998), 
				new Item(13004), 
				new Item(13006), 
				new Item(13008), 
				new Item(13010), 
				new Item(13016), 
				new Item(13018), 
				new Item(13020), 
				new Item(13022),
				new Item(13028), 
				new Item(13030),
				new Item(13032), 
				new Item(13034),
				new Item(13036), 
				new Item(13038), 
				new Item(1837), 
				new Item(5607), 
				new Item(5608), 
				new Item(5609), 
				new Item(9472), 
				new Item(9470), 
		}, false, "Voting Store");
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

		if (player.getVotePoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Vote points to buy that."));
			return;
		}

		player.setVotePoints(player.getVotePoints() - amount * getPrice(id));

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
		return "Vote points";
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
