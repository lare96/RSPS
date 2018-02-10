package com.vencillio.rs2.content.combat;

import java.security.SecureRandom;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.GameConstants;
import com.vencillio.rs2.content.combat.impl.DamageMap;
import com.vencillio.rs2.content.combat.impl.Magic;
import com.vencillio.rs2.content.combat.impl.Melee;
import com.vencillio.rs2.content.combat.impl.Ranged;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.following.Following;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobConstants;
import com.vencillio.rs2.entity.pathfinding.StraightPathFinder;

public class Combat {

	public static enum CombatTypes {
		MELEE,
		RANGED,
		MAGIC,
		NONE;
	}

	private static final SecureRandom random = new SecureRandom();

	public static int next(int length) {
		return random.nextInt(length) + 1;
	}
	
	private final Entity entity;

	private Entity attacking = null;
	private Entity lastAttackedBy = null;
	private Animation blockAnimation = null;
	private final Melee melee;
	private final Ranged ranged;

	private final Magic magic;

	private final DamageMap damageMap;
	private CombatTypes combatType = CombatTypes.MELEE;

	private long combatTimer = 0L;

	private int attackTimer = 5;

	public Combat(Entity entity) {
		this.entity = entity;
		melee = new Melee(entity);
		ranged = new Ranged(entity);
		magic = new Magic(entity);
		damageMap = new DamageMap(entity);
		
	}

	public void attack() {
		entity.face(attacking);
		
		if ((!attacking.isActive()) || (attacking.isDead()) || (entity.isDead()) || (attacking.getLocation().getZ() != entity.getLocation().getZ())) {
			reset();
			return;
		}

		if (!withinDistanceForAttack(combatType, false)) {
			return;
		}

		if (!entity.canAttack()) {
			entity.getFollowing().reset();
			reset();
			return;
		}

		entity.onCombatProcess(attacking);
		switch (combatType) {
		case MELEE:
			melee.execute(attacking);
			melee.setNextDamage(-1);
			melee.setDamageBoost(1.0D);
			break;
		case MAGIC:
			magic.execute(attacking);
			magic.setMulti(false);
			magic.setpDelay((byte) 0);
			break;
		case RANGED:
			ranged.execute(attacking);
			break;
		case NONE:
			break;
		}

		entity.afterCombatProcess(attacking);
	}

	public void forRespawn() {
		combatTimer = 0L;
		damageMap.clear();
		attackTimer = 0;
		lastAttackedBy = null;
		entity.setDead(false);
		entity.resetLevels();
	}

	public int getAttackCooldown() {
		switch (combatType) {
		case NONE:
			return 3;
		case MAGIC:
			return magic.getAttack().getAttackDelay();
		case MELEE:
			return melee.getAttack().getAttackDelay();
		case RANGED:
			if (ranged == null || ranged.getAttack() == null) {
				return 4;
			}
			return ranged.getAttack().getAttackDelay();
		}
		return 4;
	}

	public Entity getAttacking() {
		return attacking;
	}

	public int getAttackTimer() {
		return attackTimer;
	}

	public Animation getBlockAnimation() {
		return blockAnimation;
	}

	public CombatTypes getCombatType() {
		return combatType;
	}

	public DamageMap getDamageTracker() {
		return damageMap;
	}

	public double getDistanceFromTarget() {
		if (attacking == null) {
			return -1.0D;
		}
		return Math.abs(entity.getLocation().getX() - attacking.getLocation().getX()) + Math.abs(entity.getLocation().getY() - attacking.getLocation().getY());
	}

	public Entity getLastAttackedBy() {
		return lastAttackedBy;
	}

	public Magic getMagic() {
		return magic;
	}

	public Melee getMelee() {
		return melee;
	}

	public Ranged getRanged() {
		return ranged;
	}

	public boolean inCombat() {
		return combatTimer > World.getCycles();
	}

	public void increaseAttackTimer(int amount) {
		attackTimer += amount;
	}

	public boolean isWithinDistance(int req) {
		if (!entity.isNpc() && !attacking.isNpc() && Utility.getManhattanDistance(attacking.getLocation(), entity.getLocation()) == 0) {
			return false;
		}

		int x = entity.getLocation().getX();
		int y = entity.getLocation().getY();
		int x2 = attacking.getLocation().getX();
		int y2 = attacking.getLocation().getY();

		if (GameConstants.withinBlock(x, y, entity.getSize(), x2, y2)) {
			return true;
		}

		if (Utility.getManhattanDistance(x, y, x2, y2) <= req) {
			return true;
		}

		Location[] a = GameConstants.getBorder(x, y, entity.getSize());
		Location[] b = GameConstants.getBorder(x2, y2, attacking.getSize());

		for (Location i : a) {
			for (Location k : b) {
				if (Utility.getManhattanDistance(i, k) <= req) {
					return true;
				}
			}
		}
		return false;
	}

	public void process() {
		if (attackTimer > 0) {
			attackTimer -= 1;
		}

		if ((attacking != null) && (attackTimer == 0)) {
			attack();
		}

		if ((!entity.isDead()) && (!inCombat()) && (damageMap.isClearHistory()))
			damageMap.clear();
	}

	public void reset() {
		attacking = null;
		entity.getFollowing().reset();
	}

	public void resetCombatTimer() {
		combatTimer = 0L;
	}

	public void setAttack(Entity e) {
		attacking = e;
		entity.getFollowing().setFollow(e, Following.FollowType.COMBAT);
	}

	public void setAttacking(Entity attacking) {
		this.attacking = attacking;
	}

	public void setAttackTimer(int attackTimer) {
		this.attackTimer = attackTimer;
	}

	public void setBlockAnimation(Animation blockAnimation) {
		this.blockAnimation = blockAnimation;
	}

	public void setCombatType(CombatTypes combatType) {
		this.combatType = combatType;
	}

	public void setInCombat(Entity attackedBy) {
		lastAttackedBy = attackedBy;
		combatTimer = World.getCycles() + 8;
	}

	public void updateTimers(int delay) {
		attackTimer = delay;

		if (entity.getAttributes().get("attacktimerpowerup") != null) {
			attackTimer /= 2;
		}
	}

	public boolean withinDistanceForAttack(CombatTypes type, boolean noMovement) {
		if (attacking == null) {
			return false;
		}

		if (type == null) {
			type = combatType;
		}

		int dist = CombatConstants.getDistanceForCombatType(type);

		boolean ignoreClipping = false;

		if (entity.isNpc()) {
			Mob m = World.getNpcs()[entity.getIndex()];
			if (m != null) {
				if (m.getId() == 8596) {
					dist = 18;
					ignoreClipping = true;
				} else if (m.getId() == 3847) {
					if (type == CombatTypes.MELEE) {
						dist = 2;
						ignoreClipping = true;
					}
				} else if (m.getId() == 2042 || m.getId() == 2043 || m.getId() == 2044) {
					dist = 25;
					ignoreClipping = true;
				}
				
				if (MobConstants.isDragon(m)) {
					dist = 1;
				}
			}
		}

		if (!entity.isNpc()) {
			if (type == CombatTypes.MELEE) {
				if (attacking.isNpc()) {
					Mob m = World.getNpcs()[attacking.getIndex()];
					if (m != null) {
						if (m.getId() == 3847) {
							dist = 2;
							ignoreClipping = true;
						} else if (m.getId() == 2042 || m.getId() == 2043 || m.getId() == 2044) {
							dist = 25;
							ignoreClipping = true;
						}
					}
				}
			} else if (type == CombatTypes.RANGED || type == CombatTypes.MAGIC) {
				if (attacking.isNpc()) {
					Mob m = World.getNpcs()[attacking.getIndex()];
					if (m != null) {
						if (m.getId() == 2042 || m.getId() == 2043 || m.getId() == 2044) {
							dist = 25;
							ignoreClipping = true;
						} else if (m.getId() == 5535 || m.getId() == 494) {
							dist = 65;
							ignoreClipping = false;
						}
					}
				}
			}
		}

		if (!noMovement && !entity.isNpc() && !attacking.isNpc() && entity.getMovementHandler().moving()) {
			dist += 3;
		}
		
		if (!isWithinDistance(dist)) {
			return false;
		}

		if (!ignoreClipping) {
			boolean blocked = true;

			if (type == CombatTypes.MAGIC || combatType == CombatTypes.RANGED) {
				for (Location i : GameConstants.getEdges(entity.getLocation().getX(), entity.getLocation().getY(), entity.getSize())) {
					if (entity.inGodwars()) {
						if (StraightPathFinder.isProjectilePathClear(i, attacking.getLocation()) && StraightPathFinder.isProjectilePathClear(attacking.getLocation(), i)) {
							blocked = false;
							break;
						}
					} else {
						if (StraightPathFinder.isProjectilePathClear(i, attacking.getLocation()) || StraightPathFinder.isProjectilePathClear(attacking.getLocation(), i)) {
							blocked = false;
							break;
						}
					}
				}
			} else if (type == CombatTypes.MELEE) {
				for (Location i : GameConstants.getEdges(entity.getLocation().getX(), entity.getLocation().getY(), entity.getSize())) {
					if (entity.inGodwars()) {
						if (StraightPathFinder.isInteractionPathClear(i, attacking.getLocation()) && StraightPathFinder.isInteractionPathClear(attacking.getLocation(), i)) {
							blocked = false;
							break;
						}
					} else {
						if (StraightPathFinder.isInteractionPathClear(i, attacking.getLocation()) || StraightPathFinder.isInteractionPathClear(attacking.getLocation(), i)) {
							blocked = false;
							break;
						}
					}
				}
			}
			
			if (blocked) {
				return false;
			}
		}
		return true;
	}
}
