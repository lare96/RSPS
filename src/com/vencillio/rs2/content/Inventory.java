package com.vencillio.rs2.content;

import java.util.Arrays;
import java.util.Objects;

import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemContainer;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendInventory;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Inventory extends ItemContainer {
	
	private final Player player;

	public Inventory(Player player) {
		super(28, ItemContainer.ContainerTypes.STACK, false, true);
		this.player = player;
	}

	public void addOnLogin(Item item, int slot) {
		if (item == null) {
			return;
		}

		getItems()[slot] = item;
		onAdd(item);
	}
	
	public void addOrCreateGroundItem(Item item) {
		if (player.getInventory().hasSpaceFor(item)) {
			player.getInventory().insert(item);
		} else if ((item.getAmount() > 1) && (!Item.getDefinition(item.getId()).isStackable())) {
			for (int i = 0; i < item.getAmount(); i++)
				GroundItemHandler.add(item, player.getLocation(), player, player.ironPlayer() ? player : null);
		} else {
			GroundItemHandler.add(item, player.getLocation(), player, player.ironPlayer() ? player : null);
		}
		update();
	}

	public void addOrCreateGroundItem(int id, int amount, boolean update) {
		if (player.getInventory().hasSpaceFor(new Item(id, amount))) {
			player.getInventory().insert(id, amount);
		} else if ((amount > 1) && (!Item.getDefinition(id).isStackable())) {
			for (int i = 0; i < amount; i++)
				GroundItemHandler.add(new Item(id, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
		} else {
			GroundItemHandler.add(new Item(id, amount), player.getLocation(), player, player.ironPlayer() ? player : null);
		}

		if (update)
			update();
	}

	@Override
	public boolean allowZero(int id) {
		return false;
	}

	@Override
	public void onAdd(Item item) {
	}

	@Override
	public void onFillContainer() {
		player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to carry that."));
	}

	@Override
	public void onMaxStack() {
		player.getClient().queueOutgoingPacket(new SendMessage("You won't be able to carry all that!"));
	}

	@Override
	public void onRemove(Item item) {
	}

	@Override
	public void setItems(Item[] items) {
		super.setItems(items);
		update();
	}

	@Override
	public void update() {
		for (int i = 0; i < getItems().length; i++) {
			if ((getItems()[i] != null) && (getItems()[i].getAmount() >= 1000000000) && (!PlayerConstants.isOwner(player))) {
				player.getClient().setLogPlayer(true);
				break;
			}
		}

		player.getSummoning().onUpdateInventory();

		player.getClient().queueOutgoingPacket(new SendInventory(getItems()));
	}

	/**
	 * Performs a check on all items in this container to see if they match the
	 * argued Object. Rather than using <code>equals</code> to check if the
	 * elements match, it checks whether the Object is an item and if so
	 * compares the ID and quantity. This method will throw a
	 * {@link NullPointerException} if the argued Object has a value of
	 * <code>null</code>.
	 */
	public boolean contains(Object o) {
		if (!(o instanceof Item))
			return false;
		Item item = (Item) o;
		return Arrays.stream(items).filter(Objects::nonNull).anyMatch(i -> i.getId() == item.getId() && totalAmount(item.getId()) >= item.getAmount());
	}
	
	/**
	 * Gets the total amount of items with the argued item ID.
	 * 
	 * @param itemId
	 *            the item ID to get the total amount of.
	 * @return the total amount of items with the argued item ID.
	 */
	public int totalAmount(int itemId) {
		return Arrays.stream(items).filter(item -> item != null && item.getId() == itemId).mapToInt(item -> item.getAmount()).sum();
	}
}
