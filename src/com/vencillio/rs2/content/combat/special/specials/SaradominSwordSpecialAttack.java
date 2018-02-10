package com.vencillio.rs2.content.combat.special.specials;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.impl.Attack;
import com.vencillio.rs2.content.combat.special.Special;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.player.Player;

public class SaradominSwordSpecialAttack implements Special {

	@Override
	public boolean checkRequirements(Player paramPlayer) {
		return true;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 100;
	}

	@Override
	public void handleAttack(Player player) {
		player.getCombat().getMagic().setNextHit(Utility.randomNumber(20));

		player.getCombat().getMagic().setAttack(new Attack(1, player.getCombat().getAttackCooldown()), null, new Graphic(1224, 0, true), new Graphic(1207, 0, true), null);
		player.getCombat().getMagic().execute(player.getCombat().getAttacking());

		if (player.getEquipment().getItems()[3].getId() == 11838) {
			player.getCombat().getMelee().setAnimation(new Animation(1132, 0));
		} else if (player.getEquipment().getItems()[3].getId() == 12809) {
			player.getCombat().getMelee().setAnimation(new Animation(1133, 0));
		}
		
		player.getCombat().getMelee().setDamageBoost(1.4);
	}

}
