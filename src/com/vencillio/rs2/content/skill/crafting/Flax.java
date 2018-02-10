package com.vencillio.rs2.content.skill.crafting;

import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles flax picking
 * @author Daniel
 *
 */
public class Flax {
	
	/**
	 * Picks flax
	 * @param player
	 * @param x
	 * @param y
	 */
	public static void pickFlax(final Player player, final int x, final int y) {
		//final WorldObject flax = new WorldObject(2646, new Position(x, y), Direction.NORTH);
		//final WorldObject flaxPicked = new WorldObject(-1, new Position(x, y), Direction.NORTH);
		if (player.getInventory().getFreeSlots() < 1) {
			player.send(new SendMessage("Not enough space in your inventory."));
			return;
		}
		player.getInventory().addItems(new Item(1779, 1));
		player.getUpdateFlags().sendAnimation((new Animation(827)));
		player.send(new SendMessage("You pick some flax."));
    	/*if (Utility.random(3) == 1) {
    		WorldObjectManager.register(flaxPicked);
			TaskManager.submit(new FlaxEvent(flax, flaxPicked, 15));
    	}*/		
	}
	
	
}
