package com.vencillio.rs2.content.minigames.weapongame;

import java.util.HashMap;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

/**
 * Handles the Weapon Game Store
 * @author Daniel
 *
 */
public class WeaponGameStore {
	
	/**
	 * Container identification for store items
	 */
	private final static int CONTAINER_ID = 56503;
	
	/**
	 * Interface identification
	 */
	private final static int INTERFACE_ID = 56500;
	
	/**
	 * Container identification for selected item
	 */
	private final static int SELECTED_CONTAINER_ID = 56508;

	/**
	 * Holds all the store data
	 * @author Daniel
	 *
	 */
	public enum StoreData {
		DARK_CRAB(11937, 50, 35_000),
		SUPER_POTION(13066, 1, 5_000),
		COMBAT_POTION(12695, 1, 7_500),
		SARADOMIN_BREW(6685, 1, 6_000),
		HERB_BOX(11738, 1, 25_000),
		DRAGON_BOLTS_E(9244, 15, 45_000),
		HEALER_HAT(10547, 1, 125_000),
		FIGHTER_HAT(10548, 1, 125_000),
		RUNNER_HAT(10549, 1, 125_000),
		RANGER_HAT(10550, 1, 125_000),
		DRAGON_SCIMITAR(4587, 1, 100_000),
		DRAGON_DAGGER_P(5698, 1, 80_000),
		RUNE_ARMOUR_SET(13024, 1, 30_000),
		BARROWS_GLOVES(7462, 1, 95_000),
		BOOK_OF_WAR(12608, 1, 150_000),
		BOOK_OF_LAW(12610, 1, 150_000),
		BOOK_OF_DARKNESS(12612, 1, 150_000),
		FANCY_BOOTS(9005, 1, 150_250),
		FIGHTER_BOOTS(9006, 1, 150_250),
		FLIPPER(6666, 1, 175_500),
		MUDSKIPPER_HAT(6665, 1, 175_500),
		;
		
		private final int item;
		private final int amount;
		private final int price;
		private StoreData(int item, int amount, int price) {
			this.item = item;
			this.amount = amount;
			this.price = price;
		}
		
		public int getItem() {
			return item;
		}
		
		public int getAmount() {
			return amount;
		}
		
		public int getPrice() {
			return price;
		}
		
		private static HashMap<Integer, StoreData> items = new HashMap<Integer, StoreData>();

		static {
			for (final StoreData shop : StoreData.values()) {
				StoreData.items.put(shop.item, shop);
			}
		}
	}
	
	/**
	 * Handles opening store interface
	 * @param player
	 */
	public static void open(Player player) {
		int slot = 0;
		for (StoreData data : StoreData.values()) {
			player.send(new SendUpdateItemsAlt(CONTAINER_ID, data.getItem(), data.getAmount(), slot++));
		}
		player.send(new SendString("</col>You currently have @gre@" + Utility.format(player.getWeaponPoints()) + "</col> points.", 56505));
		player.send(new SendString("", 56506));
		player.send(new SendString("", 56507));
		player.send(new SendString("No item selected", 56509));
		player.send(new SendUpdateItemsAlt(SELECTED_CONTAINER_ID, -1, 0, 0));
		player.send(new SendInterface(INTERFACE_ID));		
	}
	
	/**
	 * Handles selecting an item from store
	 * @param player
	 * @param item
	 */
	public static void select(Player player, int item) {
		StoreData data = StoreData.items.get(item);

		if (data == null) {
			return;
		}
		
		if (player.getInterfaceManager().main != INTERFACE_ID) {
			player.send(new SendRemoveInterfaces());
			return;
		}
		
		ItemDefinition itemDef = GameDefinitionLoader.getItemDef(data.getItem());

		player.send(new SendString("@gre@" + Utility.format(data.getPrice()), 56506));
		player.send(new SendString("@gre@" + itemDef.getName(), 56507));
		player.send(new SendString("", 56509));
		player.send(new SendUpdateItemsAlt(SELECTED_CONTAINER_ID, data.getItem(), data.getAmount(), 0));
	
		return;
	}
	
	/**
	 * Handles purchasing item from store
	 * @param player
	 * @param item
	 */
	public static void purchase(Player player, int item) {
		StoreData data = StoreData.items.get(item);
		
		if (data == null) {
			return;
		}
		
		if (player.getInterfaceManager().main != INTERFACE_ID) {
			player.send(new SendRemoveInterfaces());
			return;
		}
		
		if (player.getWeaponPoints() < data.getPrice()) {
			player.send(new SendMessage("@red@You need " + Utility.format(data.getPrice()) + " Weapon Game Points to buy this!"));
			return;
		}
		
		String name = GameDefinitionLoader.getItemDef(data.getItem()).getName();
		
		player.setWeaponPoints(player.getWeaponPoints() - data.getPrice());
		player.getInventory().add(data.getItem(), data.getAmount(), true);
		player.send(new SendMessage("@red@Congratulations! You have purchased " + Utility.getAOrAn(name) + " " + name + "."));
		player.send(new SendString("</col>You currently have @gre@" + Utility.format(player.getWeaponPoints()) + "</col> points.", 56505));
	}

}
