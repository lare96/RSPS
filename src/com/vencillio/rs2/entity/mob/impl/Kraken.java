package com.vencillio.rs2.entity.mob.impl;

import java.text.SimpleDateFormat;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles the Kraken boss
 * @author Daniel
 *
 */
public class Kraken extends Mob {

	public Kraken(Player player, Location location) {
		super(player, 494, false, false, false, location);
		getCombat().setAttack(getOwner());
		TIME = System.currentTimeMillis();
	}
	
	private long TIME;
	
	@Override
	public void hit(Hit hit) {
	
		if (isDead() || getOwner() == null) {
			return;
		}
		
		super.hit(hit);
	
	}
	
	@Override
	public void onDeath() {
		for (Mob mobs : getOwner().tentacles) {
			if (!mobs.isDead()) {
				mobs.remove();
			}
		}
		getOwner().tentacles.clear();
		getOwner().whirlpoolsHit = 0;
		getOwner().send(new SendMessage("Fight duration: @red@" + new SimpleDateFormat("m:ss").format(System.currentTimeMillis() - TIME) + "</col>."));
	}
	
}
