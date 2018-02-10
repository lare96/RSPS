package com.vencillio.rs2.content.combat.special.effects;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class ToxicBlowpipeEffect implements CombatEffect {

	@Override
	public void execute(Player player, Entity entity) {
		int damage = player.getLastDamageDealt();
		
		if (damage == 0) {
			return;
		}

		int hp = (int) (damage * 0.5D);

		if ((hp > 9) && (player.getLevels()[3] < player.getMaxLevels()[3])) {
			short[] tempHp = player.getLevels();
			tempHp[Skills.HITPOINTS] = ((short) (tempHp[Skills.HITPOINTS] + hp));
			if (player.getLevels()[3] > player.getMaxLevels()[3]) {
				hp = player.getMaxLevels()[3] - player.getLevels()[3];
				player.getLevels()[3] = player.getMaxLevels()[3];
			}
			player.getSkill().update(3);
		} else {
			hp = 0;
		}

		String message = "";

		if (hp > 0)
			message = "You regenerate " + hp + " Hitpoints.";
		else {
			return;
		}

		player.getClient().queueOutgoingPacket(new SendMessage(message));
	}
}
