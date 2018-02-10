package com.vencillio.rs2.entity.movement;

import com.vencillio.VencillioConstants;
import com.vencillio.core.cache.map.Region;
import com.vencillio.rs2.content.minigames.duelarena.DuelingConstants;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendConfig;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendMultiInterface;

public class PlayerMovementHandler extends MovementHandler {
	
	private final Player player;

	public PlayerMovementHandler(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public boolean canMoveTo(int direction) {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		int z = player.getLocation().getZ();

		return Region.getRegion(x, y).canMove(x, y, z, direction);
	}

	@Override
	public boolean canMoveTo(int x, int y, int size, int direction) {
		int z = player.getLocation().getZ();

		return Region.getRegion(x, y).canMove(x, y, z, direction);
	}

	@Override
	public void process() {
		if ((player.isDead()) || (player.isFrozen())  || (player.isStunned()) || (player.getMagic().isTeleporting()) || ((player.getDueling().isDueling()) && player.getDueling().getRuleToggle()[DuelingConstants.NO_MOVEMENT])) {
			reset();
			return;
		}

		Point walkPoint = waypoints.poll();

		if ((walkPoint != null) && (walkPoint.getDirection() != -1)) {
			if (player.getRunEnergy().isResting()) {
				player.getRunEnergy().toggleResting();
			}

			if ((!forceMove) && (!VencillioConstants.WALK_CHECK) && (!Region.getRegion(player.getLocation()).canMove(player.getLocation(), walkPoint.getDirection()))) {
				reset();
				return;
			}

			player.getMovementHandler().getLastLocation().setAs(player.getLocation());
			player.getLocation().move(com.vencillio.rs2.GameConstants.DIR[walkPoint.getDirection()][0], com.vencillio.rs2.GameConstants.DIR[walkPoint.getDirection()][1]);
			primaryDirection = walkPoint.getDirection();
			flag = true;
		} else {
			if (flag) {
				flag = false;
			}
			return;
		}

		if (player.getRunEnergy().isRunning()) {
			if (player.getRunEnergy().canRun()) {
				Point runPoint = waypoints.poll();

				if ((runPoint != null) && (runPoint.getDirection() != -1)) {
					if ((!forceMove) && (!VencillioConstants.WALK_CHECK) && (!Region.getRegion(player.getLocation()).canMove(player.getLocation(), runPoint.getDirection()))) {

						reset();
						return;
					}

					player.getMovementHandler().getLastLocation().setAs(player.getLocation());
					player.getLocation().move(com.vencillio.rs2.GameConstants.DIR[runPoint.getDirection()][0], com.vencillio.rs2.GameConstants.DIR[runPoint.getDirection()][1]);
					secondaryDirection = runPoint.getDirection();

					player.getRunEnergy().onRun();
				}
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You don't have enough run energy to do that."));
				player.getRunEnergy().reset();
				player.getClient().queueOutgoingPacket(new SendConfig(173, player.getRunEnergy().isRunning() ? 1 : 0));
			}

		}

		ControllerManager.setControllerOnWalk(player);

		if (player.inMultiArea())
			player.getClient().queueOutgoingPacket(new SendMultiInterface(true));
		else {
			player.getClient().queueOutgoingPacket(new SendMultiInterface(false));
		}

		player.checkForRegionChange();

		if ((forceMove) && (waypoints.size() == 0))
			forceMove = false;
	}
}
