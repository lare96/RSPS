package com.vencillio.rs2.content.skill.magic.effects;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class IceBlitzEffect implements CombatEffect {
	@Override
	public void execute(Player p, Entity e) {
		if (!e.isNpc() && !e.isFrozen()) {
			Player p2 = com.vencillio.rs2.entity.World.getPlayers()[e.getIndex()];
			if (p2 == null) {
				return;
			}
			p2.getClient().queueOutgoingPacket(
					new SendMessage("You have been frozen."));
		}
		e.freeze(15, 5);
	}
}
