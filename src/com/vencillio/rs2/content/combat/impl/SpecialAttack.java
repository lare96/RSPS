package com.vencillio.rs2.content.combat.impl;

import com.vencillio.core.definitions.SpecialAttackDefinition;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.content.combat.special.SpecialAttackHandler;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendSpecialBar;

public class SpecialAttack {

	private final Player player;
	private boolean initialized = false;
	private int barId1 = 0;
	private int barId2 = 0;
	private int amount = 100;
	public static final int FULL_SPECIAL = 100;
	public static final int SPECIAL_TIMER_MAX = 50;

	public SpecialAttack(Player player) {
		this.player = player;
	}

	public void afterSpecial() {
		toggleSpecial();
		SpecialAttackHandler.updateSpecialAmount(player, barId2, amount);
		player.updateCombatType();
	}

	public boolean clickSpecialButton(int buttonId) {
		toggleSpecial();
		return true;
	}

	public void deduct(int amount) {
		this.amount -= amount;
	}

	public void add(int amount) { this.amount += amount; }

	public boolean doInstantGraniteMaulSpecial() {
		return (player.getCombat().getAttacking() != null) && (player.getCombat().withinDistanceForAttack(player.getCombat().getCombatType(), false)) && (player.canAttack());
	}

	public int getAmount() {
		return amount;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void onEquip() {
		Item weapon = player.getEquipment().getItems()[3];
		updateSpecialBar(weapon == null ? 0 : weapon.getId());
		if (initialized)
			toggleSpecial();
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public void setSpecialAmount(int amount) {
		this.amount = amount;
	}

	public void tick() {
		TaskQueue.queue(new Task(player, 50, false, Task.StackType.NEVER_STACK, Task.BreakType.NEVER, TaskIdentifier.SPECIAL_RESTORE) {
			@Override
			public void execute() {
				if (amount < 100) {
					amount += (10*(player.mode+1));
					if (amount > 100) {
						amount = 100;
					}
					if (barId1 > 0) {
						SpecialAttackHandler.updateSpecialBarText(player, barId2, amount, initialized);
						SpecialAttackHandler.updateSpecialAmount(player, barId2, amount);
					}
				}
			}

			@Override
			public void onStop() {
			}
		});
	}

	public void toggleSpecial() {
		Item weapon = player.getEquipment().getItems()[3];
		if (weapon == null) {
			initialized = false;
			return;
		}

		if ((weapon.getId() == 4153) && (!initialized) && (doInstantGraniteMaulSpecial())) {
			initialized = true;
			player.getCombat().attack();
		} else {
			initialized = (!initialized);

			if (weapon.getId() == 15241) {
				int a = player.getCombat().getAttackTimer();
				if (a > 0) {
					if (initialized)
						player.getCombat().setAttackTimer(a + 2);
					else {
						player.getCombat().setAttackTimer(a > 2 ? a - 2 : 1);
					}
				}
			}

			if (barId2 > -1)
				SpecialAttackHandler.updateSpecialBarText(player, barId2, amount, initialized);
		}
	}

	public void update() {
		SpecialAttackHandler.updateSpecialBarText(player, barId2, amount, initialized);
		SpecialAttackHandler.updateSpecialAmount(player, barId2, amount);
	}

	public void updateSpecialBar(int weaponId) {
		SpecialAttackDefinition def = Item.getSpecialDefinition(weaponId);

		player.getClient().queueOutgoingPacket(new SendConfig(78, 0));

		if (def != null) {
			barId1 = def.getBarId1();
			barId2 = def.getBarId2();
			def.getButton();

			if (weaponId == 15486)
				player.getClient().queueOutgoingPacket(new SendConfig(78, 1));
			else {
				player.getClient().queueOutgoingPacket(new SendSpecialBar(0, barId1));
			}

			update();
		} else {
			barId1 = 0;
			barId2 = 0;
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7549));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7561));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7574));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 12323));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7599));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7674));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7474));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7499));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 8493));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7574));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7624));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7699));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7800));
			player.getClient().queueOutgoingPacket(new SendSpecialBar(-1, 0));
		}
	}
}
