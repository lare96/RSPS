package com.vencillio.rs2.content.skill.ranged;

import com.vencillio.core.definitions.RangedWeaponDefinition;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.impl.Ranged;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.item.BasicItemContainer;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemContainer;
import com.vencillio.rs2.entity.mob.impl.SeaTrollQueen;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class RangedSkill {

	public static final boolean requiresArrow(Player player, int id) {
		if (id == 4214 || id == 10034 || id == 10033 || id == 12924) {
			return false;
		}

		Item weapon = player.getEquipment().getItems()[3];

		if ((weapon == null) || (weapon.getRangedDefinition() == null) || (weapon.getRangedDefinition().getType() == RangedWeaponDefinition.RangedTypes.THROWN)) {
			return false;
		}

		return true;
	}

	private final Player player;

	private Item arrow = null;

	private Location aLocation = null;

	private final ItemContainer savedArrows = new BasicItemContainer(40);
	private boolean onyxEffectActive = false;
	
	private boolean blood_forfeit_effect = false;
	private int max_bolt_hit = 0;

	private boolean karilEffectActive = false;

	public RangedSkill(Player player) {
		this.player = player;
	}

	public boolean canUseRanged() {
		Item weapon = player.getEquipment().getItems()[3];
		Item ammo = player.getEquipment().getItems()[13];

		if ((weapon == null) || (weapon.getRangedDefinition() == null)) {
			return false;
		}
		
		if (weapon.getId() == 12926) {
			if (player.getToxicBlowpipe().getBlowpipeAmmo() == null || player.getToxicBlowpipe().getBlowpipeCharge() == 0) {
				player.send(new SendMessage("The blowpipe needs to be charged with Zulrah's scales and loaded with darts."));
				return false;
			}
		}

		RangedWeaponDefinition def = weapon.getRangedDefinition();
		Item[] arrows = def.getArrows();

		if ((def.getType() == RangedWeaponDefinition.RangedTypes.SHOT) && (requiresArrow(player, weapon.getId())) && (arrows != null) && (arrows.length != 0)) {
			if (ammo == null) {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have the correct ammo to use this weapon."));
				return false;
			}

			boolean has = false;

			for (Item i : arrows) {
				if (i != null) {
					if ((ammo.equals(i)) && (ammo.getAmount() >= i.getAmount())) {
						has = true;
					}
				}
			}
			if (!has) {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have the correct ammo to use this weapon."));
				return false;
			}

		}

		return true;
	}

	public void doActionsForChinchompa(Entity attacking) {
		Item weapon = player.getEquipment().getItems()[3];
		if (weapon != null && weapon.getId() == 10034 || weapon.getId() == 10033) {
			attacking.getUpdateFlags().sendGraphic(Graphic.highGraphic(157, 0));
		} else {
			return;
		}
	}

	public void doActionsForDarkBow(Entity attacking) {
		Item weapon = player.getEquipment().getItems()[3];

		if ((weapon == null) || (weapon.getId() != 11235)) {
			return;
		}

		Ranged r = player.getCombat().getRanged();

		player.getSpecialAttack().isInitialized();

		r.setProjectile(new Projectile(player.getCombat().getRanged().getProjectile().getId()));
		r.getProjectile().setDelay(35);

		r.execute(attacking);
		r.setProjectile(new Projectile(player.getCombat().getRanged().getProjectile().getId()));

		player.getSpecialAttack().isInitialized();
	}

	public void doOnyxEffect(int damage) {
		if (damage > 0) {
			int max = player.getMaxLevels()[3];
			int newLvl = player.getSkill().getLevels()[3] + (int) (damage * 0.25D);
			int set = newLvl > max ? max : newLvl;
			player.getSkill().getLevels()[3] = ((byte) set);
			player.getSkill().update(3);
			player.getClient().queueOutgoingPacket(new SendMessage("You absorb some of your opponent's hitpoints."));
		}
		onyxEffectActive = false;
	}

	public void dropArrowAfterHit() {
		if ((arrow == null) || (aLocation == null)) {
			return;
		}

		if ((arrow.getId() == 15243) || (arrow.getId() == 4740) || (arrow.getId() == 10034) || (arrow.getId() == 10033)) {
			return;
		}
		
		if (Utility.randomNumber(2) == 0) {
			if (player.inZulrah()) {
				player.getGroundItems().drop(arrow, player.getLocation());
			} else {
				player.getGroundItems().drop(arrow, aLocation);
			}
		}		

		arrow = null;
		aLocation = null;
	}

	public void getFromAvasAccumulator() {
		for (Item i : savedArrows.getItems())
			if (i != null) {
				int r = player.getInventory().add(new Item(i));
				savedArrows.remove(new Item(i.getId(), r));
			}
	}

	public ItemContainer getSavedArrows() {
		return savedArrows;
	}

	public boolean isKarilEffectActive() {
		return karilEffectActive;
	}

	public boolean isOnyxEffectActive() {
		return onyxEffectActive;
	}

	public void removeArrowsOnAttack() {
		Item weapon = player.getEquipment().getItems()[3];
		Item ammo = player.getEquipment().getItems()[13];
		Item cape = player.getEquipment().getItems()[1];

		if ((weapon == null) || (weapon.getRangedDefinition() == null)) {
			return;
		}

		RangedWeaponDefinition def = weapon.getRangedDefinition();
		
		switch (def.getType()) {
		case SHOT:
			Item[] arrows = weapon.getRangedDefinition().getArrows();

			for (Item i : arrows) {
				if (i != null && ammo != null) {
					if ((ammo.equals(i)) && (ammo.getAmount() >= i.getAmount())) {
						arrow = new Item(i.getId(), i.getAmount());
						break;
					}
				}
			}
			
			if (ammo != null && arrow != null) {
				if (cape != null && (cape.getId() == 10499 || cape.getId() == 10498)) {
					if (Utility.randomNumber(100) >= 10) {
						return;
					}
				}

				ammo.remove(arrow.getAmount());

				if (ammo.getAmount() == 0) {
					player.getEquipment().unequip(13);
					player.getClient().queueOutgoingPacket(new SendMessage("You have run out of ammo."));
				} else {
					player.getEquipment().update(13);
				}

				Entity attack = player.getCombat().getAttacking();
				aLocation = (attack == null ? player.getLocation() : attack.getLocation());

				if (attack != null && attack instanceof SeaTrollQueen) {
					aLocation = new Location(2344, 3699);
				}
			}
			break;
		case THROWN:
			if (cape != null && (cape.getId() == 10499 || cape.getId() == 10498)) {
				if (Utility.randomNumber(100) >= 10) {
					return;
				}
			}
			
			if (weapon.getAmount() == 1) {
				player.getClient().queueOutgoingPacket(new SendMessage("You threw the last of your ammo!"));
			}
			weapon.remove(1);
			if (weapon.getAmount() == 0)
				player.getEquipment().unequip(3);
			else {
				player.getEquipment().update(3);
			}

			arrow = new Item(weapon.getId(), 1);
			Entity attack = player.getCombat().getAttacking();
			aLocation = (attack == null ? player.getLocation() : attack.getLocation());
			break;
		}
	}

	public void setKarilEffectActive(boolean karilEffectActive) {
		this.karilEffectActive = karilEffectActive;
	}

	public void setOnyxEffectActive(boolean onyxEffectActive) {
		this.onyxEffectActive = onyxEffectActive;
	}
	
	public boolean isBloodForfeitEffectActive() {
	return blood_forfeit_effect;
	}

	public void setBloodForfeitEffectActive(boolean blood_forfeit_effect) {
	this.blood_forfeit_effect = blood_forfeit_effect;
	}
	
	public int getMaxBoltHit() {
	return max_bolt_hit;
	}

	public void setMaxBoltHit(int max_bolt_hit) {
	this.max_bolt_hit = max_bolt_hit;
	}
}
