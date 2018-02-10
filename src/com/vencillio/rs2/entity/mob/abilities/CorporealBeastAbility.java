package com.vencillio.rs2.entity.mob.abilities;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.CombatEffect;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

public class CorporealBeastAbility implements CombatEffect {
	@Override
	public void execute(Entity e1, Entity e2) {
		if (e1.getCombat().getCombatType() == CombatTypes.MELEE) {
			Mob mob = com.vencillio.rs2.entity.World.getNpcs()[e1.getIndex()];
			if ((mob != null) && (mob.getCombatIndex() == 0)) {
				for (Player p : mob.getCombatants()) {
					if (p != e2) {
						mob.getCombat().getMelee().finish(p, new Hit(Utility.randomNumber(e1.getMaxHit(CombatTypes.MELEE)), Hit.HitTypes.MELEE));
					}
				}
			}
		}
	}
}
