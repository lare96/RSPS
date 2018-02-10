package com.vencillio.rs2.content.skill.craftingnew.craftable;

import com.vencillio.rs2.entity.item.Item;

public final class CraftableItem {
	
	private final Item product;
	private final Item requiredItem;
	private final int level;
	private final  double experience;

	public CraftableItem(Item product, Item requiredItem, int level, double experience) {
		this.product = product;
		this.requiredItem = requiredItem;
		this.level = level;
		this.experience = experience;
	}

	public Item getProduct() {
		return product;
	}
	
	public Item getRequiredItem() {
		return requiredItem;
	}

	public int getLevel() {
		return level;
	}

	public double getExperience() {
		return experience;
	}
}