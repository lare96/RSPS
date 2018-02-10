package com.vencillio.rs2.entity.item;

import com.vencillio.core.definitions.ItemBonusDefinition;
import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity.AttackType;
import com.vencillio.rs2.entity.item.Equipment.AttackStyles;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;

public class EquipmentConstants {

	public static enum WeaponAttackStyles {
		ATTACK_STRENGTH_DEFENSE,
		ATTACK_CONTROLLED_DEFENCE,
		ATTACK_STRENGTH_STRENGTH_DEFENSE,
		ATTACK_STRENGTH_CONTROLLED_DEFENSE,
		CONTROLLED_CONTROLLED_CONTROLLED_DEFENSE,
		CONTROLLED_STRENGTH_DEFENSE;
	}

	public static boolean isForceNewHair(int i) {
		return FULL_HELM[i] == 2;
	}

	public static boolean isFullBody(int id) {
		return FULL_BODY[id] == 1;
	}

	public static boolean isFullHelm(int id) {
		return FULL_HELM[id] == 1;
	}

	public static boolean isFullMask(int id) {
		return FULL_MASK[id] == 1;
	}

	public static final int HELM_SLOT = 0;
	public static final int CAPE_SLOT = 1;
	public static final int NECKLACE_SLOT = 2;
	public static final int WEAPON_SLOT = 3;
	public static final int TORSO_SLOT = 4;
	public static final int SHIELD_SLOT = 5;
	public static final int LEGS_SLOT = 7;
	public static final int GLOVES_SLOT = 9;
	public static final int BOOTS_SLOT = 10;
	public static final int RING_SLOT = 12;

	public static final int AMMO_SLOT = 13;

	public static final String[] SLOT_NAMES = { "helm", "cape", "amulet", "weapon", "torso", "shield", "none", "legs", "none", "gloves", "boots", "none", "ring", "ammo" };

	public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range", "Strength", "Prayer" };

	public static final int STRENGTH = 10;
	public static final int PRAYER = 11;
	public static final int RANGED_STRENGTH = 12;
	private static final byte[] FULL_BODY = new byte[PlayerConstants.MAX_ITEM_COUNT];
	private static final byte[] FULL_HELM = new byte[PlayerConstants.MAX_ITEM_COUNT];
	private static final byte[] FULL_MASK = new byte[PlayerConstants.MAX_ITEM_COUNT];
	private static final byte[] IS_METAL = new byte[PlayerConstants.MAX_ITEM_COUNT];

	private static final int[] FULL_BODY_IDS = {
		11828, 5553, 9924, 11854, 11832, 20096, 10370, 10386, 10378, 6129, 7399, 10564, 11720, 10386, 1035, 13624, 13614, 
		577, 9674, 6107, 1115, 1117, 1119, 1121, 1123, 1125, 1127, 2583, 2584, 2591, 2599, 2607, 2615, 
		2623, 2653, 2661, 2669, 3481, 4720, 4728, 4728, 4749, 4892, 4894, 4895, 4916, 4917, 4918, 4919, 
		4964, 4965, 4966, 4967, 6617, 10348, 14479, 10551, 4736, 4940, 4941, 4942, 4943, 11724, 544, 4091, 
		4101, 4111, 4991, 4990, 4989, 4988, 4758, 4712, 4868, 4869, 4870, 4871, 4757, 8839, 3058, 6654, 6180, 
		6184, 17259, 5575, 3140, 10748, 14595, 11020, 10338, 10400, 10404, 10408, 10412, 10416, 10420, 13619,
		6916, 13887, 13884, 13858, 13870, 13889, 13886, 13860, 13872, 14497, 14601, 15600, 15606, 15612, 15618,
		17193, 9944, 14114, 11854, 2513, 12414, 12480, 12500, 12814, 13107, 12393, 12596, 12492, 10330, 10338,
		12508, 12893, 12888, 13105, 13106, 13104, 12014, 6186, 12441, 12959, 11899, 11896, 12956, 6139,
		3387, 12811, 2896, 2906, 2916, 2926, 2936, 638, 636, 640, 644, 646, 12458, 13072
	};

	private static final int[] FULL_HELM_IDS = { 
		10342, 20100, 10589, 4724, 1153, 1155, 1157, 1159, 1161, 1163, 1165, 2587, 2595, 2605, 
		2613, 2619, 2627, 2657, 2665, 2673, 3486, 6623, 10350, 11335, 13896, 13898, 4724, 4904, 4905, 
		4906, 4907, 4716, 4880, 4881, 4882, 4883, 4708, 4856, 4857, 4858, 4859, 4745, 4952, 4953, 4954, 
		4955, 4732, 4928, 4929, 4930, 4931, 7534, 11718, 6109, 3748, 3749, 3751, 3753, 3755, 11663, 11664, 
		11665, 4753, 4976, 4977, 4978, 4979, 4709, 4856, 4857, 4858, 4859, 10828, 3057, 7594, 6188, 1149, 10548,
		10547, 16711, 5574, 4502, 11021, 10334, 10392, 10398, 13876, 15492, 10589, 1053, 1055, 1057, 17061, 13263, 
		14116, 1167, 12673, 12675, 12677, 12679, 12681, 2631, 11826, 1167, 1169
	};

	private static final int[] FULL_MASK_IDS = { 
		12417, 12371, 8464, 8466, 8468, 8470, 8472, 8474, 8476, 8478, 8480, 8482, 8484, 8486, 8488, 8490, 8492, 4745, 
		4724, 1153, 1155, 1157, 1159, 1161, 1163, 1165, 2587, 2595, 2605, 2613, 2619, 2627, 2657, 2665, 2673, 3486, 6623,
		10350, 11335, 4979, 4978, 4977, 4976, 4753, 3057, 7594, 6188, 5574, 15492, 1053, 1055, 1057, 13263, 11850, 11864,
		12931, 13140, 4551, 9672, 12283,
	};


	/**
	 * Gets the attack style clicking buttons
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean clickAttackStyleButtons(Player player, int buttonId) {
		switch (buttonId) {
		case 1080:
		case 1079:
		case 1078:
		case 1177:
		case 1176:
		case 1175:
		case 6170:
		case 14218:
		case 14219:
		case 14221:
		case 18079:
		case 22228:
		case 22229:
		case 22230:
			player.setAttackType(AttackType.CRUSH);
			break;
			
		case 6169:
		case 6168:
		case 6171:
		case 8236:
		case 18080:
		case 30089:
		case 30088:
		case 30091:
		case 48010:
		case 48009:
		case 48008:
			player.setAttackType(AttackType.SLASH);
			break;
			
		case 8234:
		case 8235:
		case 8237:
		case 14220:
		case 18077:
		case 18078:
		case 30090:
			player.setAttackType(AttackType.STAB);
			break;
		}
		
		switch (buttonId) {
	 	case 48010:
	 	case 21200:
	 	case 6168:
	 	case 8234:
	 	case 1177:
	 	case 30088:
	 	case 14218:
	 	case 22228:
	 	case 1080:
	 	case 17102:
			player.getEquipment().setAttackStyle(AttackStyles.ACCURATE);
			player.updateCombatType();
			return true;
	 	
	 	case 21203:
	 	case 21202:
	 	case 6171:
	 	case 6170:
	 	case 8237:
	 	case 8236:
	 	case 1176:
	 	case 33020:
	 	case 30091:
	 	case 14221:
	 	case 22230:
	 	case 1079:
	 	case 17101:
			player.getEquipment().setAttackStyle(AttackStyles.AGGRESSIVE);
			player.updateCombatType();
			return true;
	 
	 	case 48008:
	 	case 21201:
	 	case 6169:
	 	case 8235:
	 	case 18078:
	 	case 1175:
	 	case 33019:
	 	case 30089:
	 	case 14219:
	 	case 22229:
	 	case 1078:
	 	case 17100:
			player.getEquipment().setAttackStyle(AttackStyles.DEFENSIVE);
			player.updateCombatType();
			return true;
	 
	 	case 48009:
	 	case 18077:
	 	case 18080:
	 	case 18079:
	 	case 33018:
	 	case 30090:
	 	case 14220:
			player.getEquipment().setAttackStyle(AttackStyles.CONTROLLED);
			player.updateCombatType();
			return true;
 		}

		return false;
	}
	

	/**
	 * Declare equipment sets
	 */
	public static final void declare() {
		for (int i = 0; i < FULL_BODY_IDS.length; i++) {
			FULL_BODY[FULL_BODY_IDS[i]] = 1;
		}

		for (int i = 0; i < FULL_HELM_IDS.length; i++) {
			FULL_HELM[FULL_HELM_IDS[i]] = 1;
		}

		for (int i = 0; i < FULL_MASK_IDS.length; i++) {
			FULL_MASK[FULL_MASK_IDS[i]] = 1;
		}
		FULL_HELM[1050] = 2;
		FULL_HELM[14499] = 2;
		FULL_HELM[6858] = 2;
		for (int i = 0; i < 20145; i++) {
			ItemBonusDefinition def1 = GameDefinitionLoader.getItemBonusDefinition(i);
			ItemDefinition def2 = GameDefinitionLoader.getItemDef(i);
			if ((def1 != null) && (def2 != null)) {
				if ((def2.getName().contains("beret")) || (def2.getName().contains("cavalier")) || (def2.getName().contains("headband"))) {
					FULL_HELM[i] = 2;
				}
				if ((def2.getName() != null) && (def2.getName().contains("hood")) && (!def2.getName().contains("Robin")) || def2.getName().contains("mask")) {
					FULL_HELM[i] = 1;
				}
				for (int k = 0; k < def1.getBonuses().length; k++)
					if ((def1.getBonuses()[k] > 0) && (!def2.getName().contains("bow")) && (!def2.getName().contains("dart")) && (!def2.getName().contains("knife")) && (!def2.getName().contains("thrown axe")) && (!def2.getName().contains("throwing axe")) && (!def2.getName().contains("d'hide")) && (!def2.getName().contains("leather"))) {
						IS_METAL[i] = 1;
						break;
					}
			}
		}
	}

	/**
	 * Gets the attack style config
	 * @param id
	 * @param attackStyle
	 * @return
	 */
	public static int getAttackStyleConfigId(int id, AttackStyles attackStyle) {
		if (id == 0) {
			switch (attackStyle) {
			case ACCURATE:
				return 1;
			case AGGRESSIVE:
				return 2;
			case CONTROLLED:
				return -1;
			case DEFENSIVE:
				return 0;
			}
		}
		switch (getWeaponAttackStyle(id)) {
		case ATTACK_STRENGTH_DEFENSE:
			switch (attackStyle) {
			case ACCURATE:
				return 0;
			case AGGRESSIVE:
				return 1;
			case CONTROLLED:
				return 0;
			case DEFENSIVE:
				return 2;
			}
			break;
		case ATTACK_CONTROLLED_DEFENCE:
			switch (attackStyle) {
			case ACCURATE:
				return 0;
			case AGGRESSIVE:
				return 0;
			case CONTROLLED:
				return 1;
			case DEFENSIVE:
				return 2;
			}
			break;
		case ATTACK_STRENGTH_CONTROLLED_DEFENSE:
			switch (attackStyle) {
			case ACCURATE:
				return 0;
			case AGGRESSIVE:
				return 1;
			case CONTROLLED:
				return 2;
			case DEFENSIVE:
				return 3;
			}
			break;
		case ATTACK_STRENGTH_STRENGTH_DEFENSE:
			switch (attackStyle) {
			case ACCURATE:
				return 0;
			case AGGRESSIVE:
				return 1;
			case CONTROLLED:
				return 0;
			case DEFENSIVE:
				return 3;
			}
			break;
		case CONTROLLED_CONTROLLED_CONTROLLED_DEFENSE:
			switch (attackStyle) {
			case ACCURATE:
				return 0;
			case AGGRESSIVE:
				return 0;
			case CONTROLLED:
				return 0;
			case DEFENSIVE:
				return 3;
			}
			break;
		case CONTROLLED_STRENGTH_DEFENSE:
			switch (attackStyle) {
			case ACCURATE:
				return 0;
			case AGGRESSIVE:
				return 1;
			case CONTROLLED:
				return 0;
			case DEFENSIVE:
				return 2;
			}
			break;
		default:
			break;
		}
		return 0;
	}

	/**
	 * Combat type for weapon
	 * @param player
	 * @return
	 */
	public static CombatTypes getCombatTypeForWeapon(Player player) {
		Item weapon = player.getEquipment().getItems()[3];
		if ((weapon != null) && (weapon.getWeaponDefinition() != null)) {
			return weapon.getWeaponDefinition().getType();
		}

		return CombatTypes.MELEE;
	}

	/**
	 * Gets the shield block animation
	 * @param id
	 * @return
	 */
	public static int getShieldBlockAnimation(int id) {
		switch (id) {
		case 6889:
		case 3842:
		case 3844:
		case 3840:
			return 424;
		case 2997:
		case 8844:
		case 8845:
		case 8846:
		case 8847:
		case 8848:
		case 8849:
		case 8850:
		case 20072:
		case 18340:
		case 18653:
		case 17273:
			return 4177;
		}

		return 1156;
	}

	/**
	 * Gets text id for interface
	 * @param interfaceId
	 * @return
	 */
	public static final int getTextIdForInterface(int interfaceId) {
		switch (interfaceId) {
		case 7762:
			return 7765;
		case 12290:
			return 12293;
		case 1698:
			return 1701;
		case 2276:
			return 2279;
		case 1764:
			return 1767;
		case 328:
			return 355;
		case 4446:
			return 4449;
		case 4679:
			return 4682;
		case 425:
			return 428;
		case 3796:
			return 3799;
		case 8460:
			return 8463;
		case 5570:
			return 5573;
		}
		return 5857;
	}

	/**
	 * Gets the weapon attack styles
	 * @param id
	 * @return
	 */
	public static WeaponAttackStyles getWeaponAttackStyle(int id) {
		int interfaceId = Item.getWeaponDefinition(id) != null ? Item.getWeaponDefinition(id).getSidebarId() : 5855;

		switch (interfaceId) {
		case 328:
		case 425:
		case 1764:
		case 4446:
		case 5855:
			return WeaponAttackStyles.ATTACK_STRENGTH_DEFENSE;
		 
		case 12290:
			return WeaponAttackStyles.ATTACK_CONTROLLED_DEFENCE;
		 
		case 5570:
		case 1698:
		case 2276:
			return WeaponAttackStyles.ATTACK_STRENGTH_STRENGTH_DEFENSE;
		 
		case 7762:
		case 3796:
			return WeaponAttackStyles.ATTACK_STRENGTH_CONTROLLED_DEFENSE;

		case 4679:
			return WeaponAttackStyles.CONTROLLED_CONTROLLED_CONTROLLED_DEFENSE;
		 
		case 8460:
			return WeaponAttackStyles.CONTROLLED_STRENGTH_DEFENSE;
		}
		return WeaponAttackStyles.ATTACK_STRENGTH_DEFENSE;
	}

}
