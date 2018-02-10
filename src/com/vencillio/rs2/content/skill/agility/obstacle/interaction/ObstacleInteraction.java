package com.vencillio.rs2.content.skill.agility.obstacle.interaction;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.agility.Agility;
import com.vencillio.rs2.content.skill.agility.obstacle.Obstacle;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerAnimations;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public interface ObstacleInteraction {
	
	int getAnimation();
	
	String getPreMessage();
	
	String getPostMessage();
	
	void start(Player player);
	
	void onExecution(Player player,  Location start, Location end);
	
	void onCancellation(Player player);
	
	default void execute(Player player, Obstacle next, Location start, Location end, int level, float experience, int ordinal) {
		if (!canExecute(player, level)) {
			return;
		}
		
		player.getAttributes().set("TEMP_CONTROLLER", player.getController());
		player.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		player.getMovementHandler().setForceMove(true);
		player.getMovementHandler().reset();
		player.getCombat().reset();

		TaskQueue.queue(new Task(player, 1, false) {
			private final PlayerAnimations ANIMATION = player.getAnimations().copy();
			private final boolean RUNNING = player.getRunEnergy().isRunning();
			private boolean started = false;
			private Obstacle nextObstacle = next;

			@Override
			public void onStart() {
				player.getSkill().lock(4);
				player.getRunEnergy().setRunning(false);
				if (getPreMessage() != null) {
					player.send(new SendMessage(getPreMessage()));
				}
				start(player);
				
			}
			@Override
			public void execute() {
				if (nextObstacle != null && player.getLocation().equals(nextObstacle.getEnd())) {
					Location delta = Utility.delta(nextObstacle == null ? start : nextObstacle.getStart(), nextObstacle == null ? end : nextObstacle.getEnd());
					player.getUpdateFlags().sendFaceToDirection(nextObstacle.getStart().getX() + delta.getX(), nextObstacle.getStart().getY() + delta.getY());
					if (canExecute(player, level)) {
						player.getSkill().lock(4);
						nextObstacle.getType().getInteraction().start(player);
						nextObstacle.getType().getInteraction().onExecution(player, nextObstacle.getStart(), nextObstacle.getEnd());
						if (nextObstacle.getType().getInteraction().getPreMessage() != null) {
							player.send(new SendMessage(nextObstacle.getType().getInteraction().getPreMessage()));
						}
					}
					if (nextObstacle.getNext() != null) {
						nextObstacle.getType().getInteraction().onCancellation(player);
						nextObstacle = nextObstacle.getNext();
					} else {
						this.stop();
						return;
					}
				} else if (player.getLocation().equals(end)) {
					Location delta = Utility.delta(nextObstacle == null ? start : nextObstacle.getStart(), nextObstacle == null ? end : nextObstacle.getEnd());
					player.getUpdateFlags().sendFaceToDirection((nextObstacle == null ? start.getX() : nextObstacle.getStart().getX()) + delta.getX(),
							(nextObstacle == null ? start.getY() : nextObstacle.getStart().getY()) + delta.getY());
					if (nextObstacle != null) {
						if (canExecute(player, level)) {
							player.getSkill().lock(4);
							nextObstacle.getType().getInteraction().start(player);
							nextObstacle.getType().getInteraction().onExecution(player, nextObstacle.getStart(), nextObstacle.getEnd());
							if (nextObstacle.getType().getInteraction().getPreMessage() != null) {
								player.send(new SendMessage(nextObstacle.getType().getInteraction().getPreMessage()));
							}
						}
						if (nextObstacle.getNext() != null) {
							nextObstacle.getType().getInteraction().onCancellation(player);
							nextObstacle = nextObstacle.getNext();
						} else {
							this.stop();
							return;
						}
					} else {
						this.stop();
						return;
					}
				}

				if (!started) {
					started = true;
					Location delta = Utility.delta(nextObstacle == null ? start : nextObstacle.getStart(), nextObstacle == null ? end : nextObstacle.getEnd());
					player.getUpdateFlags().sendFaceToDirection((nextObstacle == null ? start.getX() : nextObstacle.getStart().getX()) + delta.getX(),(nextObstacle == null ? start.getY() : nextObstacle.getStart().getY()) + delta.getY());
					onExecution(player, start, end);

					if (ordinal > -1 && Utility.random(45) == 0) {
						GroundItemHandler.add(new Item(11849, 1), end, player, player.ironPlayer() ? player : null);
						player.send(new SendMessage("<col=C60DDE>There appears to be a wild Grace mark near you."));
					}
					
				}
				
				player.getMovementHandler().setForceMove(true);
				player.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
			}

			@Override
			public void onStop() {
				if (getPostMessage() != null) {
					player.send(new SendMessage(getPostMessage()));
				}
				
				if (experience > 0) {
					player.getSkill().addExperience(Skills.AGILITY, experience);
				}
				
				if (ordinal > -1) {
					if (ordinal == 0) {
						player.getAttributes().set("AGILITY_FLAGS", 1 << ordinal);
					} else {
						player.getAttributes().set("AGILITY_FLAGS", player.getAttributes().getInt("AGILITY_FLAGS") | (1 << ordinal));
					}
				}
				
				
				if (Location.inGnomeCourse(player)) {
					if (courseRewards(player, "Gnome Agility Course", ordinal, Agility.GNOME_FLAGS, 39)) {
						AchievementHandler.activateAchievement(player, AchievementList.COMPLETE_100_GNOME_COURSES, 1);
					}
				}
				
				if (Location.inBarbarianCourse(player)) {
					if (courseRewards(player, "Barbarian Agility Course", ordinal, Agility.BARBARIAN_FLAGS, 46.5f)) {
						AchievementHandler.activateAchievement(player, AchievementList.COMPLETE_250_BARB_COURSES, 1);
					}
				}
				
				if (Location.inWildernessCourse(player)) {
					if (courseRewards(player, "Wilderness Agility Course", ordinal, Agility.WILDERNESS_FLAGS, 498.9f)) {
						AchievementHandler.activateAchievement(player, AchievementList.COMPLETE_500_WILD_COURSES, 1);
					}
				}

				player.teleport(nextObstacle != null ? nextObstacle.getEnd() : end);
				player.getRunEnergy().setRunning(RUNNING);
				player.getAnimations().set(ANIMATION);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().sendAnimation(new Animation(65535));
				player.setController((Controller) player.getAttributes().get("TEMP_CONTROLLER"));
				player.getMovementHandler().setForceMove(false);
				player.getMovementHandler().reset();
				player.getCombat().reset();
				onCancellation(player);
			}
		});
	}

	default boolean canExecute(Player player, int level) {
		if (player.getSkill().locked()) {
			return false;
		}

		if (player.getSkill().getLevels()[Skills.AGILITY] < level) {
			DialogueManager.sendStatement(player, "You need an agility level of " + level + " to do this!");
			return false;
		}

		return true;
	}
	
	default boolean courseRewards(Player player, String course, int ordinal, int flags, float bonus) {
		if (((int) player.getAttributes().get("AGILITY_FLAGS") & flags) != flags) {
			return false;
		}
		player.getSkill().addExperience(Skills.AGILITY, bonus);
		player.send(new SendMessage("You have completed the " + course + " and receive 5 tickets."));
		if (player.getInventory().add(new Item(2996, 5)) <= 0) {
			if (player.getBank().depositFromNoting(2996, 5, 0, true) <= 0) {
				GroundItemHandler.add(new Item(2996, 5), player.getLocation(), player, player.ironPlayer() ? player : null);
				player.send(new SendMessage("@dre@Your tickets have dropped on the ground!"));
			} else {
				player.send(new SendMessage("@dre@Your tickets have been added to your bank."));
			}
		}
		player.getAttributes().set("AGILITY_FLAGS", 0);
		return true;
	}
}