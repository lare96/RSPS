package com.vencillio.rs2.content.combat.special.specials;

import com.vencillio.rs2.content.combat.impl.Attack;
import com.vencillio.rs2.content.combat.impl.Melee;
import com.vencillio.rs2.content.combat.special.Special;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;

public class DragonClawsSpecialAttack implements Special {
	
	@Override
	public boolean checkRequirements(Player player) {
		return true;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 50;
	}

	@Override
	public void handleAttack(Player player) {
		Melee m = player.getCombat().getMelee();
		Entity a = player.getCombat().getAttacking();

		m.setAnimation(new Animation(5283));

		m.execute(a);
		int d1 = player.getLastDamageDealt();
		
		if (d1 == 0) {
			m.execute(a);
			int d2 = player.getLastDamageDealt();

			m.setAttack(new Attack(2, m.getAttack().getAttackDelay()), new Animation(5283));
			if (d2 == 0) {
				m.execute(a);
			} else {
				m.setNextDamage(d2 / 2);
				m.execute(a);
			}
		} else {
			m.setNextDamage(d1 / 2);
			m.execute(a);

			m.setAttack(new Attack(2, m.getAttack().getAttackDelay()), new Animation(5283));

			int n = player.getLastDamageDealt();
			m.setNextDamage(n / 2);
			m.execute(a);
			m.setNextDamage(n - player.getLastDamageDealt());
		}
	}
	
}
