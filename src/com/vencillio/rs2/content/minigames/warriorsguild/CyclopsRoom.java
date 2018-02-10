package com.vencillio.rs2.content.minigames.warriorsguild;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * Handles the Cyclops room
 * 
 * @author Daniel
 *
 */
public final class CyclopsRoom {

	/**
	 * Array of Defenders
	 */
	public static final int[] DEFENDERS = { 8844, 8845, 8846, 8847, 8848, 8849, 8850, 12954 };

	/**
	 * Handles entering the door
	 * 
	 * @param player
	 * @return
	 */
	public static boolean handleDoor(Player player, int x, int y) {
		if (player.getX() == 2847) {
			if (player.getAttributes().get("warrguildtokentask") != null) {
				((Task) player.getAttributes().get("warrguildtokentask")).stop();
			}
			player.getAttributes().remove("cyclopsdefenderdrop");
			player.getAttributes().remove("warrguildtokentask");
			player.teleport(new Location(player.getX() - 1, player.getY(), player.getZ()));
			player.getUpdateFlags().sendFaceToDirection(x, y);
			return true;
		} else if (player.getX() == 2846) {
			if (player.getInventory().getItemAmount(8851) < 100) {
				DialogueManager.sendStatement(player, "100 tokens are required to enter! You have " + player.getInventory().getItemAmount(8851) + ".");
				return true;
			}
			player.getInventory().remove(8851, 100);
			player.getClient().queueOutgoingPacket(new SendMessage("You pay 100 tokens to enter the cyclops arena."));
			executeTimer(player);
			int defender = DEFENDERS[getDefenderIndex(player)];
			player.getAttributes().set("cyclopsdefenderdrop", Integer.valueOf(defender));
			player.getClient().queueOutgoingPacket(new SendMessage("@dre@The cyclops are now dropping: " + Item.getDefinition(defender).getName() + "."));
			player.getAttributes().set("warrguildtokensused", 0);
			player.teleport(new Location(player.getX() + 1, player.getY(), player.getZ()));
			player.getUpdateFlags().sendFaceToDirection(x, y);
			updateInterface(player);
			return true;
		}
		return false;
	}

	/**
	 * Drops the defender
	 * 
	 * @param player
	 * @param mob
	 */
	public static void dropDefender(Player player, Mob mob) {
		if (Utility.randomNumber(10) != 0) {
			return;
		}

		if (player.getAttributes().get("cyclopsdefenderdrop") == null) {
			return;
		}

		int defender = player.getAttributes().getInt("cyclopsdefenderdrop");

		GroundItemHandler.add(new Item(defender), mob.getLocation(), player, player.ironPlayer() ? player : null);
		player.getAttributes().set("cyclopsdefenderdrop", Integer.valueOf(DEFENDERS[getDefenderIndex(player)]));
		player.getClient().queueOutgoingPacket(new SendMessage("@dre@The cyclops are now dropping: " + Item.getDefinition(DEFENDERS[getDefenderIndex(player)]).getName() + "."));
		updateInterface(player);
	}

	/**
	 * Updates the itnerface
	 * 
	 * @param player
	 */
	public static void updateInterface(Player player) {
		int defender = DEFENDERS[getDefenderIndex(player)];
		player.send(new SendUpdateItems(51203, new Item[] { new Item(defender) }));
		player.send(new SendString("</col>Tokens Used: @red@ " + player.getAttributes().getInt("warrguildtokensused"), 51205));
		player.send(new SendString("</col>Cyclops Killed: @red@ " + (player.getAttributes().getInt("CYCLOPS_KILLED") == -1 ? 0 : player.getAttributes().getInt("CYCLOPS_KILLED")), 51206));

	}

	/**
	 * Executes the timer
	 * 
	 * @param player
	 */
	public static void executeTimer(Player player) {
		Task task = new TokenTask(player, 100);
		player.getAttributes().set("warrguildtokentask", task);
		TaskQueue.queue(task);
	}

	public static int getDefenderIndex(Player player) {
		int currentDefender = -1;

		Item shield = player.getEquipment().getItems()[EquipmentConstants.SHIELD_SLOT];

		for (int i = 0; i < DEFENDERS.length; i++) {
			if (player.getBank().hasItemId(DEFENDERS[i]) || player.getInventory().hasItemId(DEFENDERS[i]) || (shield != null && shield.getId() == DEFENDERS[i])) {
				currentDefender = i;
			}
		}

		if ((currentDefender + 1 >= 0) && (currentDefender + 1 < DEFENDERS.length)) {
			currentDefender++;
		}

		return currentDefender;
	}
}
