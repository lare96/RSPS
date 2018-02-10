package com.vencillio.rs2.content.io;

import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public class ItemCheck {
	
	public static class ItemReset {
		public final int id;
		public final int year;
		public final int day;

		public ItemReset(int id, int year, int day) {
			this.id = id;
			this.year = year;
			this.day = day;
		}
	}

	public static final ItemReset[] ITEM_RESETS = {
		//new ItemReset(12643, 2015, 212),
	};

	public static Item check(Player player, Item item) {
		try {
			if (item == null) {
				return null;
			}

			if (item.getId() == 2513) {
				item.setId(3140);
				return item;
			}

			if (item.getId() == 4212) {
				item.setId(4214);
				return item;
			}
			
			for (ItemReset k : ITEM_RESETS) {
				if ((player.getLastLoginDay() <= k.day) && (player.getLastLoginYear() <= k.year) && (item.getId() == k.id))
					return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}
}
