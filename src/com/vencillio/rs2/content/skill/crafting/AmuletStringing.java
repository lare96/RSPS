package com.vencillio.rs2.content.skill.crafting;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles amulet stringing
 * @author Daniel
 *
 */
public class AmuletStringing {
	
	
	/**
	 * Amulet Data
	 * @author Daniel
	 *
	 */
	public static enum AmuletData {
		GOLD(1673, 1692),
		SAPPHIRE(1675, 1694),
		EMERALD(1677, 1696),
		RUBY(1679, 1698),
		DIAMOND(1681, 1700),
		DRAGONSTONE(1683, 1702),
		ONYX(6579, 6581);
		
		private int amuletId, product;
		
		private AmuletData(final int amuletId, final int product) {
			this.amuletId = amuletId;
			this.product = product;
		}
		
		public int getAmuletId() {
			return amuletId;
		}
		
		public int getProduct() {
			return product;
		}
	}
	
	/**
	 * Strings the amulet
	 * @param player
	 * @param itemUsed
	 * @param usedWith
	 */
	public static void stringAmulet(final Player player, final int itemUsed, final int usedWith) {
		final int amuletId = (itemUsed == 1759 ? usedWith : itemUsed);
		for (final AmuletData a : AmuletData.values()) {
			if (amuletId == a.getAmuletId()) {
				
				//Removes the items
				player.getInventory().remove(1759, 1);
				player.getInventory().remove(amuletId, 1);
				
				//Adds the item
				player.getInventory().add(a.getProduct(), 1);
				
				//Gives experience
				player.getSkill().addExperience(Skills.CRAFTING, Utility.random(3));
				
				//Gets the name of item
				String name = GameDefinitionLoader.getItemDef(a.getProduct()).getName();
				
				//Send the message
				player.send(new SendMessage("You have have strung " + Utility.getAOrAn(name) + " " + name + "."));
				
				//Send the achievement
				AchievementHandler.activateAchievement(player, AchievementList.STRING_100_AMULETS, 1);
				
			}
		}
	}

}
