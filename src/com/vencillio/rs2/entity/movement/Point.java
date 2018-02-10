package com.vencillio.rs2.entity.movement;

import com.vencillio.rs2.entity.Location;

public class Point extends Location {
	private int direction;

	public Point(int x, int y, int direction) {
		super(x, y);
		setDirection(direction);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
