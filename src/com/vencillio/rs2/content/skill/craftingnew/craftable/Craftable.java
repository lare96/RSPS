package com.vencillio.rs2.content.skill.craftingnew.craftable;

import com.vencillio.rs2.entity.item.Item;

public interface Craftable {
	
	public String getName();
	
	public int getAnimation();
	
	public Item getUse();
	
	public Item getWith();
	
	public CraftableItem[] getCraftableItems();
	
	public Item[] getIngediants(int index);
	
	public String getProductionMessage();
}