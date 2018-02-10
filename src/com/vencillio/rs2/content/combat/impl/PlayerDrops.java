package com.vencillio.rs2.content.combat.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import com.vencillio.VencillioConstants;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.core.util.logger.PlayerLogger;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.pets.BossPets;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.content.wilderness.BountyEmblems;
import com.vencillio.rs2.content.wilderness.PlayerKilling;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendKillFeed;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class PlayerDrops {

	public static final ItemComparator ITEM_VALUE_COMPARATOR = new ItemComparator();

    public static final String[] DEATH_MESSAGES = {
    	"You have defeated -victim-.",
		"With a crushing blow, you defeat -victim-.",
		"It's a humiliating defeat for -victim-.",
		"-victim- didn't stand a chance against you.",
		"You have defeated -victim-.", "It's all over for -victim-.",
		"-victim- regrets the day they met you in combat.",
		"-victim- falls before your might.",
		"Can anyone defeat you? Certainly not -victim-.",
		"You were clearly a better fighter than -victim-."
      };
	

	public static final void dropItemsOnDeath(Player player) {

		Entity killer = player.getCombat().getDamageTracker().getKiller();
		
		BossPets.onDeath(player);
		

		if (killer != null && !killer.isNpc()) {
			Item weapon = killer.getPlayer().getEquipment().getItems()[3];
			if (weapon == null) {
				weapon = new Item(0);
			}
			Utility.sendPacketToPlayers(new SendKillFeed(Utility.formatPlayerName(killer.getPlayer().getUsername()), Utility.formatPlayerName(player.getPlayer().getUsername()), weapon.getId(), player.isPoisoned()), Arrays.asList(World.getPlayers()));
			killer.getPlayer().send(new SendMessage(Utility.randomElement(DEATH_MESSAGES).replaceAll("-victim-", ""+player.getUsername()+"</col>")));
			killer.getPlayer().setRogueKills(killer.getPlayer().getRogueKills() + 1);	
		
			if (!PlayerKilling.hostOnList(killer.getPlayer(), player.getClient().getHost())) {
				killer.getPlayer().send(new SendMessage("@blu@You have killed " + player.getUsername() + " recently! You were not awarded."));
				
			} else {

				PlayerKilling.addHostToList(killer.getPlayer(), player.getClient().getHost());
				killer.getPlayer().setKills(killer.getPlayer().getKills() + 1);
				
				if (!killer.getPlayer().targetName.equals(player.getUsername())) {
					if (killer.getPlayer().getRogueKills() > killer.getPlayer().getRogueRecord()) {
						killer.getPlayer().setRogueRecord(killer.getPlayer().getRogueKills());
					}		
				}
				
				AchievementHandler.activateAchievement(killer.getPlayer(), AchievementList.KILL_1_PLAYER, 1);
				AchievementHandler.activateAchievement(killer.getPlayer(), AchievementList.KILL_15_PLAYER, 1);
				AchievementHandler.activateAchievement(killer.getPlayer(), AchievementList.KILL_50_PLAYER, 1);
				

				if (killer.getPlayer().targetName.equals(player.getUsername())) {

					Optional<BountyEmblems> emblem = BountyEmblems.getBest(killer.getPlayer(), true);					
					
					if (emblem.isPresent()) {

						int slot = killer.getPlayer().getInventory().getItemSlot(emblem.get().getItemId());
						
						killer.getPlayer().getInventory().removeFromSlot(slot, emblem.get().getItemId(), 1);
						
						killer.getPlayer().getInventory().addOnLogin(new Item(emblem.get().getNextOrLast().getItemId()), slot);
						killer.getPlayer().getInventory().update();
						
					} else {
						GroundItemHandler.add(new Item(12746), player.getLocation(), killer.getPlayer());
					}

					killer.getPlayer().setHunterKills(killer.getPlayer().getHunterKills() + 1);
					if (killer.getPlayer().getHunterKills() > killer.getPlayer().getHunterRecord()) {
						killer.getPlayer().setHunterRecord(killer.getPlayer().getHunterKills());
					}
					killer.getPlayer().send(new SendMessage("@dre@Good job! You have killed your target."));
				}
			}
		}
		
		if (killer != null) {
			if (!killer.isNpc()) {
				if (PlayerConstants.isHighClass(player) || PlayerConstants.isHighClass(killer.getPlayer())) {
					return;
				}
			}
		}
		
		if (killer != null) {
			if (!killer.isNpc()) {
				PlayerLogger.DEATH_LOGGER.log(player.getUsername(), String.format("%s has been killed by %s.", Utility.formatPlayerName(player.getUsername()), Utility.formatPlayerName(killer.getPlayer().getUsername())));
			} else {
				if (killer.getMob().getDefinition() != null && killer.getMob().getDefinition().getName() != null) {
					PlayerLogger.DEATH_LOGGER.log(player.getUsername(), String.format("%s has been killed by %s.", Utility.formatPlayerName(player.getUsername()), Utility.formatPlayerName(killer.getMob().getDefinition().getName())));
				}
			}
		}
		
		int kept = 3;

		if (player.getSkulling().isSkulled()) {
			kept = 0;
		}

		if (player.getPrayer().active(Prayer.PROTECT_ITEM)) {
			kept++;
		}

		Queue<Item> items = new PriorityQueue<Item>(42, ITEM_VALUE_COMPARATOR);
		
		for (Item i : player.getInventory().getItems()) {
			if (i != null) {
				items.add(i);
			}
		}

		for (Item i : player.getEquipment().getItems()) {
			if (i != null) {
				items.add(i);
			}
		}

		Item k = null;

		player.getInventory().clear();
		player.getEquipment().clear();
		
		for (int i = 0; i < kept; i++) {
			Item keep = items.poll();

			if (keep != null) {
				if (keep.getAmount() == 1) {
					player.getInventory().add(keep, false);
				} else {
					player.getInventory().add(new Item(keep.getId(), 1), false);
					keep.remove(1);
					items.add(keep);
				}

			}
		}

		HashMap<Integer, Integer> recieved = new HashMap<>();

		while ((k = items.poll()) != null) {
			
			for (int index = 0; index < VencillioConstants.ITEM_DISMANTLE_DATA.length; index++) {
				if (k.getId() == VencillioConstants.ITEM_DISMANTLE_DATA[index][0]) {
					k.setId(VencillioConstants.ITEM_DISMANTLE_DATA[index][1]);
				}
			}
						
			if (k.getDefinition().isTradable() || ItemCheck.isItemDyedWhip(k)) {
				
				if (ItemCheck.isItemDyedWhip(k)) {
					k.setId(4151);
				}

				if ((killer == null) || (killer.isNpc())) {
					GroundItemHandler.add(k, player.getLocation(), player, player.ironPlayer() ? player : null);
				} else {
					GroundItemHandler.add(k, player.getLocation(), World.getPlayers()[killer.getIndex()]);
				}

				if (recieved.get(k.getId()) != null) {
					recieved.put(k.getId(), k.getAmount() + recieved.get(k.getId()));
				} else {
					recieved.put(k.getId(), k.getAmount());
				}
				
			} else if (!k.getDefinition().isTradable()) {
				GroundItemHandler.add(k, player.getLocation(), player, player.ironPlayer() ? player : null);
			} else {
				player.getInventory().add(k, false);
			}
		}
		
		for (int item : recieved.keySet()) {
			Item reward = new Item(item, recieved.get(item));
			if (killer != null) {
				if (!killer.isNpc()) {
					PlayerLogger.DROP_LOGGER.log(player.getUsername(), String.format("%s has recieved %s %s from %s.", Utility.formatPlayerName(player.getUsername()), reward.getAmount(), Utility.formatPlayerName(reward.getDefinition().getName()), Utility.formatPlayerName(killer.getPlayer().getUsername())));
					PlayerLogger.DROP_LOGGER.log(killer.getPlayer().getUsername(), String.format("%s has dropped %s %s for %s.", Utility.formatPlayerName(player.getUsername()), reward.getAmount(), Utility.formatPlayerName(reward.getDefinition().getName()), Utility.formatPlayerName(player.getUsername())));
				} else {
					PlayerLogger.DROP_LOGGER.log(player.getUsername(), String.format("%s has recieved %s %s from %s.", Utility.formatPlayerName(player.getUsername()), reward.getAmount(), Utility.formatPlayerName(reward.getDefinition().getName()), Utility.formatPlayerName(killer.getMob().getDefinition().getName())));
				}
			}
		}

		if ((killer == null || killer.isNpc())) {
			GroundItemHandler.add(new Item(526, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
		} else {
			GroundItemHandler.add(new Item(526, 1), player.getLocation(), World.getPlayers()[killer.getIndex()]);
		}

		player.getInventory().update();
		
	}

	public static class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item arg0, Item arg1) {
			int val1 = (!arg0.getDefinition().isTradable()) && (!ItemCheck.isItemDyedWhip(arg0)) ? 0 : GameDefinitionLoader.getHighAlchemyValue(arg0.getId());
			int val2 = (!arg1.getDefinition().isTradable()) && (!ItemCheck.isItemDyedWhip(arg1)) ? 0 : GameDefinitionLoader.getHighAlchemyValue(arg1.getId());

			if (val1 > val2)
				return -1;
			if (val1 < val2) {
				return 1;
			}

			return 0;
		}
	}
}
