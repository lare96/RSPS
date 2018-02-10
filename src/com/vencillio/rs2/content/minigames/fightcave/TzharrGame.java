package com.vencillio.rs2.content.minigames.fightcave;

import java.util.ArrayList;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.Task.BreakType;
import com.vencillio.core.task.Task.StackType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.minigames.fightcave.TzharrData.NPCS_DETAILS;
import com.vencillio.rs2.content.pets.BossPets;
import com.vencillio.rs2.content.pets.BossPets.PetData;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public final class TzharrGame {

	public static final TzharrController CONTROLLER = new TzharrController();

	public static final Location LEAVE = new Location(2438, 5168, 0);
	public static final String FIGHT_CAVE_NPCS_KEY = "fightcavesnpcs";
	public static final Location[] SPAWN_LOCATIONS = { new Location(2411, 5109), new Location(2413, 5105), new Location(2385, 5106), new Location(2380, 5102), new Location(2380, 5073), new Location(2387, 5071), new Location(2420, 5082), new Location(2416, 5107), new Location(2412, 5111), new Location(2382, 5108), new Location(2378, 5103) };

	public static final void checkForFightCave(Player p, Mob mob) {
		if (p.getController().equals(CONTROLLER)) {

			p.getJadDetails().removeNpc(mob);
			
			if (mob.getId() == NPCS_DETAILS.TZ_KEK) {

				short[] ids = new short[] { NPCS_DETAILS.TZ_KEK_SPAWN, NPCS_DETAILS.TZ_KEK_SPAWN};
				for (short i : ids) {
					Mob m = new Mob(p, i, false, false, false, mob.getLocation());
					m.getFollowing().setIgnoreDistance(true);
					m.getCombat().setAttack(p);
					p.getJadDetails().addNpc(m);
				}
			}
			
			if (p.getJadDetails().getKillAmount() == 0) {
				if (p.getJadDetails().getStage() == 14) {
					finish(p, true);
					return;
				}
				p.getJadDetails().increaseStage();
				startNextWave(p);
			}
		}
	}

	public static void finish(Player player, boolean reward) {
		if (reward) {
			player.getInventory().addOrCreateGroundItem(6570, 1, true);
			player.getInventory().addOrCreateGroundItem(6529, 16064, true);
			player.getClient().queueOutgoingPacket(new SendMessage("@dre@Congratulations, you have completed The Fight Caves"));
			DialogueManager.sendStatement(player, "Congratulations, you have completed The Fight Caves");
			World.sendGlobalMessage("<img=8> <col=C42BAD>"+player.getUsername()+" has just completed the Fight Caves minigame!");
			AchievementHandler.activateAchievement(player, AchievementList.OBTAIN_10_FIRECAPES, 1);
			AchievementHandler.activateAchievement(player, AchievementList.OBTAIN_50_FIRECAPES, 1);
			if (Utility.random(150) == 0) {
				//handlePet(player);
			}
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("@dre@You did not make it far enough to receive a reward."));
		}

		player.teleport(LEAVE);
		onLeaveGame(player);
		player.getJadDetails().reset();
	}
	
	public static void handlePet(Player player) {
		PetData petDrop = PetData.forItem(4000);
	
		if (petDrop != null) {
			if (player.getBossPet() == null) {
				BossPets.spawnPet(player, petDrop.getItem(), true);
				player.send(new SendMessage("You feel a pressence following you; " + Utility.formatPlayerName(GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName()) + " starts to follow you."));
			} else {
				player.getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
				player.send(new SendMessage("You feel a pressence added to your bank."));
			}
		} else {
			GroundItemHandler.add(new Item(4000, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
		}
	}

	public static void init(Player p, boolean kiln) {
		p.send(new SendRemoveInterfaces());
		p.setController(CONTROLLER);
		
		if (p.getJadDetails().getZ() == 0) {
			p.getJadDetails().setZ(p);
		}

		p.teleport(new Location(2413, 5117, p.getJadDetails().getZ()));
		startNextWave(p);

	}

	public static void loadGame(Player player) {
		player.setController(CONTROLLER);

		if (player.getJadDetails().getStage() != 0)
			startNextWave(player);
	}

	public static final void onLeaveGame(Player player) {
		for (Mob i : player.getJadDetails().getMobs()) {
			if (i != null) {
				i.remove();
			}
		}

		player.getJadDetails().getMobs().clear();

		player.setController(ControllerManager.DEFAULT_CONTROLLER);
	}

	public static void startNextWave(final Player p) {
		p.getClient().queueOutgoingPacket(new SendMessage("@dre@The next wave will start in a few seconds."));
		if (p.getJadDetails().getZ() == 0) {
			p.getJadDetails().setZ(p);
			p.changeZ(p.getJadDetails().getZ());
		}
		TaskQueue.queue(new Task(p, 20, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.TZHAAR) {
			@Override
			public void execute() {
				final ArrayList<Location> randomizedSpawns = new ArrayList<Location>();

				for (Location i : SPAWN_LOCATIONS) {
					randomizedSpawns.add(i);
				}

				int c;
				for (short i : TzharrData.values()[p.getJadDetails().getStage()].getNpcs()) {
					c = Utility.randomNumber(randomizedSpawns.size());
					Location l = new Location(randomizedSpawns.get(c));
					randomizedSpawns.remove(c);
					l.setZ(p.getJadDetails().getZ());
					Mob mob = new Mob(p, i, false, false, false, l);
					mob.getFollowing().setIgnoreDistance(true);
					mob.getCombat().setAttack(p);
					p.getJadDetails().addNpc(mob);
				}
				p.getClient().queueOutgoingPacket(new SendMessage("@dre@Wave: " + (p.getJadDetails().getStage() + 1)));
				stop();
			}

			@Override
			public void onStop() {
			}
		});
	}
}
