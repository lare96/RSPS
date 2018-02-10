package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.following.Following.FollowType;

public abstract class FollowToEntityTask extends Task {

	private final Entity p;
	private final Location location;

	private final int minX;
	private final int maxX;
	private final int minY;
	private final int maxY;

	public FollowToEntityTask(Entity p, Entity e) {
		super(p, 1, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.p = p;
		p.getFollowing().setFollow(e, FollowType.FOLLOW_TO);
		location = p.getLocation();

		int size = e.getSize() / 2;
		
		minX = e.getX() - size - 1;
		minY = e.getY() - size - 1;

		maxX = e.getX() + size + 1;
		maxY = e.getY() + size + 1;
	}

	@Override
	public void execute() {
		int pX = location.getX();
		int pY = location.getY();

		if (pX >= minX && pX <= maxX && pY >= minY && pY <= maxY) {
			onDestination();
			stop();
		}
	}

	public abstract void onDestination();

	@Override
	public void onStop() {
		p.getFollowing().reset();
	}

}
