package com.vencillio.rs2.entity.mob.impl;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

public class KalphiteQueen extends Mob {
	
	private Entity lastKilledBy = null;

	public KalphiteQueen() {
		super(1158, true, new Location(3480, 9495));
	}

	@Override
	public int getAffectedDamage(Hit hit) {
		switch (hit.getType()) {
		case RANGED:
		case MAGIC:
			if (getId() == 1158) {
				return 0;
			}
			break;
		case MELEE:
			if (getId() == 1160) {
				if ((hit.getAttacker() != null) && (!hit.getAttacker().isNpc())) {
					Player player = World.getPlayers()[hit.getAttacker().getIndex()];

					if ((player != null) && (player.getMelee().isVeracEffectActive())) {
						return hit.getDamage();
					}
				}

				return 0;
			}
			break;
		default:
			return hit.getDamage();
		}

		return hit.getDamage();
	}

	@Override
	public void onDeath() {
		lastKilledBy = getCombat().getLastAttackedBy();
	}

	public void transform() {
		transform(getId() == 1160 ? 1158 : 1160);

		if (lastKilledBy != null) {
			getCombat().setAttack(lastKilledBy);
		}
	}
}
