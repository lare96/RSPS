package com.vencillio.rs2.content.skill.magic.effects;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class TeleBlockEffect implements CombatEffect {

	@Override
	public void execute(Player p, Entity e) {
		if (!e.isNpc()) {
			Player p2 = com.vencillio.rs2.entity.World.getPlayers()[e.getIndex()];
			if (p2 == null) {
				return;
			}
			if (p2.getPrayer().active(Prayer.PROTECT_FROM_MAGIC)) {
				p2.getClient().queueOutgoingPacket(new SendMessage("@dre@You have been half teleblocked."));
				e.teleblock(150);
				return;
			} else {
				p2.getClient().queueOutgoingPacket(new SendMessage("@dre@You have been full teleblocked."));
				e.teleblock(300);
				return;
			}
		} else {
			e.teleblock(300);
			return;
		}
	}
}
