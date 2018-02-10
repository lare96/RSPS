package com.vencillio.rs2.content.skill.summoning;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemContainer;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInventoryInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;

public class BOBContainer extends ItemContainer {
	private final Player player;

	public BOBContainer(Player player, int size) {
		super(size, ItemContainer.ContainerTypes.STACK, true, true);
		this.player = player;
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
		player.getClient().queueOutgoingPacket(new SendMessage("Your familiar's inventory is full."));
	}

	@Override
	public void onMaxStack() {
	}

	@Override
	public void onRemove(Item item) {
	}

	public void open() {
		player.getClient().queueOutgoingPacket(new SendInventoryInterface(2700, 5063));
		update();
	}

	public void store(int id, int amount, int slot) {
		if (!player.getInterfaceManager().hasBOBInventoryOpen()) {
			return;
		}

		if (!player.getInventory().slotContainsItem(slot, id)) {
			return;
		}

		if (GameDefinitionLoader.getHighAlchemyValue(id) > 50000) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot store an item this valuable in your familiar's inventory."));
			return;
		}
		int value = 0;

		for (Item i : getItems()) {
			if (i != null) {
				value += GameDefinitionLoader.getHighAlchemyValue(i.getId()) * i.getAmount();
			}
		}

		if (value + GameDefinitionLoader.getHighAlchemyValue(id) > 175000) {
			player.getClient().queueOutgoingPacket(new SendMessage("Your familiars maximum carried value limit has been reached!"));
			return;
		}

		if ((!Item.getDefinition(id).isTradable()) || (id == 7936)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot store this item in your familiar's inventory."));
			return;
		}

		int invAmount = player.getInventory().getItemAmount(id);

		if (invAmount < amount) {
			amount = invAmount;
		}

		int added = add(new Item(id, amount), true);

		if (added > 0)
			if (added == 1)
				player.getInventory().removeFromSlot(slot, id, added);
			else
				player.getInventory().remove(id, added, false);
	}

	@Override
	public void update() {
		player.getInventory().update();
		player.getClient().queueOutgoingPacket(new SendUpdateItems(5064, player.getInventory().getItems()));
		player.getClient().queueOutgoingPacket(new SendUpdateItems(2702, getItems()));
	}

	public void withdraw(int slot, int amount) {
		if (!player.getInterfaceManager().hasBOBInventoryOpen()) {
			return;
		}

		if (!slotHasItem(slot)) {
			return;
		}

		int id = getSlotId(slot);
		int bankAmount = getItemAmount(id);

		if (bankAmount < amount) {
			amount = bankAmount;
		}

		int added = player.getInventory().add(id, amount, false);

		if (added > 0)
			remove(new Item(id, added), true);
	}
}
