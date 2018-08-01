package com.vencillio.rs2.entity.mob;

import com.vencillio.core.definitions.NpcCombatDefinition;
import com.vencillio.core.definitions.NpcCombatDefinition.Magic;
import com.vencillio.core.definitions.NpcCombatDefinition.Melee;
import com.vencillio.core.definitions.NpcCombatDefinition.Ranged;
import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.MobDeathTask;
import com.vencillio.core.task.impl.MobWalkTask;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.CombatConstants;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.minigames.barrows.Barrows.Brother;
import com.vencillio.rs2.content.minigames.godwars.GodWarsData;
import com.vencillio.rs2.content.minigames.godwars.GodWarsData.GodWarsNpc;
import com.vencillio.rs2.content.minigames.warriorsguild.ArmourAnimator;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.summoning.FamiliarMob;
import com.vencillio.rs2.content.sounds.MobSounds;
import com.vencillio.rs2.entity.*;
import com.vencillio.rs2.entity.following.Following;
import com.vencillio.rs2.entity.following.MobFollowing;
import com.vencillio.rs2.entity.mob.impl.*;
import com.vencillio.rs2.entity.mob.impl.wild.*;
import com.vencillio.rs2.entity.movement.MobMovementHandler;
import com.vencillio.rs2.entity.movement.MovementHandler;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a single Mob in the in-game world
 * 
 * @author Michael Sasse
 * 
 */
public class Mob extends Entity {
	
	private static Logger logger = Logger.getLogger(Mob.class.getSimpleName());

	public static NpcDefinition getDefinition(int id) {
		return GameDefinitionLoader.getNpcDefinition(id);
	}

	/**
	 * Spawns the boss mobs
	 */
	public static void spawnBosses() {
		new CorporealBeast();
		new SeaTrollQueen();
		new Kreearra();
		new KalphiteQueen();
		new GiantMole();
		new ChaosElemental();
		new Callisto();
		new Scorpia();
		new Vetion();
		new ChaosFanatic();
		new CrazyArchaeologist();
		logger.info("All MOB bosses have been spawned.");
	}

	/**
	 * Constructs a new MovementHandler instance
	 */
	private MovementHandler movementHandler = new MobMovementHandler(this);

	/**
	 * Constructs a new MobFollowing instance
	 */
	private MobFollowing following = new MobFollowing(this);

	/**
	 * The spawn location of the MOB
	 */
	private final Location spawnLocation;

	/**
	 * The mob walks around
	 */
	private final boolean walks;

	/**
	 * The id of the mob
	 */
	private short npcId;

	/**
	 * The original id for transforming mobs
	 */
	private final short originalNpcId;

	/**
	 * The id of the mob to transform into
	 */
	private short transformId = -1;

	/**
	 * If the mob is visible or not
	 */
	private boolean visible = true;

	/**
	 * A transform update required
	 */
	private boolean transformUpdate = false;

	/**
	 * A force walk update required
	 */
	private boolean forceWalking = false;
	/**
	 * The mob needs placement
	 */
	private boolean placement = false;
	private byte combatIndex = 0;
	private final boolean attackable;
	private boolean shouldRespawn = true;
	private final boolean face;
	private final boolean noFollow;
	private byte faceDir;
	private final boolean lockFollow;
	private final Player owner;
	private VirtualMobRegion virtualRegion = null;
	private List<Player> combatants;

	private boolean attacked = false;

	private boolean movedLastCycle = false;

	public Mob(int npcId, boolean walks, Location location) {
		this(npcId, walks, location, null, true, false, null);
	}

	/**
	 * Constructs a new Mob
	 * 
	 * @param npcId
	 *            The id of the mob
	 * @param walks
	 *            The mob can walk or not
	 * @param location
	 *            The location of the mob
	 * @param owner
	 *            The owner of the mob
	 * @param shouldRespawn
	 *            The mob should respawn after dying
	 * @param lockFollow
	 *            Themob should follow
	 * @param virtualRegion
	 *            The virtual region the mob is in
	 */
	public Mob(int npcId, boolean walks, Location location, Player owner, boolean shouldRespawn, boolean lockFollow, VirtualMobRegion virtualRegion) {
		originalNpcId = ((short) npcId);
		this.npcId = ((short) npcId);
		this.walks = walks;
		this.virtualRegion = virtualRegion;
		this.owner = owner;
		this.lockFollow = lockFollow;
		this.shouldRespawn = shouldRespawn;
		face = MobConstants.face(npcId);
		noFollow = MobConstants.noFollow(this);

		getLocation().setAs(location);
		spawnLocation = new Location(location);

		setSize(getDefinition().getSize());

		movementHandler.resetMoveDirections();

		setNpc(true);

		updateCombatType();

		Walking.setNpcOnTile(this, true);
		World.register(this);

		getUpdateFlags().setUpdateRequired(true);

		attackable = getDefinition().isAttackable();

		setActive(true);

		if (attackable) {
			if (getCombatDefinition() != null) {
				setBonuses(getCombatDefinition().getBonuses().clone());
				NpcCombatDefinition.Skill[] skills = getCombatDefinition().getSkills();
				if (skills != null) {
					int[] skill = new int[22];

					for (int i = 0; i < skills.length; i++) {
						skill[skills[i].getId()] = skills[i].getLevel();
					}

					setLevels(skill.clone());
					setMaxLevels(skill.clone());
				}

			}

			if (inMultiArea())
				combatants = new ArrayList<Player>();
			else
				combatants = null;
		} else {
			combatants = null;
		}

		if (npcId == 8725)
			faceDir = 4;
		else if ((npcId == 553) && (location.getX() == 3091) && (location.getY() == 3497))
			faceDir = 4;
		else
			faceDir = -1;

		setRetaliate(attackable);
		
		if (GodWarsData.forId(npcId) != null && GodWarsData.bossNpc(GodWarsData.forId(npcId))) {
			MobConstants.GODWARS_BOSSES.add(this);
		}
	}
	
	public Mob(int npcId, boolean walks, boolean respawn, Location location) {
		this(npcId, walks, location, null, false, false, null);
	}

	public Mob(int npcId, boolean walks, Location location, VirtualMobRegion r) {
		this(npcId, walks, location, null, true, false, r);
	}

	public Mob(Player owner, int npcId, boolean walks, boolean shouldRespawn, boolean lockFollow, Location location) {
		this(npcId, walks, location, owner, shouldRespawn, lockFollow, null);
	}

	public Mob(Player owner, VirtualMobRegion region, int npcId, boolean walks, boolean shouldRespawn, boolean lockFollow, Location location) {
		this(npcId, walks, location, owner, shouldRespawn, lockFollow, region);
	}

	public Mob(VirtualMobRegion virtualRegion, int npcId, boolean walks, boolean shouldRespawn, Location location) {
		this(npcId, walks, location, null, shouldRespawn, false, virtualRegion);
	}

	/**
	 * Adds a new combatant to the mobs list
	 * 
	 * @param p
	 *            The player to add
	 */
	public void addCombatant(Player p) {
		if (combatants == null) {
			combatants = new ArrayList<Player>();
		}

		if (!combatants.contains(p))
			combatants.add(p);
	}

	@Override
	public void afterCombatProcess(Entity attack) {
		if (attack.isDead()) {
			getCombat().reset();
		} else {
			MobAbilities.executeAbility(npcId, this, attack);
		}
	}
	
	private boolean canAttack = true;

	@Override
	public boolean canAttack() {
		Entity attacking = getCombat().getAttacking();

		if (!isCanAttack()) {
			System.out.println("In 1st return false");
			return false;
		}
		
		if (attacking.getAttributes().get("inMulti") == null && ((!inMultiArea()) || (!attacking.inMultiArea()))) {
			System.out.println("In main if");
			if ((getCombat().inCombat()) && (getCombat().getLastAttackedBy() != getCombat().getAttacking())) {
				System.out.println("In 3nd return false");
				return false;
			}

			if ((attacking.getCombat().inCombat()) && (attacking.getCombat().getLastAttackedBy() != this)) {
				System.out.println("In 3rd return false");
				return false;
			}
		}

		if (!attacking.isNpc()) {
			Player p = World.getPlayers()[attacking.getIndex()];

			if ((p != null) && (!p.getController().canAttackNPC())) {
				System.out.println("In 4th return false");
				return false;
			}

		}

		return true;
	}

	@Override
	public void checkForDeath() {
		if (getLevels()[3] <= 0) {
			TaskQueue.queue(new MobDeathTask(this));

			if (combatants != null)
				combatants.clear();
		}
	}

	/**
	 * Processes custom npcs while they are alive
	 */
	public void doAliveMobProcessing() {
	
	}

	public void doPostHitProcessing(Hit hit) {
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	public boolean face() {
		return face;
	}

	/**
	 * Gets the affected hit damage
	 * 
	 * @param hit
	 *            The hit dealt to the mob
	 * @return
	 */
	public int getAffectedDamage(Hit hit) {
		return hit.getDamage();
	}

	public List<Player> getCombatants() {
		if (combatants == null) {
			combatants = new ArrayList<Player>();
		}

		return combatants;
	}

	public NpcCombatDefinition getCombatDefinition() {
		return GameDefinitionLoader.getNpcCombatDefinition(npcId);
	}

	public NpcCombatDefinition getCombatDefinition(int id) {
		return GameDefinitionLoader.getNpcCombatDefinition(id);
	}

	public int getCombatIndex() {
		return combatIndex;
	}

	@Override
	public int getCorrectedDamage(double damage) {
		return (int)damage;
	}

	/**
	 * Gets the mobs death animation
	 * 
	 * @return
	 */
	public Animation getDeathAnimation() {
		return getCombatDefinition() != null ? getCombatDefinition().getDeath() : new Animation(0, 0);
	}

	public NpcDefinition getDefinition() {
		return GameDefinitionLoader.getNpcDefinition(npcId);
	}

	public int getFaceDirection() {
		return faceDir;
	}

	@Override
	public Following getFollowing() {
		return following;
	}

	/**
	 * Gets the mobs id
	 * 
	 * @return The mobs id
	 */
	public int getId() {
		return npcId;
	}

	@Override
	public int getMaxHit(CombatTypes type) {
		if (getCombatDefinition() == null) {
			return 1;
		}

		switch (type) {
		case NONE:
			break;
		case MAGIC:
			if (getCombatDefinition().getMagic() != null) {
				return getCombatDefinition().getMagic()[combatIndex].getMax();
			}
			break;
		case MELEE:
			if (getCombatDefinition().getMelee() != null) {
				int max = getCombatDefinition().getMelee()[combatIndex].getMax();
				if (npcId == 1673) {
					return (int) (max * (1.0D + (2.0D - getLevels()[3] / getMaxLevels()[3])));
				}
				return max;
			}

			break;
		case RANGED:
			if (getCombatDefinition().getRanged() != null) {
				return getCombatDefinition().getRanged()[combatIndex].getMax();
			}
			break;
		}
		return 1;
	}

	@Override
	public MovementHandler getMovementHandler() {
		return movementHandler;
	}

	public Location getNextSpawnLocation() {
		return spawnLocation;
	}

	public Player getOwner() {
		return owner;
	}

	public int getRespawnTime() {
		if (getCombatDefinition() != null) {
			return getCombatDefinition().getRespawnTime();
		}
		switch(getId()) {
			case 1635:
			case 1636:
			case 1637:
			case 1638:
				return 8; //4.8s
			case 1639:
			case 1640:
			case 1641:
			case 1642:
				return 14; //8.4s
			case 1643:
			case 1644:
				return 25; //15s
		}
		return 50;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	/**
	 * Gets the mobs transform id
	 * 
	 * @return
	 */
	public int getTransformId() {
		return transformId;
	}

	public VirtualMobRegion getVirtualRegion() {
		return virtualRegion;
	}

	@Override
	public void hit(Hit hit) {
		if (!canTakeDamage()) {
			return;
		}

		if (isDead())
			hit.setDamage(0);
		else {
			hit.setDamage(getAffectedDamage(hit));
		}
		
		if (npcId == 493 && getOwner().inKraken()) {
			if (hit.getDamage() != 0) {
				hit.setDamage(0);
				remove();
				new Tentacles(getOwner(), new Location(getX(), getY(), getOwner().getZ()));
				getOwner().whirlpoolsHit++;				
			}
		}
		
		if (npcId == 496 && getOwner().inKraken()) {
			if (getOwner().whirlpoolsHit != 4) {
				getOwner().hit(new Hit(Utility.random(10)));
				getOwner().send(new SendMessage("@dre@You need to attack all 4 whirlpools before doing this! Remaining: " + (4 - getOwner().whirlpoolsHit)));
				return;
			}
			if (hit.getDamage() != 0) {
				hit.setDamage(0);
				remove();
				new Kraken(getOwner(), new Location(3695, 5811, getOwner().getZ()));
			}
		}
		

		if ((npcId == 2883) && ((hit.getType() == Hit.HitTypes.MELEE) || (hit.getType() == Hit.HitTypes.RANGED))) {
			hit.setDamage(0);
		}

		if ((npcId == 2881) && ((hit.getType() == Hit.HitTypes.MAGIC) || (hit.getType() == Hit.HitTypes.RANGED))) {
			hit.setDamage(0);
		}

		if ((npcId == 2882) && ((hit.getType() == Hit.HitTypes.MAGIC) || (hit.getType() == Hit.HitTypes.MELEE))) {
			hit.setDamage(hit.getDamage() / 10);
		}

		if (hit.getDamage() > getLevels()[3])
			hit.setDamage(getLevels()[3]);
		getLevels()[3] = ((short) (getLevels()[3] - hit.getDamage()));

		if (!getUpdateFlags().isHitUpdate())
			getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		else {
			getUpdateFlags().sendHit2(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		}

		if (hit.getAttacker() != null) {
			getCombat().getDamageTracker().addDamage(hit.getAttacker(), hit.getDamage());

			if (getCombat().getAttacking() == null && isRetaliate() || !inMultiArea() && isRetaliate()) {
				getCombat().setAttack(hit.getAttacker());
			}

			if (inMultiArea() && (attackable) && isRetaliate()) {
				if (((attacked) && (hit.getAttacker() != getCombat().getAttacking())) || ((!attacked) && (!movedLastCycle) && (!getCombat().withinDistanceForAttack(getCombat().getCombatType(), true)))) {
					getCombat().setAttack(hit.getAttacker());
					attacked = false;
				}

				if (!hit.getAttacker().isNpc()) {
					Player p = World.getPlayers()[hit.getAttacker().getIndex()];
					if (p != null) {
						MobSounds.sendBlockSound(p, npcId);

						addCombatant(p);
					}
				}
			} else if ((!isDead()) && (!hit.getAttacker().isNpc())) {
				Player p = World.getPlayers()[hit.getAttacker().getIndex()];
				if (p != null) {
					MobSounds.sendBlockSound(p, npcId);
				}
			}

			doPostHitProcessing(hit);
		}

		if (!isDead()) {
			checkForDeath();
		}

		if (hit.getAttacker() != null)
			hit.getAttacker().onHit(this, hit);
	}

	public boolean inVirtualRegion() {
		return virtualRegion != null;
	}

	@Override
	public boolean isIgnoreHitSuccess() {
		return false;
	}

	public boolean isLockFollow() {
		return (owner != null) && (lockFollow);
	}

	public boolean isMovedLastCycle() {
		return movedLastCycle;
	}

	public boolean isNoFollow() {
		return noFollow;
	}

	public boolean isPlacement() {
		return placement;
	}

	/**
	 * Gets if the mob is requesting a transform update
	 * 
	 * @return the mob is transforming
	 */
	public boolean isTransformUpdate() {
		return transformUpdate;
	}

	/**
	 * Gets if the mob is visible or not
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * The mob should walk to home
	 * 
	 * @return If the mob can walk to home or not
	 */
	public boolean isWalkToHome() {
		if (GodWarsData.forId(npcId) != null && GodWarsData.bossNpc(GodWarsData.forId(npcId))) {
			return false;
		}

		if (inWilderness()) {
			return Math.abs(getLocation().getX() - spawnLocation.getX()) + Math.abs(getLocation().getY() - spawnLocation.getY()) > getSize() * 1 + 2;
		}

		if ((getFollowing().isIgnoreDistance()) || (owner != null)) {
			return false;
		}

		if (attackable) {
			return Math.abs(getLocation().getX() - spawnLocation.getX()) + Math.abs(getLocation().getY() - spawnLocation.getY()) > getSize() * 2 + 6;
		}
		return Utility.getManhattanDistance(spawnLocation, getLocation()) > 2;
	}

	@Override
	public void onAttack(Entity attack, int hit, CombatTypes type, boolean success) {
	}

	@Override
	public void onCombatProcess(Entity attack) {
		if ((npcId == 8133) && (getCombat().getCombatType() == CombatTypes.MELEE) && (combatIndex == 0)) {
			getUpdateFlags().sendGraphic(new Graphic(1834, 0, false));
		}

		attacked = true;

		if ((inMultiArea()) && (!attack.isNpc())) {
			Player p = World.getPlayers()[attack.getIndex()];

			if (p != null) {
				MobSounds.sendBlockSound(p, npcId);
				addCombatant(p);
			}
		} else if (!attack.isNpc()) {
			Player p = World.getPlayers()[attack.getIndex()];

			if (p != null)
				MobSounds.sendAttackSound(p, npcId, getCombat().getCombatType(), getLastDamageDealt() > 0);
		}
	}

	/**
	 * Actions taken on death for custom npcs
	 */
	public void onDeath() {
	}

	@Override
	public void onHit(Entity e, Hit hit) {
		if ((e.isDead()) && (inMultiArea()) && (!e.isNpc())) {
			if (combatants == null) {
				combatants = new ArrayList<Player>();
			}

			combatants.remove(World.getPlayers()[e.getIndex()]);
		}
	}

	@Override
	public void process() throws Exception {
		if ((owner != null) && (!owner.isActive() || !owner.withinRegion(getLocation()))) {
			if (!owner.inZulrah() && !isDead()) {
				remove();
				if (getId() == 1778) {
					owner.getAttributes().set("KILL_AGENT", Boolean.FALSE);
				}
				return;
			}
		}

		if ((attackable) || ((this instanceof FamiliarMob))) {
			if (forceWalking)
				return;
			if (isDead()) {
				getCombat().reset();
				return;
			}

			if (inMultiArea()) {
				if (combatants == null) {
					combatants = new ArrayList<Player>();
				}

				if (combatants.size() > 0) {
					if (getCombat().getAttacking() == null)
						combatants.clear();
					else {
						for (Iterator<Player> i = combatants.iterator(); i.hasNext();) {
							Player p = i.next();
							if ((!p.getLocation().isViewableFrom(getLocation())) || (!p.getCombat().inCombat()) || (p.isDead())) {
								i.remove();
							}
						}
					}
				}
			}

			
			doAliveMobProcessing();

			if ((getCombat().getAttackTimer() <= 1) && (getCombat().getAttacking() != null)) {
				updateCombatType();
			}

			if (isWalkToHome()) {
				getCombat().reset();
				getFollowing().reset();
				TaskQueue.queue(new MobWalkTask(this, spawnLocation, true));
			} else if ((!isDead()) && (getCombat().getAttacking() == null) && (!getFollowing().isFollowing()) && (walks) && (!forceWalking)) {
				RandomMobChatting.handleRandomMobChatting(this);
				Walking.randomWalk(this);
				
				GodWarsNpc npc = GodWarsData.forId(getId());

				if (npc != null && !getCombat().inCombat()) {
					
					for (Mob i : World.getNpcs()) {
						
						if (i == null) {
							continue;
						}
						
						GodWarsNpc other = GodWarsData.forId(i.getId());
						
						if (npc == null || other == null) {
							continue;
						}
						
						if ((i.getCombat().getAttacking() == null) && (i.getCombatDefinition() != null)) {
	
							if (npc.getAllegiance() != other.getAllegiance() && !i.getCombat().inCombat()) {
	
								if (Math.abs(getX() - i.getX()) + Math.abs(getY() - i.getY()) <= 4 + CombatConstants.getDistanceForCombatType(i.getCombat().getCombatType())) {
									i.getCombat().setAttack(this);
								}
							}
						}
					}
				}
			}
			}

			if ((!forceWalking) && (!isDead())) {
				following.process();
				getCombat().process();
		} else if ((!isDead()) && (!attackable) && (walks) && (!following.isFollowing()) && (!forceWalking)) {
			Walking.randomWalk(this);
		} else if (!forceWalking) {
			following.process();
		}
	}

	/**
	 * processes the movement of custom npcs
	 */
	public void processMovement() {
	}

	/**
	 * Removes the mob from the in-game world
	 */
	public void remove() {
		if ((Brother.isBarrowsBrother(this)) && (owner != null)) {
			owner.setBrotherNpc(null);
			owner.getCombat().resetCombatTimer();
		}

		if ((ArmourAnimator.isAnimatedArmour(npcId)) && (owner != null)) {
			owner.getAttributes().remove("warriorGuildAnimator");
		}

		visible = false;
		setActive(false);
		World.unregister(this);
		Walking.setNpcOnTile(this, false);

		if (virtualRegion != null) {
			virtualRegion = null;
		}
		
		MobConstants.GODWARS_BOSSES.remove(this);
	}

	@Override
	public void reset() {
		movedLastCycle = (getMovementHandler().getPrimaryDirection() != -1);
		getMovementHandler().resetMoveDirections();
		getFollowing().updateWaypoint();
		getUpdateFlags().reset();
		placement = false;
	}

	/**
	 * Resets the mobs levels
	 */
	@Override
	public void resetLevels() {
		if (getCombatDefinition() != null) {
			setBonuses(getCombatDefinition().getBonuses().clone());
			NpcCombatDefinition.Skill[] skills = getCombatDefinition().getSkills();
			if (skills != null) {
				int[] skill = new int[25];

				for (int i = 0; i < skills.length; i++) {
					skill[skills[i].getId()] = skills[i].getLevel();
				}

				setLevels(skill.clone());
				setMaxLevels(skill.clone());
			}

		}
	}

	@Override
	public void retaliate(Entity attacked) {
		if (!getCombat().inCombat()) {
			getCombat().setAttack(attacked);
		}
	}

	/**
	 * The mob retreats from combat
	 */
	public void retreat() {
		if (getCombat().getAttacking() != null) {
			forceWalking = true;
			getCombat().reset();
			TaskQueue.queue(new MobWalkTask(this, new Location(getX() + 5, getY() + 5), false));
		}
	}

	public void setFaceDir(int face) {
		faceDir = ((byte) face);
	}

	public void setForceWalking(boolean walkingHome) {
		forceWalking = walkingHome;
	}

	public void setPlacement(boolean placement) {
		this.placement = placement;
	}

	/**
	 * Sets if the mob should respawn after death
	 * 
	 * @param state
	 *            The mob should respawn or not
	 */
	public void setRespawnable(boolean state) {
		shouldRespawn = state;
	}

	public void setTransformId(int transformId) {
		this.transformId = ((short) transformId);
	}

	/**
	 * Sets the mobs transform status
	 * 
	 * @param transformUpdate
	 */
	public void setTransformUpdate(boolean transformUpdate) {
		this.transformUpdate = transformUpdate;
	}

	/**
	 * Sets the mob's visibility status
	 * 
	 * @param isVisible
	 *            The mob is visible or not
	 */
	public void setVisible(boolean isVisible) {
		visible = isVisible;
	}

	/**
	 * Should the mob respawn after death
	 * 
	 * @return The mob should respawn
	 */
	public boolean shouldRespawn() {
		return shouldRespawn;
	}

	/**
	 * Teleports the mob to a new location
	 * 
	 * @param p
	 *            The location to teleport the mob too
	 */
	public void teleport(Location p) {
		Walking.setNpcOnTile(this, false);
		getMovementHandler().getLastLocation().setAs(new Location(p.getX(), p.getY() + 1));
		getLocation().setAs(p);
		Walking.setNpcOnTile(this, true);
		placement = true;
		getMovementHandler().resetMoveDirections();
	}

	/**
	 * Transforms the mob
	 * 
	 * @param id
	 *            The id to transform the mob too
	 */
	public void transform(int id) {
		transformUpdate = true;
		transformId = ((short) id);
		npcId = ((short) id);
		updateCombatType();
		getUpdateFlags().setUpdateRequired(true);
		if (attackable) {
			if (getCombatDefinition() != null) {
				setBonuses(getCombatDefinition().getBonuses().clone());
				NpcCombatDefinition.Skill[] skills = getCombatDefinition().getSkills();
				if (skills != null) {
					int[] skill = new int[Skills.SKILL_COUNT];
					int[] skillMax = new int[Skills.SKILL_COUNT];

					for (int i = 0; i < skills.length; i++) {
						if (i == 3) {
							skill[3] = getLevels()[3];
							skillMax[3] = getMaxLevels()[3];
							continue;
						}
						skill[skills[i].getId()] = skills[i].getLevel();
						skillMax[skills[i].getId()] = skills[i].getLevel();
					}

					setLevels(skill.clone());
					setMaxLevels(skillMax.clone());
				}

			}
		}
	}

	/**
	 * Un-transforms a mob
	 */
	public void unTransform() {
		if (originalNpcId != npcId)
			transform(npcId);
	}

	@Override
	public void updateCombatType() {
		NpcCombatDefinition def = getCombatDefinition();

		if (def == null) {
			return;
		}

		CombatTypes combatType = CombatTypes.MELEE;

		switch (def.getCombatType()) {
		case MAGIC:
			combatType = CombatTypes.MAGIC;
			break;
		case MELEE_AND_MAGIC:
			if (!getCombat().withinDistanceForAttack(CombatTypes.MELEE, true) || Utility.randomNumber(2) == 1) {
				combatType = CombatTypes.MAGIC;
			} else {
				combatType = CombatTypes.MELEE;
			}
			break;
		case MELEE_AND_RANGED:
			if (!getCombat().withinDistanceForAttack(CombatTypes.MELEE, true) || Utility.randomNumber(2) == 1) {
				combatType = CombatTypes.RANGED;
			} else {
				combatType = CombatTypes.MELEE;
			}
			break;
		case RANGED:
			combatType = CombatTypes.RANGED;
			break;
		case RANGED_AND_MAGIC:
			if (!getCombat().withinDistanceForAttack(CombatTypes.RANGED, true) || Utility.randomNumber(2) == 1) {
				combatType = CombatTypes.MAGIC;
			} else {
				combatType = CombatTypes.RANGED;
			}
			break;
		case ALL:
			if (!getCombat().withinDistanceForAttack(CombatTypes.MELEE, true)) {
				int roll = Utility.randomNumber(2);
				if (getCombat().withinDistanceForAttack(CombatTypes.RANGED, true) && roll == 0)
					combatType = CombatTypes.RANGED;
				else
					combatType = CombatTypes.MAGIC;
				break;
			}

			int roll = Utility.randomNumber(3);

			if (roll == 0) {
				combatType = CombatTypes.MAGIC;
			} else if (roll == 1) {
				combatType = CombatTypes.RANGED;
			} else if (roll == 2) {
				combatType = CombatTypes.MELEE;
			}
			break;
		default:
			break;
		}

		getCombat().setCombatType(combatType);
		getCombat().setBlockAnimation(def.getBlock());

		switch (combatType) {
		case NONE:
			break;
		case MELEE:
			if (def.getMelee() == null || def.getMelee().length < 1) {
				remove();
				System.out.println("Null combat def error:melee for npc: " + npcId);
				return;
			}

			combatIndex = (byte) Utility.randomNumber(def.getMelee().length);
			Melee melee = def.getMelee()[combatIndex];
			getCombat().getMelee().setAttack(melee.getAttack(), melee.getAnimation());
			break;
		case MAGIC:
			if (def.getMagic() == null || def.getMagic().length < 1) {
				remove();
				System.out.println("Null combat def error:magic for npc: " + npcId);
				return;
			}

			combatIndex = (byte) Utility.randomNumber(def.getMagic().length);
			Magic magic = def.getMagic()[combatIndex];
			getCombat().getMagic().setAttack(magic.getAttack(), magic.getAnimation(), magic.getStart(), magic.getEnd(), magic.getProjectile());
			break;
		case RANGED:
			if (def.getRanged() == null || def.getRanged().length < 1) {
				remove();
				System.out.println("Null combat def error:ranged for npc: " + npcId);
				return;
			}

			combatIndex = (byte) Utility.randomNumber(def.getRanged().length);
			Ranged ranged = def.getRanged()[combatIndex];
			getCombat().getRanged().setAttack(ranged.getAttack(), ranged.getAnimation(), ranged.getStart(), ranged.getEnd(), ranged.getProjectile());
			break;
		}
	}
	

	/**
	 * If the entity is within the mobs walking distance
	 * 
	 * @param e
	 *            The entity the mob is following
	 * @return
	 */
	public boolean withinMobWalkDistance(Entity e) {
		if ((following.isIgnoreDistance()) || (owner != null)) {
			return true;
		}

		return Math.abs(e.getLocation().getX() - spawnLocation.getX()) + Math.abs(e.getLocation().getY() - spawnLocation.getY()) < getSize() * 2 + 6;
	}

	@Override
	public String toString() {
		return "Mob [spawnLocation=" + spawnLocation + ", npcId=" + npcId + ", attackable=" + attackable + ", owner=" + owner + "]";
	}

	public boolean isCanAttack() {
		return canAttack;
	}

	public void setCanAttack(boolean canAttack) {
		this.canAttack = canAttack;
	}

}