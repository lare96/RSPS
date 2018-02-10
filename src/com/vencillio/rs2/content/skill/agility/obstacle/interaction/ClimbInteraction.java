package com.vencillio.rs2.content.skill.agility.obstacle.interaction;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public interface ClimbInteraction extends ObstacleInteraction {
	
	@Override
	public default void start(Player player) {
		// Climbing has nothing special on start up.
	}
	
	@Override
	public default void onExecution(Player player, Location start, Location end) {
		player.getUpdateFlags().sendAnimation(new Animation(getAnimation()));
		TaskQueue.queue(new Task(player, 3, false) {
			@Override
			public void execute() {
				player.teleport(end);
				player.getUpdateFlags().sendAnimation(new Animation(65535));
				this.stop();
			}

			@Override
			public void onStop() {
			}
		});
	}
	
	@Override
	public default void onCancellation(Player player) {
		// Climbing has nothing special on cancellation.
	}
}