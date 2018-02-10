package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.mob.Mob;

public class ShearingTask extends Task {
	
	private final Mob mob;

	public ShearingTask(Mob mob, int cycles) {
		super(cycles, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.mob = mob;
	}

	@Override
	public void execute() {
		mob.transform(2801);
		stop();
	}

	@Override
	public void onStop() {
	}
}
