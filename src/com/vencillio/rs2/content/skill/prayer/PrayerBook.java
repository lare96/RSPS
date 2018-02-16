package com.vencillio.rs2.content.skill.prayer;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobConstants;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendOpenTab;
import com.vencillio.rs2.entity.player.net.out.impl.SendSidebarInterface;

public class PrayerBook {
	private final boolean[] activated;
	private boolean[] quickPrayers;
	private final int[] drain;
	private int headIcon = -1;

	private final Player player;

	private static final Prayer[] OVER_HEAD_DISABLED = new Prayer[] { Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_RANGE, Prayer.PROTECT_FROM_MELEE, Prayer.RETRIBUTION, Prayer.REDEMPTION, Prayer.SMITE };
	private static final Prayer[] DEFENCE_DISABLED = new Prayer[] { Prayer.THICK_SKIN, Prayer.ROCK_SKIN, Prayer.STEEL_SKIN, Prayer.CHIVALRY, Prayer.PIETY };
	private static final Prayer[] ATTACK_DISABLED = new Prayer[] { Prayer.CLARITY_OF_THOUGHT, Prayer.IMPROVED_REFLEXES, Prayer.INCREDIBLE_REFLEXES, Prayer.SHARP_EYE, Prayer.HAWK_EYE, Prayer.EAGLE_EYE, Prayer.MYSTIC_WILL, Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.CHIVALRY, Prayer.PIETY };
	private static final Prayer[] STRENGTH_DISABLED = new Prayer[] { Prayer.BURST_OF_STRENGTH, Prayer.SUPERHUMAN_STRENGTH, Prayer.ULTIMATE_STRENGTH, Prayer.SHARP_EYE, Prayer.HAWK_EYE, Prayer.EAGLE_EYE, Prayer.MYSTIC_WILL, Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.CHIVALRY, Prayer.PIETY };
	private static final Prayer[] ATT_STR_DISABLED = new Prayer[] { Prayer.CLARITY_OF_THOUGHT, Prayer.IMPROVED_REFLEXES, Prayer.INCREDIBLE_REFLEXES, Prayer.BURST_OF_STRENGTH, Prayer.SUPERHUMAN_STRENGTH, Prayer.ULTIMATE_STRENGTH, Prayer.SHARP_EYE, Prayer.HAWK_EYE, Prayer.EAGLE_EYE, Prayer.MYSTIC_WILL, Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.CHIVALRY, Prayer.PIETY };
	private static final Prayer[] COMBAT_DISABLED = new Prayer[] { Prayer.CLARITY_OF_THOUGHT, Prayer.IMPROVED_REFLEXES, Prayer.INCREDIBLE_REFLEXES, Prayer.BURST_OF_STRENGTH, Prayer.SUPERHUMAN_STRENGTH, Prayer.ULTIMATE_STRENGTH, Prayer.THICK_SKIN, Prayer.ROCK_SKIN, Prayer.STEEL_SKIN, Prayer.SHARP_EYE, Prayer.HAWK_EYE, Prayer.EAGLE_EYE, Prayer.MYSTIC_WILL, Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.CHIVALRY, Prayer.PIETY };

	public PrayerBook(Player player) {
		this.player = player;
		activated = new boolean[Prayer.values().length];
		quickPrayers = new boolean[Prayer.values().length];
		drain = new int[Prayer.values().length];
	}

	public enum PrayerType {
		OVER_HEAD,
		DEFENCE,
		ATTACK,
		STRENGTH,
		MAGE_RANGE,
		COMBAT,
		DEFAULT
	}

	public enum Prayer {
		THICK_SKIN("Thick Skin", 1, 12.0, 83, PrayerType.DEFENCE),
		BURST_OF_STRENGTH("Burst of Strength", 4, 12.0, 84, PrayerType.STRENGTH),
		CLARITY_OF_THOUGHT("Clarity of Thought", 7, 12.0, 85, PrayerType.ATTACK),
		SHARP_EYE("Sharp Eye", 8, 12.0, 700, PrayerType.MAGE_RANGE),
		MYSTIC_WILL("Mystic Will", 9, 12.0, 701, PrayerType.MAGE_RANGE),
		ROCK_SKIN("Rock Skin", 10, 8.0, 86, PrayerType.DEFENCE),
		SUPERHUMAN_STRENGTH("Superhuman Strength", 13, 8.0, 87, PrayerType.STRENGTH),
		IMPROVED_REFLEXES("Improved Reflexes", 16, 8.0, 88, PrayerType.ATTACK),
		RAPID_RESTORE("Rapid Restore", 19, 60.0, 89, PrayerType.DEFAULT),
		RAPID_HEAL("Rapid Heal", 22, 60.0, 90, PrayerType.DEFAULT),
		PROTECT_ITEM("Protect Item", 25, 30.0, 91, PrayerType.DEFAULT),
		HAWK_EYE("Hawk Eye", 26, 6.0, 702, PrayerType.MAGE_RANGE),
		MYSTIC_LORE("Mystic Lore", 27, 6.0, 703, PrayerType.MAGE_RANGE),
		STEEL_SKIN("Steel Skin", 28, 6.0, 92, PrayerType.DEFENCE),
		ULTIMATE_STRENGTH("Ultimate Strength", 31, 6.0, 93, PrayerType.STRENGTH),
		INCREDIBLE_REFLEXES("Incredible Reflexes", 34, 6.0, 94, PrayerType.ATTACK),
		PROTECT_FROM_MAGIC("Protect from Magic", 37, 4.0, 95, PrayerType.OVER_HEAD),
		PROTECT_FROM_RANGE("Protect from Range", 40, 4.0, 96, PrayerType.OVER_HEAD),
		PROTECT_FROM_MELEE("Protect from Melee", 43, 4.0, 97, PrayerType.OVER_HEAD),
		EAGLE_EYE("Eagle Eye", 44, 6.0, 704, PrayerType.MAGE_RANGE),
		MYSTIC_MIGHT("Mystic Might", 45, 6.0, 705, PrayerType.MAGE_RANGE),
		RETRIBUTION("Retribution", 46, 4.0, 98, PrayerType.OVER_HEAD),
		REDEMPTION("Redemption", 49, 3.0, 99, PrayerType.OVER_HEAD),
		SMITE("Smite", 52, 4.0, 100, PrayerType.OVER_HEAD),
		CHIVALRY("Chivalry", 60, 3.0, 706, PrayerType.COMBAT),
		PIETY("Piety", 70, 3.0, 707, PrayerType.COMBAT),

		SAPWARRIOR("Sap Warrior", 50, 4.0, 101,PrayerType.COMBAT), //What is config id?
		SAPRANGE("Sap Range", 52, 4.0, 102, PrayerType.COMBAT),
		SAPMAGE("Sap Mage", 54, 4.0, 103, PrayerType.COMBAT),
		SAPSPIRIT("Sap Spirit", 56, 4.0, 104, PrayerType.COMBAT),
		SAPDEFENCE("Sap Defence", 57, 4.0, 105, PrayerType.COMBAT),
		SAPSTRENGTH("Sap Strength", 58, 4.0, 106, PrayerType.COMBAT),
		DEFLECTMAGIC("Deflect Magic", 65, 3.0, 107, PrayerType.OVER_HEAD),
		DEFLECTMISSILES("Deflect Missiles", 68, 3.0, 108, PrayerType.OVER_HEAD),
		DEFLECTMELEE("Deflect Melee", 71, 3.0, 109, PrayerType.OVER_HEAD),
		LEECHATTACK("Leech Attack", 74, 3.0, 110, PrayerType.COMBAT),
		LEECHRANGED("Leech Ranged", 76, 3.0, 111, PrayerType.COMBAT),
		LEECHMAGIC("Leech Magic", 78, 3.0, 112, PrayerType.COMBAT),
		LEECHDEFENCE("Leech Defence", 80, 3.0, 113, PrayerType.COMBAT),
		LEECHSTRENGTH("Leech Strength", 82, 3.0, 114, PrayerType.COMBAT),
		LEECHSPECIAL("Leech Special", 84, 3.0, 115, PrayerType.COMBAT),
		WRATH("Wrath", 89, 3.0, 116, PrayerType.OVER_HEAD),
		SOULSPLIT("Soul Split", 92, 2.0, 117, PrayerType.OVER_HEAD);



		private final String name;
		private final int level;
		private final double drainRate;
		private final int configId;
		private final PrayerType type;

		private Prayer(String name, int level, double drainRate, int configId, PrayerType type) {
			this.name = name;
			this.level = level;
			this.drainRate = drainRate;
			this.configId = configId;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public int getLevel() {
			return level;
		}

		public double getDrainRate() {
			return drainRate;
		}

		public int getConfigId() {
			return configId;
		}

		public Prayer[] getDisabledPrayers() {
			switch (type) {
			case OVER_HEAD:
				return OVER_HEAD_DISABLED;
			case DEFENCE:
				return DEFENCE_DISABLED;
			case ATTACK:
				return ATTACK_DISABLED;
			case STRENGTH:
				return STRENGTH_DISABLED;
			case MAGE_RANGE:
				return ATT_STR_DISABLED;
			case COMBAT:
				return COMBAT_DISABLED;
			default:
				return null;
			}
		}

		public PrayerType getType() {
			return type;
		}
	}

	private boolean canToggle(Prayer prayer) {
		if (player.getMaxLevels()[5] < prayer.getLevel()) {
			player.send(new SendMessage("You need a Prayer level of " + prayer.getLevel() + " to use " + prayer.getName() + "."));
			return false;
		} else if (prayer == Prayer.CHIVALRY && (player.getMaxLevels()[1] < 65)) {
			player.send(new SendMessage("You need a Defence level of 65 to use Chivalry."));
			return false;
		} else if (prayer == Prayer.PIETY && (player.getMaxLevels()[1] < 70)) {
			player.send(new SendMessage("You need a Defence level of 70 to use Piety."));
			return false;
		}

		return true;
	}

	public boolean toggle(Prayer prayer) {
		boolean isEnabled = canToggle(prayer);

		if (player.isDead()) {
			return false;
		} else if (player.getSkill().getLevels()[5] == 0) {
			player.send(new SendMessage("You have run out of prayer points; you must recharge at an altar."));
			forceToggle(prayer, false);
			return false;
		} else if (!player.getController().canUsePrayer(player, prayer.ordinal())) {
			return false;
		} else if (!isEnabled) {
			player.send(new SendConfig(prayer.getConfigId(), 0));
			return false;
		}

		forceToggle(prayer, !activated[prayer.ordinal()]);

		return true;
	}

	public boolean clickButton(int button) {
		if (button >= 67050 && button <= 67075) {
			int prayerId = button - 67050;
			Prayer prayer = Prayer.values()[prayerId];

			if (!quickPrayers[prayerId]) {
				if (!canToggle(prayer)) {
					player.send(new SendConfig(630 + prayerId, 0));
					return true;
				} else {
					quickPrayers[prayer.ordinal()] = true;
					player.send(new SendConfig(630 + prayer.ordinal(), 1));
					if (prayer.getDisabledPrayers() != null) {
						for (Prayer override : prayer.getDisabledPrayers()) {
							if (override != prayer) {
								quickPrayers[override.ordinal()] = false;
								player.send(new SendConfig(630 + override.ordinal(), 0));
							}
						}
					}
				}
			} else {
				quickPrayers[prayerId] = false;
				player.send(new SendConfig(630 + prayerId, 0));
			}

			return true;
		}

		switch (button) {
		case 19136:
			toggleQuickPrayers();
			break;
		case 19137:
			for (Prayer prayer : Prayer.values()) {
				player.send(new SendConfig(630 + prayer.ordinal(), quickPrayers[prayer.ordinal()] ? 1 : 0));
			}
			player.send(new SendSidebarInterface(5, 17200));
			player.send(new SendOpenTab(5));
			break;
		case 67079:
			player.send(new SendMessage("Your quick prayers have been saved."));
			player.send(new SendSidebarInterface(5, 5608));
			break;

		case 67089:
			player.send(new SendSidebarInterface(5, 5608));
			return true;

		case 87082:
			player.send(new SendSidebarInterface(5, 25789));
			player.send(new SendOpenTab(5));
			return true;

		case 21233:
			toggle(Prayer.THICK_SKIN);
			return true;
		case 21234:
			toggle(Prayer.BURST_OF_STRENGTH);
			return true;
		case 21235:
			toggle(Prayer.CLARITY_OF_THOUGHT);
			return true;
		case 77100:
			toggle(Prayer.SHARP_EYE);
			return true;
		case 77102:
			toggle(Prayer.MYSTIC_WILL);
			return true;
		case 21236:
			toggle(Prayer.ROCK_SKIN);
			return true;
		case 21237:
			toggle(Prayer.SUPERHUMAN_STRENGTH);
			return true;
		case 21238:
			toggle(Prayer.IMPROVED_REFLEXES);
			return true;
		case 21239:
			toggle(Prayer.RAPID_RESTORE);
			return true;
		case 21240:
			toggle(Prayer.RAPID_HEAL);
			return true;
		case 21241:
			toggle(Prayer.PROTECT_ITEM);
			return true;
		case 77104:
			toggle(Prayer.HAWK_EYE);
			return true;
		case 77106:
			toggle(Prayer.MYSTIC_LORE);
			return true;
		case 21242:
			toggle(Prayer.STEEL_SKIN);
			return true;
		case 21243:
			toggle(Prayer.ULTIMATE_STRENGTH);
			return true;
		case 21244:
			toggle(Prayer.INCREDIBLE_REFLEXES);
			return true;
		case 21245:
			toggle(Prayer.PROTECT_FROM_MAGIC);
			return true;
		case 21246:
			toggle(Prayer.PROTECT_FROM_RANGE);
			return true;
		case 21247:
			toggle(Prayer.PROTECT_FROM_MELEE);
			return true;
		case 77109:
			toggle(Prayer.EAGLE_EYE);
			return true;
		case 77111:
			toggle(Prayer.MYSTIC_MIGHT);
			return true;
		case 2171:
			toggle(Prayer.RETRIBUTION);
			return true;
		case 2172:
			toggle(Prayer.REDEMPTION);
			return true;
		case 2173:
			toggle(Prayer.SMITE);
			return true;
		case 77113:
			toggle(Prayer.CHIVALRY);
			return true;
		case 77115:
			toggle(Prayer.PIETY);
			return true;
		}

		return false;
	}
	
	public boolean active(Prayer prayer) {
		return activated[prayer.ordinal()];
	}

	public void toggleQuickPrayers() {
		for (Prayer prayer : Prayer.values()) {
			if (!quickPrayers[prayer.ordinal()]) {
				if (activated[prayer.ordinal()]) {
					forceToggle(prayer, false);
				}
			} else {
				if (!toggle(prayer)) {
					return;
				}
			}
		}
	}

	private int determineHeadIcon(Prayer prayer) {
		switch (prayer) {
		case PROTECT_FROM_MAGIC:
			return 2;
		case PROTECT_FROM_RANGE:
			return 1;
		case PROTECT_FROM_MELEE:
			return 0;
		case RETRIBUTION:
			return 3;
		case REDEMPTION:
			return 5;
		case SMITE:
			return 4;
		default:
			return -1;
		}
	}

	public void forceToggle(Prayer prayer, boolean isEnabled) {
		activated[prayer.ordinal()] = isEnabled;
		player.send(new SendConfig(prayer.getConfigId(), isEnabled ? 1 : 0));

		if (isEnabled) {
			if (prayer.getDisabledPrayers() != null) {
				for (Prayer override : prayer.getDisabledPrayers()) {
					if (override != prayer) {
						forceToggle(override, false);
					}
				}
			}

			int icon = determineHeadIcon(prayer);

			if (icon != headIcon && icon != -1) {
				headIcon = icon;
				player.setAppearanceUpdateRequired(true);
			}
		} else if (prayer.getType() == PrayerType.OVER_HEAD) {
			headIcon = -1;
			player.setAppearanceUpdateRequired(true);
		}
	}

	private double[] prayerMult = {1, 1.25, 1.5, 2};

	public double getAffectedDrainRate(Prayer prayer) {
		return prayer.getDrainRate() * (1 + 0.035 * player.getBonuses()[EquipmentConstants.PRAYER] + prayerMult[player.mode]);//3.5% / 1 bonus
	}
	
	public void doEffectOnHit(Entity attacked, Hit hit) {
		if (active(Prayer.SMITE) && attacked.getLevels()[5] > 0) {
			attacked.getLevels()[5] = (byte) (attacked.getLevels()[5] - hit.getDamage() * 0.25D);
			
			if (!attacked.isNpc()) {
				Player target = World.getPlayers()[attacked.getIndex()];

				if (target != null) {
					target.getSkill().update(5);
				}
			}
		}
	}
	
	public int getDamage(Hit hit) {
		switch (hit.getType()) {
		case MELEE:
			if (active(Prayer.PROTECT_FROM_MELEE)) {
				Entity target = hit.getAttacker();
				if (target != null) {
					if (target.isNpc()) {
						Mob mob = World.getNpcs()[target.getIndex()];

						if (mob == null) {
							return hit.getDamage() / 2;
						}

						int id = mob.getId();

						if (id == 10057) {
							return hit.getDamage() / 2;
						}
						
						if (id == 2043) {
							return hit.getDamage() / 4;
						}

						if (id == 8596) {
							return hit.getDamage();
						}

						if ((id != 1677) && (id != 8133))
							return 0;
					} else {
						Player otherPlayer = World.getPlayers()[target.getIndex()];

						if (otherPlayer == null || !otherPlayer.getMelee().isVeracEffectActive()) {
							return hit.getDamage() / 2;
						}
					}
				}
				
				return hit.getDamage() / 2;
			}
			break;
			
		case MAGIC:
			if (active(Prayer.PROTECT_FROM_MAGIC)) {
				Entity target = hit.getAttacker();
				if ((target != null) && (target.isNpc())) {
					Mob mob = World.getNpcs()[target.getIndex()];
					
					if (MobConstants.isDragon(mob)) {
						return hit.getDamage();
					}

					if (mob == null) {
						return hit.getDamage() / 2;
					}
					
					//Half damage block
					if (mob.getId() == 494 || mob.getId() == 319) {
						return hit.getDamage() / 2;
					}
					
					if (mob.getId() == 2044) {
						return hit.getDamage() / 4;
					}

					//Corp
					if (mob.getId() == 8133) {
						return (int) (hit.getDamage() * 0.8D);
					}
					return 0;
				}

				return hit.getDamage() / 2;
			}

			break;
			
		case RANGED:
			if (active(Prayer.PROTECT_FROM_RANGE)) {
				Entity target = hit.getAttacker();
				if (target != null && target.isNpc()) {
					Mob mob = World.getNpcs()[target.getIndex()];

					if (mob == null) {
						return hit.getDamage() / 2;
					}

					int id = mob.getId();
					
					if (id == 2042) {
						return hit.getDamage() / 4;
					}

					if (id == 8133) {
						return (int) (hit.getDamage() * 0.8D);
					}

					return 0;
				}

				return hit.getDamage() / 2;
			}
			break;
			
		default:
			return hit.getDamage();
		}
		
		return hit.getDamage();
	}

	public byte getHeadicon() {
		return (byte) headIcon;
	}
	
	public void setQuickPrayers(boolean[] quickPrayers) {
		this.quickPrayers = quickPrayers;
	}
	
	public boolean[] getQuickPrayers() {
		return quickPrayers;
	}
	
	public boolean isQuickPrayer(Prayer prayer) {
		System.out.println("Prayer values length: " + Prayer.values().length);
		return quickPrayers[prayer.ordinal()];
	}

	public void drain() {
		int amount = 0;
		for (Prayer prayer : Prayer.values()) {
			if (active(prayer)) {
				if (++drain[prayer.ordinal()] >= getAffectedDrainRate(prayer) / 0.6) {
					amount++;
					drain[prayer.ordinal()] = 0;
				}
			}
		}

		if (amount > 0) {
			drain(amount);
		}
	}

	public void drain(int drain) {
		int prayer = player.getSkill().getLevels()[5];
		if (drain >= prayer) {
			for (int i = 0; i < this.drain.length; i++) {
				this.drain[i] = 0;
			}

			disable();
			player.getSkill().setLevel(5, 0);
			player.getClient().queueOutgoingPacket(new SendMessage("You have run out of prayer points; you must recharge at an altar."));
		} else {
			player.getSkill().deductFromLevel(5, drain < 1 ? 1 : (int) Math.ceil(drain));

			if (player.getSkill().getLevels()[5] <= 0) {
				disable();
				player.getClient().queueOutgoingPacket(new SendMessage("You have run out of prayer points; you must recharge at an altar."));
			}
		}
	}
	
	public void disable() {
		for (Prayer prayer : Prayer.values()) {
			if (active(prayer)) {
				forceToggle(prayer, false);
			}
		}
	}

	public void disable(Prayer prayer) {
	forceToggle(prayer, false);
	}
}