package com.vencillio.rs2.content.shopping.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.QuestTab;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Achievements
 * 
 * @author Daniel
 */
public class AchievementShop extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 89;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 11898:
		case 11896:
		case 11897:
		case 11899:	
		case 11900:
			return 17;
		case 12361:
			return 15;
		case 6828:
			return 70;
		case 6829: 
			return 80;
		case 6830:
			return 90;
		case 6831: 
			return 100;
		case 10840:
		case 10836:
		case 10837:
		case 10838:
		case 10839:
			return 25;
		case 7927:
		case 6583:
			return 75;
		case 65:
			return 10394;
		}

		return 12;
	}

	/**
	 * Items in shop
	 */
	public AchievementShop() {
		super(SHOP_ID, new Item[] { 
			new Item(8714), 
			new Item(8716), 
			new Item(8718), 
			new Item(8720), 
			new Item(8722), 
			new Item(8724), 
			new Item(8726), 
			new Item(8728), 
			new Item(8730), 
			new Item(8732), 
			new Item(8734), 
			new Item(8736), 
			new Item(8738), 
			new Item(8740), 
			new Item(8742), 
			new Item(11898), 
			new Item(11896), 
			new Item(11897), 
			new Item(6828), 
			new Item(6829), 
			new Item(6830), 
			new Item(6831), 
			new Item(12361), 
			new Item(7927), 
			new Item(6583), 
			new Item(11899), 
			new Item(11900), 
			new Item(10840), 
			new Item(10836), 
			new Item(10837), 
			new Item(10838), 
			new Item(10839), 
			new Item(10394),
			
		}, false, "Achievement Store");
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

		if (player.getAchievementsPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Achievements points to buy that."));
			return;
		}

		player.addAchievementPoints(player.getAchievementsPoints() - (amount * getPrice(id)));

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
		return "Achievements points";
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
