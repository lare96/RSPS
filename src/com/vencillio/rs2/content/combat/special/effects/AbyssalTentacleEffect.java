package com.vencillio.rs2.content.combat.special.effects;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the Abyssal Tentacle Whip Effect
 * @author Daniel
 *
 */
public class AbyssalTentacleEffect implements CombatEffect {
	
	@Override
	public void execute(Player player, Entity entity) {
		if (!entity.isNpc()) {
			Player p2 = com.vencillio.rs2.entity.World.getPlayers()[entity.getIndex()];
			if (p2 == null) {
				return;
			}
			p2.freeze(10, 5);
			p2.getUpdateFlags().sendGraphic(new Graphic(181));
			if (Utility.random(100) < 50) {
				p2.poison(4);
			}
		}
	}
	
}
