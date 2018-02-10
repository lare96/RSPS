package com.vencillio.rs2.content.skill.fletching.fletchable.impl;

import com.vencillio.rs2.content.skill.fletching.Fletching;
import com.vencillio.rs2.content.skill.fletching.fletchable.Fletchable;
import com.vencillio.rs2.content.skill.fletching.fletchable.FletchableItem;
import com.vencillio.rs2.entity.item.Item;

public enum Crossbow implements Fletchable {
	BRONZE_CROSSBOW(new Item(9440), new Item(9420), new FletchableItem(new Item(9454), 9, 12.0)),
	IRON_CROSSBOW(new Item(9444), new Item(9423), new FletchableItem(new Item(9457), 39, 44.0)),
	STEEL_CROSSBOW(new Item(9446), new Item(9425), new FletchableItem(new Item(9459), 46, 54.0)),
	MITHRIL_CROSSBOW(new Item(9448), new Item(9427), new FletchableItem(new Item(9461), 54, 64.0)),
	ADAMANT_CROSSBOW(new Item(9450), new Item(9429), new FletchableItem(new Item(9463), 61, 82.0)),
	RUNE_CROSSBOW(new Item(9452), new Item(9431), new FletchableItem(new Item(9465), 69, 100.0));

	private final Item use;
	private final Item with;
	private final FletchableItem[] items;

	private Crossbow(Item use, Item with, FletchableItem... items) {
		this.use = use;
		this.with = with;
		this.items = items;
	}

	public static void declare() {
		for (Crossbow cuttable : values()) {
			Fletching.SINGLETON.addFletchable(cuttable);
		}
	}

	@Override
	public int getAnimation() {
		switch (this) {
		case BRONZE_CROSSBOW:
			return 4436;
		case IRON_CROSSBOW:
			return 4438;
		case STEEL_CROSSBOW:
			return 4439;
		case MITHRIL_CROSSBOW:
			return 4440;
		case ADAMANT_CROSSBOW:
			return 4441;
		case RUNE_CROSSBOW:
			return 4442;
		default:
			return 4436;
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
		return new Item[] { use, with };
	}
}