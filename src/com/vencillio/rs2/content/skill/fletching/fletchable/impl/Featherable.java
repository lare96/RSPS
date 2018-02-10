package com.vencillio.rs2.content.skill.fletching.fletchable.impl;

import com.vencillio.rs2.content.skill.fletching.Fletching;
import com.vencillio.rs2.content.skill.fletching.fletchable.Fletchable;
import com.vencillio.rs2.content.skill.fletching.fletchable.FletchableItem;
import com.vencillio.rs2.entity.item.Item;

public enum Featherable implements Fletchable {
	HEADLESS_ARROWS(new Item(314, 15), new Item(52, 15), new FletchableItem(new Item(53, 15), 1, 1.0)),
	BRONZE_BOLT(new Item(314, 10), new Item(9375, 10), new FletchableItem(new Item(877, 10), 9, 0.5)),
	IRON_BOLT(new Item(314, 10), new Item(9377, 10), new FletchableItem(new Item(9140, 10), 39, 1.5)),
	STEEL_BOLT(new Item(314, 10), new Item(9378, 10), new FletchableItem(new Item(9141, 10), 46, 3.5)),
	MITHRIL_BOLT(new Item(314, 10), new Item(9379, 10), new FletchableItem(new Item(9142, 10), 54, 5.0)),
	ADAMANT_BOLT(new Item(314, 10), new Item(9380, 10), new FletchableItem(new Item(9143, 10), 61, 7.0)),
	RUNITE_BOLT(new Item(314, 10), new Item(9381, 10), new FletchableItem(new Item(9144, 10), 69, 10.0)),
	BRONZE_DARTs(new Item(314, 10), new Item(819, 10), new FletchableItem(new Item(806, 10), 1, 1.8)),
	IRON_DARTs(new Item(314, 10), new Item(820, 10), new FletchableItem(new Item(807, 10), 22, 3.8)),
	STEEL_DARTs(new Item(314, 10), new Item(821, 10), new FletchableItem(new Item(808, 10), 37, 7.5)),
	MITHRIL_DARTs(new Item(314, 10), new Item(822, 10), new FletchableItem(new Item(809, 10), 52, 11.2)),
	ADAMANT_DARTs(new Item(314, 10), new Item(823, 10), new FletchableItem(new Item(810, 10), 67, 15.0)),
	RUNE_DARTs(new Item(314, 10), new Item(824, 10), new FletchableItem(new Item(811, 10), 81, 18.8)),
	DRAGON_DARTs(new Item(314, 10), new Item(11232, 10), new FletchableItem(new Item(11230, 10), 95, 25.0));

	private final Item use;
	private final Item with;
	private final FletchableItem[] items;

	private Featherable(Item use, Item with, FletchableItem... items) {
		this.use = use;
		this.with = with;
		this.items = items;
	}

	public static void declare() {
		for (Featherable featherable : values()) {
			Fletching.SINGLETON.addFletchable(featherable);
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