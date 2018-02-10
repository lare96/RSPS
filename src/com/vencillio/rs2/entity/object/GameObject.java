package com.vencillio.rs2.entity.object;

import com.vencillio.rs2.entity.Location;

public class GameObject {

	private int id;
	private final Location p;
	private final byte type;
	private final byte face;
	private final boolean overrideZ;

	public GameObject(int x, int y, int z) {
		this(0, x, y, z, 0, 0);
	}

	public GameObject(int id, int x, int y, int z, int type, int face) {
		this(id, x, y, z, type, face, false);
	}

	public GameObject(int id, int x, int y, int z, int type, int face, boolean overrideZ) {
		this.id = id;
		p = new Location(x, y, z);
		this.type = ((byte) type);
		this.face = ((byte) face);
		this.overrideZ = overrideZ;
	}

	public GameObject(int id, Location location, int type, int face) {
		this.id = id;
		p = location;
		this.type = ((byte) type);
		this.face = ((byte) face);
		overrideZ = false;
	}

	@Override
	public boolean equals(Object o) {
		if ((o instanceof GameObject)) {
			GameObject g = (GameObject) o;
			return g.getLocation().equals(p);
		}
		return false;
	}

	public int getFace() {
		return face;
	}

	public int getId() {
		return id;
	}

	public Location getLocation() {
		return p;
	}

	public int getType() {
		return type;
	}

	public boolean isOverrideZ() {
		return overrideZ;
	}

	public void setId(int id) {
		this.id = id;
		ObjectManager.send(this);
	}
}
