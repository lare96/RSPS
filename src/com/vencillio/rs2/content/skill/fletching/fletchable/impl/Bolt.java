package com.vencillio.rs2.content.skill.fletching.fletchable.impl;

import com.vencillio.rs2.content.skill.fletching.Fletching;
import com.vencillio.rs2.content.skill.fletching.fletchable.Fletchable;
import com.vencillio.rs2.content.skill.fletching.fletchable.FletchableItem;
import com.vencillio.rs2.entity.item.Item;

public enum Bolt implements Fletchable {
	OPAL_BOLT(new Item(877, 10), new Item(45, 10), new FletchableItem(new Item(879, 10), 11, 1.6)),
	PEARL_BOLT(new Item(9140, 10), new Item(46, 10), new FletchableItem(new Item(880, 10), 41, 3.2)),
	RED_TOPAZ_BOLT(new Item(9141, 10), new Item(9188, 10), new FletchableItem(new Item(9336, 10), 48, 3.9)),
	SAPPHIRE_BOLT(new Item(9142, 10), new Item(9189, 10), new FletchableItem(new Item(9337, 10), 56, 4.7)),
	EMERALD_BOLT(new Item(9142, 10), new Item(9190, 10), new FletchableItem(new Item(9338, 10), 58, 5.5)),
	RUBY_BOLT(new Item(9143, 10), new Item(9191, 10), new FletchableItem(new Item(9339, 10), 63, 6.3)),
	DIAMOND_BOLT(new Item(9143, 10), new Item(9192, 10), new FletchableItem(new Item(9340, 10), 65, 7.0)),
	DRAGONSTONE_BOLT(new Item(9144, 10), new Item(9193, 10), new FletchableItem(new Item(9341, 10), 71, 8.2)),
	ONYX_BOLT(new Item(9144, 10), new Item(9194, 10), new FletchableItem(new Item(9342, 10), 73, 9.4));

	private final Item use;
	private final Item with;
	private final FletchableItem[] items;

	private Bolt(Item use, Item with, FletchableItem... items) {
		this.use = use;
		this.with = with;
		this.items = items;
	}

	public static void declare() {
		for (Bolt cuttable : values()) {
			Fletching.SINGLETON.addFletchable(cuttable);
		}
	}

	@Override
	public int getAnimation() {
		return 65535;
	}

	@Override
	public Item getUse() {
		return use;
	}

	@Override
	public Item getWith() {
		return with;
	}

	@Override
	public FletchableItem[] getFletchableItems() {
		return items;
	}

	@Override
	public String getProductionMessage() {
		return null;
	}

	@Override
	public Item[] getIngediants() {
		return new Item[] { use, with };
	}
}