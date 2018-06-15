package com.vencillio.core.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.definitions.*;
import com.vencillio.core.definitions.ItemDropDefinition.ItemDrop;
import com.vencillio.rs2.content.combat.impl.PlayerDrops;
import com.vencillio.rs2.content.gambling.FlowerGame;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class GameDefinitionLoader {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(GameDefinitionLoader.class.getSimpleName());

	private static XStream xStream = new XStream(new Sun14ReflectionProvider());

	private static int[][] alternates = new int[53000][1];

	private static Map<Integer, Integer> rareDropChances = new HashMap<Integer, Integer>();

	private static Map<Integer, byte[][]> itemRequirements = new HashMap<Integer, byte[][]>();
	private static Map<Integer, ItemDefinition> itemDefinitions = new HashMap<Integer, ItemDefinition>();
	private static Map<Integer, NpcDefinition> npcDefinitions = new HashMap<Integer, NpcDefinition>();

	private static Map<Integer, SpecialAttackDefinition> specialAttackDefinitions = new HashMap<Integer, SpecialAttackDefinition>();
	private static Map<Integer, RangedWeaponDefinition> rangedWeaponDefinitions = new HashMap<Integer, RangedWeaponDefinition>();
	private static Map<Integer, WeaponDefinition> weaponDefinitions = new HashMap<Integer, WeaponDefinition>();
	private static Map<Integer, FoodDefinition> foodDefinitions = new HashMap<Integer, FoodDefinition>();
	private static Map<Integer, PotionDefinition> potionDefinitions = new HashMap<Integer, PotionDefinition>();
	private static Map<Integer, EquipmentDefinition> equipmentDefinitions = new HashMap<Integer, EquipmentDefinition>();
	private static Map<Integer, ItemBonusDefinition> itemBonusDefinitions = new HashMap<Integer, ItemBonusDefinition>();
	private static Map<Integer, CombatSpellDefinition> combatSpellDefinitions = new HashMap<Integer, CombatSpellDefinition>();
	private static Map<Integer, NpcCombatDefinition> npcCombatDefinitions = new HashMap<Integer, NpcCombatDefinition>();
	private static Map<Integer, RangedStrengthDefinition> rangedStrengthDefinitions = new HashMap<Integer, RangedStrengthDefinition>();

	private static Map<Integer, ItemDropDefinition> mobDropDefinitions = new HashMap<Integer, ItemDropDefinition>();

	public static final void clearAlternates() {
		alternates = null;
	}

	public static final void declare() {
		xStream.alias("ItemDropDefinition", com.vencillio.core.definitions.ItemDropDefinition.class);
		xStream.alias("constant", com.vencillio.core.definitions.ItemDropDefinition.ItemDropTable.class);
		xStream.alias("common", com.vencillio.core.definitions.ItemDropDefinition.ItemDropTable.class);
		xStream.alias("uncommon", com.vencillio.core.definitions.ItemDropDefinition.ItemDropTable.class);
		xStream.alias("itemDrop", com.vencillio.core.definitions.ItemDropDefinition.ItemDrop.class);
		xStream.alias("scroll", com.vencillio.core.definitions.ItemDropDefinition.ItemDropTable.ScrollTypes.class);

		xStream.alias("location", com.vencillio.rs2.entity.Location.class);
		xStream.alias("item", com.vencillio.rs2.entity.item.Item.class);
		xStream.alias("projectile", com.vencillio.rs2.entity.Projectile.class);
		xStream.alias("graphic", com.vencillio.rs2.entity.Graphic.class);
		xStream.alias("animation", com.vencillio.rs2.entity.Animation.class);

		xStream.alias("NpcCombatDefinition", com.vencillio.core.definitions.NpcCombatDefinition.class);
		xStream.alias("skill", com.vencillio.core.definitions.NpcCombatDefinition.Skill.class);
		xStream.alias("melee", com.vencillio.core.definitions.NpcCombatDefinition.Melee.class);
		xStream.alias("magic", com.vencillio.core.definitions.NpcCombatDefinition.Magic.class);
		xStream.alias("ranged", com.vencillio.core.definitions.NpcCombatDefinition.Ranged.class);

		xStream.alias("ItemDefinition", com.vencillio.core.definitions.ItemDefinition.class);
		xStream.alias("ShopDefinition", com.vencillio.core.definitions.ShopDefinition.class);
		xStream.alias("WeaponDefinition", com.vencillio.core.definitions.WeaponDefinition.class);
		xStream.alias("SpecialAttackDefinition", com.vencillio.core.definitions.SpecialAttackDefinition.class);
		xStream.alias("RangedWeaponDefinition", com.vencillio.core.definitions.RangedWeaponDefinition.class);
		xStream.alias("RangedStrengthDefinition", com.vencillio.core.definitions.RangedStrengthDefinition.class);
		xStream.alias("FoodDefinition", com.vencillio.core.definitions.FoodDefinition.class);
		xStream.alias("PotionDefinition", com.vencillio.core.definitions.PotionDefinition.class);
		xStream.alias("skillData", com.vencillio.core.definitions.PotionDefinition.SkillData.class);
		xStream.alias("ItemBonusDefinition", com.vencillio.core.definitions.ItemBonusDefinition.class);
		xStream.alias("CombatSpellDefinition", com.vencillio.core.definitions.CombatSpellDefinition.class);
		xStream.alias("NpcDefinition", com.vencillio.core.definitions.NpcDefinition.class);
		xStream.alias("NpcSpawnDefinition", com.vencillio.core.definitions.NpcSpawnDefinition.class);
		xStream.alias("EquipmentDefinition", com.vencillio.core.definitions.EquipmentDefinition.class);
		logger.info("All GameDefinitions have been loaded.");
	}

	public static void dumpSizes() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./NPCSizes.txt"));

			for (NpcDefinition i : npcDefinitions.values()) {
				if (i != null) {
					writer.write(i.getId() + ":" + i.getSize());
					writer.newLine();
				}
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Sizes dumped.");
	}

	public static int getAlternate(int id) {
		return alternates[id][0];
	}

	public static CombatSpellDefinition getCombatSpellDefinition(int id) {
		return combatSpellDefinitions.get(id);
	}

	public static EquipmentDefinition getEquipmentDefinition(int id) {
		return equipmentDefinitions.get(id);
	}

	public static FoodDefinition getFoodDefinition(int id) {
		return foodDefinitions.get(id);
	}

	public static int getHighAlchemyValue(int id) {
		ItemDefinition def = getItemDef(id);
		
		if (def == null) {
			return 1;
		}
		
		Item item = new Item(id);
		
		if (def.isNote()) {
			item.unNote();
		}
		
		return item.getDefinition().getHighAlch();
	}

	public static ItemBonusDefinition getItemBonusDefinition(int i) {
		return itemBonusDefinitions.get(i);
	}

	public static ItemDefinition getItemDef(int i) {
		return itemDefinitions.get(i);
	}

	public static int getItemSize() {
		return itemDefinitions.size();
	}

	public static ItemDefinition getItemID(Player player, String name) {
		int count = 0;
		for (int i = 0; i < itemDefinitions.size(); i++) {
			name = name.toLowerCase();
			ItemDefinition definition = getItemDef(i);
			String output = definition.getName().toLowerCase();
			if (output.contains(name)) {
				count++;
				String results = ""+count+" - <col=007FFF>"+definition.getName()+"</col> (<col=00FF3E>"+definition.getId()+"</col>) Worth(General): "+definition.getGeneralPrice()+" GP";
				player.send(new SendMessage(""+results+""));
				if(count >= 100) {
					player.send(new SendMessage("Found "+count+"+ results for '"+name+"'. Only 100 listed."));
					return definition;
				}
				// return definition; // Will stop the method on first item found >.>
			}
		}
		player.send(new SendMessage("Found "+count+" results for '"+name+"'"));
		return null;
	}

	public static void getItemIDBonus(Player player, String name) {
		for (int i = 0; i < itemDefinitions.size(); i++) {
			name = name.toLowerCase();
			ItemDefinition definition = getItemDef(i);
			String output = definition.getName().toLowerCase();
			short[] bonuses;
			if (output.contains(name)) {
				bonuses = Item.getItemBonuses(definition.getId());

				if(bonuses != null) {
					String resultsName = "<col=007FFF>" + definition.getName() + "</col>";

					String resultsAtt = "Stab: " + bonuses[0] + " Slash: " + bonuses[1] +
							" Crush: " + bonuses[2] + " Magic: " + bonuses[3] + " Range: " + bonuses[4];

					String resultsDef = "Stab: " + bonuses[5] + " Slash: " + bonuses[6] + " Crush: " +
							bonuses[7] + " Magic: " + bonuses[8] + " Range: " + bonuses[9];

					String resultsOther = "Strength: " + bonuses[10] + " Prayer: " + bonuses[12];

					player.send(new SendMessage("" + resultsName + ""));
					player.send(new SendMessage("" + resultsAtt + ""));
					player.send(new SendMessage("" + resultsDef + ""));
					player.send(new SendMessage("" + resultsOther + ""));
				}
					return;
			}
		}
	}

	public static ItemDropDefinition getItemDropDefinition(int id) {
		return mobDropDefinitions.get(id);
	}

	public static byte[][] getItemRequirements(int id) {
		return itemRequirements.get(id);
	}

	public static int getLowAlchemyValue(int id) {
		ItemDefinition def = getItemDef(id);
		
		if (def == null) {
			return 1;
		}
		
		Item item = new Item(id);
		
		if (def.isNote()) {
			item.unNote();
		}
		
		return item.getDefinition().getLowAlch();
	}

	public static NpcCombatDefinition getNpcCombatDefinition(int id) {
		return npcCombatDefinitions.get(id);
	}

	public static NpcDefinition getNpcDefinition(int id) {
		return npcDefinitions.get(id);
	}

	public static PotionDefinition getPotionDefinition(int id) {
		return potionDefinitions.get(id);
	}

	public static RangedStrengthDefinition getRangedStrengthDefinition(int id) {
		return rangedStrengthDefinitions.get(id);
	}

	public static RangedWeaponDefinition getRangedWeaponDefinition(int id) {
		return rangedWeaponDefinitions.get(id);
	}

	public static int getRareDropChance(int id) {
		if (!rareDropChances.containsKey(id)) {
			return 80;
		}

		return rareDropChances.get(id);
	}

	public static SpecialAttackDefinition getSpecialDefinition(int id) {
		return specialAttackDefinitions.get(id);
	}

	public static int getStoreBuyFromValue(int id) {
		ItemDefinition def = getItemDef(id);

		if (def == null) {
			return 1;
		}
		
		if (def.isNote()) {
			Item item = new Item(id);
			item.unNote();
			def = item.getDefinition();
		}

		double ratio = 0;

		if (def.getLowAlch() == 0 || (def.getHighAlch() == 0 && def.getLowAlch() == 0)) {
			ratio = 1;
		} else {
			ratio = def.getHighAlch() / (double) def.getLowAlch();
		}

		return (int) Math.ceil(def.getGeneralPrice() * ratio);
	}

	public static int getStoreSellToValue(int id) {
		ItemDefinition def = getItemDef(id);
		
		if (def == null) {
			return 1;
		}
		
		Item item = new Item(id);
		
		if (item.getDefinition().isNote()) {
			item.unNote();
		}
		
		return item.getDefinition().getGeneralPrice();
	}

	public static WeaponDefinition getWeaponDefinition(int id) {
		return weaponDefinitions.get(id);
	}

	public static XStream getxStream() {
		return xStream;
	}

	public static final void loadAlternateIds() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/def/ObjectAlternates.txt"));

			String line = null;

			while ((line = reader.readLine()) != null) {
				int id = Integer.parseInt(line.substring(0, line.indexOf(":")));
				line = line.substring(line.indexOf(":") + 1);
				int alt = Integer.parseInt(line.substring(0, line.length()));

				alternates[id][0] = alt;
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("All alternative objects have been loaded.");
	}

	@SuppressWarnings("unchecked")
	public static void loadCombatSpellDefinitions() throws FileNotFoundException, IOException {
		List<CombatSpellDefinition> list = (List<CombatSpellDefinition>) xStream.fromXML(new FileInputStream("./data/def/magic/CombatSpellDefinitions.xml"));
		for (CombatSpellDefinition definition : list) {
			combatSpellDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " combat spell definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadEquipmentDefinitions() throws FileNotFoundException, IOException {
		List<EquipmentDefinition> list = (List<EquipmentDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/EquipmentDefinitions.xml"));
	
		for (EquipmentDefinition definition : list) {
	
			equipmentDefinitions.put(definition.getId(), definition);
			int size = 0;

			if (definition.getRequirements() != null) {
				byte[] array = definition.getRequirements();
				
				
				for (int i = 0; i < array.length; i++) {
					if (array[i] > 1) {
						size++;
					}
				}
			}
			
			if (size == 0) {
				continue;
			}


			/*
			System.out.println("  {");
			System.out.println("    \"id\": " + definition.getId() + ",");
			System.out.println("    \"requirements\": [");
			byte[] array = definition.getRequirements();
			
			for (int i = 0, complete = 0; i < array.length; i++) {
				if (array[i] == 1) {
					continue;
				}
				
				System.out.println("      {");
				System.out.println("        \"level\": " + array[i] + ",");

				String name = "";

				switch (i) {
				case 0:
					name = "ATTACK";
					break;
				case 1:
					name = "DEFENCE";
					break;
				case 2:
					name = "STRENGTH";
					break;
				case 3:
					name = "HITPOINTS";
					break;
				case 4:
					name = "RANGED";
					break;
				case 5:
					name = "PRAYER";
					break;
				case 6:
					name = "MAGIC";
					break;
				case 7:
					name = "COOKING";
					break;
				case 8:
					name = "WOODCUTTING";
					break;
				case 9:
					name = "FLETCHING";
					break;
				case 10:
					name = "FISHING";
					break;
				case 11:
					name = "FIREMAKING";
					break;
				case 12:
					name = "CRAFTING";
					break;
				case 13:
					name = "SMITHING";
					break;
				case 14:
					name = "MINING";
					break;
				case 15:
					name = "HERBLORE";
					break;
				case 16:
					name = "AGILITY";
					break;
				case 17:
					name = "THIEVING";
					break;
				case 18:
					name = "SLAYER";
					break;
				case 19:
					name = "FARMING";
					break;
				case 20:
					name = "HUNTER";
					break;
				case 21:
					name = "CONSTRUCTION";
					break;
				}

				complete++;
				System.out.println("        \"skill\": \"" + name + "\"");
				System.out.println("      }" + (complete >= size ? "" : ","));
			}
			
			System.out.println("    ]");
			System.out.println("  },");
			*/
		}
		
		logger.info("Loaded " + Utility.format(list.size()) + " equipment definitions.");
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		declare();
		loadNpcDropDefinitions();
	}

	@SuppressWarnings("unchecked")
	public static void loadFoodDefinitions() throws FileNotFoundException, IOException {
		List<FoodDefinition> list = (List<FoodDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/FoodDefinitions.xml"));
		for (FoodDefinition definition : list) {
			foodDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " food definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadItemBonusDefinitions() throws FileNotFoundException, IOException {
		List<ItemBonusDefinition> list = (List<ItemBonusDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/ItemBonusDefinitions.xml"));
		for (ItemBonusDefinition definition : list) {
			itemBonusDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " item bonus definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadItemDefinitions() throws FileNotFoundException, IOException {
		List<ItemDefinition> list = (List<ItemDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/ItemDefinitions.xml"));
		for (ItemDefinition definition : list) {
			itemDefinitions.put(definition.getId(), definition);
		}

		logger.info("Loaded " + Utility.format(list.size()) + " item definitions.");
	}
	
	public static Map<Integer, ItemDefinition> getItemDefinitions() {
		return itemDefinitions;
	}
	
	public static Map<Integer, ItemBonusDefinition> getItemBonusDefinitions() {
		return itemBonusDefinitions;
	}
	
	@SuppressWarnings("unchecked")
	public static void loadNpcCombatDefinitions() throws FileNotFoundException, IOException {
		List<NpcCombatDefinition> list = (List<NpcCombatDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/NpcCombatDefinitions.xml"));
		for (NpcCombatDefinition definition : list) {
			npcCombatDefinitions.put(definition.getId(), definition);
		}

		logger.info("Loaded " + Utility.format(list.size()) + " npc combat definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadNpcDefinitions() throws FileNotFoundException, IOException {
		List<NpcDefinition> list = (List<NpcDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/NpcDefinitions.xml"));
		for (NpcDefinition definition : list) {
			npcDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " NPC definitions.");
		// dumpSizes();
	}
	
	public static Map<Integer, NpcDefinition> getNpcDefinitions() {
		return npcDefinitions;
	}
	
	public static Map<Integer, ItemDropDefinition> getMobDropDefinitions() {
		return mobDropDefinitions;
	}

	@SuppressWarnings("unchecked")
	public static void loadNpcDropDefinitions() throws FileNotFoundException, IOException {
		List<ItemDropDefinition> list = (List<ItemDropDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/ItemDropDefinitions.xml"));
		for (ItemDropDefinition def : list) {
			mobDropDefinitions.put(def.getId(), def);

			if (def.getCommon() != null && def.getCommon().getDrops() != null) {
				for (ItemDrop i : def.getCommon().getDrops()) {
					if (i.getId() != 995) {
						if (i.getMax() >= 600 || i.getMin() >= 600) {
							i.setMin((short) 100);
							i.setMax((short) 600);
						}
					}
				}
			}
		}

		for (ItemDropDefinition i : mobDropDefinitions.values()) {
			if (npcCombatDefinitions.get(i.getId()) == null) {
				mobDropDefinitions.remove(i);
			}
		}

		/*for (ItemDropDefinition i : mobDropDefinitions.values()) {
			boolean always = false;
			boolean common = false;
			boolean uncommon = false;
			boolean rare = false;

			if (i.getConstant() != null && i.getConstant().getDrops() != null) {
				always = true;
			}
			
			if (i.getCommon() != null && i.getCommon().getDrops() != null) {
				common = true;
			}
			
			if (i.getUncommon() != null && i.getUncommon().getDrops() != null) {
				uncommon = true;
			}
			
			if (i.getRare() != null && i.getRare().getDrops() != null) {
				rare = true;
			}
			
			if (!always && !common && !uncommon && !rare) {
				continue;
			}
			
			System.out.println("  {");
			System.out.println("    \"id\": " + i.getId() + ",");
			
			if (always) {
				System.out.println("	\"always\": [");
		
				for (int index = 0; index < i.getConstant().getDrops().length; index++) {
					ItemDrop drop = i.getConstant().getDrops()[index];
					System.out.println("      {");
					System.out.println("        \"itemId\": " + drop.getId() + ",");
					System.out.println("        \"minAmount\": " + drop.getMin() + ",");
					System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
					System.out.println("        \"chance\": ALWAYS");
					
					if (index + 1 == i.getConstant().getDrops().length) {
						System.out.println("      }");
					} else {
						System.out.println("      },");
					}
				}
				
				System.out.println("    ],");
			}
	
			System.out.println("	\"drops\": [");
			
			if (common) {
				for (int index = 0; index < i.getCommon().getDrops().length; index++) {
					ItemDrop drop = i.getCommon().getDrops()[index];
					System.out.println("      {");
					System.out.println("        \"itemId\": " + drop.getId() + ",");
					System.out.println("        \"minAmount\": " + drop.getMin() + ",");
					System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
					System.out.println("        \"chance\": COMMON");
					
					if (index + 1 == i.getCommon().getDrops().length && !uncommon && !rare) {
						System.out.println("      }");
					} else {
						System.out.println("      },");
					}
				}
			}
			
			if (uncommon) {
				for (int index = 0; index < i.getUncommon().getDrops().length; index++) {
					ItemDrop drop = i.getUncommon().getDrops()[index];
					System.out.println("      {");
					System.out.println("        \"itemId\": " + drop.getId() + ",");
					System.out.println("        \"minAmount\": " + drop.getMin() + ",");
					System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
					System.out.println("        \"chance\": UNCOMMON");
					
					if (index + 1 == i.getUncommon().getDrops().length && !rare) {
						System.out.println("      }");
					} else {
						System.out.println("      },");
					}
				}
			}

			if (rare) {
				for (int index = 0; index < i.getRare().getDrops().length; index++) {
					ItemDrop drop = i.getRare().getDrops()[index];
					System.out.println("      {");
					System.out.println("        \"itemId\": " + drop.getId() + ",");
					System.out.println("        \"minAmount\": " + drop.getMin() + ",");
					System.out.println("        \"maxAmount\": " + drop.getMax() + ",");
					System.out.println("        \"chance\": RARE");
					
					if (index + 1 == i.getRare().getDrops().length) {
						System.out.println("      }");
					} else {
						System.out.println("      },");
					}
				}
			}
			
			System.out.println("	]");
			System.out.println("  },");
		}*/

		logger.info("Loaded " + Utility.format(list.size()) + " npc drops.");
	}

	@SuppressWarnings("unchecked")
	public static void loadNpcSpawns() throws FileNotFoundException, IOException {
		List<NpcSpawnDefinition> list = (List<NpcSpawnDefinition>) xStream.fromXML(new FileInputStream("./data/def/npcs/NpcSpawnDefinitions.xml"));
		for (NpcSpawnDefinition def : list) {
			if (Region.getRegion(def.getLocation().getX(), def.getLocation().getY()) == null) {
				continue;
			}

			if (npcDefinitions.get(def.getId()).isAttackable() && npcCombatDefinitions.get(def.getId()) == null) {
				continue;
			}
			
			Mob m = new Mob(def.getId(), def.isWalk(), def.getLocation());
			
			if (m.getId() == 1011) {
				FlowerGame.setGambler(m);
			}

			if (def.getFace() > 0) {
				m.setFaceDir(def.getFace());
			} else {
				m.setFaceDir(-1);
			}
		}
		logger.info("Loaded " + Utility.format(list.size()) + " NPC spawns.");
	}

	@SuppressWarnings("unchecked")
	public static void loadPotionDefinitions() throws FileNotFoundException, IOException {
		List<PotionDefinition> list = (List<PotionDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/PotionDefinitions.xml"));
		for (PotionDefinition definition : list) {
			if (definition.getName() == null) {
				definition.setName(itemDefinitions.get(definition.getId()).getName());
			}
			potionDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " potion definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadRangedStrengthDefinitions() throws FileNotFoundException, IOException {
		List<RangedStrengthDefinition> list = (List<RangedStrengthDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/RangedStrengthDefinitions.xml"));
		for (RangedStrengthDefinition definition : list) {
			rangedStrengthDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " ranged strength bonus definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadRangedWeaponDefinitions() throws FileNotFoundException, IOException {
		List<RangedWeaponDefinition> list = (List<RangedWeaponDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/RangedWeaponDefinitions.xml"));
		for (RangedWeaponDefinition definition : list) {
			rangedWeaponDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " ranged weapon definitions.");
	}

	public static final void loadRareDropChances() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/def/npcs/DropChances.txt"));

			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.length() == 0 || line.startsWith("//")) {
					continue;
				}

				try {
					int id = Integer.parseInt(line.substring(0, line.indexOf(":")));
					line = line.substring(line.indexOf(":") + 1);
					int value = Integer.parseInt(line.substring(0, line.indexOf("/")));

					rareDropChances.put(id, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			reader.close();

			// remove me
			/*
			 * new File("./data/def/npcs/DropChances.txt").delete();
			 * BufferedWriter writer = new BufferedWriter(new
			 * FileWriter("./data/def/npcs/DropChances.txt")); for
			 * (Entry<Integer, Byte> i : rareDropChances.entrySet()) {
			 * writer.write(i.getKey() + ":" + i.getValue() + "//" +
			 * itemDefinitions.get(i.getKey()).getName()); writer.newLine(); }
			 * writer.close();
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Successfully loaded all rare drops.");
	}

	@SuppressWarnings("unchecked")
	public static void loadShopDefinitions() throws FileNotFoundException, IOException {
		List<ShopDefinition> list = (List<ShopDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/ShopDefinitions.xml"));
		for (ShopDefinition def : list) {
			Shop.getShops()[def.getId()] = new Shop(def.getId(), def.getItems(), def.isGeneral(), def.getName());
		}
		logger.info("Loaded " + Utility.format(list.size()) + " shops.");
	}

	@SuppressWarnings("unchecked")
	public static void loadSpecialAttackDefinitions() throws FileNotFoundException, IOException {
		List<SpecialAttackDefinition> list = (List<SpecialAttackDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/SpecialAttackDefinitions.xml"));
		for (SpecialAttackDefinition definition : list) {
			specialAttackDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " special attack definitions.");
	}

	@SuppressWarnings("unchecked")
	public static void loadWeaponDefinitions() throws FileNotFoundException, IOException {
		List<WeaponDefinition> list = (List<WeaponDefinition>) xStream.fromXML(new FileInputStream("./data/def/items/WeaponDefinitions.xml"));
		for (WeaponDefinition definition : list) {
			weaponDefinitions.put(definition.getId(), definition);
		}
		logger.info("Loaded " + Utility.format(list.size()) + " weapon definitions.");
	}
	
	public static Map<Integer, WeaponDefinition> getWeaponDefinitions() {
		return weaponDefinitions;
	}

	public static void setNotTradable(int id) {
		itemDefinitions.get(id).setUntradable();
	}

	public static void setRequirements() {
		for (Object def : equipmentDefinitions.values().toArray()) {
			EquipmentDefinition definition = (EquipmentDefinition) def;

			if (definition == null || definition.getRequirements() == null) {
				continue;
			}

			byte[][] requirements = new byte[Skills.SKILL_COUNT][2];
			int count = 0;

			for (int i = 0; i < definition.getRequirements().length; i++) {
				if (definition.getRequirements()[i] == 1) {
					continue;
				} else {
					if (count < Skills.SKILL_COUNT) {
						requirements[count][0] = (byte) i;
						requirements[count][1] = definition.getRequirements()[i];
					}
					count++;
				}
			}

			byte[][] set = new byte[count][2];

			for (int i = 0; i < count; i++) {
				if (count < Skills.SKILL_COUNT) {
					set[i][0] = requirements[i][0];
					set[i][1] = requirements[i][1];
				}
			}

			itemRequirements.put(((EquipmentDefinition) def).getId(), set);

			((EquipmentDefinition) def).setRequirements(null);
		}
	}

	public static void writeDropPreference() {
		try {
			Queue<Item> items = new PriorityQueue<Item>(42, PlayerDrops.ITEM_VALUE_COMPARATOR);

			for (ItemDefinition i : itemDefinitions.values()) {
				if (!i.isTradable() || i.getNoteId() != -1 && items.contains(new Item(i.getNoteId()))) {
					continue;
				}

				items.add(new Item(i.getId()));
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter("./DropSettings.txt"));

			Item item = null;

			while ((item = items.poll()) != null) {
				writer.write(item.getId() + ":" + item.getDefinition().getName());
				writer.newLine();
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private GameDefinitionLoader() {
	}
}
