package com.vencillio.rs2.content.combat.special.specials;

import com.vencillio.rs2.content.combat.special.Special;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the Toxic blowpipe special attack
 * @author Daniel
 *
 */
public class ToxicBlowpipeSpecialAttack implements Special {
	
	@Override
	public boolean checkRequirements(Player player) {
		if (player.getToxicBlowpipe().getBlowpipeAmmo() == null) {
			return false;
		}
		if (player.getToxicBlowpipe().getBlowpipeAmmo().getAmount() <= 0) {
			return false;
		}
		if (player.getToxicBlowpipe().getBlowpipeCharge() <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 50;
	}

	@Override
	public void handleAttack(Player player) {
		
	}
}