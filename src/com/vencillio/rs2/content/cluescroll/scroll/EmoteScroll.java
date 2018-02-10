package com.vencillio.rs2.content.cluescroll.scroll;

import java.util.Arrays;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.Task.BreakType;
import com.vencillio.core.task.Task.StackType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.GameConstants;
import com.vencillio.rs2.content.cluescroll.Clue;
import com.vencillio.rs2.content.cluescroll.Clue.ClueType;
import com.vencillio.rs2.content.cluescroll.ClueDifficulty;
import com.vencillio.rs2.content.cluescroll.ClueScroll;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class EmoteScroll implements ClueScroll {
	private final int scrollId;
	private final ClueDifficulty difficulty;
	private final Location endLocation;
	private final int endLocationDiameter;
	private final Object[] data;
	private final Item[] requiredItems;
	
	public EmoteScroll(int scrollId, ClueDifficulty difficulty, Item[] requiredItems, Location endLocation, int endLocationDiameter, Object... data) {
		this.scrollId = scrollId;
		this.difficulty = difficulty;
		this.requiredItems = requiredItems;
		this.endLocation = endLocation;
		this.endLocationDiameter = endLocationDiameter;
		this.data = data;
	}

	@Override
	public boolean inEndArea(Location location) {
		return Utility.getExactDistance(endLocation, location) <= endLocationDiameter;
	}

	@Override
	public Clue getClue() {
		return new Clue(ClueType.EMOTE, data);
	}

	@Override
	public ClueDifficulty getDifficulty() {
		return difficulty;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return inEndArea(player.getLocation());
	}

	@Override
	public boolean execute(Player player) {
		if (!player.getInventory().hasItemId(scrollId)) {
			return false;
		}

		if (player.getAttributes().is("KILL_AGENT")) {
			return false;
		}

		if (Arrays.equals(requiredItems, player.getEquipment().getItems())) {
			if (getDifficulty().equals(ClueDifficulty.HARD)) {
				player.getAttributes().set("KILL_AGENT", Boolean.TRUE);
				Location spawn = GameConstants.getClearAdjacentLocation(player.getLocation(), 1);
				Mob doubleAgent = new Mob(player, 1778, false, false, true, spawn);
				doubleAgent.getCombat().setAttacking(player);
				doubleAgent.getUpdateFlags().sendGraphic(new Graphic(86));
			} else {
				onAgentDeath(player);
			}
		} else {
			player.send(new SendMessage("You must only wear the required items."));
		}
	
		return true;
	}

	public void onAgentDeath(Player player) {
		TaskQueue.queue(new Task(player, 1, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.TREASURE_TRAILS) {
			int ticks = 0;
			Location spawn;
			Mob uri = null;
			
			@Override
			public void execute() {
				switch (ticks++) {
				case 1:
					spawn = GameConstants.getClearAdjacentLocation(player.getLocation(), 1);
					World.sendStillGraphic(86, 0, spawn);
					break;
					
				case 2:
					uri = new Mob(player, 1776, false, false, false, spawn);
					uri.getUpdateFlags().faceEntity(player.getIndex());
					uri.getUpdateFlags().sendAnimation(new Animation(863));
					reward(player, "Uri gives you");
					break;
					
				case 5:
					if (uri != null && uri.isActive()) {
						World.sendStillGraphic(287, 0, spawn);
						uri.remove();
					}
					stop();
					break;
				}
			}

			@Override
			public void onStop() {
				player.getAttributes().set("KILL_AGENT", Boolean.FALSE);
			}

		});
	}

	@Override
	public int getScrollId() {
		return scrollId;
	}

	public int getAnimationId() {
		return Integer.parseInt(String.valueOf(data[0]));
	}
}