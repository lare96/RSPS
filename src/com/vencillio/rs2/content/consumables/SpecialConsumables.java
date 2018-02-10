package com.vencillio.rs2.content.consumables;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class SpecialConsumables {
	public static void specialFood(Player player, Item item) {
		switch (item.getId()) {
		case 3146:
			player.getClient().queueOutgoingPacket(new SendMessage("You eat the poisoned karambwan..."));
			player.getClient().queueOutgoingPacket(new SendMessage("...and it damages you!"));
			player.hit(new Hit(5, Hit.HitTypes.NONE));
			break;
		case 712:
			player.getUpdateFlags().sendForceMessage("Aaah, nothing like a nice cuppa tea!");

			break;
		case 3801:
			player.getUpdateFlags().sendAnimation(new Animation(1329));
			player.getClient().queueOutgoingPacket(new SendMessage("You chug the keg. You feel reinvigortated..."));
			player.getClient().queueOutgoingPacket(new SendMessage("...but extremely drunk too"));
			player.getSkill().deductFromLevel(0, 10);
			break;
		}
	}
}
