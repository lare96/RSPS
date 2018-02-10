package com.vencillio.core.definitions;

import com.vencillio.rs2.content.combat.impl.Attack;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity.AttackType;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Projectile;

public class NpcCombatDefinition {

	public enum CombatTypes {
		MELEE, RANGED, MAGIC, MELEE_AND_RANGED, MELEE_AND_MAGIC, RANGED_AND_MAGIC, ALL
	}

	/**
	 * Holds the values for magic based attacks for npcs
	 * 
	 * @author Michael Sasse
	 * 
	 */
	public class Magic {

		private Attack attack;
		private Animation animation;
		private Graphic start;
		private Projectile projectile;
		private Graphic end;
		private byte max;

		public Animation getAnimation() {
			return animation;
		}

		public Attack getAttack() {
			return attack;
		}

		public Graphic getEnd() {
			return end;
		}

		public int getMax() {
			return max;
		}

		public Projectile getProjectile() {
			return projectile;
		}

		public Graphic getStart() {
			return start;
		}
	}

	public class Melee {

		private Attack attack;
		private AttackType attackType;
		private Animation animation;
		private byte max;

		public Animation getAnimation() {
			return animation;
		}

		public Attack getAttack() {
			return attack;
		}

		public int getMax() {
			return max;
		}
		
		public AttackType getAttackType() {
			return attackType;
		}
	}

	public class Ranged {

		private Attack attack;
		private Animation animation;
		private Graphic start;
		private Projectile projectile;
		private Graphic end;
		private byte max;

		public Animation getAnimation() {
			return animation;
		}

		public Attack getAttack() {
			return attack;
		}

		public Graphic getEnd() {
			return end;
		}

		public int getMax() {
			return max;
		}

		public Projectile getProjectile() {
			return projectile;
		}

		public Graphic getStart() {
			return start;
		}
	}

	public class Skill {

		private int id;
		private int level;

		public int getId() {
			return id;
		}

		public int getLevel() {
			return level;
		}
		
		@Override
		public String toString() {
			return "[" + id + ", " + level + "]";
		}
	}

	private short id;
	private CombatTypes combatType;
	private short respawnTime;
	private Animation block;
	private Animation death;
	private Skill[] skills;

	private int[] bonuses;

	private Melee[] melee;

	private Magic[] magic;

	private Ranged[] ranged;

	public Animation getBlock() {
		return block;
	}

	public int[] getBonuses() {
		return bonuses;
	}

	public CombatTypes getCombatType() {
		return combatType;
	}

	public Animation getDeath() {
		return death;
	}

	public int getId() {
		return id;
	}

	public Magic[] getMagic() {
		return magic;
	}

	public Melee[] getMelee() {
		return melee;
	}

	public Ranged[] getRanged() {
		return ranged;
	}

	public short getRespawnTime() {
		return respawnTime;
	}

	public Skill[] getSkills() {
		return skills;
	}
}
