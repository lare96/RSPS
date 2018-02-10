package com.vencillio.rs2.content.skill.summoning;

import com.vencillio.rs2.entity.player.Player;

public abstract interface FamiliarSpecial {
	public static enum SpecialType {
		COMBAT, NONE;
	}

	public abstract boolean execute(Player paramPlayer,
			FamiliarMob paramFamiliarMob);

	public abstract int getAmount();

	public abstract double getExperience();

	public abstract SpecialType getSpecialType();
}
