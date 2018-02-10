package com.vencillio.rs2.content.combat.formula;

import com.vencillio.core.definitions.RangedWeaponDefinition;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.content.skill.slayer.Slayer;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

/**
 * @author Valiant (http://www.rune-server.org/members/Valiant)
 * @information Represents an attackers/victims rolls in ranged combat
 */
public class RangeFormulas {

	/**
	 * Calculates the victims defence to ranged attacks
	 * 
	 * @param player
	 * @return
	 */
	public static int calculateRangeDefence(Entity defending) {
		Player defender = null;

		if (!defending.isNpc()) {
			defender = World.getPlayers()[defending.getIndex()];
		} else {
			if (defending.getBonuses() != null && defending.getLevels() != null) {
				return defending.getLevels()[1] + defending.getBonuses()[9] + (defending.getBonuses()[9] / 2);
			}
			return 0;
		}

		int defenceLevel = defender.getSkill().getLevels()[1];
		if (defender.getPrayer().active(Prayer.THICK_SKIN)) {
			defenceLevel += defender.getMaxLevels()[1] * 0.05;
		} else if (defender.getPrayer().active(Prayer.ROCK_SKIN)) {
			defenceLevel += defender.getMaxLevels()[1] * 0.1;
		} else if (defender.getPrayer().active(Prayer.STEEL_SKIN)) {
			defenceLevel += defender.getMaxLevels()[1] * 0.15;
		} else if (defender.getPrayer().active(Prayer.CHIVALRY)) {
			defenceLevel += defender.getMaxLevels()[1] * 0.2;
		} else if (defender.getPrayer().active(Prayer.PIETY)) {
			defenceLevel += defender.getMaxLevels()[1] * 0.25;
		}
		return defenceLevel + defender.getBonuses()[9] + (defender.getBonuses()[9] / 2);
	}

	/**
	 * 
	 * Calculates the attackers attack
	 * 
	 * @param c
	 * @return
	 */
	public static int calculateRangeAttack(Entity entity) {

		Player attacker = null;

		if (!entity.isNpc()) {
			attacker = com.vencillio.rs2.entity.World.getPlayers()[entity.getIndex()];

			if ((attacker != null) && (ItemCheck.hasDFireShield(attacker)) && (attacker.getMagic().isDFireShieldEffect())) {
				return 100;
			}

		} else {
			if (entity.getLevels() != null) {
				int rangeLevel = entity.getLevels()[4];
				return (int) (rangeLevel + (entity.getBonuses()[4] * 1.25));
			}
			return 0;
		}

		double rangeLevel = attacker.getSkill().getLevels()[4];
		if (attacker.getSpecialAttack().isInitialized()) {
			rangeLevel *= getRangedSpecialAccuracyModifier(attacker);
		}
		if (ItemCheck.wearingFullVoidRanged(attacker)) {
			rangeLevel += attacker.getMaxLevels()[4] * 5.5;
		}
		if (attacker.getPrayer().active(Prayer.SHARP_EYE)) {
			rangeLevel *= 1.05;
		} else if (attacker.getPrayer().active(Prayer.HAWK_EYE)) {
			rangeLevel *= 1.10;
		} else if (attacker.getPrayer().active(Prayer.EAGLE_EYE)) {
			rangeLevel *= 1.15;
		}
		if (attacker.getSpecialAttack().isInitialized()) {
			if (ItemCheck.wearingFullVoidRanged(attacker)) {
				rangeLevel *= 5.50;
			}
		}
		return (int) (rangeLevel + (attacker.getBonuses()[4] * 3.50));
	}

	public static final double[][] RANGED_PRAYER_MODIFIERS = { { 3.0D, 0.05D }, { 11.0D, 0.1D }, { 19.0D, 0.15D } };

	public static int getEffectiveRangedStrength(Player player) {
		Item weapon = player.getEquipment().getItems()[3];

		if ((weapon == null) || (weapon.getRangedDefinition() == null)) {
			return 0;
		}
		
		if (weapon.getId() == 12926) {
			if (player.getToxicBlowpipe().getBlowpipeAmmo() == null) {
				return 0;
			}
			return player.getToxicBlowpipe().getBlowpipeAmmo().getRangedStrengthBonus() + 40;
		}

		int rStr = player.getBonuses()[12];

		if ((weapon.getRangedDefinition().getType() == RangedWeaponDefinition.RangedTypes.THROWN) || (weapon.getRangedDefinition().getArrows() == null) || (weapon.getRangedDefinition().getArrows().length == 0)) {
			rStr = weapon.getRangedStrengthBonus();
		} else {
			Item ammo = player.getEquipment().getItems()[13];
			if (ammo != null) {
				rStr = ammo.getRangedStrengthBonus();
			}
		}

		return rStr;
	}

	public static int getRangedMaxHit(Player player) {
		double pBonus = 1.0D;
		int vBonus = 0;
		int sBonus = 0;

		if (ItemCheck.wearingFullVoidRanged(player)) {
			vBonus *= 80.0;
		}

		switch (player.getEquipment().getAttackStyle()) {
		case ACCURATE:
			sBonus = 3;
			break;
		case AGGRESSIVE:
			sBonus = 2;
			break;
		case DEFENSIVE:
			sBonus = 1;
			break;
		default:
			break;
		}
		
		for (int i = 0; i < RANGED_PRAYER_MODIFIERS.length; i++) {
			if (player.getPrayer().active(Prayer.values()[(int) RANGED_PRAYER_MODIFIERS[i][0]])) {
				pBonus += RANGED_PRAYER_MODIFIERS[i][1];
				break;
			}
		}

		int str = player.getSkill().getLevels()[4];
		double eS = (int) (str * pBonus + sBonus + vBonus);
		int rngStr = getEffectiveRangedStrength(player);
		double base = 5.0D + (eS + 8.0D) * (rngStr + 64) / 64.0D;

		if (player.getSpecialAttack().isInitialized()) {
			base = (int) (base * getRangedSpecialModifier(player));
		}

		Item helm = player.getEquipment().getItems()[0];

		if ((helm != null) && (helm.getId() == 15492) && (player.getCombat().getAttacking().isNpc()) && (player.getSlayer().hasTask())) {
			Mob m = com.vencillio.rs2.entity.World.getNpcs()[player.getCombat().getAttacking().getIndex()];
			if ((m != null) && (Slayer.isSlayerTask(player, m))) {
				base += base * 0.125D;
			}

		}

		return (int) base / 11;
	}

	public static double getRangedSpecialAccuracyModifier(Player player) {
		Item weapon = player.getEquipment().getItems()[3];
		Item arrow = player.getEquipment().getItems()[13];

		if (weapon == null) {
			return 1.0D;
		}

		switch (weapon.getId()) {
		case 11785:
			return 2D;
		case 12926:
			return 1.4D;
		case 11235:
			if (arrow.getId() == 11212) {
				return 1.5D;
			}
			return 1.3D;
		case 13883:
		case 15241:
			return 1.2D;
		case 9185:
			if (arrow.getId() == 9243)
				return 1.15D;
			if (arrow.getId() == 9244)
				return 1.65D;
			if (arrow.getId() == 9245) {
				return 1.15D;
			}
			return 1.0D;
		}

		return 1.0D;
	}

	public static double getRangedSpecialModifier(Player player) {
		Item weapon = player.getEquipment().getItems()[3];
		Item arrow = player.getEquipment().getItems()[13];

		if (weapon == null) {
			return 1.0D;
		}

		switch (weapon.getId()) {
		case 12926:
			return 1.5D;
		case 11235:
			if (arrow.getId() == 11212) {
				return 1.5D;
			}
			return 1.3D;
		case 13883:
		case 15241:
			return 1.2D;
		case 11785:
		case 9185:
			if (arrow.getId() == 9243)
				return 1.15D;
			if (arrow.getId() == 9244)
				return 1.45D;
			if (arrow.getId() == 9245) {
				return 1.15D;
			}
			return 1.0D;
		}

		return 1.0D;
	}

}