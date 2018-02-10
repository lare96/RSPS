package com.vencillio.rs2.content.skill.fletching.fletchable;

import com.vencillio.rs2.entity.item.Item;

public final class FletchableItem {
	
	private final Item product;
	
	private final int level;
	
	private final  double experience;

	public FletchableItem(Item product, int level, double experience) {
		this.product = product;
		this.level = level;
		this.experience = experience;
	}

	public Item getProduct() {
		return product;
	}

	public int getLevel() {
		return level;
	}

	public double getExperience() {
		return experience;
	}
}