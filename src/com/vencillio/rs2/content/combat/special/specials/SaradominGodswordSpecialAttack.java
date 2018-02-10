package com.vencillio.rs2.content.combat.special.specials;

import com.vencillio.rs2.content.combat.special.Special;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the Saradomin godsword special attack
 * @author Daniel
 *
 */
public class SaradominGodswordSpecialAttack implements Special {
	
	/**
	 * Checks if player meets requirements 
	 * @param player
	 */
	@Override
	public boolean checkRequirements(Player player) {
		return true;
	}

	/**
	 * Special attack amount being used
	 */
	@Override
	public int getSpecialAmountRequired() {
		return 50;
	}

	/**
	 * Handles the attack special
	 * @param player
	 */
	@Override
	public void handleAttack(Player player) {
		player.getCombat().getMelee().setAnimation(new Animation(7058, 0));
		player.getUpdateFlags().sendGraphic(Graphic.highGraphic(1209, 0));
	}
}
