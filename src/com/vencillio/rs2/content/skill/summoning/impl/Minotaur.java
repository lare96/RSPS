package com.vencillio.rs2.content.skill.summoning.impl;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.skill.summoning.FamiliarMob;
import com.vencillio.rs2.content.skill.summoning.FamiliarSpecial;
import com.vencillio.rs2.entity.player.Player;

public class Minotaur implements FamiliarSpecial {
	@Override
	public boolean execute(Player player, FamiliarMob mob) {
		int max = 13;

		switch (mob.getData().ordinal()) {
		case 63:
			max = 19;
			break;
		case 52:
			max = 16;
			break;
		}

		mob.getCombat().setCombatType(CombatTypes.MAGIC);
		mob.getAttributes().set("summonfammax", Integer.valueOf(max));

		return true;
	}

	@Override
	public int getAmount() {
		return 6;
	}

	@Override
	public double getExperience() {
		return 1.0D;
	}

	@Override
	public FamiliarSpecial.SpecialType getSpecialType() {
		return FamiliarSpecial.SpecialType.COMBAT;
	}
}
