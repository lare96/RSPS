package com.vencillio.rs2.content.combat.impl;

import com.vencillio.core.task.RunOnceTask;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.GraphicTask;
import com.vencillio.core.task.impl.HitTask;
import com.vencillio.rs2.content.combat.Combat;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.CombatConstants;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.formula.FormulaData;
import com.vencillio.rs2.content.combat.formula.MagicFormulas;
import com.vencillio.rs2.entity.*;

public class Magic {

	private final Entity entity;
	private Attack attack = new Attack(4, 5);
	private Animation animation = null;
	private Graphic start = null;
	private Graphic end = null;
	private Projectile projectile = null;
	private byte pDelay = 0;
	private int nextHit = 0;
	private boolean multi = false;

	public Magic(Entity entity) {
		this.entity = entity;
	}

	public void execute(Entity attacking) {
		if (attack == null) {
			return;
		}

		entity.getCombat().updateTimers(attack.getAttackDelay() + 1);

		if (animation != null) {
			entity.getUpdateFlags().sendAnimation(animation);
		}

		if (start != null && start.getId() != 0) {
			entity.getUpdateFlags().sendGraphic(start);
		}

		if (projectile != null) {
			final int lockon = attacking.isNpc() ? attacking.getIndex() + 1 : -attacking.getIndex() - 1;
			final int offsetX = ((entity.getLocation().getY() - attacking.getLocation().getY()) * -1) - 2;
			final int offsetY = ((entity.getLocation().getX() - attacking.getLocation().getX()) * -1) - 3;
			if (pDelay > 0) {
				TaskQueue.queue(new RunOnceTask(entity, pDelay) {
					@Override
					public void onStop() {
						World.sendProjectile(projectile, CombatConstants.getOffsetProjectileLocation(entity), lockon, (byte) offsetX, (byte) offsetY);
					}
				});
			} else {
				World.sendProjectile(projectile, CombatConstants.getOffsetProjectileLocation(entity), lockon, (byte) offsetX, (byte) offsetY);
			}
		}

		entity.doConsecutiveAttacks(attacking);
		finish(attacking);
	}

	public void finish(Entity attacking) { //Entity is the one attacking, attacking is the one being attacked

		boolean success;

		double accuracy = MagicFormulas.getMagicAttackRoll(entity);
		double defence = MagicFormulas.getMagicDefenceRoll(entity.getCombat().getAttacking());
		double chance = FormulaData.getChance(accuracy, defence);
		boolean accurate = FormulaData.isAccurateHit(chance);

		success = accurate;

		if (nextHit > -1) {
			success = true;
		} else if (nextHit == -1) {
			success = false;
		}

		int damage = nextHit == -2 ? entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.MAGIC) + 1)) : nextHit;


		if (entity.getPlayer() != null) {

			if(success && damage > 0) { //check for poison here instead to only poison if magic is not splash
				PoisonWeapons.checkForPoison(entity.getPlayer(), attacking);
			}

			if (entity.getPlayer().getEquipment().isWearingItem(11791) || entity.getPlayer().getEquipment().isWearingItem(12904))
			{
				damage *= 1.15;
			}
			if (entity.getPlayer().getEquipment().isWearingItem(12002))
			{
				damage *= 1.1;
			}
		}



		if (nextHit != -2) {
			nextHit = -2;
		}

		Hit hit = new Hit(entity, (success || entity.isNpc()) || (entity.isIgnoreHitSuccess()) ? damage : -1, Hit.HitTypes.MAGIC);

		entity.onAttack(attacking, hit.getDamage(), CombatTypes.MAGIC, success || entity.isNpc());

		entity.setLastDamageDealt(hit.getDamage());
		entity.setLastHitSuccess((success || entity.isNpc()) || (entity.isIgnoreHitSuccess()));

		if (hit.getDamage() > -1) {
			TaskQueue.queue(new HitTask(attack.getHitDelay(), false, hit, attacking));
		}

		Graphic end = null;

		if ((success || entity.isNpc()) && (this.end != null))
			end = this.end;
		else if (!success && !entity.isNpc()) {
			end = new Graphic(85, 0, true);
		}

		if (end != null) {
			TaskQueue.queue(new GraphicTask(attack.getHitDelay(), false, end, attacking));
		}
		attacking.getCombat().setInCombat(entity);
	}

	public Attack getAttack() {
		return attack;
	}

	public byte getpDelay() {
		return pDelay;
	}

	public boolean isMulti() {
		return multi;
	}

	public void setAttack(Attack attack, Animation animation, Graphic start, Graphic end, Projectile projectile) {
		this.attack = attack;
		this.animation = animation;
		if (start != null) {
			this.start = new Graphic(start.getId(), start.getDelay(), true);			
		} else {
			this.start = start;
		}
		if (end != null) {
			this.end = new Graphic(end.getId(), end.getDelay(), true);			
		} else {
			this.end = end;
		}
		this.projectile = projectile;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

	public void setNextHit(int hit) {
		nextHit = hit;
	}

	public void setpDelay(byte pDelay) {
		this.pDelay = pDelay;
	}
}
