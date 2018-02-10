package com.vencillio.core.task.impl;

import com.vencillio.core.cache.map.MapLoading;
import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

public class HarvestTask extends Task {

	private final Player player;
	private final String message;
	private final int object;
	private final int item;
	private final int x;
	private final int y;
	private final int z;

	public static final String FLAX = "You pick some flax.";

	public HarvestTask(Player player, int object, int item, int x, int y, int z) {
		super(player, 2, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.object = object;
		this.item = item;
		this.x = x;
		this.y = y;
		this.z = player.getLocation().getZ();

		if (player.getInventory().getFreeSlots() == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to harvest this."));
			stop();
		} else {
			player.getUpdateFlags().sendAnimation(827, 0);
		}

		if (item == 1779) {
			message = FLAX;
		} else {
			String name = Item.getDefinition(item).getName();
			message = "You pick " + Utility.getAOrAn(name) + " " + name + ".";
		}
	}

	@Override
	public void execute() {
		player.getClient().queueOutgoingPacket(new SendSound(358, 0, 0));
		player.getClient().queueOutgoingPacket(new SendMessage(message));
		player.getInventory().add(item, 1);
		stop();
		if (Utility.randomNumber(8) == 0) {
			final RSObject o = Region.getObject(x, y, z);
			final GameObject go = new GameObject(ObjectManager.BLANK_OBJECT_ID, x, y, z, o.getType(), 0);

			ObjectManager.register(go);
			MapLoading.removeObject(object, x, y, z, o.getType(), o.getFace());

			TaskQueue.queue(new Task(100) {
				@Override
				public void execute() {
					ObjectManager.remove(go);
					ObjectManager.register(new GameObject(object, x, y, z, o.getType(), o.getFace()));// /TODO:
																										// remove
																										// this
																										// on
																										// client
																										// sided
																										// object
																										// spawning
					MapLoading.addObject(false, object, x, y, z, o.getType(), o.getFace());
					stop();
				}

				@Override
				public void onStop() {
				}

			});
		}
	}

	@Override
	public void onStop() {
	}

}
