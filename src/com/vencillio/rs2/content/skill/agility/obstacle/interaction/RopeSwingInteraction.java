package com.vencillio.rs2.content.skill.agility.obstacle.interaction;

import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.Task.BreakType;
import com.vencillio.core.task.Task.StackType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.ForceMoveTask;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendAnimateObject;

public interface RopeSwingInteraction extends ObstacleInteraction {

	@Override
	public default void start(Player player) {
	}

	@Override
	public default void onExecution(Player player, Location start, Location end) {
		int modX = end.getX() - player.getLocation().getX();
		int modY = end.getY() - player.getLocation().getY();
		int xMod = Math.abs(modX) > Math.abs(modY) ? modX < 0 ? -1 : modX > 0 ? 1 : 0 : 0;
		int yMod = Math.abs(modY) > Math.abs(modY) ? modY < 0 ? -1 : modY > 0 ? 1 : 0 : 0;

		TaskQueue.queue(new Task(player, 1, true, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
				if (player.getLocation().equals(new Location(start.getX() + xMod, start.getY() + yMod))) {
					stop();
					return;
				}
				
				int dX = Integer.signum(start.getX() - player.getX());
				int dY = Integer.signum(start.getY() - player.getY());
				
				player.getMovementHandler().walkTo(dX + xMod, dY + yMod);
			}

			@Override
			public void onStop() {
				RSObject obj = (RSObject) player.getAttributes().get("AGILITY_OBJ");
				player.send(new SendAnimateObject(obj, 497));
				TaskQueue.queue(new ForceMoveTask(player, 2, new Location(player.getX() + xMod, player.getY() + yMod), new Location(modX, modY), 751, 28, 66, obj.getFace() == 0 ? 2 : 0));
			}
		});
	}

	@Override
	public default void onCancellation(Player player) {
	}
}