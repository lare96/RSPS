package com.vencillio.rs2.content.skill.magic.effects;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;

public class SmokeRushEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		if ((p.getLastDamageDealt() >= 0) && (Utility.randomNumber(2) == 0))
			e.poison(2);
	}
}
