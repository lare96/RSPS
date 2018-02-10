package com.vencillio.rs2.entity.mob.impl;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;

public class CorporealBeast extends Mob {
	
	private Mob[] darkEnergyCores = null;

	public CorporealBeast() {
		super(319, true, new Location(2946, 4386));
	}

	public boolean areCoresDead() {
		if (darkEnergyCores == null) {
			return true;
		}

		for (Mob mob : darkEnergyCores) {
			if (!mob.isDead()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void doPostHitProcessing(Hit hit) {
		if ((getCombatants().size() != 0) && (getLevels()[3] != 0) && (getLevels()[3] <= 150) && (areCoresDead()))
			darkEnergyCores = DarkEnergyCore.spawn();
	}

	@Override
	public void onDeath() {
		darkEnergyCores = null;
	}

	public void spawn() {
		new CorporealBeast();
	}
}
