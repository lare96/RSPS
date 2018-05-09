package com.vencillio.rs2.entity.mob;

import com.vencillio.core.definitions.ItemDropDefinition;
import com.vencillio.core.definitions.ItemDropDefinition.ItemDropTable;
import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.core.util.logger.PlayerLogger;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.cluescroll.ClueDifficulty;
import com.vencillio.rs2.content.cluescroll.ClueScroll;
import com.vencillio.rs2.content.cluescroll.ClueScrollManager;
import com.vencillio.rs2.content.cluescroll.scroll.EmoteScroll;
import com.vencillio.rs2.content.minigames.barrows.Barrows;
import com.vencillio.rs2.content.minigames.fightcave.TzharrGame;
import com.vencillio.rs2.content.minigames.godwars.GodWars;
import com.vencillio.rs2.content.minigames.warriorsguild.ArmourAnimator;
import com.vencillio.rs2.content.minigames.warriorsguild.CyclopsRoom;
import com.vencillio.rs2.content.pets.BossPets;
import com.vencillio.rs2.content.pets.BossPets.PetData;
import com.vencillio.rs2.content.skill.summoning.FamiliarMob;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.mob.impl.GiantMole;
import com.vencillio.rs2.entity.mob.impl.Kraken;
import com.vencillio.rs2.entity.mob.impl.SeaTrollQueen;
import com.vencillio.rs2.entity.mob.impl.Zulrah;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendKillFeed;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Handles Mop drops
 * 
 * @author Daniel
 *
 */
public class MobDrops {

	/**
	 * Random
	 */
	private static final SecureRandom random = new SecureRandom();

	/**
	 * Rare check
	 */
	private static boolean rares = true;

	/**
	 * Calculates amount
	 * 
	 * @param drop
	 * @return
	 */
	public static int calculateAmount(ItemDropDefinition.ItemDrop drop) {
		if (drop.getMax() <= drop.getMin()) {
			return drop.getMin();
		}
		if (drop.getMin() + random.nextInt(drop.getMax() - drop.getMin()) < 1) {
			return 1;
		}
		return drop.getMin() + random.nextInt(drop.getMax() - drop.getMin());
	}

	/**
	 * Drops the item
	 * 
	 * @param entity
	 * @param mob
	 * @param table
	 * @param dropLocation
	 */
	public static void drop(Entity entity, Mob mob, ItemDropDefinition.ItemDropTable table, Location dropLocation) {
		if ((table != null) && (table.getDrops() != null)) {
			ItemDropDefinition.ItemDrop drop = table.getDrops()[random.nextInt(table.getDrops().length)];

			if (drop == null) {
				return;
			}

			Item item = new Item(drop.getId(), calculateAmount(drop));

			if (!entity.isNpc()) {
				if (item.getDefinition().getGeneralPrice() >= 1_000_000) {
					PlayerLogger.DROP_LOGGER.log(entity.getPlayer().getUsername(), String.format("%s has recieved %s %s from %s.", Utility.formatPlayerName(entity.getPlayer().getUsername()), item.getAmount(), item.getDefinition().getName(), Utility.formatPlayerName(mob.getDefinition().getName())));
					AchievementHandler.activateAchievement(entity.getPlayer(), AchievementList.OBTAIN_10_RARE_DROPS, 1);
					World.sendGlobalMessage("<col=1F8C26>" + entity.getPlayer().getUsername() + " received a drop: " + Utility.format(item.getAmount()) + " x " + item.getDefinition().getName() + ".");
				} else {
					World.sendRegionMessage("<col=1F8C26>" + entity.getPlayer().getUsername() + " recieved a drop: " + Utility.format(item.getAmount()) + " x " + item.getDefinition().getName() + ".", mob.getLocation());					
				}
			}

			if (!item.getDefinition().isStackable()) {
				if (item.getAmount() > 5) {
					item.setId(item.getDefinition().getNoteId());
					GroundItemHandler.add(item, dropLocation, (entity == null) || (entity.isNpc()) ? null : World.getPlayers()[entity.getIndex()], World.getPlayers()[entity.getIndex()].ironPlayer() ? World.getPlayers()[entity.getIndex()] : null);
				} else {
					int am = item.getAmount();
					item.setAmount(1);
					for (int i = 0; i < am; i++) {
						GroundItemHandler.add(item, dropLocation, (entity == null) || (entity.isNpc()) ? null : World.getPlayers()[entity.getIndex()], World.getPlayers()[entity.getIndex()].ironPlayer() ? World.getPlayers()[entity.getIndex()] : null);
					}
				}
			} else {
				GroundItemHandler.add(item, dropLocation, (entity == null) || (entity.isNpc()) ? null : World.getPlayers()[entity.getIndex()], World.getPlayers()[entity.getIndex()].ironPlayer() ? World.getPlayers()[entity.getIndex()] : null);
			}
		}
	}

	public static Item getDrop(Entity entity, ItemDropDefinition.ItemDropTable table) {
		Item item = null;
		if ((table != null) && (table.getDrops() != null)) {
			ItemDropDefinition.ItemDrop drop = table.getDrops()[random.nextInt(table.getDrops().length)];

			if (drop == null) {
				return item;
			}

			item = new Item(drop.getId(), calculateAmount(drop));
		}
		return item;
	}

	public static void dropConstants(Entity entity, Mob mob, ItemDropDefinition.ItemDropTable constants, Location dropLocation) {
		if ((constants == null) || (constants.getDrops() == null)) {
			return;
		}

		for (ItemDropDefinition.ItemDrop i : constants.getDrops()) {
			Item item = new Item(i.getId(), calculateAmount(i));

			if (!item.getDefinition().isStackable()) {
				if (item.getAmount() > 5) {
					item.setId(item.getDefinition().getNoteId());
					GroundItemHandler.add(item, dropLocation, (entity == null) || (entity.isNpc()) ? null : World.getPlayers()[entity.getIndex()], World.getPlayers()[entity.getIndex()].ironPlayer() ? World.getPlayers()[entity.getIndex()] : null);
				} else {
					int am = item.getAmount();
					item.setAmount(1);
					for (int ii = 0; ii < am; ii++) {
						GroundItemHandler.add(item, dropLocation, (entity == null) || (entity.isNpc()) ? null : World.getPlayers()[entity.getIndex()], World.getPlayers()[entity.getIndex()].ironPlayer() ? World.getPlayers()[entity.getIndex()] : null);
					}
				}
			} else {
				GroundItemHandler.add(item, dropLocation, (entity == null) || (entity.isNpc()) ? null : World.getPlayers()[entity.getIndex()], World.getPlayers()[entity.getIndex()].ironPlayer() ? World.getPlayers()[entity.getIndex()] : null);
			}
		}
	}

	public static List<Item> getDropConstants(Entity entity, ItemDropDefinition.ItemDropTable constants) {
		List<Item> items = new ArrayList<>();
		if ((constants == null) || (constants.getDrops() == null)) {
			return items;
		}
		for (ItemDropDefinition.ItemDrop i : constants.getDrops()) {
			Item item = new Item(i.getId(), calculateAmount(i));
			items.add(item);
		}
		return items;
	}

	public static boolean dropClues(Player player, Mob mob, Location dropLocation) {

		NpcDefinition def = mob.getDefinition();

		double rand = Math.random();
		double chance = 1 - 1 / 175.0;
		if (rand >= chance) {
			int level = def == null ? 1 : def.getLevel();

			Item item = null;

			if (level < 70) {
				item = ClueScrollManager.getRandomClue(player, Math.random() > 0.5 ? ClueDifficulty.MEDIUM : ClueDifficulty.EASY);
			} else if (level >= 70 && level < 120) {
				item = ClueScrollManager.getRandomClue(player, Math.random() > 0.5 ? ClueDifficulty.HARD : ClueDifficulty.MEDIUM);
			} else if (level >= 120) {
				item = ClueScrollManager.getRandomClue(player, ClueDifficulty.HARD);
			}

			if (item != null) {
				GroundItemHandler.add(item, dropLocation, player, player.ironPlayer() ? player : null);
				return true;
			}
			
		}
		return false;
	}

	public static void dropItems(Entity entity, Mob mob) {
		if (entity == null || entity.isNpc() || mob == null || !mob.isNpc()) {
			return;
		}
		
		Player player = World.getPlayers()[entity.getIndex()];
		
		if (player == null) {
			return;
		}


		if (player.getEquipment() != null && player.getEquipment().getItems() != null) {
			Item weapon = player.getEquipment().getItems()[3];
			
			if (weapon == null) {
				weapon = new Item();
			}
			
			Utility.sendPacketToPlayers(new SendKillFeed(Utility.formatPlayerName(player.getUsername()), Utility.formatPlayerName(mob.getDefinition().getName()), weapon.getId(), mob.isPoisoned()), Arrays.asList(World.getPlayers()));
		}
		
		if (mob.getId() == 1778) {
			if (ClueScrollManager.playerHasScroll(player)) {
				for (Item item : player.getInventory().getItems()) {

					if (item == null) {
						continue;
					}

					ClueScroll scroll = ClueScrollManager.getClue(item.getId());

					if ((scroll != null) && (scroll instanceof EmoteScroll)) {
						((EmoteScroll) scroll).onAgentDeath(player);
						return;
					}
				}
			}
		}

		switch (mob.getId()) {
		case 2215:
		case 3162:
		case 2205:
		case 3129:
		case 4005:
		case 6342:
		case 319:
		case 415:
		case 8:
		case 3127:
		case 2265:
		case 6618:
		case 2266:
		case 2267:
		case 5779:
		case 2054:
		case 6615:
		case 4315:
		case 6619:
			player.getProperties().addProperty(mob, 1);
			break;
			
		case 6609:
			AchievementHandler.activateAchievement(player, AchievementList.KILL_100_CALLISTO, 1);
			player.getProperties().addProperty(mob, 1);
		break;
			
		case 494:
			AchievementHandler.activateAchievement(player, AchievementList.KILL_25_KRAKENS, 1);
			AchievementHandler.activateAchievement(player, AchievementList.KILL_150_KRAKENS, 1);
			player.getProperties().addProperty(mob, 1);
			break;
			
		case 239:
			AchievementHandler.activateAchievement(player, AchievementList.KILL_KING_BLACK_DRAGON, 1);
			player.getProperties().addProperty(mob, 1);
			break;

		case 100:
			AchievementHandler.activateAchievement(player, AchievementList.KILL_ROCK_CRABS, 1);
			break;

		case 467:
			AchievementHandler.activateAchievement(player, AchievementList.KILL_250_SKELETAL_WYVERNS, 1);
			break;

		case 2805:
			AchievementHandler.activateAchievement(player, AchievementList.KILL_75_COWS, 1);
			break;

		case 2955:
		case 2954:
			player.setArenaPoints(player.getArenaPoints() + 1);
			player.send(new SendMessage("@dre@You now have " + player.getArenaPoints() + " Mage Arena points."));
			AchievementHandler.activateAchievement(player, AchievementList.EARN_100_MAGE_ARENA_POINTS, 1);
			AchievementHandler.activateAchievement(player, AchievementList.EARN_500_MAGE_ARENA_POINTS, 1);
			break;

		}
		

		if ((entity instanceof FamiliarMob)) {
			Mob m = World.getNpcs()[entity.getIndex()];

			if (m != null) {
				entity = m.getOwner();
			}
		}

		Location dropLocation = mob != null ? mob.getLocation() : null;

		if (dropLocation == null) {
			Exception e = new Exception("Mob is null?");
			e.printStackTrace();
			return;
		}

		if (mob instanceof SeaTrollQueen) {
			dropLocation = new Location(2344, 3699);
		}
		
		if (mob instanceof Zulrah) {
			dropLocation = new Location(player.getX(), player.getY(), player.getZ());
		}
		
		if (mob instanceof Kraken) {
			dropLocation = new Location(player.getX(), player.getY(), player.getZ());
		}
		
		if (mob instanceof GiantMole) {
			dropLocation = new Location(player.getX(), player.getY(), player.getZ());
		}
		

		// int level = mob.getDefinition().getLevel();

		ItemDropDefinition drops = GameDefinitionLoader.getItemDropDefinition(mob.getId());

		int ucRoll = 10;

		int rtMod = 0;

		boolean drop = true;
		int amount = 1;

		if (mob.getMaxLevels()[3] == 0) {
			drop = false;
		}

		Player p = null;
		if ((entity != null) && (!entity.isNpc()) && ((p = World.getPlayers()[entity.getIndex()]) != null)) {
			if (ArmourAnimator.isAnimatedArmour(mob.getId())) {
				ArmourAnimator.dropForAnimatedArmour(p, mob);
				return;
			}
			
			if (p.getController() == ControllerManager.GOD_WARS_CONTROLLER) {
				try {
					GodWars.onGodwarsKill(p, mob.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (mob.getId() == 2463) {
				p.getAttributes().set("CYCLOPS_KILLED", p.getAttributes().getInt("CYCLOPS_KILLED") == -1 ? 1 : p.getAttributes().getInt("CYCLOPS_KILLED") + 1);
				CyclopsRoom.updateInterface(p);
				if (drop)
					CyclopsRoom.dropDefender(p, mob);
			} else {
				try {
					p.getSlayer().checkForSlayer(mob);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Barrows.onBarrowsDeath(p, mob);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					TzharrGame.checkForFightCave(p, mob);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Item ring = p.getEquipment().getItems()[12];

			if ((ring != null) && (ring.getId() == 2572)) {
				ucRoll += 5;
				rtMod = 2;
			}

			if (p.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
				int random = Utility.random(150);
				if (random == 75) {
					GroundItemHandler.add(new Item(985, 1), dropLocation, p, p.ironPlayer() ? p : null);
				}
				if (random == 100) {
					GroundItemHandler.add(new Item(987, 1), dropLocation, p, p.ironPlayer() ? p : null);
				}
				/*
				 * if (random.nextInt(100) <= 5) { int seed =
				 * Plants.values()[Misc
				 * .randomNumber(Plants.values().length)].seed;
				 * GroundItemHandler.add(new Item(seed, Misc.randomNumber(3)),
				 * dropLocation, p); }
				 */

			}
		} else if (ArmourAnimator.isAnimatedArmour(mob.getId())) {
			return;
		}

		if (drops == null) {
			return;
		}

		if ((drops.getConstant() != null) && (drops.getConstant().getDrops() != null)) {
			dropConstants(entity, mob, drops.getConstant(), dropLocation);
		}

		if (!drop) {
			return;
		}
		
		if (dropClues(player, mob, dropLocation)) {
			return;
		}

		if ((drops.getCommon() == null) || (drops.getCommon().getDrops() == null)) {
			return;
		}

		boolean ucTable = random.nextInt(100) <= ucRoll;

		boolean hasCommon = (drops.getCommon() != null) && (drops.getCommon().getDrops() != null) && (drops.getCommon().getDrops().length > 0);
		boolean hasUncommon = (drops.getUncommon() != null) && (drops.getUncommon().getDrops() != null) && (drops.getUncommon().getDrops().length > 0);
		boolean hasRare = (drops.getRare() != null) && (drops.getRare().getDrops() != null) && (drops.getRare().getDrops().length > 0);

		if ((rares) && (hasRare) && (rollRareDrop(entity, mob, drops.getRare(), rtMod, dropLocation))) {
			return;
		}

		for (int i = 0; i < amount; i++) {
			if ((hasUncommon) && (ucTable))
				drop(entity, mob, drops.getUncommon(), dropLocation);
			else if (hasCommon)
				drop(entity, mob, drops.getCommon(), dropLocation);
		}

	}

	public static List<Item> getDropItems(Player player, int mob, int mod, boolean raresOnly) {
		List<Item> items = new ArrayList<>();

		Item weapon = null;

		weapon = player.getEquipment().getItems()[3];

		if (weapon == null) {
			weapon = new Item(0);
		}

		ItemDropDefinition drops = GameDefinitionLoader.getItemDropDefinition(mob);

		int ucRoll = 10;

		int rtMod = 0;

		boolean drop = true;
		int amount = 1;

		Item ring = player.getEquipment().getItems()[12];

		if ((ring != null) && (ring.getId() == 2572)) {
			ucRoll += 5;
			rtMod = 2;
		}

		if (!raresOnly && player.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
			if (random.nextInt(100) <= 8) {
				if (Utility.randomNumber(40) < 20) {
					items.add(new Item(985));
				} else {
					items.add(new Item(987));
				}
			}
		}

		if (drops == null) {
			return items;
		}

		if (!raresOnly && (drops.getConstant() != null) && (drops.getConstant().getDrops() != null)) {
			items.addAll(getDropConstants(player, drops.getConstant()));
		}

		if (!drop) {
			return items;
		}

		if ((drops.getCommon() == null) || (drops.getCommon().getDrops() == null)) {
			return items;
		}

		boolean ucTable = random.nextInt(100) <= ucRoll; //ucRoll 10 default (10% chance uncommon)

		boolean hasCommon = (drops.getCommon() != null) && (drops.getCommon().getDrops() != null) && (drops.getCommon().getDrops().length > 0);
		boolean hasUncommon = (drops.getUncommon() != null) && (drops.getUncommon().getDrops() != null) && (drops.getUncommon().getDrops().length > 0);
		boolean hasRare = (drops.getRare() != null) && (drops.getRare().getDrops() != null) && (drops.getRare().getDrops().length > 0);
		Item rareDrops = null;
		if ((rares) && (hasRare) && ((rareDrops = getRareDrop(player, drops.getRare(), rtMod + mod)) != null)) {
			items.add(rareDrops);
			return items;
		}

		if (!raresOnly) {
			for (int i = 0; i < amount; i++) {
				if ((hasUncommon) && (ucTable))
					items.add(getDrop(player, drops.getUncommon()));
				else if (hasCommon)
					items.add(getDrop(player, drops.getCommon()));
			}
		}
		return items;
	}

	public static int getLevel(Mob mob, int chance) {
		int lvl = mob.getDefinition().getLevel();

		if ((lvl / 6 > chance) || (lvl / 6 == 0)) {
			return chance;
		}

		return lvl / 6;
	}

	public static void main(String[] args) {
		try {
			GameDefinitionLoader.declare();
			GameDefinitionLoader.loadItemDefinitions();
			//GameDefinitionLoader.loadNpcDropDefinitions();
			GameDefinitionLoader.loadRareDropChances();
			GameDefinitionLoader.loadNpcDefinitions();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int npcId = 3162;

		ItemDropTable table = GameDefinitionLoader.getItemDropDefinition(npcId).getRare();

		// estimates rare percentage

		HashMap<Item, Integer> averages = new HashMap<>();

		int mod = 10 * 10;
		double trials = 1_000_000;

		for (int i = 0; i < trials; i++) {
			ItemDropDefinition.ItemDrop rare = table.getDrops()[random.nextInt(table.getDrops().length)];
			int chance = GameDefinitionLoader.getRareDropChance(rare.getId());

			int roll = random.nextInt(1000);
			if (roll >= 500 && roll <= 500 + chance + mod) {
				averages.put(new Item(rare.getId()), (averages.get(new Item(rare.getId())) == null ? 0 : averages.get(new Item(rare.getId()))) + 1);
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|");
		System.out.println(GameDefinitionLoader.getNpcDefinition(npcId).getName());
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|");
		for (Item item : averages.keySet()) {
			int avrg = averages.get(item);
			System.out.println(item.getDefinition().getName() + " - " + avrg * 100 / trials + "%");
		}
	}

	public static boolean rollRareDrop(Entity e, Mob mob, ItemDropDefinition.ItemDropTable table, int mod, Location dropLocation) {
		ItemDropDefinition.ItemDrop rare = table.getDrops()[random.nextInt(table.getDrops().length)];

		Player p = null;
		if ((e != null) && (!e.isNpc())) {
			p = World.getPlayers()[e.getIndex()];
		}

		if (rare != null) {
			int chance = GameDefinitionLoader.getRareDropChance(rare.getId());

			int roll = random.nextInt(1000 - (p != null && chance < 50 ? p.getRareDropEP().getEpAddon() >= 1000 ? 600 : p.getRareDropEP().getEpAddon() : 0));

			if (roll >= 500 && roll <= 500 + chance + mod) {

				int am = rare.getMin() < rare.getMax() ? rare.getMin() + random.nextInt(rare.getMax() - rare.getMin()) : rare.getMin();

				if (p != null) {
					if (chance < 80 || p.getRareDropEP().getReceived() >= 4) {
						p.getRareDropEP().reset();
					}

					p.getRareDropEP().addReceived();

					if (!p.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)) {
						Item item = new Item(rare.getId(), am);
						World.sendRegionMessage("<col=1F8C26>" + e.getPlayer().getUsername() + " received a drop: " + Utility.format(item.getAmount()) + " x " + item.getDefinition().getName() + ".", mob.getLocation());
						
					}
				}

				Item drop = new Item(rare.getId(), am);

				if (!e.isNpc()) {
					PetData petDrop = PetData.forItem(drop.getId());

					if (petDrop != null) {
						if (e.getPlayer().getBossPet() == null) {
							BossPets.spawnPet(e.getPlayer(), petDrop.getItem(), true);
							e.getPlayer().send(new SendMessage("You feel a presence following you; " + Utility.formatPlayerName(GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName()) + " starts to follow you."));
						} else {
							e.getPlayer().getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
							e.getPlayer().send(new SendMessage("You feel a presence added to your bank."));
							AchievementHandler.activateAchievement(e.getPlayer(), AchievementList.OBTAIN_1_BOSS_PET, 1);
							AchievementHandler.activateAchievement(e.getPlayer(), AchievementList.OBTAIN_10_BOSS_PET, 1);
						}
					} else {
						GroundItemHandler.add(drop, dropLocation, p, p.ironPlayer() ? p : null);
					}
				}
				return true;
			}
		}
		return false;
	}

	public static Item getRareDrop(Entity e, ItemDropDefinition.ItemDropTable table, int mod) {
		Item item = null;
		ItemDropDefinition.ItemDrop rare = table.getDrops()[random.nextInt(table.getDrops().length)];

		Player p = null;
		if ((e != null) && (!e.isNpc())) {
			p = World.getPlayers()[e.getIndex()];
		}

		if (rare != null) {
			int chance = GameDefinitionLoader.getRareDropChance(rare.getId());

			int roll = random.nextInt(1000 - (p != null && chance < 80 ? p.getRareDropEP().getEpAddon() : 0));

			if (roll >= 500 && roll <= 500 + chance + mod) {

				int am = rare.getMin() < rare.getMax() ? rare.getMin() + random.nextInt(rare.getMax() - rare.getMin()) : rare.getMin();

				if (p != null) {
					if (chance < 80 || p.getRareDropEP().getReceived() >= 4) {
						p.getRareDropEP().reset();
					}

					p.getRareDropEP().addReceived();
				}

				item = new Item(rare.getId(), am);
			}
		}
		return item;
	}

	public static void setRares(boolean set) {
		rares = set;
	}
}
