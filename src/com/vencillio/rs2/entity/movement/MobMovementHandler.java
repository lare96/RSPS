package com.vencillio.rs2.entity.movement;

import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.Walking;

public class MobMovementHandler extends MovementHandler {
	private final Mob mob;

	public MobMovementHandler(Mob mob) {
		super(mob);
		this.mob = mob;
	}

	@Override
	public boolean canMoveTo(int direction) {
		return Walking.canMoveTo(mob, direction);
	}

	@Override
	public boolean canMoveTo(int x, int y, int size, int direction) {
		return Walking.canMoveTo(mob, x, y, direction, size);
	}

	@Override
	public boolean moving() {
		return mob.getFollowing().isFollowing();
	}

	@Override
	public void process() {
	}
}
