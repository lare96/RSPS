package com.vencillio.rs2.content.skill.firemaking;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;

public class FireTask extends Task {
	private final GameObject object;
	private final Player p;

	public FireTask(Player p, int cycles, GameObject object) {
		super(cycles, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.object = object;
		ObjectManager.register(object);
		this.p = p;
	}

	@Override
	public void execute() {
		ObjectManager.remove(object);
		GroundItemHandler.add(new Item(592, 1), object.getLocation(), p, p.ironPlayer() ? p : null);
		stop();
	}

	@Override
	public void onStop() {
	}
}
