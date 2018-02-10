package com.vencillio.rs2.entity.mob.impl.wild;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Chaos Elemental Combat
 * @author Daniel
 *
 */
public class ChaosElemental extends Mob {
	
	/**
	 * Spawning Chaos Elemental
	 */
	public ChaosElemental() {
		super(2054, true, new Location(3277, 3921, 0));
	}
	
	/**
	 * Player doing damage to MOB
	 */
	@Override
	public void hit(Hit hit) {

		if (isDead()) {
			return;
		}
		
		super.hit(hit);
		
		int random = Utility.random(20);
		
		if (random ==  5) {
			teleportSpecial();
		} else if (random == 10) {
			equipmentSpecial();
		}
	
	}
	
	/**
	 * Respawn time of Chaos Elemental
	 */
	@Override
	public int getRespawnTime() {
		return 60;
	}
	
	/**
	 * Sending Projectile
	 * @param id
	 * @return
	 */
	private Projectile getProjectile(int id) {
		return new Projectile(id, 1, 40, 70, 43, 31, 16);
	}
	
	/**
	 * Checks who is attack Chaos Elemental
	 * @return
	 */
	private boolean attacking() {
		if (getCombat().getAttacking() != null) {
			if (!getCombat().getAttacking().isNpc()) {
				return true;				
			}
		}
		return false;
	}
	
	/**
	 * Handles teleporting attacker away
	 */
	private void teleportSpecial() {
		if (attacking()) {
			Player player = (Player) getCombat().getAttacking();
			World.sendProjectile(getProjectile(553), player, this);
			player.teleport(new Location(player.getX() - Utility.random(3), player.getY() - Utility.random(3), 0));
			player.send(new SendMessage("The Chaos Elemental has teleported you away from it."));
			player.getUpdateFlags().sendGraphic(new Graphic(554));
		}
	}
	
	/**
	 * Handles removing equipment to attacker
	 */
	private void equipmentSpecial() {
		if (attacking()) {
			Player player = (Player) getCombat().getAttacking();
			World.sendProjectile(getProjectile(556), player, this);
			if (player.getEquipment().getItems()[3] != null) {
				if (player.getInventory().getFreeSlots() == 0) {
					int id = player.getInventory().getSlotId(0);
					player.getGroundItems().dropFull(id, 0);
				}
				player.getEquipment().unequip(3);							
				player.send(new SendMessage("The Chaos Elemental has removed some of your worn equipment."));
				player.getUpdateFlags().sendGraphic(new Graphic(557));
			}
		}
	}

}
