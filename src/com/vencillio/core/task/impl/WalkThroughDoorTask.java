package com.vencillio.core.task.impl;

import com.vencillio.core.cache.map.Door;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

public class WalkThroughDoorTask extends Task {

	protected final Player p;
	protected final Door door;
	protected final int xMod;
	protected final int yMod;
	protected byte stage = 0;
	protected final Controller start;
	private final Location dest;

	public WalkThroughDoorTask(Player p, int x, int y, int z, Location dest) {
		super(p, 1, true);
		this.p = p;
		this.door = Region.getDoor(x, y, z);
		start = p.getController();
		p.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		p.getMovementHandler().setForceMove(true);
		
		xMod = dest.getX() - p.getLocation().getX();
		yMod = dest.getY() - p.getLocation().getY();

		this.dest = dest;
		
		if ((xMod != 0 && yMod != 0) || door == null) {
			p.setController(start);
			stop();
		}
	}

	@Override
	public void execute() {
		if (stage == 0) {
			p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
			ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
			door.append();
			ObjectManager.send(new GameObject(door.getCurrentId(), door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
		} else if (stage == 1) {
			p.getMovementHandler().walkTo(xMod, yMod);
			p.setController(start);
		} else if (stage == 2) {
			ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
			door.append();
			ObjectManager.send(new GameObject(door.getCurrentId(), door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
			stop();
		}

		stage++;
	}

	@Override
	public void onStop() {
		p.setController(start);
		p.teleport(dest);
		p.getMovementHandler().setForceMove(false);
	}

}
