package com.vencillio.rs2.entity.mob.impl.wild;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.Hit.HitTypes;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Vet'ion boss
 * @author Daniel
 *
 */
public class Vetion extends Mob {

	/**
	 * Vet'ion
	 */
	public Vetion() {
		super(6611, true, new Location(3214, 3793, 0));
	}
	
	/**
	 * Vet'ion's pets
	 */
	private Mob pet1, pet2;
	private boolean spawnPets = true;
	private boolean secondTrans = false;
	
	/**
	 * Handles Vet'ion hitting
	 */
	@Override
	public void onHit(Entity entity, Hit hit) {
		if (entity != null && !entity.isNpc()) {
			int random = Utility.random(10);
			if (random == 1) {
				earthquake();
			}
		}
	}
	
	/**
	 * Handles attacking entity's hit
	 */
	@Override
	public void hit(Hit hit) {
		if (isDead()) {
			return;
		}
		
		if (pet1 != null && pet2 != null) {
			if (!pet1.isDead() || !pet2.isDead()) {
				if (hit.getAttacker() != null || !hit.getAttacker().isNpc()) {
					hit.getAttacker().getPlayer().send(new SendMessage("@dre@Vet'ion can't take damage with his pets spawned!"));				
					return;
				}
			} else if (pet1.isDead() && pet2.isDead()) {
				spawnPets = false;
				setTakeDamage(true);
			}
		}

		super.hit(hit);
		
		if (!secondTrans && getLevels()[3] - hit.getDamage() <= 0) {
			doReborn();
		}
		
		if (spawnPets && getLevels()[3] <= 127 && hit.getDamage() >= 0) {
			if (getId() == 6611) {
				spawnPets(6613);			
			} else if (getId() == 6612) {
				spawnPets(6614);	
			}
		}
	}
	
	/**
	 * Updates Vet'ion's combat type
	 */
	@Override
	public void updateCombatType() {
		if (getCombat().getAttacking() != null) {
			if (!getCombat().getAttacking().isNpc()) {
				if (!getCombat().withinDistanceForAttack(CombatTypes.MELEE, true)) {
					getCombat().setCombatType(CombatTypes.MAGIC);
					castLightning(getCombat().getAttacking().getPlayer());
				} else {
					getCombat().setCombatType(CombatTypes.MELEE);
				}
			}
		}
	}
	
	/**
	 * Handles Vet'ion's reborn effect
	 */
	private void doReborn() {
		transform(6612);
		getLevels()[3] = 255;
		getUpdateFlags().sendForceMessage("Do it again!");
		spawnPets = true;
		secondTrans = true;
		pet1 = pet2 = null;
	}
	
	/**
	 * Handles Vet'ion's lighting effects
	 * @param player
	 */
	private void castLightning(Player player) {
		for (int i = 0; i < 3; i++) {
			int offsetX = player.getX() - getX();
			int offsetY = player.getY() - getY();
			if (i == 0 || i == 2) {
				offsetX += i == 0 ? -1 : 1;
				offsetY++;
			}
			Location end = new Location(getX() + offsetX, getY() + offsetY, 0);
			World.sendProjectile(new Projectile(592, 1, 10, 100, 65, 10, 20), getLocation(), -1, (byte) offsetX, (byte) offsetY);
			World.sendStillGraphic(775, 100, end);
			TaskQueue.queue(new Task(player, 3, false) {
				@Override
				public void execute() {
					stop();
				}

				@Override
				public void onStop() {
					if (player.getLocation().equals(end)) {
						player.hit(new Hit(30, HitTypes.MAGIC));
						player.getUpdateFlags().sendForceMessage("OUCH!");
					}
				}
			});
		}
	}
	
	/**
	 * Handles Vet'ion's earthquake effect
	 */
	private void earthquake() {
		getUpdateFlags().sendAnimation(new Animation(5507));
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isActive()) {
				continue;
			}
			
			if (Utility.getExactDistance(player.getLocation(), getLocation()) <= 11) {
				player.hit(new Hit(25 + Utility.random(20), HitTypes.MELEE));
				player.send(new SendMessage("Vet'ion pummels the ground sending a shattering earthquake shockwave through you."));
			}
		}
	}
	
	/**
	 * Handles spawning Vet'ion's pets
	 * @param npcID
	 */
	private void spawnPets(int npcID) {
		setTakeDamage(false);
		pet1 = new Mob(null, npcID, true, false, new Location(getX(), getY() - 2, getZ()));
		pet2 = new Mob(null, npcID, true, false, new Location(getX(), getY() + 2, getZ()));
		getUpdateFlags().sendForceMessage(npcID == 6613 ? "Kill, my pets!" : "Bahh! Go, dogs!!");
		pet1.getUpdateFlags().sendForceMessage("GRRRRRRRRRRRR");
		pet2.getUpdateFlags().sendForceMessage("GRRRRRRRRRRRR");
		if (getCombat().getAttacking() != null) {
			if (!getCombat().getAttacking().isNpc()) {
				pet1.getCombat().setAttack(getCombat().getAttacking() == null ? getCombat().getLastAttackedBy() : getCombat().getAttacking());
				pet2.getCombat().setAttack(getCombat().getAttacking() == null ? getCombat().getLastAttackedBy() : getCombat().getAttacking());
			}
		}
	}	
	

	@Override
	public void onDeath() {
		transform(6611);
		spawnPets = true;
		secondTrans = false;
		pet1 = pet2 = null;
	}
}
