package com.vencillio.core.task;

import com.vencillio.rs2.entity.Entity;

public abstract class RunOnceTask extends Task {

	public RunOnceTask(Entity entity, int delay) {
		super(entity, delay);
	}

	@Override
	public void execute() {
		stop();
	}

	@Override
	public abstract void onStop();

}
