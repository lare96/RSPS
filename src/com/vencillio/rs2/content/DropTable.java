package com.vencillio.rs2.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.definitions.ItemDropDefinition;
import com.vencillio.core.definitions.ItemDropDefinition.ItemDrop;
import com.vencillio.core.definitions.ItemDropDefinition.ItemDropTable;
import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

public class DropTable {
	
	private final static int STRING = 59901;
	
	public static void open(Player player) {
	
		clear(player);
		
		for (int i = 0; i < 50; i++) {
			player.send(new SendString("", STRING + i));
		}
		
		player.send(new SendString("", 59818));

		player.send(new SendInterface(59800));
	
	}
	
	public static void searchNpc(Player player, String name) {
		name = name.trim().toLowerCase();
		
		List<NpcDefinition> npcdefs = GameDefinitionLoader.getNpcDefinitions().values().stream().filter(npcdef -> {
			if (npcdef == null || !GameDefinitionLoader.getMobDropDefinitions().containsKey(npcdef.getId())) {
				return false;
			}
			return npcdef.isAttackable();
		}).collect(Collectors.toList());
		
		HashMap<Integer, Integer> toAdd = new HashMap<>();
		
		for (NpcDefinition def : npcdefs) {
			if (def.getName().toLowerCase().contains(name)) {
				if (toAdd.size() < 3) {
					toAdd.put(233253 + toAdd.size(), def.getId());
				} else {
					toAdd.put(233997 + toAdd.size(), def.getId());
				}
				player.send(new SendString("</col> " + def.getName(), STRING + toAdd.size() - 1));	
			}
		}
		
		for (int i = toAdd.size(); i < 50; i++) {
			player.send(new SendString("", STRING + i));
		}

		
		player.send(new SendString("</col>Monster Drop Guide - Results @red@" + toAdd.size(), 59805));

		player.getAttributes().set("DROPTABLE_SEARCH", toAdd);
	}
	
	public static void searchItem(Player player, String name) {
		name = name.trim().toLowerCase();

		HashMap<Integer, Integer> toAdd = new HashMap<>();
		
		List<NpcDefinition> npcdefs = GameDefinitionLoader.getNpcDefinitions().values().stream().filter(npcdef -> {
			if (npcdef == null || !GameDefinitionLoader.getMobDropDefinitions().containsKey(npcdef.getId())) {
				return false;
			}
			return npcdef.isAttackable();
		}).collect(Collectors.toList());
		
		for (NpcDefinition def : npcdefs) {
			ItemDropDefinition dropDef = GameDefinitionLoader.getMobDropDefinitions().get(def.getId());
			
			boolean found = false;
			
			if (dropDef.getConstant() != null && dropDef.getConstant().getDrops() != null) {
				for (ItemDrop drop : dropDef.getConstant().getDrops()) {
					if (GameDefinitionLoader.getItemDef(drop.getId()).getName().toLowerCase().contains(name)) {
						found = true;
					}
				}
			}
			
			if (!found && dropDef.getCommon() != null && dropDef.getCommon().getDrops() != null) {
				for (ItemDrop drop : dropDef.getCommon().getDrops()) {
					if (GameDefinitionLoader.getItemDef(drop.getId()).getName().toLowerCase().contains(name)) {
						found = true;
					}
				}
			}
			
			if (!found && dropDef.getUncommon() != null && dropDef.getUncommon().getDrops() != null) {
				for (ItemDrop drop : dropDef.getUncommon().getDrops()) {
					if (GameDefinitionLoader.getItemDef(drop.getId()).getName().toLowerCase().contains(name)) {
						found = true;
					}
				}
			}
			
			if (!found && dropDef.getRare() != null && dropDef.getRare().getDrops() != null) {
				for (ItemDrop drop : dropDef.getRare().getDrops()) {
					if (GameDefinitionLoader.getItemDef(drop.getId()).getName().toLowerCase().contains(name)) {
						found = true;
					}
				}
			}
			
			if (found) {
				if (toAdd.size() < 3) {
					toAdd.put(233253 + toAdd.size(), def.getId());
				} else {
					toAdd.put(233997 + toAdd.size(), def.getId());
				}
				player.send(new SendString("</col> " + def.getName(), STRING + toAdd.size() - 1));	
			}
		}
		
		player.send(new SendString("</col>Monster Drop Guide - Results @red@" + toAdd.size(), 59805));

		for (int i = toAdd.size(); i < 50; i++) {
			player.send(new SendString("", STRING + i));
		}
		
		player.getAttributes().set("DROPTABLE_SEARCH", toAdd);
	}
	
	public static void displayNpc(Player player, int npcId) {
	
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);
		
		if (npcDef == null) {
			player.send(new SendMessage("It appears this NPC is nulled! Please report it to a staff member."));
			return;
		}
		
		clear(player);
		
		ItemDropDefinition table = GameDefinitionLoader.getItemDropDefinition(npcId);
		
		if (table == null) {
			return;
		}
		
		player.monsterSelected = npcId;
		
		player.send(new SendString("</col>Name: @red@" + npcDef.getName(), 59806));
		player.send(new SendString("</col>Level: @red@" + npcDef.getLevel(), 59807));
		player.send(new SendString("", 59818));
		
		player.send(new SendMessage("@dre@Now displaying drop tables of " + npcDef.getName() + "."));
		
		Item[] items = new Item[250];

		sendBest(player, table.getConstant(), items, 0);
		sendBest(player, table.getCommon(), items, 1);
		sendBest(player, table.getUncommon(), items, 2);
		
		List<Item> rares = new ArrayList<>();
		List<Item> v_rares = new ArrayList<>();
		
		for (ItemDrop drop : table.getRare().getDrops()) {
			if (GameDefinitionLoader.getRareDropChance(drop.getId()) <= 50) {
				v_rares.add(new Item(drop.getId(), drop.getMax()));
			} else {
				rares.add(new Item(drop.getId(), drop.getMax()));
			}
		}

		Collections.sort(rares, (first, second) -> second.getAmount() * second.getDefinition().getGeneralPrice() - first.getAmount() * first.getDefinition().getGeneralPrice());
		
		for (int i = 0; i < 250; i+=5) {
			items[3 + i] = i/5 < rares.size() ? rares.get(i/5) : null;
		}

		Collections.sort(v_rares, (first, second) -> GameDefinitionLoader.getRareDropChance(first.getId()) - GameDefinitionLoader.getRareDropChance(second.getId()));
		
		for (int i = 0; i < 250; i+=5) {
			items[4 + i] = i/5 < v_rares.size() ? v_rares.get(i/5) : null;
		}
		
		player.send(new SendUpdateItems(59813, items));
		
		player.send(new SendInterface(59800));
	}
	
	private static void sendBest(Player player, ItemDropTable itemDropTable, Item[] items, int slot) {
		if (itemDropTable == null || itemDropTable.getDrops() == null) {
			for (int i = 0; i < 250; i+=5) {
				items[slot + i] = null;
			}
			return;
		}
		
		List<Item> itemList = new ArrayList<>();
		
		for (ItemDrop drop : itemDropTable.getDrops()) {
			if (drop != null) {
				itemList.add(new Item(drop.getId(), drop.getMax()));
			}
		}
		
		Collections.sort(itemList, (first, second) -> second.getAmount() * second.getDefinition().getGeneralPrice() - first.getAmount() * first.getDefinition().getGeneralPrice());
		
		for (int i = 0; i < 250; i+=5) {
			items[slot + i] = i/5 < itemList.size() ? itemList.get(i/5) : null;
		}
	}
	
	private static void clear(Player player) {
	
		player.send(new SendString("</col>Name: ", 59806));
		player.send(new SendString("</col>Level: ", 59807));
		player.send(new SendString("", 59818));
	
		player.send(new SendUpdateItems(59813, null));
	}
	
	public static void itemDetails(Player player, int itemId) {
	
		ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);
		
		if (itemDef == null) {
			player.send(new SendMessage("It appears this item is nulled! Please report it to a staff member."));
			return;
		}
		
		player.send(new SendUpdateItemsAlt(59757, itemDef.getId(), 1, 0));
		player.send(new SendString("</col>Item: @gre@" + itemDef.getName(), 59753));
		player.send(new SendString("</col>Price: @gre@" + Utility.formatPrice(itemDef.getGeneralPrice()), 59754));
		player.send(new SendString("</col>Tradeable: @gre@" + Utility.formatBoolean(itemDef.isTradable()), 59755));
		player.send(new SendString("</col>Noted: @gre@" + Utility.formatBoolean(itemDef.isNote()), 59756));
		
		player.send(new SendInterface(59750));
	}
	
	
	

}
