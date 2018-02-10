package com.vencillio.rs2.entity.item;

import java.math.BigInteger;

import com.vencillio.core.definitions.WeaponDefinition;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.EasterRing;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.impl.Attack;
import com.vencillio.rs2.content.skill.ranged.RangedConstants;
import com.vencillio.rs2.content.skill.ranged.ToxicBlowpipe;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Entity.AttackType;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.item.EquipmentConstants.WeaponAttackStyles;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendEquipment;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSidebarInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class Equipment {

	public static enum AttackStyles {
		ACCURATE,
		AGGRESSIVE,
		CONTROLLED,
		DEFENSIVE;
	}

	public static boolean ignoreShieldEmote(int id) {
		return id == 3842 || id == 3844 || id == 3840 || id == 6889;
	}

	private final Item[] items = new Item[14];

	private AttackStyles attackStyle = AttackStyles.ACCURATE;

	private final Player player;

	public Equipment(Player player) {
		this.player = player;
	}

	public void addOnLogin(Item item, int slot) {
		if (item == null) {
			return;
		}

		getItems()[slot] = item;
	}

	public void calculateBonuses() {
		player.setBonuses(new int[player.getBonuses().length]);

		for (Item i : items) {
			if (i != null) {
				short[] b = i.getItemBonuses();

				if (b != null) {
					for (int k = 0; k < b.length; k++) {
						player.getBonuses()[k] += b[k];
					}
				}
			}
		}

		updateBonusInterface();
	}

	public boolean canEquip(Item item, int slot) {
		if ((slot > items.length) || (slot < 0)) {
			return false;
		}

		byte[][] requirements = item.getItemRequirements();

		if (requirements != null) {

			for (byte[] requirement : requirements) {
				if (player.getMaxLevels()[requirement[0]] < requirement[1]) {
					String name = com.vencillio.rs2.content.skill.Skills.SKILL_NAMES[requirement[0]];
					player.getClient().queueOutgoingPacket(new SendMessage("You need " + Utility.getAOrAn(name) + " " + name + " level of " + requirement[1] + " to equip this."));
					return false;
				}
			}
		}

		if (slot == 3) {
			boolean twoHanded = item.getWeaponDefinition() != null && item.getWeaponDefinition().isTwoHanded();

			if ((items[5] != null) && (items[3] != null) && (twoHanded) && (player.getInventory().getFreeSlots() < 1)) {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to equip that."));
				return false;
			}

		}

		return !((item.getId() == 773) && (!PlayerConstants.isOwner(player)));
	}

	public void clear() {
		if (!player.isActive()) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null) {
					items[i] = null;
				}
			}

			return;
		}

		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				items[i] = null;
				player.getClient().queueOutgoingPacket(new SendEquipment(i, 0, 0));
			}
		}
		updateSidebar();
		updatePlayerAnimations();
		updateAttackStyle();
		player.getSpecialAttack().onEquip();
		player.getMagic().getSpellCasting().disableAutocast();
		player.updateCombatType();
		updateBlockAnimation();

		player.setBonuses(new int[player.getBonuses().length]);
		player.setAppearanceUpdateRequired(true);
		player.getCombat().reset();
	}

	public boolean contains(int itemID) {
		Item[] itemsEquipped = getItems();

		for (Item equippedItem : itemsEquipped) {
			if (equippedItem != null && equippedItem.getId() == itemID) {
				return true;
			}
		}

		return false;
	}

	public void equip(Item item, int clickSlot) {
		int slot = item.getEquipmentDefinition() != null ? item.getEquipmentDefinition().getSlot() : 3;

		if ((!canEquip(item, slot)) || (!player.getController().canEquip(player, item.getId(), slot))) {
			return;
		}

		if ((item.getId() == 7927) && (!EasterRing.canEquip(player))) {
			return;
		}

		if ((items[slot] != null) && (items[slot].getId() == item.getId()) && (items[slot].getDefinition().isStackable())) {
			if (items[slot].getAmount() + player.getInventory().getItems()[clickSlot].getAmount() < 0) {
				return;
			}
			items[slot] = new Item(items[slot].getId(), items[slot].getAmount() + player.getInventory().getItems()[clickSlot].getAmount());
			player.getInventory().getItems()[clickSlot] = null;
		} else if (items[slot] != null) {
			Item add = items[slot];
			items[slot] = item;

			if (add.getDefinition().isStackable()) {
				player.getInventory().setSlot(null, clickSlot);
				player.getInventory().add(add);
			} else {
				player.getInventory().setSlot(add, clickSlot);
			}
		} else {
			items[slot] = item;
			player.getInventory().clear(clickSlot);
		}

		if (slot == 3) {
			boolean twoHanded = item.getWeaponDefinition() != null ? item.getWeaponDefinition().isTwoHanded() : false;

			if ((twoHanded) && (items[5] != null)) {
				player.getInventory().add(items[5]);
				items[5] = null;
				player.getClient().queueOutgoingPacket(new SendEquipment(5, 0, 0));
			}
		} else if (slot == 5) {
			if (items[3] != null) {
				boolean twoHanded = items[3].getWeaponDefinition() != null ? items[3].getWeaponDefinition().isTwoHanded() : false;

				if (twoHanded) {
					player.getInventory().add(items[3]);
					items[3] = null;
					player.getClient().queueOutgoingPacket(new SendEquipment(3, 0, 0));
					updatePlayerAnimations();
				}
			}
		}

		if ((slot == 5) || ((slot == 3) && (items[5] == null))) {
			updateBlockAnimation();
		}

		if (slot == 3 || slot == EquipmentConstants.SHIELD_SLOT) {
			updateSidebar();
			updatePlayerAnimations();
			updateAttackStyle();
			player.getSpecialAttack().onEquip();
			player.getMagic().getSpellCasting().disableAutocast();
			player.updateCombatType();
		} else if ((slot == 13) && (player.getCombat().getCombatType() == CombatTypes.RANGED)) {
			player.updateCombatType();
		}

		player.getClient().queueOutgoingPacket(new SendEquipment(slot, items[slot].getId(), items[slot].getAmount()));
		player.getInventory().update();
		player.setAppearanceUpdateRequired(true);
		player.getCombat().reset();

		if (item.getId() == 7927) {
			EasterRing.init(player);
		}

		if (item.getDefinition().getName() != "staff" && item.getDefinition().getName() != "wand") {
		}

		calculateBonuses();
	}

	public AttackStyles getAttackStyle() {
		return attackStyle;
	}

	public int getEffectedDamage(int hit) {
		Item shield = items[5];

		if ((shield != null) && (player.getLevels()[5] > 0) && ((shield.getId() == 12825) || (shield.getId() == 13740) || (shield.getId() == 13742))) {
			int reduction = (int) (hit * 0.3D);
			hit -= reduction;
			player.getSkill().deductFromLevel(5, reduction / 2);
		}

		return hit;
	}

	public int getEquipmentCount() {
		int am = 0;

		for (Item i : items) {
			if (i != null) {
				am++;
			}
		}
		return am;
	}

	public Item[] getItems() {
		return items;
	}

	public boolean isWearingItem(int id) {
		for (Item item : items) {
			
			if (item == null) continue;
			
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean isWearingItem(int id, int slot) {
		return (items[slot] != null) && (items[slot].getId() == id);
	}

	public void onLogin() {
		player.setBonuses(new int[player.getBonuses().length]);

		for (int i = 0; i < 14; i++) {
			if (items[i] != null) {
				if (i == 3) {
					player.getSpecialAttack().onEquip();
				}

				player.getClient().queueOutgoingPacket(new SendEquipment(i, items[i].getId(), items[i].getAmount()));
			} else {
				player.getClient().queueOutgoingPacket(new SendEquipment(i, 0, 0));
			}
		}

		updatePlayerAnimations();
		updateBlockAnimation();
		updateSidebar();
		updateAttackStyle();
		calculateBonuses();
		player.updateCombatType();
	}

	public boolean remove(Item item) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].getId() == item.getId()) {
				items[i] = null;
				update(i);
				calculateBonuses();
				return true;
			}
		}

		return false;
	}

	public void removeAll() {
		for (int i = 0; i < 14; i++) {
			items[i] = null;
		}
	}

	public void setAttackStyle(AttackStyles attackStyle) {
		this.attackStyle = attackStyle;
	}

	public boolean slotHasItem(int slot) {
		if ((slot > items.length) || (slot < 0)) {
			return false;
		}

		return items[slot] != null;
	}

	public boolean unequip(int slot) {
		if ((slot > items.length) || (slot < 0) || (items[slot] == null)) {
			return false;
		}
		
		if (!player.getController().canUnequip(player)) {
			return false;
		}

		if (!player.getInventory().hasSpaceFor(items[slot])) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to unequip that."));
			return false;
		}

		player.getInventory().add(items[slot]);

		items[slot] = null;

		if ((slot == 5) || ((slot == 3) && (items[5] == null))) {
			updateBlockAnimation();
		}

		if (slot == 3) {
			updateSidebar();
			updatePlayerAnimations();
			updateAttackStyle();
			player.getSpecialAttack().onEquip();
			player.getMagic().getSpellCasting().disableAutocast();
			player.updateCombatType();
		} else if ((slot == 13) && (player.getCombat().getCombatType() == CombatTypes.RANGED)) {
			player.updateCombatType();
		}

		player.getClient().queueOutgoingPacket(new SendEquipment(slot, -1, -1));

		player.setAppearanceUpdateRequired(true);

		calculateBonuses();
		return true;
	}

	public void update() {
		for (int i = 0; i < items.length; i++)
			player.getClient().queueOutgoingPacket(new SendEquipment(i, items[i] != null ? items[i].getId() : 0, items[i] != null ? items[i].getAmount() : 0));
	}

	public void update(int slot) {
		if ((slot > items.length) || (slot < 0)) {
			return;
		}

		player.getClient().queueOutgoingPacket(new SendEquipment(slot, items[slot] != null ? items[slot].getId() : 0, items[slot] != null ? items[slot].getAmount() : 0));
	}

	public void updateAttackStyle() {
		int weaponId = items[3] != null ? items[3].getId() : 0;
		WeaponAttackStyles currentStyle = EquipmentConstants.getWeaponAttackStyle(weaponId);
		switch (currentStyle) {
		case ATTACK_CONTROLLED_DEFENCE:
			player.setAttackType(AttackType.SLASH);
			switch (attackStyle) {
			case AGGRESSIVE:
				attackStyle = AttackStyles.CONTROLLED;
				break;
			case ACCURATE:
			case CONTROLLED:
			case DEFENSIVE:
				break;
			}
			break;
		case ATTACK_STRENGTH_CONTROLLED_DEFENSE:
			switch (attackStyle) {
			case CONTROLLED:
				player.setAttackType(AttackType.STAB);
				break;
			case ACCURATE:
			case AGGRESSIVE:
			case DEFENSIVE:
				player.setAttackType(AttackType.SLASH);
				break;
			}
			break;
		case ATTACK_STRENGTH_DEFENSE:
			player.setAttackType(AttackType.CRUSH);
			switch (attackStyle) {
			case CONTROLLED:
				attackStyle = AttackStyles.AGGRESSIVE;
				break;
			case ACCURATE:
			case AGGRESSIVE:
			case DEFENSIVE:
				break;
			}
			break;
		case ATTACK_STRENGTH_STRENGTH_DEFENSE:
			player.setAttackType(AttackType.STAB);
			switch (attackStyle) {
			case CONTROLLED:
				attackStyle = AttackStyles.AGGRESSIVE;
				break;
			case AGGRESSIVE:
			case ACCURATE:
			case DEFENSIVE:
				break;
			}
			break;
		case CONTROLLED_CONTROLLED_CONTROLLED_DEFENSE:
			player.setAttackType(AttackType.STAB);
			switch (attackStyle) {
			case ACCURATE:
			case AGGRESSIVE:
				attackStyle = AttackStyles.CONTROLLED;
				break;
			case CONTROLLED:
			case DEFENSIVE:
				break;
			}
			break;
		case CONTROLLED_STRENGTH_DEFENSE:
			player.setAttackType(AttackType.STAB);
			switch (attackStyle) {
			case ACCURATE:
				attackStyle = AttackStyles.AGGRESSIVE;
				break;
			case AGGRESSIVE:
				player.setAttackType(AttackType.SLASH);
				break;
			case CONTROLLED:
			case DEFENSIVE:
				break;
			}
			break;
		}
		player.getClient().queueOutgoingPacket(new SendConfig(43, EquipmentConstants.getAttackStyleConfigId(weaponId, attackStyle)));
	}

	public void updateBlockAnimation() {
		int block = 424;
		
		if (items[5] != null && items[5].getDefinition().getName().contains("defender")) {
			block = 4177;
		} else if (items[5] != null && !ignoreShieldEmote(items[5].getId())) {
			block = EquipmentConstants.getShieldBlockAnimation(items[5].getId());
		} else if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
			block = items[3].getWeaponDefinition().getBlock();
		}

		player.getCombat().setBlockAnimation(new Animation(block, 0));
	}

	public void updateBonusInterface() {
		int offset = 0;
		String text = "";
		short[] bonuses = player.getBonuses();
		for (int i = 0; i < 12; i++) {
			if (bonuses[i] >= 0)
				text = EquipmentConstants.BONUS_NAMES[i] + ": +" + bonuses[i];
			else {
				text = EquipmentConstants.BONUS_NAMES[i] + ": -" + Math.abs(bonuses[i]);
			}

			if (i == 10) {
				offset = 1;
			}

			player.getClient().queueOutgoingPacket(new SendString(text, 1675 + i + offset));
		}
	}

	public void updateMeleeDataForCombat() {
		int hitDelay = 1;
		int attackSpeed;
		int attackAnimation;
		if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
			attackAnimation = items[3].getWeaponDefinition().getAttackAnimations()[attackStyle.ordinal()];
			attackSpeed = items[3].getWeaponDefinition().getAttackSpeeds()[attackStyle.ordinal()];
		} else {
			attackAnimation = Item.getWeaponDefinition(0).getAttackAnimations()[attackStyle.ordinal()];
			attackSpeed = 5;
		}

		player.getCombat().getMelee().setAttack(new Attack(hitDelay, attackSpeed), new Animation(attackAnimation, 0));
	}

	public void updatePlayerAnimations() {
		int stand = 808;
		int walk = 819;
		int run = 824;
		int standTurn = 823;
		int turn180 = 820;
		int turn90CW = 821;
		int turn90CCW = 822;

		if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
			WeaponDefinition def = items[3].getWeaponDefinition();
			stand = def.getStand();
			walk = def.getWalk();
			run = def.getRun();
			standTurn = def.getStandTurn();
			turn180 = def.getTurn180();
			turn90CW = def.getTurn90CW();
			turn90CCW = def.getTurn90CCW();
		}

		player.getAnimations().set(stand, standTurn, walk, turn180, turn90CW, turn90CCW, run);
	}

	public void updateRangedDataForCombat() {
		int hitDelay = 3;

		Projectile proj = RangedConstants.getProjectile(player);
		int attackAnimation;
		int attackSpeed;
		if ((items[3] != null) && (items[3].getWeaponDefinition() != null)) {
			attackAnimation = items[3].getWeaponDefinition().getAttackAnimations()[attackStyle.ordinal()];
			attackSpeed = items[3].getWeaponDefinition().getAttackSpeeds()[attackStyle.ordinal()];

			if ((ItemCheck.isUsingCrossbow(player)) && (proj != null)) {
				proj.setCurve(0);
				proj.setStartHeight(40);
				proj.setEndHeight(40);
				proj.setDuration(55);
			}
		} else {
			attackAnimation = Item.getWeaponDefinition(0).getAttackAnimations()[attackStyle.ordinal()];
			attackSpeed = 5;
		}

		player.getCombat().getRanged().setProjectileOffset(0);
		
		if (ToxicBlowpipe.hasBlowpipe(player)) {
			Entity attacking = player.getCombat().getAttacking();
			if (attacking != null) {
				if (attacking.isNpc()) {
					attackSpeed = 2;
				} else {
					attackSpeed = 4;
				}
			}
			player.getCombat().getRanged().setAttack(new Attack(hitDelay, attackSpeed), new Animation(attackAnimation, 0), new Graphic(65535), null, proj);
		} else {
			player.getCombat().getRanged().setAttack(new Attack(hitDelay, attackSpeed), new Animation(attackAnimation, 0), RangedConstants.getDrawbackGraphic(player), RangedConstants.getEndGraphic(), proj);
		}
	}

	public void updateSidebar() {
		int interfaceId = 5855;
		int textId = 5857;

		Item weapon = items[3];

		if ((weapon != null) && (weapon.getWeaponDefinition() != null)) {
			interfaceId = weapon.getWeaponDefinition().getSidebarId();
			textId = EquipmentConstants.getTextIdForInterface(interfaceId);
		}

		player.getClient().queueOutgoingPacket(new SendSidebarInterface(0, interfaceId));
		player.getClient().queueOutgoingPacket(new SendString((weapon != null) && (weapon.getDefinition() != null) ? weapon.getDefinition().getName() : "Unarmed", textId));
	}
	
	/**
	 * Method for calculating player's carried wealth
	 * 
	 * @param container
	 * @return
	 */
	public BigInteger getContainerNet() {
		BigInteger toReturn = BigInteger.ZERO;
		for (Item item : items) {
			if (item == null || item.getDefinition() == null) {
				continue;
			}
			toReturn = toReturn.add(new BigInteger(String.valueOf(item.getDefinition().getGeneralPrice())).multiply(new BigInteger(String.valueOf(item.getAmount()))));
		}
		return toReturn;
	}
	
}
