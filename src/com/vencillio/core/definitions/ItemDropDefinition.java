package com.vencillio.core.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vencillio.rs2.entity.item.Item;

public class ItemDropDefinition {

	public static class ItemDrop {
		private short id;

		private int min;

		public int max;

		public ItemDrop() {

		}

		public int getId() {
			return id;
		}

		public int getMax() {
			return max;
		}

		public int getMin() {
			return min;
		}

		public void setId(int id) {
			this.id = (short) id;
		}

		/**
		 * @param id
		 *            the id to set
		 */
		public void setId(short id) {
			this.id = id;
		}

		/**
		 * @param max
		 *            the max to set
		 */
		public void setMax(short max) {
			this.max = max;
		}

		/**
		 * @param min
		 *            the min to set
		 */
		public void setMin(short min) {
			this.min = min;
		}

	}

	public static class ItemDropTable {

		public enum ScrollTypes {
			EASY,
			MEDIUM,
			HARD,
			ELITE
		}

		public enum TableTypes {
			COMMON,
			UNCOMMON,
			ALWAYS
		}

		private TableTypes type;

		private ScrollTypes[] scrolls;

		private ItemDrop[] charms;

		private ItemDrop[] drops;

		public ItemDropTable() {
		}

		public ItemDrop[] getCharms() {
			return charms;
		}

		public ItemDrop[] getDrops() {
			return drops;
		}

		public ScrollTypes[] getScrolls() {
			return scrolls;
		}

		public TableTypes getType() {
			return type;
		}
	}

	private short id;

	private ItemDropTable constant;

	private ItemDropTable common;

	private ItemDropTable uncommon;

	private ItemDropTable rare;
	
	private ItemDropTable always;

	private boolean useRareTable;

	public ItemDropDefinition() {
	}

	public ItemDropTable getCommon() {
		return common;
	}

	public ItemDropTable getConstant() {
		return constant;
	}

	public int getId() {
		return id;
	}
	
	public ItemDropTable getAlways() {
		return always;
	}

	public ItemDropTable getRare() {
		return rare;
	}

	public ItemDropTable getUncommon() {
		return uncommon;
	}

	public boolean isRareTable() {
		return useRareTable;
	}

	public Item[] getMostExpensiveDrops(int amount) {

		List<Item> drops = new ArrayList<>();

		if (constant != null && constant.getDrops() != null) {
			for (ItemDrop item : constant.getDrops()) {
				if (item == null) {
					continue;
				}
				Item itemDrop = new Item(item.getId());
				if (itemDrop.getDefinition() == null) {
					continue;
				}
				drops.add(itemDrop);
			}
		}
		
		if (common != null && common.getDrops() != null) {
			for (ItemDrop item : common.getDrops()) {
				if (item == null) {
					continue;
				}
				Item itemDrop = new Item(item.getId());
				if (itemDrop.getDefinition() == null) {
					continue;
				}
				drops.add(itemDrop);
			}
		}
		
		if (uncommon != null && uncommon.getDrops() != null) {
			for (ItemDrop item : uncommon.getDrops()) {
				if (item == null) {
					continue;
				}
				Item itemDrop = new Item(item.getId());
				if (itemDrop.getDefinition() == null) {
					continue;
				}
				drops.add(itemDrop);
			}
		}
		
		if (rare != null && rare.getDrops() != null) {
			for (ItemDrop item : rare.getDrops()) {
				if (item == null) {
					continue;
				}
				Item itemDrop = new Item(item.getId());
				if (itemDrop.getDefinition() == null) {
					continue;
				}
				drops.add(itemDrop);
			}
		}

		Item[] items = new Item[amount];
		
		Collections.sort(drops, (first, second) -> second.getDefinition().getGeneralPrice() - first.getDefinition().getGeneralPrice());
		
		for (int i = 0; i < items.length; i++) {
			if (drops.size() <= i) {
				break;
			}
			items[i] = drops.get(i);
		}

		return items;
	}
}
