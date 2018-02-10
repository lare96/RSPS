package com.vencillio.rs2.content.skill.agility.obstacle.interaction.rooftop.ardougne;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.skill.agility.obstacle.interaction.ObstacleInteraction;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public interface ArdougneRoofJumpInteraction2 extends ObstacleInteraction {
	
	@Override
	public default void start(Player player) {
		player.getUpdateFlags().sendFaceToDirection(2658, 3298);
	}
	
	@Override
	public default void onExecution(Player player, Location start, Location end) {
		TaskQueue.queue(new Task(player, 1, true) {
			int ticks = 0;
			
			@Override
			public void execute() {
				switch (ticks++) {
				case 1:
					player.getUpdateFlags().sendAnimation(new Animation(2586));
					player.teleport(new Location(2658, 3298, 1));
					break;
					
				case 2:
					player.getUpdateFlags().sendFaceToDirection(2658, 3298);
					player.getUpdateFlags().sendAnimation(new Animation(2588));
					break;
					
				case 3:
					player.getMovementHandler().walkTo(3, 0);
					break;

				case 7:
					player.getUpdateFlags().sendFaceToDirection(2663, 3296);
					player.getUpdateFlags().sendAnimation(new Animation(2586));
					break;

				case 8:
					player.getUpdateFlags().sendAnimation(new Animation(2588));
					player.teleport(new Location(2663, 3297, 1));
					break;

				case 9:
					player.getMovementHandler().walkTo(3, 0);
					break;
					
				case 13:
					player.getUpdateFlags().sendAnimation(new Animation(2586));
					break;

				case 14:
					player.teleport(end);
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