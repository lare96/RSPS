package com.vencillio.rs2.content.combat;

import com.vencillio.rs2.entity.Entity;

public class Hit {
	
	public static enum HitTypes {
		NONE, MELEE, RANGED, MAGIC, POISON, DISEASE, DEFLECT, CANNON, MONEY
	}

	private int damage;
	private HitTypes type;
	private Entity attacker;

	private final boolean success;

	public Hit(Entity attacker, int damage, HitTypes type) {
		this.attacker = attacker;
		this.damage = damage;
		this.type = type;
		success = (damage > 0);
	}

	public Hit(int damage) {
		this(null, damage, HitTypes.NONE);
	}

	public Hit(int damage, HitTypes type) {
		this(null, damage, type);
	}

	public Entity getAttacker() {
		return attacker;
	}

	public int getCombatHitType() {
		switch (type) {
		case MELEE:
			return 0;
		case RANGED:
			return 1;
		case MAGIC:
			return 2;
		case DEFLECT:
			return 3;
		case CANNON:
			return 4;
		case MONEY:
			return 5;
		case NONE:
		case POISON:
		case DISEASE:
			return 6;		
		}

		return 0;
	}

	public int getHitType() {
		switch (type) {
		case NONE:
		case MELEE:
		case RANGED:
		case MAGIC:
		case DEFLECT:
		case CANNON:
		case MONEY:
			if (damage > 0) {
				return 1;
			}
			return 0;
		case POISON:
			return 2;
		case DISEASE:
			return 3;
			
		default:
			break;
		}

		return 0;
	}
	
	public int getDamage() {
	return damage;
}

	public HitTypes getType() {
		return type;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setType(HitTypes type) {
		this.type = type;
	}
}
