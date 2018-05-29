package com.vencillio.rs2.content.skill.ranged;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class BoltSpecials {

	public static void checkForBoltSpecial(Player player, Entity attacking, Hit hit) {
		Item arrow = player.getEquipment().getItems()[13];
		Item weapon = player.getEquipment().getItems()[3];

		if ((arrow == null) || (weapon == null) || (player.getCombat().getCombatType() != CombatTypes.RANGED) || (!RangedSkill.requiresArrow(player, weapon.getId()))) {
			return;
		}

		int decrease = (attacking instanceof Player && attacking.getPlayer() != null && attacking.getPlayer().getEquipment().contains(9944) ? 5 : 0);
		int random = Utility.random(230);
		
		if (random > 30 - decrease) {
			return;
		}

		switch (arrow.getId()) {
		case 9244:
			if (!attacking.isNpc()) {
				Player p = com.vencillio.rs2.entity.World.getPlayers()[attacking.getIndex()];

				if (p == null) {
					return;
				}

				Item shield = p.getEquipment().getItems()[5];

				if ((shield != null) && ((shield.getId() == 1540) || (shield.getId() == 11283))) {
					return;
				}

			}

			attacking.getUpdateFlags().sendGraphic(Graphic.lowGraphic(756, 0));
//		player.getSpecialAttack().toggleSpecial();
			break;
		case 9245:
			player.getRanged().setOnyxEffectActive(true);
			attacking.getUpdateFlags().sendGraphic(Graphic.lowGraphic(753, 0));
//			player.getSpecialAttack().toggleSpecial();
			break;
		case 9241:
			attacking.getUpdateFlags().sendGraphic(Graphic.lowGraphic(752, 0));
//			player.getSpecialAttack().toggleSpecial();
			if (Utility.randomNumber(3) == 0) {
				attacking.poison(5);
			}
			break;
		case 9243:
			attacking.getUpdateFlags().sendGraphic(Graphic.lowGraphic(758, 0));
//			player.getSpecialAttack().toggleSpecial();
			break;
			
		case 9242:
			attacking.getUpdateFlags().sendGraphic(Graphic.lowGraphic(754, 0));
			player.getRanged().setBloodForfeitEffectActive(true);
			int self_inflict_damage = (int) (player.getSkill().getLevels()[Skills.HITPOINTS] * 0.1);
		
			int max_hit = (int) (attacking.isNpc() ? attacking.getLevels()[Skills.HITPOINTS] * 0.2 : attacking.getPlayer().getSkill().getLevels()[Skills.HITPOINTS] * 0.2);
			if (max_hit > 200) {
				max_hit = 200;
			}
			
			attacking.checkForDeath();
			player.checkForDeath();
			
			attacking.hit(new Hit(max_hit));
			player.hit(new Hit(self_inflict_damage));
			
			player.getClient().queueOutgoingPacket(new SendMessage("You drain 10% of your hitpoints and 20% of your opponent's hitpoints."));
			player.getRanged().setBloodForfeitEffectActive(false);
			break;
		}
	}
}
