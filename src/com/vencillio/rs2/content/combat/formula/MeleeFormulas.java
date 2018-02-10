package com.vencillio.rs2.content.combat.formula;

import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.content.skill.slayer.Slayer;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Equipment;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobConstants;
import com.vencillio.rs2.entity.player.Player;

/**
 * @author Valiant (http://www.rune-server.org/members/Valiant) Represents an
 *         attackers/victims rolls in melee combat
 * @since Todays Date
 */
public class MeleeFormulas {

	public static double getDefenceRoll(Entity attacking, Entity defending) {
		Player blocker = null;
		if (!defending.isNpc()) {
			blocker = com.vencillio.rs2.entity.World.getPlayers()[defending.getIndex()];
		} else {
			if (defending.getBonuses() != null) {
				return getEffectiveDefence(defending) + defending.getBonuses()[attacking.getAttackType().ordinal()];
			}
			return getEffectiveDefence(defending);
		}
		double effectiveDefence = getEffectiveDefence(defending);
		effectiveDefence += blocker.getBonuses()[5 + attacking.getAttackType().ordinal()] - 10;
		int styleBonusDefence = 0;
		if (blocker.getEquipment().getAttackStyle() == Equipment.AttackStyles.ACCURATE)
			styleBonusDefence += 3;
		else if (blocker.getEquipment().getAttackStyle() == Equipment.AttackStyles.CONTROLLED) {
			styleBonusDefence += 1;
		}
		effectiveDefence *= (1 + (styleBonusDefence) / 64);
		if (ItemCheck.wearingFullBarrows(blocker, "Verac")) {
			effectiveDefence *= 0.75;
		}
		return effectiveDefence;
	}

	public static double getEffectiveDefence(Entity entity) {
		Player blocker = null;
		if (!entity.isNpc()) {
			blocker = com.vencillio.rs2.entity.World.getPlayers()[entity.getIndex()];
		} else {
			if (entity.getLevels() != null) {
				return entity.getLevels()[1] + 8;
			}
			return 8;
		}
		double baseDefence = blocker.getSkill().getLevels()[1];
		if (blocker.getPrayer().active(Prayer.THICK_SKIN)) {
			baseDefence += 0.5;
		} else if (blocker.getPrayer().active(Prayer.ROCK_SKIN)) {
			baseDefence += 0.7;
		} else if (blocker.getPrayer().active(Prayer.STEEL_SKIN)) {
			baseDefence += 1.3;
		} else if (blocker.getPrayer().active(Prayer.CHIVALRY)) {
			baseDefence += 1.14;
		} else if (blocker.getPrayer().active(Prayer.PIETY)) {
			baseDefence *= 1.18;
		}
		return Math.floor(baseDefence) + 8;
	}

	public static double getAttackRoll(Entity entity) {
		Player attacker = null;

		if (!entity.isNpc()) {
			attacker = com.vencillio.rs2.entity.World.getPlayers()[entity.getIndex()];
			
			if (attacker == null) {
				return getEffectiveAccuracy(entity);
			}
		} else {
			return getEffectiveAccuracy(entity);
		}

		double specAccuracy = getSpecialAccuracy(attacker);
		double effectiveAccuracy = getEffectiveAccuracy(entity);
		int styleBonusAttack = 0;

		if (attacker.getEquipment().getAttackStyle() == Equipment.AttackStyles.ACCURATE)
			styleBonusAttack = 3;
		else if (attacker.getEquipment().getAttackStyle() == Equipment.AttackStyles.CONTROLLED) {
			styleBonusAttack = 1;
		}
		effectiveAccuracy *= (1 + (styleBonusAttack) / 64);
		
		if (ItemCheck.wearingFullBarrows(attacker, "Dharok")) {
			effectiveAccuracy *= 2.30;
		}
		return (int) (effectiveAccuracy * specAccuracy);
	}

	public static double getEffectiveAccuracy(Entity entity) {
		double attackBonus;
		double baseAttack;
		if (!entity.isNpc()) {
			Player attacker = World.getPlayers()[entity.getIndex()];
			if (attacker == null) {
				return 0.0;
			}
			attackBonus = attacker.getBonuses()[attacker.getAttackType().ordinal()];
			baseAttack = attacker.getLevels()[0];
			
			if (attacker.getPrayer().active(Prayer.CLARITY_OF_THOUGHT)) {
				baseAttack += 1.05;
			} else if (attacker.getPrayer().active(Prayer.IMPROVED_REFLEXES)) {
				baseAttack += 1.10;
			} else if (attacker.getPrayer().active(Prayer.INCREDIBLE_REFLEXES)) {
				baseAttack += 1.15;
			} else if (attacker.getPrayer().active(Prayer.CHIVALRY)) {
				baseAttack += 1.20;
			} else if (attacker.getPrayer().active(Prayer.PIETY)) {
				baseAttack *= 1.23;
			}
		} else {
			if (entity.getBonuses() != null) {
				attackBonus = entity.getBonuses()[entity.getAttackType().ordinal()];
			} else {
				attackBonus = 0;
			}
			
			if (entity.getLevels() != null) {
				baseAttack = entity.getLevels()[0];
			} else {
				baseAttack = 0;
			}
		}

		return Math.floor(baseAttack + attackBonus) + 16;
	}

	/**
	 * Calculates the attackers base damage output
	 * 
	 * @param player
	 * @param special
	 * @return the value
	 */
	public static double calculateBaseDamage(Player player) {

		Entity defending = player.getCombat().getAttacking();

		double base = 0;
		double effective = getEffectiveStr(player);
		double specialBonus = getSpecialStr(player);
		double strengthBonus = player.getBonuses()[10];

		base = (13 + effective + (strengthBonus / 8) + ((effective * strengthBonus) / 64)) / 10;

		if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT] != null) {
			switch (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId()) {
			case 4718:
			case 4886:
			case 4887:
			case 4888:
			case 4889:
				if (ItemCheck.wearingFullBarrows(player, "Dharok")) {
					int maximumHitpoints = player.getMaxLevels()[Skills.HITPOINTS];
					int currentHitpoints = player.getLevels()[Skills.HITPOINTS];
					double dharokEffect = ((maximumHitpoints - currentHitpoints) * 0.01) + 1.3;
					base *= dharokEffect;
					
				}
			}
		}

		Item helm = player.getEquipment().getItems()[0];

		if (((helm != null) && (helm.getId() == 8921)) || ((helm != null) && (helm.getId() == 15492)) || ((helm != null) && (helm.getId() == 13263) && (defending.isNpc()) && (player.getSlayer().hasTask()))) {
			Mob m = com.vencillio.rs2.entity.World.getNpcs()[defending.getIndex()];
			if ((m != null) && (Slayer.isSlayerTask(player, m))) {
				base += 0.125;
			}

		}

		if ((ItemCheck.isUsingBalmung(player)) && (defending.isNpc())) {
			Mob m = com.vencillio.rs2.entity.World.getNpcs()[defending.getIndex()];
			if ((m != null) && (MobConstants.isDagannothKing(m))) {
				base += 0.25;
			}
		}

		base = (base * specialBonus);

		if (ItemCheck.hasBNeckAndObbyMaulCombo(player) || ItemCheck.wearingFullVoidMelee(player)) {
			base = (base * 1.25);
		}
		return Math.floor(base);
	}

	public static double getSpecialStr(Player player) {
		Item weapon = player.getEquipment().getItems()[3];
		if (weapon == null || !player.getSpecialAttack().isInitialized()) {
			return 1.0;
		}
		switch (weapon.getId()) {
		case 11802:
			return 1.55;
		case 11804:
		case 11806:
		case 11808:
			return 1.30;
		case 4587:
		case 4153:
			return 1.0;
		case 5698:
		case 5680:
		case 1231:
			return 1.10;
		case 1215:
			return 1.10;
		case 3204:
			return 1.15;
		case 1305:
			return 1.15;
		case 1434:
			return 1.35;
		case 4151:
		case 861: 
			return 1.1;
		case 12006:
			return 1.1;
		case 10877:
			return 1.2933;
		case 13188:
			return 1.05;			
			
		}
		return 0.5D;
	}

	public static double getSpecialAccuracy(Player player) {
		if (player == null) {
			return 0.0D;
		}
		Item weapon = player.getEquipment().getItems()[3];
		if (weapon == null || !player.getSpecialAttack().isInitialized()) {
			return 1.0;
		}
		switch (weapon.getId()) {
		case 5698:
		case 5680:
		case 1231:
			return 2.0;
		case 1215:
			return 2.05;
		case 3204:
			return 1.20;
		case 1305:
			return 1.20;
		case 1434:
			return 1.35;
		case 11802:
			return 1.55;
		case 11804:
		case 11806:
		case 11808:
			return 1.30;
		case 4151:
		case 861:
		case 4587:
		case 12006:
			return 1.15;
		case 10877:
			return 1.2933;
		case 13188:
			return 1.25;
		}
		return 0.0D;
	}

	/*
	 * Gets the attackers effective strength output
	 */
	public static double getEffectiveStr(Player player) {
		return ((player.getLevels()[2]) * getPrayerStr(player));
	}

	/**
	 * Calculates and returns the attackers prayer strength modification bonus
	 * 
	 * @param player
	 * @return
	 */
	public static double getPrayerStr(Player player) {
		if (player.getPrayer().active(Prayer.BURST_OF_STRENGTH))
			return 1.05;
		else if (player.getPrayer().active(Prayer.SUPERHUMAN_STRENGTH))
			return 1.1;
		else if (player.getPrayer().active(Prayer.ULTIMATE_STRENGTH))
			return 1.15;
		else if (player.getPrayer().active(Prayer.CHIVALRY))
			return 1.18;
		else if (player.getPrayer().active(Prayer.PIETY))
			return 1.23;
		return 1.0;
	}

}