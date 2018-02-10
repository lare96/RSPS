package com.vencillio.core.definitions;

import com.vencillio.rs2.entity.item.Item;

public class RangedWeaponDefinition {

	public enum RangedTypes {
		THROWN, SHOT
	}

	private short id;
	private RangedTypes type;
	private Item[] arrows;

	public Item[] getArrows() {
		return arrows;
	}

	public int getId() {
		return id;
	}

	public RangedTypes getType() {
		return type;
	}
}
