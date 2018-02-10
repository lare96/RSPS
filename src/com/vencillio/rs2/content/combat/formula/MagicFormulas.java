package com.vencillio.rs2.content.combat.formula;

import com.vencillio.rs2.content.skill.magic.spells.Charge;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.content.skill.slayer.Slayer;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

/**
 * @author Valiant (http://www.rune-server.org/members/Valiant) Represents an
 *         attackers/victims rolls in magic combat
 * @since Todays Date
 */
public class MagicFormulas {

	public static double getEffectiveMagicAccuracy(Entity entity) {
		Player attacker = null;

		if (!entity.isNpc()) {
			attacker = com.vencillio.rs2.entity.World.getPlayers()[entity.getIndex()];
		} else {
			double attackBonus = 0;
			double baseAttack = 0;
			
			if (entity.getBonuses() != null) {
				attackBonus = entity.getBonuses()[3];
			}
			
			if (entity.getLevels() != null) {
				baseAttack = entity.getLevels()[6];
			}
			
			return Math.floor(baseAttack + attackBonus) + 8;
		}

		double attackBonus = attacker.getBonuses()[3];
		double baseAttack = attacker.getSkill().getLevels()[6];

		if (attacker.getPrayer().active(Prayer.MYSTIC_WILL)) {
			baseAttack += 1.05;
		} else if (attacker.getPrayer().active(Prayer.MYSTIC_LORE)) {
			baseAttack += 1.10;
		} else if (attacker.getPrayer().active(Prayer.MYSTIC_MIGHT)) {
			baseAttack *= 1.15;
		}
		return Math.floor(baseAttack + attackBonus) + 15;
	}

	public static double getMagicAttackRoll(Entity entity) {
		double specAccuracy = 1.0;
		double effectiveAccuracy = getEffectiveMagicAccuracy(entity);
		int styleBonusAttack = 0;
		effectiveAccuracy *= (1 + (styleBonusAttack) / 64);
		return (int) (effectiveAccuracy * specAccuracy);
	}

	public static double getMagicDefenceRoll(Entity entity) {
		Player blocker = null;
		if (!entity.isNpc()) {
			blocker = com.vencillio.rs2.entity.World.getPlayers()[entity.getIndex()];
		} else {
			if (entity.getBonuses() != null && entity.getLevels() != null) {
				double effectiveDefence = getEffectiveMagicDefence(entity);
				effectiveDefence += entity.getBonuses()[8];
				int level = entity.getLevels()[6];
				effectiveDefence = (int) (Math.floor(level * 1.10) + Math.floor(effectiveDefence * 0.25));
				return effectiveDefence;
			}
			return 0;
		}
		int styleBonusDefence = 0;
		double effectiveDefence = getEffectiveMagicDefence(entity);
		effectiveDefence += blocker.getBonuses()[8];
		int level = blocker.getSkill().getLevels()[6];
		effectiveDefence = (int) (Math.floor(level * 1.10) + Math.floor(effectiveDefence * 0.25));
		effectiveDefence *= (1 + (styleBonusDefence) / 64);
		return effectiveDefence;
	}

	public static double getEffectiveMagicDefence(Entity entity) {
		Player blocker = null;
		if (!entity.isNpc()) {
			blocker = com.vencillio.rs2.entity.World.getPlayers()[entity.getIndex()];
		} else {
			if (entity.getLevels() != null) {
				return Math.floor(entity.getLevels()[1]) + 8;
			}
			return 0;
		}
		double baseDefence = blocker.getSkill().getLevels()[1];
		return Math.floor(baseDefence) + 10;
	}

	/**
	 * Calculates the attackers max magical damage output
	 * 
	 * @param player
	 * @return
	 */
	public static int magicMaxHit(Player player) {
		
		int spellId = player.getMagic().getSpellCasting().getCurrentSpellId();

		if (spellId == -1) {
			return 0;
		}
		double damage = player.getMagic().getSpellCasting().getDefinition(spellId).getBaseMaxHit();
		double damageMultiplier = 1;

		Item helm = player.getEquipment().getItems()[0];

		if ((helm != null) && (helm.getId() == 15492) && (player.getCombat().getAttacking().isNpc()) && (player.getSlayer().hasTask())) {
			Mob m = com.vencillio.rs2.entity.World.getNpcs()[player.getCombat().getAttacking().getIndex()];
			if ((m != null) && (Slayer.isSlayerTask(player, m))) {
				damageMultiplier += 0.125D;
			}

		}

		if (player.getMagic().isDFireShieldEffect()) {
			return 23;
		}

		if ((spellId >= 1190) && (spellId <= 1192) && (Charge.isChargeActive(player))) {
			damageMultiplier += 0.6D;
		}

		if (player.getSkill().getLevels()[6] > player.getSkill().getLevelForExperience(6, player.getSkill().getExperience()[6]) && player.getSkill().getLevelForExperience(6, player.getSkill().getExperience()[6]) >= 95) {
			damageMultiplier += .03 * (player.getSkill().getLevels()[6] - 99);
		}
		if (player.getEquipment().getItems()[3] != null) {
			switch (player.getEquipment().getItems()[3].getId()) {
			case 20076:
			case 20074:
				damageMultiplier += 0.15;
				break;
			case 20086:
				damageMultiplier += 0.8;
				break;
			}
		}
		if (spellId > 0) {
			switch (spellId) {
			case 12037:
				damage += player.getSkill().getLevels()[6] / 10;
				break;
			}
		}
		damage *= damageMultiplier;
		return (int) damage;
	}

}
