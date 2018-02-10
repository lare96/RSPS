package com.vencillio.rs2.content;

import com.vencillio.core.util.ItemNames;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles crystal chest
 * 
 * @author Daniel
 *
 */
public class CrystalChest {

	/**
	 * Ids of key halves
	 */
	public static final Item[] KEY_HALVES = { new Item(985), new Item(987) };

	/**
	 * Crystal key Id
	 */
	public static final Item KEY = new Item(989);

	/**
	 * Creates the key
	 * 
	 * @param player
	 */
	public static void createKey(final Player player) {
		if (player.getInventory().contains(KEY_HALVES)) {
			player.getInventory().remove(KEY_HALVES[0]);
			player.getInventory().remove(KEY_HALVES[1]);
			player.getInventory().add(KEY);
			DialogueManager.sendItem1(player, "You have combined the two parts to form a key.", KEY.getId());
		}
	}

	public static final Item[] UNCOMMON_CHEST_REWARDS = { new Item(ItemNames.ZAMORAK_PLATEBODY), new Item(ItemNames.ADAMANT_PLATEBODY), new Item(ItemNames.ADAMANT_PLATELEGS), new Item(ItemNames.ADAMANT_FULL_HELM), new Item(ItemNames.ZAMORAK_PLATELEGS), new Item(ItemNames.SARADOMIN_PLATEBODY), new Item(ItemNames.SARADOMIN_PLATELEGS), new Item(ItemNames.GUTHIX_PLATEBODY), new Item(ItemNames.GUTHIX_PLATELEGS), new Item(ItemNames.ZAMORAK_FULL_HELM), new Item(ItemNames.GUTHIX_FULL_HELM), new Item(ItemNames.SARADOMIN_FULL_HELM), new Item(ItemNames.DRAGON_MED_HELM), new Item(ItemNames.DRAGON_PLATESKIRT), new Item(ItemNames.DRAGON_SQ_SHIELD), new Item(ItemNames.HELM_OF_NEITIZNOT), new Item(ItemNames.WARRIOR_HELM), new Item(ItemNames.ARCHER_HELM), new Item(ItemNames.FARSEER_HELM), new Item(ItemNames.RUNE_SCIMITAR), new Item(ItemNames.RUNE_2H_SWORD), new Item(ItemNames.DRAGON_DAGGER), new Item(ItemNames.DRAGON_SCIMITAR), new Item(ItemNames.DRAGON_MACE), new Item(ItemNames.DRAGON_BATTLEAXE),
			new Item(ItemNames.DRAGON_LONGSWORD), new Item(ItemNames.TZHAARKETEM), new Item(ItemNames.TZHAARKETOM), new Item(ItemNames.ADAMANT_SCIMITAR), new Item(ItemNames.ADAMANT_2H_SWORD), new Item(ItemNames.RUNE_PICKAXE), };

	public static final Item[] RARE_CHEST_REWARDS = { new Item(ItemNames.UNCUT_DRAGONSTONE), new Item(ItemNames.UNCUT_ONYX), new Item(ItemNames.DRAGON_SPEAR), new Item(ItemNames.DRAGON_2H_SWORD) };

	/**
	 * Chest rewards
	 */
	public static final Item[] COMMON_CHEST_REWARDS = {

			/* Armours */
			new Item(ItemNames.RUNE_FULL_HELM), new Item(ItemNames.RUNE_PLATEBODY), new Item(ItemNames.RUNE_KITESHIELD), new Item(ItemNames.RUNE_PLATELEGS), new Item(ItemNames.RUNE_PLATESKIRT), new Item(ItemNames.RUNE_BOOTS), new Item(ItemNames.RUNE_CHAINBODY), new Item(ItemNames.RUNE_CROSSBOW), new Item(ItemNames.RING_OF_RECOIL),

			/* Skilling */
			new Item(ItemNames.RAW_SWORDFISH), new Item(ItemNames.RUNE_BAR), new Item(ItemNames.RUNITE_ORE), new Item(ItemNames.GRIMY_AVANTOE), new Item(ItemNames.GRIMY_CADANTINE), new Item(ItemNames.GRIMY_DWARF_WEED), new Item(ItemNames.GRIMY_GUAM), new Item(ItemNames.GRIMY_HARRALANDER), new Item(ItemNames.GRIMY_IRIT), new Item(ItemNames.UNCUT_DIAMOND), new Item(ItemNames.UNCUT_EMERALD), new Item(ItemNames.UNCUT_RUBY), new Item(ItemNames.UNCUT_SAPPHIRE),

			/* Random */
			new Item(ItemNames.BONES), new Item(ItemNames.SPINACH_ROLL), new Item(ItemNames.TAN_CAVALIER), new Item(ItemNames.DARK_CAVALIER), new Item(ItemNames.BLACK_CAVALIER), new Item(ItemNames.BLACK_BERET), new Item(ItemNames.RED_HEADBAND), new Item(ItemNames.PIRATES_HAT), new Item(ItemNames.BROWN_HEADBAND), new Item(ItemNames.SHARK), new Item(ItemNames.MONKEY_NUTS), new Item(ItemNames.EYE_PATCH) };

	/**
	 * Searches the chest
	 * 
	 * @param player
	 * @param x
	 * @param y
	 */

	public static void searchChest(final Player player, final int x, final int y) {
		if (player.getInventory().contains(KEY)) {
			player.send(new SendMessage("You unlock the chest with your key."));
			player.getInventory().remove(KEY);
			AchievementHandler.activateAchievement(player, AchievementList.OPEN_70_CRYSTAL_CHESTS, 1);
			player.getUpdateFlags().sendAnimation(new Animation(881));
			player.getInventory().add(new Item(995, Utility.random(3200)));
			Item itemReceived;
			switch (Utility.random(50)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_CHEST_REWARDS);
				break;
			case 25:
				itemReceived = Utility.randomElement(RARE_CHEST_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_CHEST_REWARDS);
			}

			player.getInventory().addOrCreateGroundItem(itemReceived.getId(), itemReceived.getAmount(), true);
			player.send(new SendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getDefinition().getName()) + " " + itemReceived.getDefinition().getName() + " in the chest."));
			if (itemReceived.getDefinition().getGeneralPrice() < 100_000) {
				switch (Utility.random(50)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					itemReceived = Utility.randomElement(UNCOMMON_CHEST_REWARDS);
					break;
				case 25:
					itemReceived = Utility.randomElement(RARE_CHEST_REWARDS);
					break;
				default:
					itemReceived = Utility.randomElement(COMMON_CHEST_REWARDS);
				}
				player.getInventory().addOrCreateGroundItem(itemReceived.getId(), itemReceived.getAmount(), true);
				player.send(new SendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getDefinition().getName()) + " " + itemReceived.getDefinition().getName() + " in the chest."));
			}
		} else {
			player.send(new SendMessage("You need a key to open this chest."));
		}
	}

}
