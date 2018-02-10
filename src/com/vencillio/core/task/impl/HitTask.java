package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;

/**
 * Represents a hit delay function.
 * 
 * @author Michael Sasse
 * 
 */
public class HitTask extends Task {

	/**
	 * The graphic.
	 */
	private final Hit hit;
	/**
	 * The entity.
	 */
	private final Entity entity;

	/**
	 * Creates a new graphic to queue.
	 * 
	 * @param graphic
	 *            the graphic.
	 * @param delay
	 *            the action delay.
	 */
	public HitTask(int delay, boolean immediate, Hit hit, Entity entity) {
		super(entity, delay, immediate, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.hit = hit;
		this.entity = entity;
		if (delay <= 1) {
			sendBlockAnimation();
		} else {
			final Task t = this;

			TaskQueue.queue(new Task(delay - 1) {

				@Override
				public void execute() {
					if (t.stopped()) {
						stop();
						return;
					}

					sendBlockAnimation();
					stop();
				}

				@Override
				public void onStop() {
				}
			});
		}
	}

	@Override
	public void execute() {
		entity.hit(hit);
		stop();
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	@Override
	public void onStop() {
	}

	public void sendBlockAnimation() {
		if (hit.getAttacker() != null && entity.getCombat().getBlockAnimation() != null && !entity.isDead()) {
			int a = entity.getCombat().getAttackTimer();
			if (a != entity.getCombat().getAttackCooldown()) {
				entity.getUpdateFlags().sendAnimation(entity.getCombat().getBlockAnimation());
			}
		}
	}
}
