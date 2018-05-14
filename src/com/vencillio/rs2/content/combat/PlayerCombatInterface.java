package com.vencillio.rs2.content.combat;

import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.PlayerDeathTask;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.Hit.HitTypes;
import com.vencillio.rs2.content.combat.formula.MagicFormulas;
import com.vencillio.rs2.content.combat.formula.MeleeFormulas;
import com.vencillio.rs2.content.combat.formula.RangeFormulas;
import com.vencillio.rs2.content.combat.impl.PoisonWeapons;
import com.vencillio.rs2.content.combat.impl.RingOfRecoil;
import com.vencillio.rs2.content.combat.special.SpecialAttackHandler;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControlGame;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.magic.MagicEffects;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.content.skill.magic.spells.Vengeance;
import com.vencillio.rs2.content.skill.magic.weapons.TridentOfTheSeas;
import com.vencillio.rs2.content.skill.magic.weapons.TridentOfTheSwamp;
import com.vencillio.rs2.content.skill.melee.BarrowsSpecials;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.content.skill.ranged.BoltSpecials;
import com.vencillio.rs2.content.skill.ranged.ToxicBlowpipe;
import com.vencillio.rs2.content.skill.slayer.SlayerMonsters;
import com.vencillio.rs2.content.skill.summoning.FamiliarMob;
import com.vencillio.rs2.content.sounds.PlayerSounds;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobConstants;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class PlayerCombatInterface implements CombatInterface {

	private final Player player;

	public PlayerCombatInterface(Player player) {
		this.player = player;
	}

	@Override
	public void afterCombatProcess(Entity entity) {
		if (player.getSpecialAttack().isInitialized() && player.getCombat().getCombatType() != CombatTypes.MAGIC) {
			SpecialAttackHandler.executeSpecialEffect(player, entity);
			player.getSpecialAttack().afterSpecial();
		}

		if (!player.getMagic().isDFireShieldEffect()) {
			player.getMagic().getSpellCasting().resetOnAttack();
		}

		player.getMelee().afterCombat();

		player.updateCombatType();
	}

	@Override
	public boolean canAttack() {
		if (!player.getController().canUseCombatType(player, player.getCombat().getCombatType())) {
			return false;
		}

		Entity attacking = player.getCombat().getAttacking();
		CombatTypes type = player.getCombat().getCombatType();

		if (attacking.getIndex() <= -1) {
			return false;
		}

		if (attacking.isNpc()) {
			Mob mob = World.getNpcs()[attacking.getIndex()];
			if (mob == null) {
				return false;
			}
			if (MobConstants.SLAYER_REQUIREMENTS.get(mob.getId()) != null) {
				int requirement = MobConstants.SLAYER_REQUIREMENTS.get(mob.getId());
				if (player.getSkill().getLevels()[Skills.SLAYER] < requirement) {
					player.send(new SendMessage("You need a slayer level of " + requirement + " to attack this monster!"));
					return false;
				}
			}
		}

		if (player.getSpecialAttack().isInitialized() && player.getCombat().getCombatType() != CombatTypes.MAGIC && (!player.getController().canUseSpecialAttack(player) || !SpecialAttackHandler.hasSpecialAmount(player))) {
			player.getSpecialAttack().toggleSpecial();
			player.getCombat().setAttackTimer(2);
			return false;
		}
		
		if (TridentOfTheSeas.hasTrident(player)) {
			if (player.getSeasTrident().getCharges() == 0) {
				player.send(new SendMessage("You have no charges to use this!"));
			} else {
				player.getMagic().getSpellCasting().enableAutocast(9999);				
			}
		}
		
		if (TridentOfTheSwamp.hasTrident(player)) {
			if (player.getSwampTrident().getCharges() == 0) {
				player.send(new SendMessage("You have no charges to use this!"));
			} else {
				player.getMagic().getSpellCasting().enableAutocast(9998);				
			}
		}

		if (type == CombatTypes.MAGIC && !player.getMagic().getSpellCasting().canCast())
			return false;
		if (type == CombatTypes.RANGED && !player.getRanged().canUseRanged()) {
			return false;
		}

		if (type == CombatTypes.MELEE && attacking.isNpc()) {
			Mob mob = World.getNpcs()[attacking.getIndex()];
			if (mob != null) {
				for (int i : MobConstants.FLYING_MOBS) {
					if (mob.getId() == i) {
						player.getClient().queueOutgoingPacket(new SendMessage("You cannot reach this npc!"));
						return false;
					}
				}
			}

		}

		if (!player.inMultiArea() || !attacking.inMultiArea()) {
			if (player.getCombat().inCombat() && player.getCombat().getLastAttackedBy() != player.getCombat().getAttacking()) {
				player.getClient().queueOutgoingPacket(new SendMessage("You are already under attack."));
				if(!PlayerConstants.isOwner(player))
					return false;
			}

			if (attacking.getCombat().inCombat() && attacking.getCombat().getLastAttackedBy() != player && !player.getSummoning().isFamiliar(attacking.getCombat().getLastAttackedBy())) {
				player.getClient().queueOutgoingPacket(new SendMessage("This " + (player.getCombat().getAttacking().isNpc() ? "monster" : "player") + " is already under attack."));
				if(!PlayerConstants.isOwner(player))
					return false;
			}

		}

		if (!attacking.isNpc()) {
			Player other = World.getPlayers()[attacking.getIndex()];

			if (other != null && !player.getController().canAttackPlayer(player, other))
				return false;
		} else {
			Mob mob = World.getNpcs()[attacking.getIndex()];

			if (mob != null) {
				if (mob instanceof FamiliarMob) {
					if (!mob.inWilderness())
						return false;
					if (mob.getOwner().equals(player)) {
						player.getClient().queueOutgoingPacket(new SendMessage("You cannot attack your own familiar!"));
						return false;
					}
				}

				if (!player.getController().canAttackNPC()) {
					player.getClient().queueOutgoingPacket(new SendMessage("You can't attack NPCs here."));
					return false;
				}
				if (!SlayerMonsters.canAttackMob(player, mob))
					return false;
				
				if (!mob.getDefinition().isAttackable() || (mob.getOwner() != null && !mob.getOwner().equals(player))) {
					player.getClient().queueOutgoingPacket(new SendMessage("You can't attack this NPC."));
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void checkForDeath() {
		if (player.getLevels()[3] <= 0 && !player.isDead()) {

			TaskQueue.queue(new PlayerDeathTask(player));
		} else if (player.getPrayer().active(Prayer.REDEMPTION)) {
			if (player.getLevels()[3] <= player.getMaxLevels()[3] * .1) {
				player.getPrayer().drain(player.getLevels()[5]);
				player.getLevels()[3] += (int) (player.getMaxLevels()[5] * 0.25);
				player.getUpdateFlags().sendGraphic(new Graphic(436));
			}
		}
	}

	@Override
	public int getCorrectedDamage(double damage) {
		//double temp = damage;
			switch (player.mode) {
				case 0:
					damage = (int) (90 * Math.log10((damage + 43) / 12) - 50);
					break;
				case 1:
					damage = (int) (280 * Math.log10((damage + 105) / 4) - 397);
					break;
				case 2:
					damage = (int) (800 * Math.log10(damage + 258) - 1929);
					break;
				case 3:
					damage = (int) (2300 * Math.log10((damage + 700) / 2) - 5850);
					break;
				default:
					damage = (int) damage;
					break;
			}
			//System.out.println("Mode number: " + player.mode + " [" + temp + "][" + damage + "]");

		Item weapon = player.getEquipment().getItems()[3];
		Item ammo = player.getEquipment().getItems()[13];

		if (player.getCombat().getCombatType() == CombatTypes.RANGED) {
			if (player.getSpecialAttack().isInitialized()) {
				if ((weapon != null) && (weapon.getId() == 11235)) {

					if (ammo != null) {
						if (ammo.getId() == 11212 || ammo.getId() == 11227 || ammo.getId() == 0 || ammo.getId() == 11228) {
							if (damage < 8) {
								damage = 8;
							}
						} else if (damage < 5) {
							damage = 5;
						}
					}
				}
			}
		} else if (player.getCombat().getCombatType() == CombatTypes.MAGIC && ItemCheck.hasDFireShield(player) && player.getMagic().isDFireShieldEffect() && damage < 15) {
			damage = 15;
		}

		return (int) damage;
	}

	@Override
	public int getMaxHit(CombatTypes type) {
		switch (type) {
		case MAGIC:
			return (MagicFormulas.magicMaxHit(player));
		case MELEE:
			return (int) (MeleeFormulas.calculateBaseDamage(player));
		case RANGED:
			return (RangeFormulas.getRangedMaxHit(player));
		case NONE:
			return 0;
		}
		return (int) (MeleeFormulas.calculateBaseDamage(player));
	}

	@Override
	public void hit(Hit hit) {
		if (!player.canTakeDamage() || player.isImmuneToHit() || player.getMagic().isTeleporting() && !player.getController().isSafe(player)) {
			return;
		}

		if (player.isStunned()) {
			return;
		}

		if (player.isDead()) {
			hit.setDamage(0);
		}

		if (hit.getAttacker() != null) {
			if (hit.getAttacker().isNpc()) {
				Mob mob = World.getNpcs()[hit.getAttacker().getIndex()];
				if (mob != null && MobConstants.isDragon(mob)) {
					if (ItemCheck.isWearingAntiDFireShield(player) && (hit.getType() == Hit.HitTypes.MAGIC)) {
						if (ItemCheck.hasDFireShield(player)) {
							player.getMagic().incrDragonFireShieldCharges(mob);
						}
						if (player.hasFireImmunity()) {
							player.getClient().queueOutgoingPacket(new SendMessage("You resist all of the dragonfire."));
							hit.setDamage(0);
						} else {
							player.getClient().queueOutgoingPacket(new SendMessage("You manage to resist some of the dragonfire."));
							hit.setDamage((int) (hit.getDamage() * 0.3));
						}
					} else if (hit.getType() == Hit.HitTypes.MAGIC && player.hasSuperFireImmunity()) {
						player.getClient().queueOutgoingPacket(new SendMessage("You resist all of the dragonfire."));
						hit.setDamage(0);
					} else if (hit.getType() == Hit.HitTypes.MAGIC && player.hasFireImmunity()) {
						player.getClient().queueOutgoingPacket(new SendMessage("You manage to resist some of the dragonfire."));
						hit.setDamage((int) (hit.getDamage() * 0.5));
					} else if ((hit.getType() == Hit.HitTypes.MAGIC)) {
						player.getClient().queueOutgoingPacket(new SendMessage("You are horribly burned by the dragonfire."));
					}
				}
			} else {
				Player p = World.getPlayers()[hit.getAttacker().getIndex()];

				if (p != null && !player.getController().canAttackPlayer(p, player) || !player.getController().canAttackPlayer(player, p)) {
					return;
				}
			}
			
		}

		hit.setDamage(player.getPrayer().getDamage(hit));
		hit.setDamage(player.getEquipment().getEffectedDamage(hit.getDamage()));


		if (hit.getDamage() > player.getLevels()[3]) {
			hit.setDamage(player.getLevels()[3]);
		}

		if (hit.getType() != Hit.HitTypes.POISON && hit.getType() != Hit.HitTypes.NONE) {
			player.getDegrading().degradeEquipment(player);
		}

		player.getLevels()[3] = (short) (player.getLevels()[3] - hit.getDamage());

		if (!player.getUpdateFlags().isHitUpdate()) {
			player.getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		} else {
			player.getUpdateFlags().sendHit2(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		}

		if (hit.getType() != Hit.HitTypes.POISON) {
			if (player.getTrade().trading()) {
				player.getTrade().end(false);
			} else {
				if (player.getInterfaceManager().hasInterfaceOpen()) {
					player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
				}
			}
		}

		checkForDeath();

		if (player.getLevels()[3] > 0 && !player.isDead()) {
			if (player.getEquipment().isWearingItem(11090)) {
				if (player.getSkill().getLevels()[3] > 0 && player.getSkill().getLevels()[3] <= player.getMaxLevels()[3] * 0.20) {
					player.send(new SendMessage("The Phoenix necklace of life saves you but was destroyed in the process"));
					player.getEquipment().remove(new Item(11090, 1));
					player.getLevels()[3] += (player.getMaxLevels()[3] * 0.30);
				}
			}
	
			if (player.getEquipment().isWearingItem(2570) && !player.inDuelArena()) {
				if (player.getSkill().getLevels()[3] > 0 && player.getSkill().getLevels()[3] <= player.getMaxLevels()[3] * 0.10) {
					player.getEquipment().remove(new Item(2570, 1));
					player.getMagic().teleport(3085, 3491, 0, TeleportTypes.SPELL_BOOK);
					player.send(new SendMessage("The Ring of life has saved you; but was destroyed in the process."));
					hit.getAttacker().getCombat().reset();
				}
			}
		}

		if (hit.getAttacker() != null) {

			RingOfRecoil.doRecoil(player, hit.getAttacker(), hit.getDamage());
			
			
			if (player.getEquipment().getItems()[3] != null && player.getEquipment().getItems()[3].getId() == 12006) {
				if (Utility.random(100) < 25) {
					if (hit.getAttacker() != null) {
						hit.getAttacker().poison(4);						
					}
				}
			}

			if (player.isRetaliate() && player.getCombat().getAttacking() == null && !player.getMovementHandler().moving()) {
				player.getCombat().setAttack(hit.getAttacker());
			}

			player.getCombat().getDamageTracker().addDamage(hit.getAttacker(), hit.getDamage());

			hit.getAttacker().onHit(player, hit);

			if (hit.getType() != HitTypes.NONE && hit.getType() != HitTypes.POISON) {
				if (hit.getDamage() >= 4 && player.getMagic().isVengeanceActive()) {
					Vengeance.recoil(player, hit);
				}
			}
		}

		player.getSkill().update(3);
	}

	@Override
	public boolean isIgnoreHitSuccess() {
		if (player.getCombat().getCombatType() == CombatTypes.RANGED && player.getSpecialAttack().isInitialized()) {
			Item weapon = player.getEquipment().getItems()[3];

			if (weapon != null && weapon.getId() == 11235) {
				return true;
			}

		}

		return false;
	}

	@Override
	public void onAttack(Entity attack, int hit, CombatTypes type, boolean success) {
		if (success || type == CombatTypes.MAGIC) {
			if (attack.getLevels()[3] < hit) {
				hit = attack.getLevels()[3];
			}

			player.getSkill().addCombatExperience(type, hit);
		}

		if (!attack.isNpc()) {
			Player p = World.getPlayers()[attack.getIndex()];
			
			if (p != null) {
				player.getSkulling().checkForSkulling(player, p);
			}
			
		} else {
			//player.send(new SendEntityFeed(attack.getMob().getDefinition().getName(), attack.getLevels()[3] - hit, attack.getMaxLevels()[3]));
		}

		switch (type) {
		case MAGIC:
			if (success) {
				MagicEffects.doMagicEffects(player, attack, player.getMagic().getSpellCasting().getCurrentSpellId());
			}
			player.getMagic().getSpellCasting().removeRunesForAttack();
			break;
		case MELEE:
			break;
		case RANGED:
			player.getRanged().removeArrowsOnAttack();

			if (player.getRanged().isOnyxEffectActive()) {
				player.getRanged().doOnyxEffect(hit);
			}
			break;
		case NONE:
			break;
		}
	}

	@Override
	public void onCombatProcess(Entity entity) {
		if (player.getSpecialAttack().isInitialized() && player.getCombat().getCombatType() != CombatTypes.MAGIC) {
			SpecialAttackHandler.handleSpecialAttack(player);
			if (player.getCombat().getCombatType() == CombatTypes.RANGED) {
				player.getRanged().doActionsForDarkBow(entity);
			}
		} else if (player.getCombat().getCombatType() == CombatTypes.RANGED) {
			//BoltSpecials.checkForBoltSpecial(player, entity);
			BarrowsSpecials.checkForBarrowsSpecial(player);
			player.getRanged().doActionsForDarkBow(entity);
		} else {
			if (player.getMagic().isDFireShieldEffect()) {
				player.getMagic().decrDragonFireShieldCharges();
			}

			BarrowsSpecials.checkForBarrowsSpecial(player);
		}
		
		if (player.getCombat().getCombatType() != CombatTypes.MAGIC) {
			PlayerSounds.sendSoundForId(player, player.getSpecialAttack().isInitialized(), player.getEquipment().getItems()[3] != null ? player.getEquipment().getItems()[3].getId() : 0);
		}

		if (player.getCombat().getCombatType() == CombatTypes.MAGIC) {
			player.getMagic().getSpellCasting().appendMultiSpell(player);
		}

		PoisonWeapons.checkForPoison(player, entity);

		player.getDegrading().degradeWeapon(player);

		if (ToxicBlowpipe.hasBlowpipe(player)) {
			ToxicBlowpipe.degrade(player);
		}
		
		if (TridentOfTheSeas.hasTrident(player)) {
			TridentOfTheSeas.degrade(player);
		}
		
		if (TridentOfTheSwamp.hasTrident(player)) {
			TridentOfTheSwamp.degrade(player);
		}
	}

	@Override
	public void onHit(Entity entity, Hit hit) {
		if (player.getAttributes().get(PestControlGame.PEST_GAME_KEY) != null) {
			player.getAttributes().set(PestControlGame.PEST_DAMAGE_KEY, player.getAttributes().get(PestControlGame.PEST_DAMAGE_KEY) != null ? player.getAttributes().getInt(PestControlGame.PEST_DAMAGE_KEY) + hit.getDamage() : hit.getDamage());
		}

		if (player.getCombat().getCombatType() == CombatTypes.RANGED) {
			player.getRanged().dropArrowAfterHit();
			player.getRanged().doActionsForChinchompa(entity);
			if (hit.getDamage() != 0 && !ToxicBlowpipe.hasBlowpipe(player)) {
				BoltSpecials.checkForBoltSpecial(player, entity, hit);
			}
		}

		if (hit.getType() != Hit.HitTypes.POISON && hit.getType() != Hit.HitTypes.NONE) {
			player.getPrayer().doEffectOnHit(entity, hit);

		}

		if (player.getMelee().isGuthanEffectActive())
			BarrowsSpecials.doGuthanEffect(player, entity, hit);
		else if (player.getMelee().isToragEffectActive())
			BarrowsSpecials.doToragEffect(player, entity);
		else if (player.getRanged().isKarilEffectActive())
			BarrowsSpecials.doKarilEffect(player, entity);
		else if (player.getMagic().isAhrimEffectActive()) {
			BarrowsSpecials.doAhrimEffect(player, entity, hit.getDamage());
		}

		if (entity.isNpc() && entity.isDead()) {
			player.getCombat().setAttackTimer(0);
			player.getCombat().resetCombatTimer();
		}

		if (player.getMagic().isDFireShieldEffect()) {
			player.getMagic().reset();
			player.getMagic().getSpellCasting().updateMagicAttack();
			player.updateCombatType();
		}

		if(player.getCombat().getCombatType() == CombatTypes.MAGIC && player.getMagic().getSpellCasting().isAutocasting()) {
			player.getMagic().getSpellCasting().updateMagicAttack();
		}
	}

	@Override
	public void updateCombatType() {
		CombatTypes type;
		if (player.getMagic().getSpellCasting().isCastingSpell()) {
			type = CombatTypes.MAGIC;
		} else {
			type = EquipmentConstants.getCombatTypeForWeapon(player);
		}

		player.getCombat().setCombatType(type);

		switch (type) {
		case MELEE:
			player.getEquipment().updateMeleeDataForCombat();
			break;
		case RANGED:
			player.getEquipment().updateRangedDataForCombat();
			break;
		default:
			break;
		}
	}

}
