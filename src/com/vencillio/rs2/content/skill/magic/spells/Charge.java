package com.vencillio.rs2.content.skill.magic.spells;

import com.vencillio.rs2.content.skill.magic.Spell;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Charge extends Spell {

	public static boolean isChargeActive(Player p) {
		return (p.getAttributes().get("charge") != null) && (System.currentTimeMillis() - ((Long) p.getAttributes().get("charge")).longValue() < 420000L);
	}

	@Override
	public boolean execute(Player player) {
		if (player.getAttributes().get("charge") != null) {
			if (System.currentTimeMillis() - ((Long) player.getAttributes().get("charge")).longValue() < 420000L) {
				player.getClient().queueOutgoingPacket( new SendMessage( "You are still affected by a magical charge."));
				return false;
			}
			player.getAttributes().remove("charge");
		}

		if (player.getAttributes().get("chargeCoolDown") != null) {
			if (System.currentTimeMillis() - ((Long) player.getAttributes().get("chargeCoolDown")).longValue() < 60000L) {
				player.getClient() .queueOutgoingPacket( new SendMessage( "You must wait atleast 1 minute to cast this spell again."));
				return false;
			}
		} else
			player.getAttributes().remove("chargeCoolDown");

		player.getAttributes().set("charge", Long.valueOf(System.currentTimeMillis()));
		player.getAttributes().set("chargeCoolDown", Long.valueOf(System.currentTimeMillis()));

		player.getUpdateFlags().sendGraphic(Graphic.highGraphic(308, 5));
		player.getUpdateFlags().sendAnimation(new Animation(811));
		player.getClient().queueOutgoingPacket( new SendMessage("You feel charged with magic power."));
		return false;
	}

	@Override
	public double getExperience() {
		return 180.0D;
	}

	@Override
	public int getLevel() {
		return 80;
	}

	@Override
	public String getName() {
		return "Charge";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(565, 3), new Item(554, 3), new Item(556, 3) };
	}
}
