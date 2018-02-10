package com.vencillio.rs2.entity.mob.impl.wild;

import java.util.ArrayList;
import java.util.List;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;

/**
 * Handles the Scorpia boss
 * @author Daniel
 *
 */
public class Scorpia extends Mob {

	public Scorpia() {
		super(6615, true, new Location(3232, 10341, 0));
	}

	private List<Mob> guardians = new ArrayList<>();

	private boolean spawnGuardians = false;

	@Override
	public void doAliveMobProcessing() {
		if (isSpawnGuardians()) {
			boolean clear = false;
			if (!guardians.isEmpty()) {
				for (Mob guardian : guardians) {
					if (!guardian.isDead()) {
						if (getLevels()[3] >= 200) {
							clear = true;
							guardian.remove();
							continue;
						}
						
						if (Utility.getExactDistance(guardian.getLocation(), getLocation()) <= 5) {
							if (Utility.randomNumber(10) == 1) {
								getLevels()[3] += 1;								
							}
						} else {
							clear = true;
							guardian.remove();
						}
					}
				}
				
				if (clear) {
					guardians.clear();
				}
			}
		}
	}

	@Override
	public void hit(Hit hit) {

		if (isDead()) {
			return;
		}

		super.hit(hit);

		if (getLevels()[3] < 100 && !isSpawnGuardians()) {
			spawnGuardians();
		}
	}

	private void spawnGuardians() {
		setSpawnGuardians(true);
		for (int index = 0; index < 2; index++) {
			Mob mob = new Mob(6617, true, new Location(getX() + index, getY(), getZ()));
			mob.getFollowing().setFollow(this);
			mob.getUpdateFlags().faceEntity(getIndex());
			guardians.add(mob);
		}
	}
	
	@Override
	public void onAttack(Entity attack, int hit, CombatTypes type, boolean success) {
		super.onAttack(attack, hit, type, success);
		if (success) {
			if (Utility.random(10) == 0) {
				attack.poison(20);
			}
		}
	}

	@Override
	public int getRespawnTime() {
		return 60;
	}

	@Override
	public void onDeath() {
		setSpawnGuardians(false);
		for (Mob guardians : guardians) {
			if (!guardians.isDead()) {
				guardians.remove();
			}
		}
		guardians.clear();
	}

	public boolean isSpawnGuardians() {
		return spawnGuardians;
	}

	public void setSpawnGuardians(boolean spawnGuardians) {
		this.spawnGuardians = spawnGuardians;
	}

}
