package com.vencillio.core.task.impl;

import com.vencillio.core.cache.map.Door;
import com.vencillio.core.task.Task;

public class TickDoorTask extends Task {

	public TickDoorTask(Door door) {
		super(null, 1);
		if (door.original()) {
			stop();
			return;
		}
	}

	@Override
	public void execute() {

		stop();
	}

	@Override
	public void onStop() {
	}

}
