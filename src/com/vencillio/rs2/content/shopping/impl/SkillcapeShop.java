package com.vencillio.rs2.content.shopping.impl;

import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.content.shopping.ShopConstants;
import com.vencillio.rs2.content.shopping.Shopping.ShopType;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class SkillcapeShop extends Shop {

	public static final int SHOP_ID = 20;

	static enum Skillcape {
		ATTACK(new int[] { 9747, 9748, 9749 }),
		DEFENCE(new int[] { 9753, 9754, 9755 }),
		STRENGTH(new int[] { 9750, 9751, 9752 }),
		CONSTITUTION(new int[] { 9768, 9769, 9770 }),
		RANGED(new int[] { 9756, 9757, 9758 }),
		PRAYER(new int[] { 9759, 9760, 9761 }),
		MAGIC(new int[] { 9762, 9763, 9764 }),
		COOKING(new int[] { 9801, 9802, 9803 }),
		WOODCUTTING(new int[] { 9807, 9808, 9809 }),
		FLETCHING(new int[] { 9783, 9784, 9785 }),
		FISHING(new int[] { 9798, 9799, 9800 }),
		FIREMAKING(new int[] { 9804, 9805, 9806 }),
		CRAFTING(new int[] { 9780, 9781, 9782 }),
		SMITHING(new int[] { 9795, 9796, 9797 }),
		MINING(new int[] { 9792, 9793, 9794 }),
		HERBLORE(new int[] { 9774, 9775, 9776 }),
		AGILITY(new int[] { 9771, 9772, 9773 }),
		THIEVING(new int[] { 9777, 9778, 9779 }),
		SLAYER(new int[] { 9786, 9787, 9788 }),
		FARMING(new int[] { 9810, 9811, 9812 }),
		RUNECRAFTING(new int[] { 9765, 9766, 9767 }),
		CONSTRUCTION(new int[] { 9789, 9790, 9791 }),
		HUNTER(new int[] { 9948, 9949, 9950 });

		private int[] items;

		private Skillcape(int[] items) {
			this.items = items;
		}

		public Item getCape() {
			return new Item(items[0]);
		}

		public Item getTrimmedCape() {
			return new Item(items[1]);
		}

		public static Item forCape(int cape) {
			for (Skillcape sc : values()) {
				if (sc.getCape().getId() == cape || sc.getTrimmedCape().getId() == cape) {
					return new Item(sc.items[2]);
				}
			}
			return null;
		}
	}

	public SkillcapeShop() {
		super(new Item[Skills.SKILL_COUNT], "Skillcape Shop");
		type = ShopType.INSTANCE;
	}

	@Override
	public void buy(Player player, int slot, int id, int amount) {
		if (amount > 500) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You can only buy 500 maximum at a time from these shops."));
			amount = 500;
		}

		if (!hasItem(slot, id))
			return;

		if (get(slot).getAmount() == 0) {
			player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] The shop is out of stock on that item."));
			return;
		}

		Item buying = new Item(id, amount);

		Item gold = new Item(995, getSellPrice(id) * amount);

		if (!(player.getInventory().getFreeSlots() > amount * 2)) {
			if (!buying.getDefinition().isStackable()) {
				int slots = player.getInventory().getFreeSlots() / 2;
				if (slots > 0) {
					buying.setAmount(slots);
					amount = slots;
					gold.setAmount(getSellPrice(id) * amount);
				} else {
					player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough inventory space to buy this item."));
					return;
				}
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough inventory space to buy this item."));
				return;
			}
		}

		if (gold.getAmount() > 0) {
			if (!player.getInventory().hasItemAmount(gold)) {
				player.getClient().queueOutgoingPacket(new SendMessage("[" + ShopConstants.COLOUR + "*</col>] You do not have enough coins to buy that."));
				return;
			}
		}

		if (gold.getAmount() > 0) {
			player.getInventory().remove(gold, false);
		}

		if (player.getSkill().hasTwo99s()) {
			player.getInventory().add(buying.getId() + 1, amount);
			player.getInventory().add(buying.getId(), amount);
		} else {
			player.getInventory().add(buying);
			player.getInventory().add(buying.getId() + 2, amount);
		}
	}

	@Override
	public void onOpen(Player player) {
		clear();

		boolean trimmed = player.getSkill().hasTwo99s();

		for (int index = 0; index < Skills.SKILL_COUNT; index++) {

			if (index == Skills.SUMMONING || index == Skills.DUNGEONEERING) {
				continue;
			}

			int level = player.getMaxLevels()[index];

			if (level >= 99) {
				if (index == Skills.HUNTER) {
					add(new Item(trimmed ? Skillcape.HUNTER.getTrimmedCape() : Skillcape.HUNTER.getCape()), false);
				} else if (index == Skills.CONSTRUCTION) {
					add(new Item(trimmed ? Skillcape.CONSTRUCTION.getTrimmedCape() : Skillcape.CONSTRUCTION.getCape()), false);
				} else {
					add(new Item(trimmed ? Skillcape.values()[index].getTrimmedCape() : Skillcape.values()[index].getCape()), false);
				}
			}
		}
	}

	@Override
	public boolean sell(Player player, int id, int amount) {
		player.send(new SendMessage("You cannot sell items to this store."));
		return false;
	}

	@Override
	public int getSellPrice(int id) {
		return 99_000;
	}
}