package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public abstract class PullLeverTask extends Task {

	private final Player player;
	private final int minX;
	private final int maxX;
	private final int minY;
	private final int maxY;
	private Location location;
	private byte wait = 0;

	public PullLeverTask(Player player, int x, int y, int xLength, int yLength) {
		super(player, 1, true, Task.StackType.NEVER_STACK,
				Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		location = player.getLocation();
		minX = (x - 1);
		maxX = (minX + xLength + 1);
		minY = (y - 1);
		maxY = (minY + yLength + 1);
	}

	@Override
	public void execute() {
		int pX = location.getX();
		int pY = location.getY();

		if ((pX >= minX) && (pX <= maxX) && (pY >= minY) && (pY <= maxY)) {
			if (wait == 1) {
				player.getUpdateFlags().sendAnimation(2140, 0);
			}

			if (wait == 3) {
				onDestination();
				stop();
			}

			wait = ((byte) (wait + 1));
		}
	}

	public abstract void onDestination();

	@Override
	public void onStop() {
	}
}
