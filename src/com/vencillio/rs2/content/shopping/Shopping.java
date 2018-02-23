package com.vencillio.rs2.content.shopping;

import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendInventoryInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * Shopping
 * 
 * @author Daniel
 * @author Michael
 */
public class Shopping {
	
	public static enum ShopType {
		DEFAULT,
		PLAYER,
		INSTANCE;
	}

	public static final int SHOP_INTERFACE_ID = 3900;
	public static final int INVENTORY_INTERFACE_ID = 3823;
	private final Player player;
	private long shopId = -1L;

	private ShopType shopType = ShopType.DEFAULT;

	public Shopping(Player player) {
		this.player = player;
	}

	public void buy(int id, int amount, int slot) {
		if (shopId == -1L)
			return;

		Shop shop;
		if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
			shop = Shop.getShops()[((int) shopId)];
		} else {
			Player p = World.getPlayerByName(shopId);

			if(PlayerConstants.isAdministrator(player)) {
				player.send(new SendMessage("Administrators are not allowed to buy items from player shops."));
				return;
			}

			if (p == null) {
				DialogueManager.sendStatement(p, new String[] { "The shop owner is no longer online." });
				return;
			}

			shop = p.getPlayerShop();
		}

		if (shop == null) {
			return;
		}

		shop.buy(player, slot, id, amount);
		player.getClient().queueOutgoingPacket(new SendUpdateItems(3823, player.getInventory().getItems()));
	}

	public void doShopPriceUpdate() {
		Item[] prices = new Item[Shop.SHOP_SIZE];
		Shop shop;

		if (shopId == -1) {
			return;
		}

		if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
			shop = Shop.getShops()[(int) shopId];

			for (int i = 0; i < prices.length; i++) {
				if (shop.get(i) != null) {
					prices[i] = new Item(0, shop.getSellPrice(shop.get(i).getId()));
					int currency = 0;
					if (shop != null && shop.getCurrencyName() != null) {
						switch (shop.getCurrencyName()) {
						case "Achievements points":
							currency = 10;
							break;
						case "Mage Arena points":
							currency = 9;
							break;
						case "Prestige points":
							currency = 7;
							break;
						case "Credits":
							currency = 6;
							break;
						case "Pest points":
							currency = 5;
							break;
						case "Slayer points":
							currency = 4;
							break;
						case "Bounty points":
							currency = 3;
							break;
						case "Vote points":
							currency = 2;
							break;
						case "Tokkul":
							currency = 1;
							break;
						default:
							currency = 0;
							break;
						}
					}
					player.send(new SendString(shop.getSellPrice(shop.get(i).getId()) + "," + currency, 28000 + i));
				}
			}
		} else {
			Player owner = World.getPlayerByName(shopId);

			if (owner == null) {
				return;
			}

			shop = owner.getPlayerShop();

			for (int i = 0; i < prices.length; i++) {
				if (shop.get(i) != null) {
					int currency = 0;
					if (shop != null && shop.getCurrencyName() != null) {
						switch (shop.getCurrencyName()) {
						case "Achievements points":
							currency = 10;
							break;
						case "Mage Arena points":
							currency = 9;
							break;
						case "Prestige points":
							currency = 7;
							break;
						case "Credits":
							currency = 6;
							break;
						case "Pest points":
							currency = 5;
							break;
						case "Slayer points":
							currency = 5;
							break;
						case "Bounty points":
							currency = 4;
							break;
						case "Vote points":
							currency = 3;
							break;
						case "Tokkul":
							currency = 1;
							break;
						default:
							currency = 0;
							break;
						}
					}
					player.send(new SendString(shop.getSellPrice(shop.get(i).getId()) + "," + currency, 28000 + i));
					prices[i] = new Item(0, shop.getSellPrice(shop.get(i).getId()));
				}
			}
		}
	}

	public long getShopId() {
		return shopId;
	}
	

	public void open(int id) {
	
		if (player.ironPlayer()) {
			boolean can = false;
			for (int index = 0; index < ShopConstants.IRON_SHOPS.length; index++) {
				if (id == ShopConstants.IRON_SHOPS[index]) {
					can = true;
				}
			}
			
			if (!can) {
				DialogueManager.sendStatement(player, "<img=" + (player.getRights() - 1) + ">@gry@ Iron players do not have access to this shop! ");
				return;
			}
		}
	
		Shop shop = Shop.getShops()[id];

		if (shop == null) {
			return;
		}

		shopType = shop.getShopType();

		shopId = id;
		
		if (shopType == ShopType.INSTANCE) {
			shop.onOpen(player);
		}

		player.getClient().queueOutgoingPacket(new SendUpdateItems(3823, player.getInventory().getItems()));
		player.getClient().queueOutgoingPacket(new SendUpdateItems(3900, shop.getItems()));
		player.getClient().queueOutgoingPacket(new SendString(shop.getName(), 3901));
		player.getClient().queueOutgoingPacket(new SendInventoryInterface(3824, 3822));

		doShopPriceUpdate();
	}

	public void open(Player owner) {
		
		if (player.ironPlayer()) {
			player.send(new SendMessage("You may not access this store as you are an Iron player."));
			return;
		}

		if(PlayerConstants.isAdministrator(player)) {
			player.send(new SendMessage("You may not access this store as an administrator"));
			return;
		}
	
		if (owner == null || owner.ironPlayer()) {
			DialogueManager.sendStatement(player, new String[] { "Player not found." });
			return;
		}

		shopType = ShopType.PLAYER;

		Shop shop = owner.getPlayerShop();

		if (shop == null) {
			return;
		}

		shopId = owner.getUsernameToLong();

		player.getClient().queueOutgoingPacket(new SendUpdateItems(3823, player.getInventory().getItems()));
		player.getClient().queueOutgoingPacket(new SendUpdateItems(3900, shop.getItems()));
		player.getClient().queueOutgoingPacket(new SendString(owner.determineIcon(owner) + shop.getName()+"'s shop ", 3901));
		player.getClient().queueOutgoingPacket(new SendInventoryInterface(3824, 3822));

		doShopPriceUpdate();
	}

	public void reset() {
		shopId = -1L;
	}

	public void sell(int id, int amount, int slot) {
		if (shopId == -1L)
			return;
		Shop shop;
		if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
			shop = Shop.getShops()[((int) shopId)];
		} else {
			Player p = World.getPlayerByName(shopId);

			if(PlayerConstants.isAdministrator(player)) {
				player.send(new SendMessage("You may not sell on this shop as an administrator"));
				return;
			}

			if (p == null) {
				DialogueManager.sendStatement(p, new String[] { "The shop owner is no longer online." });
				return;
			}

			shop = p.getPlayerShop();
		}

		if (shop == null) {
			return;
		}

		if (shop.sell(player, id, amount)) {
			player.getClient().queueOutgoingPacket(new SendUpdateItems(3823, player.getInventory().getItems()));
		}
	}

	public void sendBuyPrice(int id) {
		if ((shopId == -1L) || (shopType == ShopType.PLAYER)) {
			return;
		}

		if (id == 995) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell coins to a shop."));
			return;
		}

		if (!Item.getDefinition(id).isTradable()) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item."));
			return;
		}

		Shop shop = Shop.getShops()[((int) shopId)];

		if ((shop != null) && (!shop.isGeneral()) && (!shop.isDefaultItem(id))) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You cannot sell this item to this shop."));
			return;
		}

		int value = Shop.getShops()[((int) shopId)].getBuyPrice(id);
		String itemName = Item.getDefinition(id).getName();
		String price = "" + value;
		String coin = value == 1 ? "coin" : "coins";

		if ((value > 1000) && (value < 1000000))
			price = value / 1000 + "k (" + value + ")";
		else if (value >= 1000000)
			price = value / 1000000 + "m (" + value / 1000 + "k)";
		else if (value == 1) {
			price = "one";
		}

		player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] " + ShopConstants.COLOUR + "" + itemName + "</col>: shop will buy for " +ShopConstants.COLOUR+"" + price + "</col> " + coin + "."));
	}

	public void sendSellPrice(int id) {
		if (shopId == -1L)
			return;
		int value;
		if (shopType == ShopType.PLAYER) {
			Player p = World.getPlayerByName(shopId);

			if (p == null) {
				DialogueManager.sendStatement(p, "The shop owner is no longer online.");
				return;
			}

			value = p.getPlayerShop().getSellPrice(id);
		} else {
			value = Shop.getShops()[((int) shopId)].getSellPrice(id);
		}

		String itemName = Item.getDefinition(id).getName();

		if (shopType != ShopType.PLAYER || shopType == ShopType.INSTANCE) {
			if (Shop.getShops()[((int) shopId)].getCurrencyName() == null) {
				String price = "" + value;
				String coin = value == 1 ? "coin" : "coins";

				if ((value > 1000) && (value < 1000000))
					price = value / 1000 + "k (" + value + ")";
				else if (value >= 1000000)
					price = value / 1000000 + "m (" + value / 1000 + "k)";
				else if (value == 1) {
					price = "one";
				}

				player.getClient().queueOutgoingPacket(new SendMessage("["+ShopConstants.COLOUR+"*</col>] "+ShopConstants.COLOUR+"" + itemName + "</col>: currently costs "+ShopConstants.COLOUR+"" + price + "</col> " + coin + "."));
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("["+ShopConstants.COLOUR+"*</col>] "+ShopConstants.COLOUR+"" + itemName + "</col>: currentky costs "+ShopConstants.COLOUR+"" + value + "</col> " + Shop.getShops()[((int) shopId)].getCurrencyName() + "."));
			}
		} else {
			String price = "" + value;
			String coin = value == 1 ? "coin" : "coins";

			if ((value > 1000) && (value < 1000000))
				price = value / 1000 + "k (" + value + ")";
			else if (value >= 1000000)
				price = value / 1000000 + "m (" + value / 1000 + "k)";
			else if (value == 1) {
				price = "one";
			}

			player.getClient().queueOutgoingPacket(new SendMessage("["+ShopConstants.COLOUR+"*</col>] "+ShopConstants.COLOUR+"" + itemName + "</col>: currently costs "+ShopConstants.COLOUR+"" + price + "</col> " + coin + "."));
		}
	}

	public boolean shopping() {
		return shopId > -1L;
	}

	public void update() {
		if (!shopping())
			return;
		Shop shop;
		if (shopType == ShopType.DEFAULT || shopType == ShopType.INSTANCE) {
			shop = Shop.getShops()[((int) shopId)];
		} else {
			Player p = World.getPlayerByName(shopId);

			if (p == null) {
				DialogueManager.sendStatement(player, new String[] { "This player is no longer online." });
				shopId = -1L;
				return;
			}

			shop = p.getPlayerShop();
		}

		if (shop.isUpdate()) {
			player.getClient().queueOutgoingPacket(new SendUpdateItems(3900, shop.getItems()));
			doShopPriceUpdate();
		}
	}
}
