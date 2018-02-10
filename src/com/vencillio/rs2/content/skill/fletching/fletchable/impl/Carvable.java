package com.vencillio.rs2.content.skill.fletching.fletchable.impl;

import com.vencillio.rs2.content.skill.fletching.Fletching;
import com.vencillio.rs2.content.skill.fletching.fletchable.Fletchable;
import com.vencillio.rs2.content.skill.fletching.fletchable.FletchableItem;
import com.vencillio.rs2.entity.item.Item;

public enum Carvable implements Fletchable {
	LOG(new Item(946), new Item(1511), new FletchableItem(new Item(52, 15), 1, 5.0), new FletchableItem(new Item(50), 5, 5.0), new FletchableItem(new Item(48), 10, 10.0), new FletchableItem(new Item(9440), 9, 6.0)),
	OAK_LOG(new Item(946), new Item(1521), new FletchableItem(new Item(54), 20, 16.5), new FletchableItem(new Item(56), 25, 25.0), new FletchableItem(new Item(9442), 24, 16.0)),
	WILLOW_LOG(new Item(946), new Item(1519), new FletchableItem(new Item(60), 35, 33.3), new FletchableItem(new Item(58), 40, 41.5), new FletchableItem(new Item(9444), 39, 22.0)),
	TEAK_LOG(new Item(946), new Item(6333), new FletchableItem(new Item(9446), 46, 27.0)),
	MAPLE_LOG(new Item(946), new Item(1517), new FletchableItem(new Item(64), 50, 50.0), new FletchableItem(new Item(62), 55, 51.3), new FletchableItem(new Item(9448), 54, 32.0)),
	MAHOGANY_LOG(new Item(946), new Item(6332), new FletchableItem(new Item(9450), 61, 41.0)),
	YEW_LOG(new Item(946), new Item(1515), new FletchableItem(new Item(68), 65, 67.5), new FletchableItem(new Item(66), 70, 75.0), new FletchableItem(new Item(9452), 69, 50.0)),
	MAGIC_LOG(new Item(946), new Item(1513), new FletchableItem(new Item(72), 80, 83.3), new FletchableItem(new Item(70), 85, 91.5)),
	
	OPAL_BOLT_TIP(new Item(1755), new Item(1609), new FletchableItem(new Item(45, 12), 11, 1.5)),
	JADE_BOLT_TIP(new Item(1755), new Item(1611), new FletchableItem(new Item(9187, 12), 26, 2.0)),
	PEARL_BOLT_TIP(new Item(1755), new Item(411), new FletchableItem(new Item(46, 24), 41, 3.2)),
	PEARLS_BOLT_TIP(new Item(1755), new Item(413), new FletchableItem(new Item(46, 6), 41, 3.2)),
	TOPAZ_BOLT_TIP(new Item(1755), new Item(1613), new FletchableItem(new Item(9188, 12), 48, 3.9)),
	SAPPHIRE_BOLT_TIP(new Item(1755), new Item(1607), new FletchableItem(new Item(9189, 12), 56, 4.7)),
	EMERALD_BOLT_TIP(new Item(1755), new Item(1605), new FletchableItem(new Item(9190, 12), 53, 5.5)),
	RUBY_BOLT_TIP(new Item(1755), new Item(1603), new FletchableItem(new Item(9191, 12), 63, 6.3)),
	DIAMOND_BOLT_TIP(new Item(1755), new Item(1601), new FletchableItem(new Item(9192, 12), 65, 7.0)),
	DRAGONSTONE_BOLT_TIP(new Item(1755), new Item(1615), new FletchableItem(new Item(9193, 12), 71, 8.2)),
	ONYX_BOLT_TIP(new Item(1755), new Item(6573), new FletchableItem(new Item(9194, 12), 73, 9.4));

	private final Item use;
	private final Item with;
	private final FletchableItem[] items;

	private Carvable(Item use, Item with, FletchableItem... items) {
		this.use = use;
		this.with = with;
		this.items = items;
	}

	public static void declare() {
		for (Carvable cuttable : values()) {
			Fletching.SINGLETON.addFletchable(cuttable);
		}
	}

	@Override
	public int getAnimation() {
		switch (this) {
		case OPAL_BOLT_TIP:
		case PEARL_BOLT_TIP:
			return 891;
		case TOPAZ_BOLT_TIP:
			return 892;
		case SAPPHIRE_BOLT_TIP:
			return 888;
		case EMERALD_BOLT_TIP:
			return 889;
		case RUBY_BOLT_TIP:
			return 887;
		case DIAMOND_BOLT_TIP:
			return 890;
		case DRAGONSTONE_BOLT_TIP:
			return 890;
		case ONYX_BOLT_TIP:
			return 2717;
		default:
			return 1248;
		}
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
		return new Item[] { with };
	}
}