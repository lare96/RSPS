package com.vencillio.rs2.content.skill.summoning.impl;

import com.vencillio.rs2.content.skill.summoning.FamiliarMob;
import com.vencillio.rs2.content.skill.summoning.FamiliarSpecial;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class SpiritWolf implements FamiliarSpecial {
	@Override
	public boolean execute(Player player, FamiliarMob mob) {
		Entity a = mob.getOwner().getCombat().getAttacking();

		if (a != null) {
			Projectile p = new Projectile(1333);

			p.setCurve(0);
			p.setStartHeight(0);
			p.setEndHeight(0);

			World.sendProjectile(p, mob, a);

			mob.face(a);

			if (!a.isNpc()) {
				player.getClient()
						.queueOutgoingPacket(
								new SendMessage(
										"This special does not effect players!"));
			} else {
				Mob m = World.getNpcs()[a.getIndex()];

				if (m != null) {
					if (m.getDefinition().getLevel() > 100) {
						player.getClient()
								.queueOutgoingPacket(
										new SendMessage(
												"The mob was too strong and resisted the special move!"));
						return true;
					}

					player.getCombat().reset();
					m.retreat();
				}
			}
		} else {
			player.getClient().queueOutgoingPacket(
					new SendMessage("You are not fighting anything."));
			return false;
		}

		return true;
	}

	@Override
	public int getAmount() {
		return 3;
	}

	@Override
	public double getExperience() {
		return 0.1D;
	}

	@Override
	public FamiliarSpecial.SpecialType getSpecialType() {
		return FamiliarSpecial.SpecialType.NONE;
	}
}
