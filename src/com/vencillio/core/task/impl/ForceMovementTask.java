package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;

public abstract class ForceMovementTask extends Task {

	protected final Player player;
	protected final Controller start;
	protected final Controller to;

	protected final Location dest;
	protected final int xMod;
	protected final int yMod;

	public ForceMovementTask(Player player, Location dest, Controller to) {
		super(player, 1);
		this.player = player;
		this.dest = dest;
		this.to = to;
		start = player.getController();
		player.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);

		int xDiff = player.getLocation().getX() - dest.getX();
		int yDiff = player.getLocation().getY() - dest.getY();

		if (xDiff != 0)
			xMod = (xDiff < 0 ? 1 : -1);
		else
			xMod = 0;
		if (yDiff != 0)
			yMod = (yDiff < 0 ? 1 : -1);
		else
			yMod = 0;
		if (xDiff != 0 && yDiff != 0) {
			stop();
			player.setController(start);
		} else {
			player.getMovementHandler().reset();
		}
		player.getCombat().reset();
	}

	@Override
	public void execute() {
		player.getMovementHandler().setForceMove(true);
		player.getMovementHandler().walkTo(xMod, yMod);
		if (player.getLocation().getX() + xMod == dest.getX() && player.getLocation().getY() + yMod == dest.getY()) {
			onDestination();
			stop();
		}
	}

	public abstract void onDestination();

	@Override
	public void onStop() {
		player.setController(to);
	}
}
