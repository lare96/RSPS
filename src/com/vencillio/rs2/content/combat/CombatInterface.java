package com.vencillio.rs2.content.combat;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;

public interface CombatInterface {

	public abstract void afterCombatProcess(Entity paramEntity);

	public abstract boolean canAttack();

	public abstract void checkForDeath();

	public abstract int getCorrectedDamage(double paramInt);

	public abstract int getMaxHit(CombatTypes paramCombatTypes);

	public abstract void hit(Hit paramHit);

	public abstract boolean isIgnoreHitSuccess();

	public abstract void onAttack(Entity paramEntity, int paramInt, CombatTypes paramCombatTypes, boolean paramBoolean);

	public abstract void onCombatProcess(Entity paramEntity);

	public abstract void onHit(Entity paramEntity, Hit paramHit);

	public abstract void updateCombatType();

}
