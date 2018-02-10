package com.vencillio.rs2.content.skill.magic.effects;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;

public class EntangleEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		e.freeze(15, 5);
	}
}
