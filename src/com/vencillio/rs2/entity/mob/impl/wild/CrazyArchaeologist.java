package com.vencillio.rs2.entity.mob.impl.wild;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.Hit.HitTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the Crazy Archaeologist boss
 * @author Daniel
 *
 */
public class CrazyArchaeologist extends Mob {

	public CrazyArchaeologist() {
		super(6618, true, new Location(2969, 3708, 0));
	}
	
	private String[] messages = {
		"I'm Bellock - respect me!",
		"Get off my site!",
		"No-one messes with Bellock's dig!",
		"These ruins are mine!",
		"Taste my knowledge!",
		"You belong in a museum!",
	};
	
	private final String specialMessage = "Rain of knowledge!";
	
	private final String deathMessage = "Ow!";
	
	private boolean usingSpecial = false;
	
	@Override
	public void onHit(Entity entity, Hit hit) {
		if (entity != null && !entity.isNpc()) {
			int random = Utility.random(10);
			if (random == 1) {
				usingSpecial = true;
				getUpdateFlags().sendForceMessage(specialMessage);	
				special(entity.getPlayer());
			} else if (!isDead() || !usingSpecial) {
				getUpdateFlags().sendForceMessage(Utility.randomElement(messages));				
			}
		}
	}
	
	@Override
	public void onDeath() {
		getUpdateFlags().sendForceMessage(deathMessage);		
	}
	
	public void special(Player player) {
		for (int i = 0; i < 3; i++) {
			int offsetX = player.getX() - getX();
			int offsetY = player.getY() - getY();
			if (i == 0 || i == 2) {
				offsetX += i == 0 ? -1 : 1;
				offsetY++;
			}
			Location end = new Location(getX() + offsetX, getY() + offsetY, 0);
			World.sendProjectile(new Projectile(551, 1, 10, 100, 65, 10, 20), getLocation(), -1, (byte) offsetX, (byte) offsetY);
			World.sendStillGraphic(659, 100, end);
			TaskQueue.queue(new Task(player, 3, false) {
				@Override
				public void execute() {
					stop();
				}
	
				@Override
				public void onStop() {
					if (player.getLocation().equals(end)) {
						int damage = Utility.random(15) + Utility.random(15) + 12;
						if (damage > 23) {
							damage = 23;
						}
						player.hit(new Hit(damage, HitTypes.MAGIC));
						usingSpecial = false;
					}
				}
			});
		}			
	}
	
}
