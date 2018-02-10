package com.vencillio.rs2.content.combat.impl;

import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;

public abstract interface CombatEffect {

	public abstract void execute(Player paramPlayer, Entity paramEntity);
}
