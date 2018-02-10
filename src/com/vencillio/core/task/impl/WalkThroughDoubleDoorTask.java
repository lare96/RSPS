package com.vencillio.core.task.impl;

import com.vencillio.core.cache.map.DoubleDoor;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

public class WalkThroughDoubleDoorTask extends Task {

	protected final Player p;
	protected final DoubleDoor door;
	protected final int xMod;
	protected final int yMod;
	protected byte stage = 0;
	protected final Controller start;

	public WalkThroughDoubleDoorTask(Player p, int x, int y, int z, Location dest) {
		super(p, 1, true);
		this.p = p;
		this.door = Region.getDoubleDoor(x, y, z);
		start = p.getController();
		p.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
		
		int xDiff = dest.getX() - p.getLocation().getX();
		int yDiff = dest.getY() - p.getLocation().getY();

		if (xDiff != 0)
			xMod = (xDiff < 0 ? 1 : -1);
		else
			xMod = 0;
		if (yDiff != 0)
			yMod = (yDiff < 0 ? 1 : -1);
		else
			yMod = 0;

		if (door == null) {
			p.setController(start);
			stop();
		}
		
		p.getMovementHandler().setForceMove(true);
	}

	@Override
	public void execute() {
		if (stage == 0) {
			p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
			ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
			ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
			door.append();
			ObjectManager.send(new GameObject(door.getCurrentId1(), door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
			ObjectManager.send(new GameObject(door.getCurrentId2(), door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
		} else if (stage == 1) {
			p.getMovementHandler().walkTo(xMod, yMod);
			p.setController(start);
		} else if (stage == 2) {
			ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
			ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
			door.append();
			ObjectManager.send(new GameObject(door.getCurrentId1(), door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
			ObjectManager.send(new GameObject(door.getCurrentId2(), door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
			stop();
		}

		stage++;
	}

	@Override
	public void onStop() {
		p.getMovementHandler().setForceMove(false);
		p.setController(start);
	}
}