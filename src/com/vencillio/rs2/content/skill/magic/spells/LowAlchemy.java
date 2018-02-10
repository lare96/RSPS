package com.vencillio.rs2.content.skill.magic.spells;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.skill.magic.Spell;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendOpenTab;

public class LowAlchemy extends Spell {
	
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
		Item coins = new Item(995, GameDefinitionLoader.getLowAlchemyValue(item));

		if (!player.getInventory().hasSpaceFor(coins)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to cast High alchemy."));
			return false;
		}

		player.getInventory().remove(item);
		player.getInventory().add(coins);

		player.getUpdateFlags().sendAnimation(712, 0);
		player.getUpdateFlags().sendGraphic(new Graphic(112, true));

		player.getClient().queueOutgoingPacket(new SendOpenTab(6));

		player.getSkill().lock(5);

		return true;
	}

	@Override
	public double getExperience() {
		return 850.5D;
	}

	@Override
	public int getLevel() {
		return 21;
	}

	@Override
	public String getName() {
		return "Low alchemy";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(554, 3), new Item(561, 1) };
	}
}
