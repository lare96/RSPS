package com.vencillio.rs2.entity.mob.impl.wild;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.Hit.HitTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Chaos Fanatic boss
 * @author Daniel
 *
 */
public class ChaosFanatic extends Mob {
	
	public ChaosFanatic() {
		super(6619, true, new Location(2980, 3846, 0));
	}
	
	private String[] messages = {
		"Burn!",
		"WEUGH!",
		"Develish Oxen Roll!",
		"All your wilderness are belong to them!",
		"AhehHeheuhHhahueHuUEehEahAH",
		"I shall call him squidgy and he shall be my squidgy!",
	};
	
	@Override
	public void onHit(Entity entity, Hit hit) {
		if (entity != null && !entity.isNpc()) {
			getUpdateFlags().sendForceMessage(Utility.randomElement(messages));
			int random = Utility.random(10);
			if (random == 1) {
				castOrbs(entity.getPlayer());
			}
		}
	}
	
	private void castOrbs(Player player) {
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
						player.hit(new Hit(Utility.random(15) + Utility.random(15) + 10, HitTypes.MAGIC));
						if (player.getEquipment().getItems()[3] != null) {
							if (player.getInventory().getFreeSlots() == 0) {
								int id = player.getInventory().getSlotId(0);
								player.getGroundItems().dropFull(id, 0);
							}
							player.getEquipment().unequip(3);							
							player.send(new SendMessage("The Chaos Fanatic has removed some of your worn equipment."));
							player.getUpdateFlags().sendGraphic(new Graphic(557));
						}
					}
				}
			});
		}	
	}

}
