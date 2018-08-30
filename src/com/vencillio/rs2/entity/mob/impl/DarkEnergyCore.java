package com.vencillio.rs2.entity.mob.impl;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.Projectile;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

import java.util.List;

public class DarkEnergyCore extends Mob {

	public static int CORPOREAL_BEAST_INDEX;
	
	public static final int DARK_ENERGY_CORE_ID = 320;

	public static final Mob getCorp() {
		return World.getNpcs()[CORPOREAL_BEAST_INDEX];
	}

	private static final Projectile getProjectile() {
		return new Projectile(0);
	}

	public static final Mob[] spawn(int corpIndex) {
		System.out.println("In dark energy core spawn");

		Mob corp = World.getNpcs()[corpIndex];

		CORPOREAL_BEAST_INDEX = corpIndex;

		List<Player> players = corp.getCombatants();

		Mob[] cores = new Mob[players.size()];

		System.out.println("cores size: " + players.size());
		for (int i = 0; i < players.size(); i++) {
			Location l = new Location(players.get(i).getLocation());
			System.out.println("Dark core location: " + l);
			l.move(1, 0);
			cores[i] = new DarkEnergyCore(l, players.get(i));
		}

		return cores;
	}

	public boolean moving = false;

	private final Player bind;

	private byte pause = -1;

	public DarkEnergyCore(Location location, Player bind) {
		super(DARK_ENERGY_CORE_ID, false, location, null, false, false, null);
		this.bind = bind;
		getLevels()[3] = 25;
	}

	public Hit getHit() {
		return new Hit(this, Utility.randomNumber(10), Hit.HitTypes.NONE);
	}

	@Override
	public void onHit(Entity e, Hit hit) {
		Mob corp = getCorp();
		int tmp9_8 = 3;
		short[] tmp9_5 = corp.getLevels();
		tmp9_5[tmp9_8] = ((short) (tmp9_5[tmp9_8] + hit.getDamage() / 4));

		if (corp.getLevels()[3] > corp.getMaxLevels()[3])
			corp.getLevels()[3] = corp.getMaxLevels()[3];
	}

	@Override
	public void process() {
		if ((bind.isDead()) || (!bind.isActive()) || (getCorp().isDead()) || (!getCorp().getCombatants().contains(bind))) {
			remove();
			return;
		}

		if ((!moving) && (!isDead()))
			if ((Math.abs(bind.getLocation().getX() - getLocation().getX()) <= 1) && (Math.abs(bind.getLocation().getY() - getLocation().getY()) <= 1)) {
				bind.hit(getHit());
			} else {
				if (pause == -1) {
					pause = 4;
				}

				if ((this.pause = (byte) (pause - 1)) == 0) {
					pause = -1;
					travel();
				}
			}
	}

	public void travel() {
		moving = true;

		final int lockon = -bind.getIndex() - 1;
		final byte offsetX = (byte) ((bind.getLocation().getY() - bind.getLocation().getY()) * -1);
		final byte offsetY = (byte) ((bind.getLocation().getX() - bind.getLocation().getX()) * -1);

		final Projectile p = getProjectile();

		TaskQueue.queue(new Task(this, 1) {
			byte stage = 0;

			@Override
			public void execute() {
				if (stage == 0) {
					World.sendProjectile(p, getLocation(), lockon, offsetX, offsetY);
				} else if (stage == 1) {
					getUpdateFlags().sendAnimation(10393, 0);
					face(bind);
				} else if (stage == 2) {
					setVisible(false);
				} else if (stage == 4) {
					moving = false;
					teleport(new Location(bind.getLocation().getX() + 1, bind.getLocation().getY()));
					setVisible(true);
					stop();
				}

				stage = ((byte) (stage + 1));
			}

			@Override
			public void onStop() {
			}
		});
	}
}
