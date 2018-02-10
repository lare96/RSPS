package com.vencillio.rs2.content.combat.special.effects;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class AbyssalWhipEffect implements CombatEffect {
	
	@Override
	public void execute(Player p, Entity e) {
		if (!e.isNpc()) {
			Player p2 = com.vencillio.rs2.entity.World.getPlayers()[e.getIndex()];
			if (p2 == null) {
				return;
			}
			if (p2.getRunEnergy().getEnergy() >= 4) {
				int absorb = (int) (p2.getRunEnergy().getEnergy() * 0.25D);
				p2.getRunEnergy().deduct(absorb);
				p.getRunEnergy().add(absorb);
				p.getClient().queueOutgoingPacket(new SendMessage("You absorb 25% of your opponents energy."));
				p2.getClient().queueOutgoingPacket(new SendMessage("25% of your energy has been absorbed!"));
			}
		}
	}
}
