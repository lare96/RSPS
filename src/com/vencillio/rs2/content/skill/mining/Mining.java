package com.vencillio.rs2.content.skill.mining;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.Task.BreakType;
import com.vencillio.core.task.Task.StackType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Mining {
	
	private final static Set<Location> DEAD_ORES = new HashSet<>();
	
	public static void declare() {
		Pickaxe.declare();
		Ore.declare();
	}
	
	public static enum Pickaxe {
		BRONZE_PICKAXE(1265, 1, 8, new Animation(625)),
		IRON_PICKAXE(1267, 1, 7, new Animation(626)),
		STEEL_PICKAXE(1269, 6, 6, new Animation(627)),
		MITHRIL_PICKAXE(1273, 21, 5, new Animation(629)),
		ADAMANT_PICKAXE(1271, 31, 4, new Animation(628)),
		RUNE_PICKAXE(1275, 41, 3, new Animation(624)),			
		DRAGON_PICKAXE(11920, 61, 2, new Animation(6758)),
		DRAGON_PICKAXE_OR(12797, 61, 1, new Animation(335));

		private final int item;
		private final int level;
		private final int weight;
		private final Animation animation;
		
		private static final HashMap<Integer, Pickaxe> PICKAXES = new HashMap<>();
		
		public static void declare() {
			for (Pickaxe pickaxe : values()) {
				PICKAXES.put(pickaxe.item, pickaxe);
			}
		}

		private Pickaxe(int item, int level, int weight, Animation animation) {
			this.item = item;
			this.level = level;
			this.animation = animation;
			this.weight = weight;
		}

		public int getItem() {
			return item;
		}

		public int getLevel() {
			return level;
		}

		public Animation getAnimation() {
			return animation;
		}

		public int getWeight() {
			return weight;
		}

		public static Pickaxe get(Player player) {
			Pickaxe highest = null;
			
			Queue<Pickaxe> picks = new PriorityQueue<>((first, second) -> second.getLevel() - first.getLevel());
			
			if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT] != null) {
				highest = PICKAXES.get(player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId());
				
				if (highest != null) {
					picks.add(highest);
					highest = null;
				}
			}
			
			for (Item item : player.getInventory().getItems()) {
				if (item == null) {
					continue;
				}
				
				Pickaxe pick = PICKAXES.get(item.getId());
				
				if (pick == null) {
					continue;
				}

				picks.add(pick);
			}
			
			Pickaxe pick = picks.poll();
			
			if (pick == null) {
				return null;
			}
			
			while (player.getLevels()[Skills.MINING] < pick.getLevel()) {
				if (highest == null) {
					highest = pick;
				}
				
				pick = picks.poll();
			};
			
			return pick;
		}
	}
	
	public static enum Ore {
		COPPER("Copper ore", new int[] { 13708, 13709 }, 1, 17.5, new int[] { 436 }, 10081, 6, 4),
		TIN("Tin ore", new int[] { 13712, 13713 }, 1, 17.5, new int[] { 438 }, 10081, 6, 4),
		IRON("Iron ore", new int[] { 13710, 13711 }, 15, 35, new int[] { 440 }, 10081, 12, 7),
		SILVER_ORE("Silver ore", new int[] { 13716, 13717 }, 20, 40, new int[] { 442 }, 10081, 15, 10),
		COAL_ORE("Coal ore", new int[] { 13706, 13714 }, 30, 50, new int[] { 453 }, 10081, 12, 10),
		GOLD_ORE("Gold ore", new int[] { 13707, 13715 }, 40, 65, new int[] { 444 }, 10081, 15, 10),
		MITHRIL_ORE("Mithril ore", new int[] { 13718, 13719 }, 55, 80, new int[] { 447 }, 10081, 15, 13),
		ADAMANT_ORE("Adamant ore", new int[] { 13720, 14168 }, 70, 95, new int[] { 449 }, 10081, 15, 16),
		RUNITE_ORE("Runite ore", new int[] { 14175 }, 85, 125, new int[] { 451 }, 10081, 15, 18),
		ESSENCE("Essence", new int[] { 14912, 2491 }, 30, 10, new int[] { 1436 }, -1, -1, -1),
		GEM_ROCK("Gem Rock", new int[] { 14856, 14855, 14854 }, 40, 65, new int[] { 1625, 1627, 1629, 1623, 1621, 1619, 1617 }, 10081, 135, 140);
		
		private final String name;
		private int[] objects;
		private final int level;
		private final double exp;
		private final int[] ore;
		private final int replacement;
		private final int respawn;
		private final int immunity;
		
		private static final HashMap<Integer, Ore> ORES = new HashMap<>();
		
		public static void declare() {
			for (Ore ore : values()) {
				for (int object : ore.objects) {
					ORES.put(object, ore);
				}
			}
		}
		
		private Ore(String name, int[] objects, int level, double exp, int[] ore, int replacement, int respawn, int immunity) {
			this.name = name;
			this.objects = objects;
			this.level = level;
			this.exp = exp;
			this.ore = ore;
			this.replacement = replacement;
			this.respawn = respawn;
			this.immunity = immunity;
		}
		
		public String getName() {
			return name;
		}

		public int getLevel() {
			return level;
		}

		public double getExp() {
			return exp;
		}

		public int[] getOre() {
			return ore;
		}

		public int getReplacement() {
			return replacement;
		}

		public int getRespawn() {
			return respawn;
		}

		public int getImmunity() {
			return immunity;
		}

		public static Ore get(int id) {
			return ORES.get(id);
		}
	}

	
	public static boolean clickRock(Player player, RSObject object) {
		if (player.getSkill().locked() || object == null) {
			return false;
		}
		
		Ore ore = Ore.get(object.getId());
		
		if (ore == null) {
			return false;
		}
		
		if (player.getLevels()[Skills.MINING] < ore.getLevel()) {
			DialogueManager.sendStatement(player, "You need a Mining level of " + ore.getLevel() + " to mine that ore.");
			return false;
		}
		
		Pickaxe pickaxe = Pickaxe.get(player);
		
		if (pickaxe == null) {
			DialogueManager.sendStatement(player, "You don't have a pickaxe.");
			return false;
		}
		
		if (player.getLevels()[Skills.MINING] < pickaxe.getLevel()) {
			player.send(new SendMessage("You need a Mining level of " + pickaxe.getLevel() + " to use that pickaxe."));
			DialogueManager.sendStatement(player, "You need a Mining level of " + pickaxe.getLevel() + " to use that pickaxe.");
			return false;
		}

		if (player.getCombat().inCombat() || player.getCombat().getAttacking() != null) {
			player.send(new SendMessage("You can't do that right now!"));
			return false;
		}
		
		if (player.getInventory().getTakenSlots() == 28) {
			DialogueManager.sendStatement(player, "Your inventory is full!");
			return false;
		}
		
		player.send(new SendMessage("You swing your pick at the rock."));

		int ticks = ore.immunity == -1 ? 2 : ore.getImmunity() - (int) ( (player.getLevels()[Skills.MINING] - ore.getLevel()) * 2 / (double) pickaxe.getWeight());
		int gemTick = ore.getImmunity();
		
		
		if (ticks < 1) {
			ticks = 1;
		}
		
		int time = ore.getName().equalsIgnoreCase("gem rock") ? gemTick : ticks;
		
		TaskQueue.queue(new Task(player, 1, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION) {
			int ticks = 0;
			
			@Override
			public void execute() {
				if (ore == Ore.ESSENCE) {
					if (player.getInventory().getFreeSlots() == 0) {
						DialogueManager.sendStatement(player, "Your inventory is full!");
						stop();
						return;
					}
				}
				if (ticks++ == time || DEAD_ORES.contains(new Location(object.getX(), object.getY(), object.getZ()))) {
					if (ore == Ore.ESSENCE) {
						player.getInventory().add(ore.getOre()[Utility.random(ore.getOre().length)], 1);
						player.getSkill().addExperience(Skills.MINING, ore.getExp());
						AchievementHandler.activateAchievement(player, AchievementList.MINE_1000_ROCKS, 1);
						ticks = 0;
						if (player.getInventory().getFreeSlots() == 0) {
							DialogueManager.sendStatement(player, "Your inventory is full!");
							stop();
						}
						return;
					} else {
						stop();
						return;
					}
				}
				
				player.getUpdateFlags().sendAnimation(pickaxe.getAnimation());
			}

			@Override
			public void onStop() {
				player.getUpdateFlags().sendAnimation(new Animation(65535));
				if (time + 1 == ticks) {
					if (ore != Ore.ESSENCE) {
						System.out.println(Arrays.asList(ore.getOre()));
						player.getInventory().add(ore.getOre()[Utility.randomNumber(ore.getOre().length)], 1);
						player.getSkill().addExperience(Skills.MINING, ore.getExp());
						AchievementHandler.activateAchievement(player, AchievementList.MINE_1000_ROCKS, 1);
					}	
					if (ore.getReplacement() > 0) {
						ObjectManager.spawnWithObject(ore.getReplacement(), object.getX(), object.getY(), object.getZ(), object.getType(), object.getFace());
						DEAD_ORES.add(new Location(object.getX(), object.getY(), object.getZ()));
						
						TaskQueue.queue(new Task(player, ore.getRespawn(), false, StackType.STACK, BreakType.NEVER, TaskIdentifier.MINING_ROCK) {
							@Override
							public void execute() {
								stop();
							}
		
							@Override
							public void onStop() {
								DEAD_ORES.remove(new Location(object.getX(), object.getY(), object.getZ()));
								ObjectManager.spawnWithObject(object.getId(), object.getX(), object.getY(), object.getZ(), object.getType(), object.getFace());
							}
						});
					}
				}
			}
		});
		
		return true;
	}
	
	public static void main(String[] args) {
		int pickaxe = Pickaxe.RUNE_PICKAXE.getWeight();
		int ore_req = 1;
		int immunity = 4;
		
		System.out.println("Immunity: " + immunity + " [" + (int) (immunity * 5 / 3.0) + "s]");
		
		for (int i = ore_req; i < 100; i++) {
			
			int result = immunity - (int) ( (i - ore_req) * 2 / (double) pickaxe );
			
			if (result <= 2) {
				System.out.println("Level: " + i + " = " + result + " [" + (int) (result * 5 / 3.0) + "s]");
				break;
			}
			
			System.out.println("Level: " + i + " = " + result + " [" + (int) (result * 5 / 3.0) + "s]");
		}
	}
}