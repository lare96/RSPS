package com.vencillio.rs2.content.skill.magic;

import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public abstract class Spell {

	public abstract boolean execute(Player paramPlayer);

	public abstract double getExperience();

	public abstract int getLevel();

	public abstract String getName();

	public abstract Item[] getRunes();
}
