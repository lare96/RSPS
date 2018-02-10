package com.vencillio.rs2.content.combat;

import com.vencillio.rs2.entity.Entity;

public abstract interface CombatEffect {
	
	public abstract void execute(Entity paramEntity1, Entity paramEntity2);
}
