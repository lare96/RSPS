package com.vencillio.rs2.content.combat.impl;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.GraphicTask;
import com.vencillio.core.task.impl.HitTask;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.formula.RangeFormulas;
import com.vencillio.rs2.content.skill.prayer.Curses;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.EquipmentConstants;

public class Ranged {

	private final Entity entity;
	private Attack attack = null;
	private Animation animation = null;
	private Graphic start = null;
	private Graphic end = null;
	private Projectile projectile = null;

	private int pOffset = 0;
	private byte gOffset = 0;

	public Ranged(Entity entity) {
		this.entity = entity;
	}

	public void execute(Entity attacking) {
		if ((attack == null) || (attacking == null) || (attacking.isDead())) {
			return;
		}

		boolean success;

		success = Utility.randomNumber(RangeFormulas.calculateRangeDefence(entity.getCombat().getAttacking())) <= Utility.randomNumber(RangeFormulas.calculateRangeAttack(entity));

		int damage = entity.getCorrectedDamage(Combat.next(entity.getMaxHit(CombatTypes.RANGED) + 1));

		if (entity.getPlayer() != null) {
			if (success && damage > 0) { //check for poison here instead to only poison if magic is not splash
				PoisonWeapons.checkForPoison(entity.getPlayer(), entity.getCombat().getAttacking());

				if(entity.getPlayer().getRanged().isOnyxEffectActive()) {
					damage *= 1.2;
				}

				if(attacking.isNpc()) {
					if (entity.getPlayer().getEquipment().isWearingItem(8839, EquipmentConstants.TORSO_SLOT) && entity.getPlayer().getEquipment().isWearingItem(8840, EquipmentConstants.LEGS_SLOT)) {
						Curses.applySoulSplit(attacking.getMob(), attacking.getLevels()[3] < damage ? attacking.getLevels()[3] : damage, entity.getPlayer());
					}
				}
			}
		}

		Hit hit = new Hit(entity, (success) || (entity.isIgnoreHitSuccess()) ? damage : 0, Hit.HitTypes.RANGED);

		entity.setLastDamageDealt(hit.getDamage());
		entity.setLastHitSuccess((success) || (entity.isIgnoreHitSuccess()));

		entity.getCombat().updateTimers(attack.getAttackDelay());

		if (animation != null) {
			entity.getUpdateFlags().sendAnimation(animation);
		}

		if (start != null) {
			executeStartGraphic();
		}

		if (projectile != null) {
			executeProjectile(attacking);
		}

		TaskQueue.queue(new HitTask(attack.getHitDelay(), false, hit, attacking));

		if (end != null) {
			TaskQueue.queue(new GraphicTask(attack.getHitDelay(), false, end, attacking));
		}

		attacking.getCombat().setInCombat(entity);
		entity.doConsecutiveAttacks(attacking);
		entity.onAttack(attacking, hit.getDamage(), CombatTypes.RANGED, success);
	}

	public void executeProjectile(Entity target) {
		final int lockon = target.isNpc() ? target.getIndex() + 1 : -target.getIndex() - 1;
		final byte offsetX = (byte) ((entity.getLocation().getY() - target.getLocation().getY()) * -1);
		final byte offsetY = (byte) ((entity.getLocation().getX() - target.getLocation().getX()) * -1);

		if (pOffset > 0) {
			final Projectile p = new Projectile(projectile);
			TaskQueue.queue(new Task(pOffset) {
				@Override
				public void execute() {
					World.sendProjectile(p, entity.getLocation(), lockon, offsetX, offsetY);
					stop();
				}

				@Override
				public void onStop() {
				}
			});
		} else {
			World.sendProjectile(projectile, entity.getLocation(), lockon, offsetX, offsetY);
		}
	}

	public void executeStartGraphic() {
		if (gOffset > 0) {
			final Graphic g = new Graphic(start);

			TaskQueue.queue(new Task(gOffset) {
				@Override
				public void execute() {
					entity.getUpdateFlags().sendGraphic(g);
					stop();
				}

				@Override
				public void onStop() {
				}
			});
		} else {
			entity.getUpdateFlags().sendGraphic(start);
		}
	}

	public Animation getAnimation() {
		return animation;
	}

	public Attack getAttack() {
		return attack;
	}

	public Projectile getProjectile() {
		return projectile;
	}

	public int getProjectileOffset() {
		return pOffset;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setAttack(Attack attack) {
		this.attack = attack;
	}

	public void setAttack(Attack attack, Animation animation, Graphic start, Graphic end, Projectile projectile) {
		this.attack = attack;
		this.animation = animation;
		this.start = start;
		this.end = end;
		this.projectile = projectile;
	}

	public void setEnd(Graphic end) {
		this.end = end;
	}

	public void setProjectile(Projectile projectile) {
		this.projectile = projectile;
	}

	public void setProjectileOffset(int pOffset) {
		this.pOffset = pOffset;
	}

	public void setStart(Graphic start) {
		this.start = start;
	}

	public void setStartGfxOffset(byte gOffset) {
		this.gOffset = gOffset;
	}
}
