package com.vencillio.rs2.entity.mob.abilities;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

public class BorkAbility implements CombatEffect {
	@Override
	public void execute(Entity e1, Entity e2) {
		if (e1.isNpc()) {
			Mob m = com.vencillio.rs2.entity.World.getNpcs()[e1.getIndex()];

			if ((m != null) && (m.getCombat().getCombatType() == CombatTypes.MAGIC) && (m.getCombatants() != null) && (m.getCombatants().size() > 0))
				for (Player p : m.getCombatants())
					if (!p.equals(e1.getCombat().getAttacking()))
						m.getCombat().getMagic().finish(p);
		}
	}
}
