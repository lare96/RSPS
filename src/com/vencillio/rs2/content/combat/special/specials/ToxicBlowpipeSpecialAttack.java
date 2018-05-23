package com.vencillio.rs2.content.combat.special.specials;

import com.vencillio.rs2.content.combat.impl.Ranged;
import com.vencillio.rs2.content.combat.special.Special;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the Toxic blowpipe special attack
 * @author Daniel
 *
 */
public class ToxicBlowpipeSpecialAttack implements Special {
	
	@Override
	public boolean checkRequirements(Player player) {
		System.out.println("In check reqs blowpipe");
		if (player.getToxicBlowpipe().getBlowpipeAmmo() == null) {
			return false;
		}
		if (player.getToxicBlowpipe().getBlowpipeAmmo().getAmount() <= 0) {
			return false;
		}
		if (player.getToxicBlowpipe().getBlowpipeCharge() <= 0) {
			return false;
		}
		System.out.println("Returned true");
		return true;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 50;
	}

	@Override
	public void handleAttack(Player player) {
		Ranged r = player.getCombat().getRanged();

		r.setStart(new Graphic(256, 5, true));
		r.setAnimation(new Animation(1074, 0));
		r.setProjectile(new Projectile(249));
		r.setStartGfxOffset((byte) 1);

		r.getProjectile().setDelay(35);

		r.execute(player.getCombat().getAttacking());

		r.setStartGfxOffset((byte) 0);
		r.setProjectileOffset(0);

		r.setProjectile(new Projectile(249));
	}
}