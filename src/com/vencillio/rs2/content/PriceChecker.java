package com.vencillio.rs2.content;

import java.text.NumberFormat;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGame;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemContainer;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInventoryInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

/**
 * Handles price checker
 * 
 * @author Daniel
 *
 */
public class PriceChecker extends ItemContainer {

	private final Player player;

	public PriceChecker(Player player, int size) {
		super(size, ItemContainer.ContainerTypes.STACK, true, true);
		this.player = player;
	}

	@Override
	public boolean allowZero(int paramInt) {
		return false;
	}

	@Override
	public void onAdd(Item paramItem) {
	}

	@Override
	public void onFillContainer() {
	}

	@Override
	public void onMaxStack() {
	}

	@Override
	public void onRemove(Item paramItem) {
	}

	@Override
	public void update() {
		player.getClient().queueOutgoingPacket(new SendUpdateItems(48542, getItems()));
		player.getClient().queueOutgoingPacket(new SendUpdateItems(5064, player.getInventory().getItems()));
		player.send(new SendString(""+ NumberFormat.getInstance().format(getContainerNet()), 48513));
	}
	
	private int[][] ITEM_STRINGS = {
		{ 0, 48550 }, { 1, 48551 },	{ 2, 48552 }, { 3, 48553 }, { 4, 48554 }, { 5, 48555 }, { 6, 48556 }, { 7, 48557 },
		{ 8, 48558 }, { 9, 48559 }, { 10, 48560 }, { 11, 48561 }, { 12, 48562 }, { 13, 48563 }, { 14, 48564 }, 
		{ 15, 48565 }, { 16, 48566 }, { 17, 48567 }, { 18, 48568 }, { 19, 48569 }, { 20, 48570 }, { 21, 48571 }, 
		{ 22, 48572 }, { 23, 48573 }, { 24, 48574 }, { 25, 48575 }, { 26, 48576 }, { 27, 48577 },
	};

	public void open() {
		if (WeaponGame.gamePlayers.contains(player) || player.getCombat().inCombat() || player.isDead() || player.getMagic().isTeleporting()) {
			return;
		}
		update();
		for (int i = 0; i < 27; i ++) {
			player.send(new SendString("", ITEM_STRINGS[i][1]));
			player.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(48542, 0, 0, i));
		}
		player.getClient().queueOutgoingPacket(new SendInventoryInterface(48500, 5063));
	}
	
	public void store(Item item) {
		if (item != null) {
			store(item.getId(), item.getAmount());
		}
	}
	
	public void store(int id, int amount) {
	
		if (WeaponGame.gamePlayers.contains(player)) {
			return;
		}
		
		// Check if item is untradeable 
		if (!Item.getDefinition(id).isTradable() || Item.getDefinition(id).getName().contains("Clue scroll")) {
			player.getClient().queueOutgoingPacket(new SendMessage("This item is untradeable!"));
			return;
		}

		if (player.getInventory().getItemAmount(id) < amount) {
			amount = player.getInventory().getItemAmount(id);
		}
		
		int slot = getItemSlot(id);
		Item item = new Item(id, amount);
		
		if (slot == -1 || !item.getDefinition().isStackable()) {
			for (int i = 0; i < getSize(); i++) {
				if (getItems()[i] == null) {
					slot = i;
					break;
				}
			}
		}
		
		add(item, true);
		
		// Add the item prices
		player.send(new SendString(""+ NumberFormat.getInstance().format(GameDefinitionLoader.getStoreSellToValue(id)) + "x" + NumberFormat.getInstance().format(amount), ITEM_STRINGS[slot][1]));
		
		// Remove the item from inventory
		player.getInventory().remove(new Item(id, amount), false);
		
		// Update
		update();
	}

	public void withdraw(int itemId, int slot, int amount) {
		Item item = get(slot);
		
		// validates item
		if (item == null || item.getId() != itemId) {
			return;
		}
		
		amount = removeFromSlot(slot, itemId, amount);
		
		// Remove item from interface
		if (amount <= 0) {
			return;
		}

		// Add item back to inventory
		player.getInventory().add(new Item(itemId, amount));
		
		shift();

		for (int i = 0; i < getSize(); i++) {
			if (getItems()[i] != null) {
				player.send(new SendString(""+ NumberFormat.getInstance().format(GameDefinitionLoader.getStoreSellToValue(getItems()[i].getId())), ITEM_STRINGS[i][1]));
			} else {
				player.send(new SendString("", ITEM_STRINGS[i][1]));
			}
		}
		
		// Update
		update();
	}
	
	public void depositeAll() {
		for (Item item : player.getInventory().getItems()) {
			store(item);
		}
	}
	
	public void withdrawAll() {
		for (int i = 0; i < getSize(); i++) {
			Item item = get(i);
			
			if (item == null) {
				continue;
			}

			// Add item back to inventory
			player.getInventory().add(item);
			player.send(new SendString("", ITEM_STRINGS[i][1]));
		}

		clear();
		update();
	}
}