package com.vencillio.rs2.content.membership;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.core.util.chance.Chance;
import com.vencillio.core.util.chance.WeightedChance;
import com.vencillio.core.util.chance.WeightedObject;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

/**
 * Membership Mystery Box
 * @author Daniel
 */
public class MysteryBoxMinigame {

	/**
	 * The expensive amount
	 */
	private final static int EXPENSIVE_AMOUNT = 1_000_000;
	
	/**
	 * Credits required for playing
	 */
	private final static int CREDITS_REQUIRED = 2;
	
	/**
	 * The interface identification 
	 */
	private final static int INTERFACE_ID = 17000;
	
	/**
	 * The container identification
	 */
	private final static int CONTAINER_ID = 17002;
	
	/**
	 * The message color
	 */
	private final static String MESSAGE_COLOR = "<col=255>";
	
	/**
	 * All available items to win
	 */
	public static Chance<Item> available = new Chance<Item>(Arrays.asList(
			/* 
			 * Common items
			 */
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1079, 1)), //Rune platelegs
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1093, 1)), //Rune plateskirt
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1113, 1)), //Rune chainbody
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1127, 1)), //Rune platebody
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1147, 1)), //Rune med helm
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1163, 1)), //Rune full helm
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1185, 1)), //Rune sq shield
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1201, 1)), //Rune kiteshield
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1213, 1)), //Rune dagger
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4131, 1)), //Rune boots
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3476, 1)), //Rune plateskirt (g)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3477, 1)), //Rune plateskirt (t)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7336, 1)), //Rune shield (h1)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7342, 1)), //Rune shield (h2)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7348, 1)), //Rune shield (h3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7354, 1)), //Rune shield (h4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7360, 1)), //Rune shield (h5)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10286, 1)), //Rune helm (h1)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10288, 1)), //Rune helm (h2)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10290, 1)), //Rune helm (h3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10292, 1)), //Rune helm (h4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10294, 1)), //Rune helm (h5)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(560, 350)), //Death rune
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(565, 350)), //Blood rune
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(561, 500)), //Nature rune
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(892, 500)), //Rune arrow
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(868, 175)), //Rune knife
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2491, 1)), //Black d'hide vamb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2497, 1)), //Black d'hide chaps
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2503, 1)), //Black d'hide body
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12871, 1)), //Black dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12869, 1)), //Red dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12867, 1)), //Blue dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12865, 1)), //Green dragonhide set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1725, 1)), //Amulet of strength
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1712, 1)), //Amulet of glory(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10602, 1)), //Mystic hat (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(10601, 1)), //Mystic hat
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4091, 1)), //Mystic robe top
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4105, 1)), //Mystic gloves (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4103, 1)), //Mystic robe bottom (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4113, 1)), //Mystic robe bottom (light)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4111, 1)), //Mystic robe top (light)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4101, 1)), //Mystic robe top (dark)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(392, 55)), //Manta ray
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11937, 25)), //Dark crab
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(158, 5)), //Super strength(3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2441, 5)), //Super strength(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2437, 5)), //Super attack(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2443, 5)), //Super defence(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(166, 5)), //Super defence(2)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3027, 5)), //Super restore(3)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3025, 5)), //Super restore(4)
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(13066, 1)), //Super potion set
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6688, 5)), //Saradomin brew(3)
			
			/*
			 * Uncommon items
			 */
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1149, 1)), //Dragon med helm
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1187, 1)), //Dragon sq shield
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1215, 1)), //Dragon dagger
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1231, 1)), //Dragon dagger(p)
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1249, 1)), //Dragon spear
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1305, 1)), //Dragon longsword
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1377, 1)), //Dragon battleaxe
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1434, 1)), //Dragon mace
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1615, 1)), //Dragonstone
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1631, 1)), //Uncut dragonstone
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1645, 1)), //Dragonstone ring
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4087, 1)), //Dragon platelegs
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4585, 1)), //Dragon plateskirt
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(7158, 1)), //Dragon 2h sword

			/*
			 * Rare items
			 */
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4980, 1)), //Verac's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4986, 1)), //Verac's flail 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4998, 1)), //Verac's plateskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4992, 1)), //Verac's brassard 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4956, 1)), //Torag's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4962, 1)), //Torag's hammers 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4968, 1)), //Torag's platebody 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4974, 1)), //Torag's platelegs 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4932, 1)), //Karil's coif 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4938, 1)), //Karil's crossbow 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4944, 1)), //Karil's leathertop 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4950, 1)), //Karil's leatherskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4908, 1)), //Guthan's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4914, 1)), //Guthan's warspear 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4920, 1)), //Guthan's platebody 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4926, 1)), //Guthan's chainskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4860, 1)), //Ahrim's hood 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4866, 1)), //Ahrim's staff 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4872, 1)), //Ahrim's robetop 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4878, 1)), //Ahrim's robeskirt 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4884, 1)), //Dharok's helm 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4890, 1)), //Dharok's greataxe 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4896, 1)), //Dharok's platebody 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4902, 1)), //Dharok's platelegs 0
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11840, 1)), //Dragon boots
			
			/*
			 * Very rare items
			 */
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6585, 1)), //Amulet of fury
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11283, 1)), //Dragonfire shield
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11335, 1)), //Dragon full helm
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(3140, 1)), //Dragon chainbody
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12829, 1)), //Spirit shield
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6731, 1)), //Seers ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6733, 1)), //Archers ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6735, 1)), //Warrior ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6737, 1)), //Berserker ring
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12922, 1)), //Tanzanite fang
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12932, 1)), //Magic fang
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(6570, 1)), //Fire cape
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12795, 1)) //Steam battlestaff

	));
	
	/**
	 * Opens the Mystery Box
	 * @param player
	 */
	public static void open(Player player) {
		player.send(new SendString("", 17015));	
		player.send(new SendString("</col>Credits: @gre@" + Utility.format(player.getCredits()), 17006));
		player.send(new SendString("Mystery Box is a gambling minigame where you can bet @gre@" + CREDITS_REQUIRED + " </col>credits.", 17008));
		player.send(new SendUpdateItemsAlt(CONTAINER_ID, -1, 0, 0));
		player.send(new SendInterface(INTERFACE_ID));
	}
	
	/**
	 * Check if player can play the Mystery Box
	 * @param player
	 * @return
	 */
	public static boolean can(Player player) {
		if (player.getInterfaceManager().main != INTERFACE_ID) {
			player.send(new SendRemoveInterfaces());
			return false;
		}
		if (PlayerConstants.isPlayer(player)) {
			player.send(new SendMessage(MESSAGE_COLOR + "You must be a member to do this!"));
			return false;
		}
		if (player.getCredits() < CREDITS_REQUIRED) {
			player.send(new SendMessage(MESSAGE_COLOR + "You do not have enough credits to do this!"));
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.send(new SendMessage(MESSAGE_COLOR + "You do not have enough inventory spaces to do this!"));
			return false;
		}
		if (player.playingMB) {
			player.send(new SendMessage(MESSAGE_COLOR + "Please wait before doing this!"));
			return false;
		}
		return true;
	}
	
	/**
	 * Handles playing the Mystery Box
	 * @param player
	 */
	public static void play(Player player) {
		if (!can(player)) {
			return;
		}
		
		player.playingMB = true;
		player.setCredits(player.getCredits() - CREDITS_REQUIRED);
		player.send(new SendString("</col>Credits: @gre@" + Utility.format(player.getCredits()), 17006));
		TaskQueue.queue(new Task(player, 1, true) {
			int ticks = 10;
			int cycles = 0;
			
			@Override
			public void execute() {
				Item item = available.nextObject().get(); 
				player.send(new SendUpdateItemsAlt(CONTAINER_ID, item.getId(), item.getAmount(), 0));
				if (cycles++ == ticks) {
					reward(player, item);
					stop();
				}
			}
			@Override
			public void onStop() {
				player.playingMB = false;
			}
		});	
	}
	
	/**
	 * The reward for playing Mystery Box
	 * @param player
	 * @param itemWon
	 */
	public static void reward(Player player, Item itemWon) {
		ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemWon.getId());
	
		player.send(new SendMessage(MESSAGE_COLOR + "Congratulations! You have won " + Utility.getAOrAn(itemDef.getName()) + " " + itemDef.getName() + "!"));
		player.send(new SendString("Won: " + itemWon.getDefinition().getName() + "!", 17015));
		player.getInventory().add(itemWon);
		
		if (itemWon.getDefinition().getGeneralPrice() >= EXPENSIVE_AMOUNT) {
			World.sendGlobalMessage("[ " + MESSAGE_COLOR + "Mystery Box </col>] " + MESSAGE_COLOR + player.determineIcon(player) + " " + player.getUsername() + "</col> has just won " + Utility.getAOrAn(itemDef.getName()) + MESSAGE_COLOR +  " " + itemDef.getName() + "</col>!");
		}
	}
	
	/**
	 * Debug
	 * @param args
	 */
	public static void main(String[] args) {
		int common = 0;
		int uncommon = 0;
		int rare = 0;
		int very_rare = 0;
		double trials = 1_000_000.0;
		
		for (int i = 0; i < trials; i++) {
			WeightedObject<Item> item = available.nextObject();
			switch ((int) item.getWeight()) {
			
			case 10:
				common++;
				break;
				
			case 7:
				uncommon++;
				break;
				
			case 3:
				rare++;
				break;
				
			case 1:
				very_rare++;
				break;
			}
		}
		DecimalFormat formatter = new DecimalFormat("#.##");
		formatter.setRoundingMode(RoundingMode.DOWN);
		System.out.println("runs: " + trials);
		trials = (common + uncommon + rare + very_rare);
		System.out.println(formatter.format(common*100/trials) + "% - common: " + Utility.format(common));
		System.out.println(formatter.format(uncommon*100/trials) + "% - uncommon: " + Utility.format(uncommon));
		System.out.println(formatter.format(rare*100/trials) + "% - rares: " + Utility.format(rare));
		System.out.println(formatter.format(very_rare*100/trials) + "% - very rares: " + Utility.format(very_rare));
	}

}
