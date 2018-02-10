package com.vencillio.rs2.content.dwarfcannon;

import java.util.ArrayList;
import java.util.Iterator;

import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.HitTask;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.GameConstants;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.pathfinding.StraightPathFinder;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendAnimateObject;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Dwarf Cannon
 * @author Daniel
 *
 */
public class DwarfCannon extends RSObject {

	/**
	 * The rotation directions of the Dwarf Cannon
	 */
	public static int[] ROTATION_DIRECTIONS = { 515, 516, 517, 518, 519, 520, 521, 514 };

	/**
	 * The directions
	 */
	public static final int[] DIRECIONS = { 1, 2, 4, 7, 6, 5, 3 };

	/**
	 * The owner of the Dwarf Cannon
	 */
	private final Player cannonOwner;
	
	/**
	 * The Dwarf Cannon location
	 */
	private final Location cannonLocation;
	
	/**
	 * The location of the Dwarf Cannon owner
	 */
	private final Location ownerLocation;
	
	/**
	 * The amount of balls in Dwarf Cannon
	 */
	private int ammunition = 0;

	/**
	 * The current stage of Dwarf Cannon
	 */
	private byte stage = 1;

	/**
	 * Check to notify Dwarf Cannon owner that ammunition has been depleted
	 */
	private boolean notify = true;

	/**
	 * The direction
	 */
	private int dir = 7;

	/**
	 * Dwarf Cannon
	 * @param owner
	 * @param x
	 * @param y
	 * @param z
	 */
	public DwarfCannon(Player owner, int x, int y, int z) {
		super(x - 1, y - 1, z, 7, 10, 0);
		this.cannonOwner = owner;
		Region.getRegion(x, y).addObject(this);
		ObjectManager.register(getGameObject());
		cannonLocation = new Location(x - 1, y - 1, z);
		ownerLocation = new Location(x, y, z);
		cannonOwner.getUpdateFlags().sendAnimation(827, 0);
	}
	
	public boolean isOwner(Player player) {
		return cannonOwner.equals(player);
	}

	public GameObject getGameObject() {
		return new GameObject(getId(), getX(), getY(), getZ(), getType(), getFace());
	}
	
	public Hit getHit() {
		return new Hit(cannonOwner, Utility.randomNumber(31), Hit.HitTypes.CANNON);
	}
	
	public Location getLoc() {
		return cannonLocation;
	}
	
	public Projectile getCannonFire() {
		Projectile p = new Projectile(53);
		p.setStartHeight(50);
		p.setEndHeight(50);
		p.setCurve(0);
		return p;
	}
	
	
	/**
	 * Handles constructing the Dwarf Cannon
	 * @param id
	 * @return
	 */
	public boolean construct(int id) {
		if (stage == 1 && id == 8) {
			cannonOwner.getInventory().remove(8);
			stage = ((byte) (stage + 1));
			ObjectManager.removeFromList(getGameObject());
			setId(8);
			Region.getRegion(getX(), getY()).addObject(this);
			ObjectManager.register(getGameObject());
			cannonOwner.getUpdateFlags().sendAnimation(827, 0);
			return true;
		}
		if (stage == 2 && id == 10) {
			cannonOwner.getInventory().remove(10);
			stage = ((byte) (stage + 1));
			ObjectManager.removeFromList(getGameObject());
			setId(9);
			Region.getRegion(getX(), getY()).addObject(this);
			ObjectManager.register(getGameObject());
			cannonOwner.getUpdateFlags().sendAnimation(827, 0);
			return true;
		}
		if (stage == 3 && id == 12) {
			cannonOwner.getInventory().remove(12);
			stage = ((byte) (stage + 1));
			ObjectManager.removeFromList(getGameObject());
			setId(6);
			Region.getRegion(getX(), getY()).addObject(this);
			ObjectManager.register(getGameObject());
			World.addCannon(this);
			cannonOwner.getUpdateFlags().sendAnimation(827, 0);
			return true;
		}

		return false;
	}

	/**
	 * Gets the items for Dwarf Cannon stages
	 * @return
	 */
	public Item[] getItemsForStage() {
		switch (stage) {
		case 1:
			return new Item[] { new Item(6, 1) };
		case 2:
			return new Item[] { new Item(6, 1), new Item(8, 1) };
		case 3:
			return new Item[] { new Item(6, 1), new Item(8, 1), new Item(10) };
		case 4:
			return new Item[] { new Item(6, 1), new Item(8, 1), new Item(10, 1), new Item(12, 1) };
		}
		return null;
	}

	/**
	 * Gets all the Mobs in Dwarf Cannon path
	 * @return
	 */
	public Mob[] getMobsInPath() {
		ArrayList<Mob> attack = new ArrayList<Mob>();
		for (Iterator<Mob> mobs = cannonOwner.getClient().getNpcs().iterator(); mobs.hasNext();) {
			Mob mob = mobs.next();
			int dir = GameConstants.getDirection(Integer.signum(cannonLocation.getX() - mob.getX()), Integer.signum(cannonLocation.getY() - mob.getY()));
			if (DIRECIONS[dir] == this.dir) {
				boolean canAttack = !cannonOwner.getCombat().inCombat() || cannonOwner.inMultiArea() || (cannonOwner.getCombat().inCombat() && cannonOwner.getCombat().getLastAttackedBy().equals(mob));
				boolean clearPath = StraightPathFinder.isProjectilePathClear(getX(), getY(), cannonLocation.getZ(), mob.getX(), mob.getY());
				if (mob.getLevels()[3] > 0 && canAttack && clearPath) {
					attack.add(mob);
				}
			}
		}
		Mob[] mob = new Mob[attack.size()];
		for (int i = 0; i < mob.length; i++) {
			mob[i] = attack.get(i);
		}
		return mob;
	}

	/**
	 * Handles loading the Dwarf Cannon with ammunition
	 * @param player
	 * @param item
	 * @param obj
	 * @return
	 */
	public boolean load(Player player, int item, int obj) {
		if (!isOwner(player)) {
			player.send(new SendMessage("This is not your cannon!"));
			return true;
		}
		if (item != 2 && obj != 6) {
			return false;
		}
		if (!player.getInventory().hasItemId(2)) {
			player.send(new SendMessage("You do not have any Cannon balls."));
			return true;
		}
		int needed = 30 - ammunition;
		if (needed == 0) {
			player.send(new SendMessage("Your cannon is full."));
			return true;
		}
		int invBalls = player.getInventory().getItemAmount(2);
		if (invBalls <= needed) {
			player.getInventory().remove(2, invBalls);
			player.send(new SendMessage("You load the last of your cannon balls"));
			ammunition += invBalls;
		} else {
			player.getInventory().remove(2, needed);
			player.send(new SendMessage("You load " + needed + " balls into the cannon."));
			ammunition += needed;
		}
		return true;
	}

	/**
	 * Handles logging out with Dwarf Cannon
	 */
	public void onLogout() {
		if (!pickup(cannonOwner, getX(), getY())) {
			for (Item i : getItemsForStage()) {
				cannonOwner.getBank().add(i);
			}
			if (ammunition > 0) {
				cannonOwner.getBank().add(2, ammunition);
			}
			if (stage == 4) {
				World.removeCannon(this);
			}
			cannonOwner.getAttributes().remove("dwarfmulticannon");
			Region.getRegion(getX(), getY()).removeObject(this);
			ObjectManager.remove(getGameObject());
		}
	}

	/**
	 * Handles picking up the Dwarf Cannon
	 * @param player
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean pickup(Player player, int x, int y) {
		if (!isOwner(player)) {
			player.send(new SendMessage("This is not your cannon!"));
			return true;
		}
		if (!player.getInventory().hasSpaceFor(getItemsForStage())) {
			player.send(new SendMessage("You do not have enough inventory space to pick up your cannon."));
			return false;
		}
		if (stage == 4 && ammunition > 0 && !player.getInventory().hasSpaceFor(new Item(2, ammunition))) {
			player.send(new SendMessage("You do not have enough inventory space to pick up your cannon."));
			return false;
		}
		if (ammunition > 0) {
			player.getInventory().add(2, ammunition, false);
		}
		player.getUpdateFlags().sendFaceToDirection(getGameObject().getLocation());
		player.getInventory().add(getItemsForStage(), true);
		Region.getRegion(x, y).removeObject(this);
		ObjectManager.remove(getGameObject());
		player.getAttributes().remove("dwarfmulticannon");
		cannonOwner.getUpdateFlags().sendAnimation(827, 0);
		if (stage == 4) {
			World.removeCannon(this);
		}
		return true;
	}

	/**
	 * Rotates the Dwarf Cannon
	 * @param player
	 */
	public void rotate(Player player) {
		if (ammunition != 0) {
			player.send(new SendAnimateObject(this, ROTATION_DIRECTIONS[dir]));			
		}
	}

	/**
	 * Tick of Dwarf Cannon
	 */
	public void tick() {
		if (stage != 4) {
			return;
		}
		dir = (dir == 7 ? 0 : dir + 1);
		if (!cannonLocation.isViewableFrom(cannonOwner.getLocation())) {
			return;
		}
		if (ammunition == 0) {
			if (!notify) {
				notify = true;
				cannonOwner.send(new SendMessage("You have run out of Cannonballs!"));
			}
			return;
		}
		if (notify) {
			notify = false;
		}
		Mob[] mobs = getMobsInPath();
		if (mobs != null)
			for (Mob i : mobs)
				if (i != null) {
					int lockon = i.getIndex() + 1;
					byte offsetX = (byte) ((i.getLocation().getY() - i.getLocation().getY()) * -1);
					byte offsetY = (byte) ((i.getLocation().getX() - i.getLocation().getX()) * -1);
					World.sendProjectile(getCannonFire(), ownerLocation, lockon, offsetX, offsetY);
					Hit hit = getHit();
					TaskQueue.queue(new HitTask(3, false, hit, i));
					cannonOwner.getSkill().addCombatExperience(CombatTypes.RANGED, hit.getDamage());
					if (--ammunition == 0) {
						break;
					}
			}
		}
	
}
