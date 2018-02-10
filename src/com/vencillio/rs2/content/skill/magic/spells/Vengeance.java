package com.vencillio.rs2.content.skill.magic.spells;

import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.skill.magic.Spell;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Vengeance extends Spell {
	
	public static final int DELAY = 30000;
	public static final double RECOIL = 0.55D;

	public static void recoil(Player player, Hit hit) {
		if (hit.getDamage() <= 0) {
			return;
		}

		int recoil = (int) (hit.getDamage() * 0.55D);

		hit.getAttacker().hit(new Hit(player, recoil, Hit.HitTypes.NONE));
		player.getUpdateFlags().sendForceMessage("Taste vengeance!");
		player.getMagic().deactivateVengeance();
	}

	@Override
	public boolean execute(Player player) {
		if (player.getMagic().isVengeanceActive()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You have already vengeance casted!"));
			return false;
		}
		if (System.currentTimeMillis() - player.getMagic().getLastVengeance() < 30000L) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can only cast vengeance once every 30 seconds."));
			return false;
		}
		player.getUpdateFlags().sendAnimation(4410, 0);
		player.getUpdateFlags().sendGraphic(Graphic.highGraphic(726, 0));
		player.getMagic().activateVengeance();
		AchievementHandler.activateAchievement(player, AchievementList.CAST_VENGEANCE_350_TIMES, 1);
		return true;
	}

	@Override
	public double getExperience() {
		return 112.0D;
	}

	@Override
	public int getLevel() {
		return 94;
	}

	@Override
	public String getName() {
		return "Vengeance";
	}

	@Override
	public Item[] getRunes() {
		return new Item[] { new Item(9075, 4), new Item(557, 10), new Item(560, 2) };
	}
}
