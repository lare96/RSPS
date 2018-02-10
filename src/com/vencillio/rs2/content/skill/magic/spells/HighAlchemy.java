package com.vencillio.rs2.content.skill.magic.spells;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.skill.magic.Spell;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendOpenTab;

public class HighAlchemy extends Spell {

	@Override
	public boolean execute(Player player) {
		if (player.getSkill().locked())
			return false;
		if (player.getAttributes().get("magicitem") == null) {
			return false;
		}

		int item = player.getAttributes().getInt("magicitem");

		if (item == 995) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot cast alchemy on this item."));
			return false;
		}

		Item coins = new Item(995, GameDefinitionLoader.getHighAlchemyValue(item));

		if (!player.getInventory().hasSpaceOnRemove(new Item(item), coins)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to cast High alchemy."));
			return false;
		}
		player.getInventory().remove(item);
		player.getInventory().add(coins);

		player.getUpdateFlags().sendAnimation(713, 0);
		player.getUpdateFlags().sendGraphic(new Graphic(113, true));

		player.getClient().queueOutgoingPacket(new SendOpenTab(6));

		player.getSkill().lock(5);
		
		AchievementHandler.activateAchievement(player, AchievementList.HIGH_ALCH_250_ITEMS, 1);

		return true;
	}

	@Override
	public double getExperience() {
		return 4450.8D;
	}

	@Override
	public int getLevel() {
		return 55;
	}

	@Override
	public String getName() {
		return "High alchemy";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(554, 5), new Item(561, 1) };
	}
}
