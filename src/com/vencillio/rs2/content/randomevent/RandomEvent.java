package com.vencillio.rs2.content.randomevent;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public enum RandomEvent {
	SINGLETON;
	
	public boolean testRandom(Player player) {
		if (Utility.random(100) == 0) {
			if (canStart(player)) {
				start(player);
			}
			return true;
		}
		
		return false;
	}
	
	private boolean canStart(Player player) {
		if (player.getCombat().inCombat() || player.getCombat().getAttacking() != null) {
			return false;
		}

		if (player.inWilderness()) {
			return false;
		}
		
		if (player.getDueling().isDueling()) {
			return false;
		}
		
		if (player.getTrade().trading()) {
			return false;
		}
		
		return true;
	}
	
	private void start(Player player) {
		player.send(new SendInterface(16135));
		player.send(new SendString("", 16144));
	}
	
	public boolean clickButton(Player player, int button) {
		return false;
	}
}