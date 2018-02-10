package com.vencillio.rs2.content.skill.agility.obstacle.interaction.rooftop.ardougne;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.skill.agility.obstacle.interaction.ObstacleInteraction;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public interface ArdougneRoofJumpInteraction extends ObstacleInteraction {
	
	@Override
	public default void start(Player player) {
	}
	
	@Override
	public default void onExecution(Player player, Location start, Location end) {
		TaskQueue.queue(new Task(player, 1, true) {
			int ticks = 0;
			
			@Override
			public void execute() {
				switch (ticks++) {
				case 1:
					player.getUpdateFlags().sendFaceToDirection(2667, 3311);
					player.getUpdateFlags().sendAnimation(new Animation(2586));
					break;
					
				case 2:
					player.teleport(new Location(2667, 3311, 1));
					player.getUpdateFlags().sendAnimation(new Animation(2588));
					break;
					
				case 3:
					player.getUpdateFlags().sendFaceToDirection(2665, 3315);
					break;

				case 4:
					player.getUpdateFlags().sendAnimation(new Animation(2588));
					player.teleport(new Location(2665, 3315, 1));
					break;

				case 6:
					player.teleport(end);
					player.getUpdateFlags().sendAnimation(new Animation(2588));
					stop();
					break;
				}
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