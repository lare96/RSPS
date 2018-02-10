package com.vencillio.rs2.entity.mob.impl;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

public class Kreearra extends Mob {
	
	public static final int KREEARRA = 3162;
	public static final int SPECIAL_CHANCE = 20;
	public static final int TORNADO_A = 1196;
	public static final int TORNADO_B = 1197;
	public static final int TORNADO_X = 2833;
	public static final int TORNADO_Y = 5302;
	private boolean tornados = false;

	public Kreearra() {
		super(3162, true, new Location(2831, 5303, 2));
	}

	public void checkForDamage(Location a) {
		for (Player player : getCombatants()) {
			Location b = player.getLocation();
			if ((Math.abs(a.getX() - b.getX()) <= 1) && (Math.abs(a.getY() - b.getY()) <= 1))
				player.hit(new Hit(Utility.randomNumber(20)));
		}
	}

	@Override
	public void doAliveMobProcessing() {
		if ((getCombat().getAttacking() != null) && (!tornados) && (Utility.randomNumber(20) == 0)) {
			tornados = true;
			initTornados();
		}
	}

	public void initTornados() {
		final Location a = new Location(2833, 5302, 2);
		final Location b = new Location(2833, 5302, 2);
		final Location c = new Location(2833, 5302, 2);
		final Location d = new Location(2833, 5302, 2);

		getUpdateFlags().sendForceMessage("Feel Armadyl's power!");

		TaskQueue.queue(new Task(1) {
			byte stage = 0;

			@Override
			public void execute() {
				if (isDead()) {
					stop();
					return;
				}

				a.move(1, 1);
				World.sendStillGraphic(1196, 0, a);
				checkForDamage(a);

				b.move(-1, 1);
				World.sendStillGraphic(1197, 0, b);
				checkForDamage(b);

				c.move(-1, -1);
				World.sendStillGraphic(1196, 0, c);
				checkForDamage(c);

				d.move(1, -1);
				World.sendStillGraphic(1197, 0, d);
				checkForDamage(d);

				if ((this.stage = (byte) (stage + 1)) == 5) {
					tornados = false;
					stop();
				}
			}

			@Override
			public void onStop() {
			}
		});
	}
}
