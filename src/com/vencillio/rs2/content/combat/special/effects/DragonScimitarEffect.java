package com.vencillio.rs2.content.combat.special.effects;

import com.vencillio.rs2.content.combat.impl.CombatEffect;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Dragon Scimitar special effect
 * @author Daniel
 *
 */
public class DragonScimitarEffect implements CombatEffect {

	public final Prayer PROTECT_FROM_MAGIC = Prayer.PROTECT_FROM_MAGIC;
	public final Prayer PROTECT_FROM_RANGED = Prayer.PROTECT_FROM_RANGE;
	public final Prayer PROTECT_FROM_MELEE = Prayer.PROTECT_FROM_MELEE;
	
	@Override
	public void execute(Player player, Entity opponent) {
		
		if (opponent.isNpc()) {
			return;
		}
		
		if (player.getLastDamageDealt() > 0) {
			if (opponent.getPlayer() == null) {
				return;
			}
			
			if (opponent.getPlayer().getPrayer().active(PROTECT_FROM_MAGIC)) {
				opponent.getPlayer().getPrayer().disable(PROTECT_FROM_MAGIC);
			} else if (opponent.getPlayer().getPrayer().active(PROTECT_FROM_RANGED)) {
				opponent.getPlayer().getPrayer().disable(PROTECT_FROM_RANGED);
			} else if (opponent.getPlayer().getPrayer().active(PROTECT_FROM_MELEE)) {	
				opponent.getPlayer().getPrayer().disable(PROTECT_FROM_MELEE);
			}
			player.send(new SendMessage("You have cancelled " + opponent.getPlayer().getUsername() + "'s protection prayer."));
			opponent.getPlayer().send(new SendMessage("Your protection prayer has been cancelled by " + player.getUsername()));
		}
	}
	
}
