package com.vencillio.rs2.content.skill.magic;

import java.util.ArrayList;
import java.util.HashMap;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles Tab Creations
 * @author Daniel
 *
 */
public class TabCreation {

	/**
	 * Holds the Tab data
	 * @author Daniel
	 *
	 */
	public enum ItemData {
		VARROCK(8007, 25, new Item[] { new Item(557, 1), new Item(556, 3), new Item(563, 1) }),
		LUMBRIDGE(8008, 31, new Item[] { new Item(557, 1), new Item(556, 3), new Item(563, 1) }),
		FALADOR(8009, 43, new Item[] { new Item(555, 1), new Item(556, 3), new Item(563, 1) }),
		CAMELOT(8010, 55, new Item[] { new Item(556, 5), new Item(563, 1) }),
		ARDOUGNE(8011, 63, new Item[] { new Item(555, 2), new Item(556, 3), new Item(563, 2) }),
		WATCHTOWER(8012, 58, new Item[] { new Item(563, 2), new Item(555, 2) }),
		HOME(8013, 90, new Item[] { new Item(557, 3), new Item(556, 5), new Item(563, 2) }),
		BONES_TO_BANANAS(8014, 15, new Item[] { new Item(561, 1), new Item(557, 2), new Item(555, 2) }),
		BONES_TO_PEACHES(8015, 60, new Item[] { new Item(561, 2), new Item(557, 4), new Item(555, 4) });
		
		private final int tabID;
		private final int levelRequired;
		private final Item[] itemsRequired;
		
		private ItemData(int tabID, int levelRequired, Item...itemsRequired) {
			this.tabID = tabID;
			this.levelRequired = levelRequired;
			this.itemsRequired = itemsRequired;
		}
		
		public int getTab() {
			return tabID;
		}
		
		public int getLevel() {
			return levelRequired;
		}
		
		public Item[] getItems() {
			return itemsRequired;
		}
		
		private static HashMap<Integer, ItemData> tabs = new HashMap<Integer, ItemData>();

		static {
			for (final ItemData tab : ItemData.values()) {
				ItemData.tabs.put(tab.tabID, tab);
			}
		}
	}
	
	/**
	 * Handles making a tab
	 * @param player
	 * @param itemID
	 */
	public static void handle(Player player, int itemID) {
		
		ItemData data = ItemData.tabs.get(itemID);
			
		if (data == null) {
			return;
		}
		
		if (player.getInterfaceManager().main != 26700) {
			return;
		}
		
		if (player.getLevels()[Skills.MAGIC] < data.getLevel()) {
			player.send(new SendMessage("<col=2555>You need a Magic level of " + data.getLevel() + " to do this!"));
			return;
		}
		
		if (PlayerConstants.isPlayer(player)) {
			return;
		}
		
		ArrayList<String> required = new ArrayList<String>();
		
		for (int index = 0; index < data.getItems().length; index++) {
			if (!player.getInventory().hasAllItems(data.getItems()[index]) || !player.getInventory().hasItemAmount(1761, 1)) {
				String name = GameDefinitionLoader.getItemDef(data.getItems()[index].getId()).getName();
				int amount = data.getItems()[index].getAmount();
				required.add(amount + " " + name);
				continue;
			}			
		}
		
		for (int index = 0; index < data.getItems().length; index++) {
			if (!player.getInventory().hasAllItems(data.getItems()[index]) || !player.getInventory().hasItemAmount(1761, 1)) {
				required.add("1 Soft clay");
				player.send(new SendMessage("<col=2555>You need " + required));
				return;
			}
		}
		
		for (int index = 0; index < data.getItems().length; index++) {
			player.getInventory().remove(data.getItems()[index]);
		}
		
		player.getInventory().remove(1761, 1);
		
		player.getInventory().add(data.getTab(), 1);
		
		player.getSkill().addExperience(Skills.MAGIC, 500);
		
		String name = GameDefinitionLoader.getItemDef(data.getTab()).getName();
		
		player.send(new SendMessage("<col=2555>You have successfully created " + Utility.getAOrAn(name) + " " + name + "."));

	}
	
	/**
	 * Gets information on a tab
	 * @param player
	 * @param itemID
	 */
	public static void getInfo(Player player, int itemID) {
	
		ItemData data = ItemData.tabs.get(itemID);
			
		if (data == null) {
			return;
		}
		
		ArrayList<String> required = new ArrayList<String>();
		
		for (int index = 0; index < data.getItems().length; index++) {
				String name = GameDefinitionLoader.getItemDef(data.getItems()[index].getId()).getName();
				int amount = data.getItems()[index].getAmount();
				required.add(amount + " " + name);
				continue;	
		}
		
		required.add("1 Soft clay");

		player.send(new SendString("" + required, 26707));
		player.send(new SendString("You need a Magic level of " + data.getLevel(), 26708));
	
	}
	
}
