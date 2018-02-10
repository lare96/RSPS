package com.vencillio.rs2.content.bank;

import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGame;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemContainer;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.*;

import java.util.Arrays;

public class Bank extends ItemContainer {

	public static enum RearrangeTypes {
		SWAP,
		INSERT;
	}

	public static enum WithdrawTypes {
		ITEM,
		NOTE;
	}

	public static final int SIZE = 350;
	private final Player player;
	private boolean searching = false;

	private int bankTab = 0;

	private int[] tabAmounts = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public RearrangeTypes rearrangeType = RearrangeTypes.SWAP;

	public WithdrawTypes withdrawType = WithdrawTypes.ITEM;

	public Bank(Player player) {
		super(SIZE, ItemContainer.ContainerTypes.ALWAYS_STACK, true, false);
		this.player = player;
	}

	@Override
	public boolean allowZero(int id) {
		return false;
	}

	public boolean clickButton(int buttonId) {
		if (!player.getInterfaceManager().hasBankOpen()) {
			return false;
		}
		if (buttonId >= 195093 && buttonId <= 195130) {
			player.getBank().setSearching(false);
			if (buttonId % 2 == 0) {
				player.getBank().setBankTab(9 - (195130 - buttonId) / 4);
			} else {
				player.getBank().collapse(9 - (195130 - (buttonId - 1)) / 4, 0);
			}
			return true;
		}
		switch (buttonId) {
		case 195084: // search
			setSearching(!isSearching());
			break;
		case 195087: // option
			setSearching(false);
			if (player.getInterfaceManager().main == 32500) {
				player.send(new SendInterface(5292));
			} else {
				player.send(new SendInterface(32500));
			}
			break;
		case 195085:
			Item[] k = player.getInventory().getItems();
			for (int i = 0; i < k.length; i++) {
				if (k[i] != null) {
					if (!hasSpaceFor(new Item(k[i])))
						break;
					deposit(k[i], i);
				}

			}
			if (k.length > 1) {
				player.getClient().queueOutgoingPacket(new SendMessage("You have deposited some of the items in your inventory."));
			}
			return true;
		case 195086:
			Item[] e = player.getEquipment().getItems();
			for (int i = 0; i < e.length; i++) {
				if (e[i] != null) {
					int deposited = depositFromNoting(e[i].getId(), e[i].getAmount(), bankTab, false);

					if (deposited != 0) {
						if (e[i].getAmount() == deposited)
							player.getEquipment().getItems()[i] = null;
						else if (e[i].getAmount() > deposited) {
							e[i].remove(deposited);
						}
					}
				}
			}
			update();
			if (e.length > 1) {
				player.getClient().queueOutgoingPacket(new SendMessage("You have deposited some of your worn-items."));
			}
			player.getEquipment().onLogin();
			player.setAppearanceUpdateRequired(true);
			return true;
		case 31194:
			rearrangeType = RearrangeTypes.SWAP;
			return true;
		case 31195:
			rearrangeType = RearrangeTypes.INSERT;
			return true;
		case 21011:
			withdrawType = WithdrawTypes.ITEM;
			return true;
		case 21010:
			withdrawType = WithdrawTypes.NOTE;
			return true;
		}
		return false;
	}

	public void deposit(int id, int amount, int slot) {
		if (!player.getInterfaceManager().hasBankOpen() || WeaponGame.gamePlayers.contains(player)) {
			return;
		}

		if (!player.getInventory().slotContainsItem(slot, id)) {
			return;
		}

		int invAmount = player.getInventory().getItemAmount(id);

		if (invAmount < amount) {
			amount = invAmount;
		}
		
		Item item = new Item(id, amount);
		
		if (item.getDefinition().isNote()) {
			item.unNote();
		}

		boolean contains = hasItemId(item.getId());
		
		int added = 0;
		
		added = add(item, true);

		if (added > 0) {
			if (!contains) {
				changeTabAmount(bankTab, 1, false);
				RearrangeTypes temp = rearrangeType;
				rearrangeType = RearrangeTypes.INSERT;
				swap(bankTab == 0 ? tabAmounts[bankTab] - 1 : getData(bankTab, 1), getItemSlot(item.getId()));
				rearrangeType = temp;
			}

			if (amount == 1 && !Item.getDefinition(id).isStackable()) {
				player.getInventory().setSlot(null, slot);
			} else {
				player.getInventory().remove(id, added);
			}
		} else {
			added = add(new Item(id, amount), true);

			if (added > 0) {
				if (!contains) {
					changeTabAmount(bankTab, 1, false);
					RearrangeTypes temp = rearrangeType;
					rearrangeType = RearrangeTypes.INSERT;
					swap(bankTab == 0 ? tabAmounts[bankTab] - 1 : getData(bankTab, 1), getItemSlot(item.getId()));
					rearrangeType = temp;
				}

				if (amount == 1 && !Item.getDefinition(id).isStackable()) {
					player.getInventory().setSlot(null, slot);
				} else {
					player.getInventory().remove(id, added);
				}
			}
		}
	}

	public void deposit(Item i, int slot) {
		deposit(i.getId(), i.getAmount(), slot);
	}
	
	public int depositFromNoting(Item item, int tab, boolean update) {
		boolean contains = getItemAmount(item.getId()) != 0;
		int added = add(new Item(item.getId(), item.getAmount()), update);
	
		if (!contains && added > 0) {
			changeTabAmount(tab, 1, false);
			RearrangeTypes temp = rearrangeType;
			rearrangeType = RearrangeTypes.INSERT;
			swap(tab == 0 ? tabAmounts[tab] - 1 : getData(tab, 1), getItemSlot(item.getId()));
			rearrangeType = temp;
		}
	
		return added;
	}

	public int depositFromNoting(int id, int amount, int tab, boolean update) {
		boolean contains = getItemAmount(id) != 0;
		int added = add(new Item(id, amount), update);

		if (!contains && added > 0) {
			changeTabAmount(tab, 1, false);
			RearrangeTypes temp = rearrangeType;
			rearrangeType = RearrangeTypes.INSERT;
			swap(tab == 0 ? tabAmounts[tab] - 1 : getData(tab, 1), getItemSlot(id));
			rearrangeType = temp;
		}

		return added;
	}

	@Override
	public void onAdd(Item item) {
	}

	@Override
	public void onFillContainer() {
		player.getClient().queueOutgoingPacket(new SendMessage("Your bank is now full."));
	}

	@Override
	public void onMaxStack() {
		player.getClient().queueOutgoingPacket(new SendMessage("Your bank won't be able to hold all that!"));
	}

	@Override
	public void onRemove(Item item) {
	}

	public void openBank() {

		if(!player.getUsername().equalsIgnoreCase("tanner")) {
			if (player.isUltimateIron()) {
				DialogueManager.sendStatement(player, "<img=13>@gry@Ultimate Ironman@bla@'s do not have access to a bank!");
				return;
			}

			if (player.inWGGame()) {
				return;
			}

			if (player.isBusy() && !PlayerConstants.isOwner(player)) {
				return;
			}
		}
		
		if (player.getPin() == null || player.enteredPin) {
			shift();
			update();
			player.getClient().queueOutgoingPacket(new SendInventoryInterface(5292, 5063));
		} else {
			player.send(new SendInterface(48750));
		}
		
	}

	@Override
	public void swap(int to, int from) {
		if (from == to) {
			return;
		}
		
		if (rearrangeType == RearrangeTypes.SWAP) {
			Item item = getItems()[to];
			getItems()[to] = getItems()[from];
			getItems()[from] = item;
		} else if (rearrangeType == RearrangeTypes.INSERT) {
			int index = from;

			if (from > to) {
				while (index != to) {
					Item item = getItems()[index - 1];
					getItems()[index - 1] = getItems()[index];
					getItems()[index] = item;
					index--;
				}
			} else if (from < to) {
				while (index != to) {
					Item item = getItems()[index + 1];
					getItems()[index + 1] = getItems()[index];
					getItems()[index] = item;
					index++;
				}
			}
		}
	}

	@Override
	public void update() {
		player.getClient().queueOutgoingPacket(new SendUpdateItems(5064, player.getInventory().getItems()));
		player.getClient().queueOutgoingPacket(new SendUpdateItems(5382, player.getBank().getItems(), player.getBank().getTabAmounts()));
		player.getClient().queueOutgoingPacket(new SendInventory(player.getInventory().getItems()));
		player.send(new SendString("" + player.getBank().getTakenSlots(), 22033));
	}

	@Override
	public void clear() {
		Arrays.fill(tabAmounts, 0);
		bankTab = 0;
		setSearching(false);
		super.clear();
	}

	public void withdraw(int item, int amount) {
		if (!player.getInterfaceManager().hasBankOpen()) {
			return;
		}

		if (!hasItemId(item)) {
			return;
		}

		int bankAmount = getItemAmount(item);
		int bankSlot = getItemSlot(item);

		if (bankAmount < amount) {
			amount = bankAmount;
		}
		int old = item;
		if (withdrawType == WithdrawTypes.NOTE) {
			if (!Item.getDefinition(item).canNote())
				player.getClient().queueOutgoingPacket(new SendMessage("This item cannot be withdrawn as a note."));
			else {
				item = Item.getDefinition(item).getNoteId();
			}
		}

		int added = player.getInventory().add(item, amount, false);

		if (added > 0) {
			if (remove(new Item(old, added), true) > 0) {
				if (added == bankAmount) {
					int tab = getData(bankSlot, 0);
					changeTabAmount(tab, -1, tab == 0 && tabAmounts[0] == 1);
				}
			}
		}
	}

	public void collapse(int tab, int toTab) {
		if (tab == 0) {
			Arrays.fill(tabAmounts, 0);
			tabAmounts[0] = getTakenSlots();
			shift();
			update();
			return;
		}

		if (toTab == 0) {
			player.send(new SendConfig(1000, 0));
			bankTab = 0;
		}

		if (toTab == tab || tab > 9) {
			shift();
			update();
			return;
		}

		int itemSlot = getData(tab, 1);
		int initialTabAmount = tabAmounts[tab];

		for (int fromSlot = 0; fromSlot < initialTabAmount; fromSlot++) {
			itemToTab(itemSlot, toTab, false);
		}

		collapse(tab + 1, tab);
	}

	public int getData(int input, int type) {
		int totalSlots = 0;
		for (int tab = 0; tab < (type == 1 ? input + 1 : 10); tab++) {
			if (type == 0 && input <= totalSlots + tabAmounts[tab] - 1 && input >= totalSlots) {
				return tab;
			}
			totalSlots += tabAmounts[tab];
		}
		return totalSlots - 1;
	}

	public void itemToTab(int slot, int toTab, boolean refresh) {
		int fromTab = getData(slot, 0);

		if (fromTab == toTab || (toTab > 1 && tabAmounts[toTab - 1] == 0 && tabAmounts[toTab] == 0)) {
			return;
		}

		changeTabAmount(fromTab, -1, refresh);
		changeTabAmount(toTab, 1, false);
		RearrangeTypes temp = rearrangeType;
		rearrangeType = RearrangeTypes.INSERT;
		swap(getData(toTab, 1), slot);
		rearrangeType = temp;

		if (refresh) {
			update();
		}
	}

	public void changeTabAmount(int tab, int amount, boolean collapse) {
		tabAmounts[tab] += amount;
		if (tabAmounts[tab] <= 0 && collapse) {
			collapse(tab, 0);
		}
	}

	public int getTabAmount(int tab) {
		return tabAmounts[tab];
	}

	public int[] getTabAmounts() {
		return tabAmounts;
	}

	public void setTabAmounts(int[] tabAmounts) {
		this.tabAmounts = tabAmounts;
	}

	public void setBankTab(int bankTab) {
		this.bankTab = bankTab;
	}

	public boolean isSearching() {
		return searching;
	}

	public void setSearching(boolean searching) {
		this.searching = searching;
		if (!isSearching()) {
			player.send(new SendConfig(1012, 0));
		} else {
			player.send(new SendEnterString());
		}
		player.send(new OutgoingPacket() {
			@Override
			public void execute(Client paramClient) {
			}

			@Override
			public int getOpcode() {
				return 187;
			}
		});
	}
}