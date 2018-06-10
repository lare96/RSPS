package com.vencillio.rs2.content.combat.impl;

import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.HitTask;
import com.vencillio.rs2.content.combat.Combat;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.formula.FormulaData;
import com.vencillio.rs2.content.combat.formula.MeleeFormulas;
import com.vencillio.rs2.content.skill.prayer.Curses;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.player.Player;

@SuppressWarnings("all")
public class Melee {

	private final Entity entity;
	private Attack attack = new Attack(1, 5);
	private Animation animation = new Animation(422, 0);

	private int nextDamage = -1;
	private double damageBoost = 1.0D;

	public Melee(Entity entity) {
		this.entity = entity;
	}

	public void execute(Entity attacking) {
		if (attack == null) {
			return;
		}

		double accuracy = MeleeFormulas.getAttackRoll(entity);
		double defence = MeleeFormulas.getDefenceRoll(entity, entity.getCombat().getAttacking());
		double chance = FormulaData.getChance(accuracy, defence);
		boolean accurate = FormulaData.isAccurateHit(chance);

		boolean success;

		success = accurate;

		int damage = (int) (entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.MELEE) + 1)) * damageBoost);

		if(attacking.isNpc() && entity.getPlayer() != null) {
			Player p = entity.getPlayer();
			if(success) { //check for poison here instead to only poison if magic is not splash
				PoisonWeapons.checkForPoison(p, entity.getCombat().getAttacking());
			}
			if(p.getEquipment().isWearingItem(8839, EquipmentConstants.TORSO_SLOT) && p.getEquipment().isWearingItem(8840, EquipmentConstants.LEGS_SLOT) && success)
				Curses.applySoulSplit(attacking.getMob(), attacking.getLevels()[3] < damage ? attacking.getLevels()[3] : damage, p);
		}

		if (nextDamage != -1) {
			damage = nextDamage;
			success = true;
		}
		
		Hit hit = new Hit(entity, (success) || (entity.isIgnoreHitSuccess()) ? damage : 0, Hit.HitTypes.MELEE);
		entity.setLastDamageDealt(!success ? 0 : hit.getDamage());

		entity.setLastHitSuccess((success) || (entity.isIgnoreHitSuccess()));

		entity.onAttack(attacking, hit.getDamage(), CombatTypes.MELEE, success);

		entity.getCombat().updateTimers(attack.getAttackDelay());

		if (animation != null) {
			entity.getUpdateFlags().sendAnimation(animation);
		}

		entity.doConsecutiveAttacks(attacking);
		finish(attacking, hit);
	}

	public void finish(Entity attacking, Hit hit) {
		attacking.getCombat().setInCombat(entity);
		TaskQueue.queue(new HitTask(attack.getHitDelay(), false, hit, attacking));
	}

	public Animation getAnimation() {
		return animation;
	}

	public Attack getAttack() {
		return attack;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setAttack(Attack attack, Animation animation) {
		this.attack = attack;
		this.animation = animation;
	}

	public void setDamageBoost(double damageBoost) {
		this.damageBoost = damageBoost;
	}

	public void setNextDamage(int nextDamage) {
		this.nextDamage = nextDamage;
	}
}
