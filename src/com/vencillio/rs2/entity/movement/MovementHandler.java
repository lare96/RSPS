package com.vencillio.rs2.entity.movement;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;

public abstract class MovementHandler {
	protected int primaryDirection = -1;
	protected int secondaryDirection = -1;
	protected Location lastLocation = new Location(0, 0);
	protected Deque<Point> waypoints = new ConcurrentLinkedDeque<Point>();
	protected final Entity entity;
	protected boolean flag = false;
	
	protected Location forceStart;
	protected Location forceEnd;
	protected short forceSpeed1;
	protected short forceSpeed2;
	protected byte forceDirection;
	
	protected boolean forceMove = false;

	private boolean forced = false;

	public MovementHandler(Entity entity) {
		this.entity = entity;
	}

	private void addStep(int x, int y) {
		if (waypoints.size() >= 100) {
			return;
		}
		Point last = waypoints.peekLast();
		int deltaX = x - last.getX();
		int deltaY = y - last.getY();
		int direction = Utility.direction(deltaX, deltaY);
		if (direction > -1)
			waypoints.add(new Point(x, y, direction));
	}

	public void addToPath(Location location) {
		if (waypoints.size() == 0) {
			reset();
		}
		Point last = waypoints.peekLast();
		int deltaX = location.getX() - last.getX();
		int deltaY = location.getY() - last.getY();
		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		for (int i = 0; i < max; i++) {
			if (deltaX < 0)
				deltaX++;
			else if (deltaX > 0) {
				deltaX--;
			}
			if (deltaY < 0)
				deltaY++;
			else if (deltaY > 0) {
				deltaY--;
			}
			addStep(location.getX() - deltaX, location.getY() - deltaY);
		}
	}

	public abstract boolean canMoveTo(int paramInt);

	public abstract boolean canMoveTo(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

	public void finish() {
		waypoints.removeFirst();
	}

	public void flag() {
		flag = true;
		forced = false;
	}
	
	public Location getForceStart() {
		return forceStart;
	}
	
	public void setForceStart(Location forceStart) {
		this.forceStart = forceStart;
	}
	
	public Location getForceEnd() {
		return forceEnd;
	}
	
	public void setForceEnd(Location forceEnd) {
		this.forceEnd = forceEnd;
	}
	
	public short getForceSpeed1() {
		return forceSpeed1;
	}
	
	public void setForceSpeed1(short forceSpeed1) {
		this.forceSpeed1 = forceSpeed1;
	}
	
	public short getForceSpeed2() {
		return forceSpeed2;
	}
	
	public void setForceSpeed2(short forceSpeed2) {
		this.forceSpeed2 = forceSpeed2;
	}
	
	public byte getForceDirection() {
		return forceDirection;
	}
	
	public void setForceDirection(byte forceDirection) {
		this.forceDirection = forceDirection;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public int getPrimaryDirection() {
		return primaryDirection;
	}

	public int getSecondaryDirection() {
		return secondaryDirection;
	}

	public boolean hasDirection() {
		return primaryDirection != -1;
	}

	public boolean isFlagged() {
		return flag;
	}

	public boolean isForced() {
		return forced;
	}

	public boolean isForceMove() {
		return forceMove;
	}

	public boolean moving() {
		return (waypoints.size() > 0) && (!entity.isFrozen()) && (!entity.isStunned());
	}

	public abstract void process();

	public void reset() {
		waypoints.clear();

		Location p = entity.getLocation();
		waypoints.add(new Point(p.getX(), p.getY(), -1));
	}

	public void resetMoveDirections() {
		primaryDirection = -1;
		secondaryDirection = -1;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public void setForceMove(boolean forceMove) {
		this.forceMove = forceMove;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public void setPath(Deque<Point> path) {
		waypoints = path;
	}

	public void setPrimaryDirection(int primaryDirection) {
		this.primaryDirection = primaryDirection;
	}

	public void setSecondaryDirection(int secondaryDirection) {
		this.secondaryDirection = secondaryDirection;
	}
	
	public void walkTo(int x, int y) {
		Location location = entity.getLocation();
		int newX = location.getX() + x;
		int newY = location.getY() + y;
		reset();
		addToPath(new Location(newX, newY));
		finish();
	}
}
