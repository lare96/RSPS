package com.vencillio.rs2.content.skill.agility.obstacle.interaction;

import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public interface WalkInteraction extends ObstacleInteraction {
	
	@Override
	public default void start(Player player) {
		player.getAnimations().setRunEmote(getAnimation());
		player.getAnimations().setWalkEmote(getAnimation());
		player.getAnimations().setStandEmote(getAnimation());
	}
	
	@Override
	public default void onExecution(Player player, Location start, Location end) {
		int xDiff = -player.getLocation().getX() + end.getX();
		int yDiff = -player.getLocation().getY() + end.getY();
		
		player.getMovementHandler().reset();
		player.getCombat().reset();
		player.getMovementHandler().walkTo(xDiff, yDiff);
		player.setAppearanceUpdateRequired(true);
	}
	
	@Override
	public default void onCancellation(Player player) {
	}
}