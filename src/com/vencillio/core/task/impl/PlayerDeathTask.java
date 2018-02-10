package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.Hit.HitTypes;
import com.vencillio.rs2.content.combat.impl.PlayerDrops;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class PlayerDeathTask extends Task {

	private final Player player;
	private final Controller c;

	public PlayerDeathTask(final Player player) {
		super(player, 5, false, Task.StackType.NEVER_STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);

		this.player = player;
		c = player.getController();

		if (player.isDead()) {
			stop();
			return;
		}

		player.getUpdateFlags().faceEntity(65535);
		player.setDead(true);
		player.getMovementHandler().reset();

		TaskQueue.queue(new Task(player, 2, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
			@Override
			public void execute() {
				player.getUpdateFlags().sendGraphic(new Graphic(293));
				player.getUpdateFlags().sendAnimation(836, 0);

				Entity killer = player.getCombat().getDamageTracker().getKiller();

				if (player.getPrayer().active(Prayer.RETRIBUTION)) {
					player.getUpdateFlags().sendGraphic(new Graphic(437));
					
					if (killer != null && !killer.isNpc()) {
						int damage = Utility.random((int) (player.getMaxLevels()[5] * 0.25)) + 1;
						
						killer.hit(new Hit(player, damage, HitTypes.NONE));
					}
				}

				stop();
			}

			@Override
			public void onStop() {
			}
		});
	}

	@Override
	public void execute() {

		if (!c.isSafe(player) && !player.inZulrah()) {
			PlayerDrops.dropItemsOnDeath(player);
		}

		
		if (player.isJailed()) {
			player.teleport(PlayerConstants.JAILED_AREA);
		} else if (player.getController() != null) {
			player.teleport(player.getController().getRespawnLocation(player));
		} else {
			player.teleport(PlayerConstants.HOME);
		}

		if (player.isPoisoned()) {
			player.curePoison(0);
		}

		if (player.getSkulling().isSkulled()) {
			player.getSkulling().unskull(player);
		}

		if (player.getMagic().isVengeanceActive()) {
			player.getMagic().setVengeanceActive(false);
		}

		player.getSpecialAttack().setSpecialAmount(100);
		player.getSpecialAttack().update();

		player.getPrayer().disable();

		player.getRunEnergy().setEnergy(100);

		player.getEquipment().onLogin();

		player.setFreeze(0);

		player.setTeleblockTime(0);

		if (player.inWilderness()) {
			player.setDeaths(player.getDeaths() + 1);
		}
		
		player.setHunterKills(0);
		player.setRogueKills(0);

		player.setAppearanceUpdateRequired(true);
		player.getClient().queueOutgoingPacket(new SendMessage("Oh dear, you have died!"));
		player.getUpdateFlags().sendAnimation(65535, 0);
		
		AchievementHandler.activateAchievement(player, AchievementList.DIE_1_TIME, 1);
		AchievementHandler.activateAchievement(player, AchievementList.DIE_10_TIME, 1);
		AchievementHandler.activateAchievement(player, AchievementList.DIE_50_TIME, 1);

		c.onDeath(player);
		
		if (player.getCombat().getDamageTracker().getKiller() != null) {
			c.onKill(player, player.getCombat().getDamageTracker().getKiller());
		}
		
		player.getCombat().forRespawn();
		
		stop();
	}

	@Override
	public void onStop() {
	}
}