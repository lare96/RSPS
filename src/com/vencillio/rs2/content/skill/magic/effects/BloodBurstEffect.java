package com.vencillio.rs2.content.skill.magic.effects;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;

public class BloodBurstEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		int dmg = p.getLastDamageDealt();
		if (dmg >= 4) {
			int heal = dmg / 4;
			int tmp20_19 = 3;
			short[] tmp20_16 = p.getLevels();
			tmp20_16[tmp20_19] = ((short) (tmp20_16[tmp20_19] + heal));
			if (p.getLevels()[3] > p.getMaxLevels()[3]) {
				p.getLevels()[3] = p.getMaxLevels()[3];
			}
			p.getSkill().update(3);
		}
	}
}
