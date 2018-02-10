package com.vencillio.rs2.content.minigames.pestcontrol.monsters;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.FollowToEntityTask;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.minigames.pestcontrol.Pest;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControlConstants;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControlGame;
import com.vencillio.rs2.entity.Location;

public class Spinner extends Pest {
	
	private final Portal portal;
	private final Task heal = null;

	public Spinner(Location l, PestControlGame game, Portal portal) {
		super(game, PestControlConstants.SPINNERS[Utility.randomNumber(PestControlConstants.SPINNERS.length)], l);
		setRetaliate(false);
		this.portal = portal;
	}

	public void heal() {
		if ((heal == null) || (heal.stopped())) {
			TaskQueue.queue(new FollowToEntityTask(this, portal) {
				@Override
				public void onDestination() {
					getUpdateFlags().sendAnimation(3911, 0);
					portal.heal(5);
					stop();
				}
			});
		}
	}

	@Override
	public void tick() {
		if (portal.isDead()) {
			return;
		}

		if ((portal.isDamaged()) && (Utility.randomNumber(3) == 0)) {
			heal();
		}
	}
}
