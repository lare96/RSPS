package com.vencillio.rs2.entity.item.impl;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public class GroundItem {
	private Item item;
	private Location location;
	private short time;
	private boolean global = false;
	private String owner;
	private final long longOwnerName;
	private boolean exists = true;
	private String include = null;

	/**
	 * If the item is a global item
	 */
	public boolean isGlobal;

	/**
	 * The time it takes to respawn the item
	 */
	private int respawnTimer;

	public GroundItem(Item item, Location location) {
		this(item, location, 0, null);
	}

	/**
	 * Creates a new global ground item
	 * 
	 * @param item
	 * @param location
	 * @param respawnTimer
	 */
	public GroundItem(Item item, Location location, int respawnTimer) {
		this(item, location, 0, null);
		this.respawnTimer = respawnTimer;
		this.global = true;
		this.isGlobal = true;
	}

	public GroundItem(Item item, Location location, int time, String owner) {
		this.item = item;
		this.location = location;
		this.time = ((short) time);
		this.owner = owner;
		if (owner != null)
			longOwnerName = Utility.nameToLong(owner);
		else
			longOwnerName = 0L;
	}

	public GroundItem(Item item, Location location, String owner) {
		this(item, location, 0, owner);
	}

	public void countdown() {
		if (!isGlobal) {
			time = ((short) (time + 1));
		}
	}

	@Override
	public boolean equals(Object o) {
		if ((o instanceof GroundItem)) {
			GroundItem other = (GroundItem) o;

			return (item.equals(other.getItem())) && (location.equals(other.getLocation())) && (longOwnerName == other.getLongOwnerName()) && (global == other.isGlobal());
		}

		return false;
	}

	public void erase() {
		exists = false;
	}

	public boolean exists() {
		return exists;
	}

	public Item getItem() {
		return item;
	}

	public Location getLocation() {
		return location;
	}

	public long getLongOwnerName() {
		return longOwnerName;
	}

	public Player getOwner() {
		return World.getPlayerByName(owner);
	}

	public String getOwnerName() {
		return owner;
	}

	public int getRespawnTimer() {
		return respawnTimer;
	}

	public boolean globalize() {
		return (time == 100) && (item.getDefinition().isTradable());
	}

	public boolean isGlobal() {
		return global;
	}

	public boolean remove() {
		return (time >= 350) || (time < 0);
	}

	public void resetTime() {
		time = 0;
	}
	
	public void include(String include) {
		this.include = include;
	}
	
	public String include() {
		return include;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setTime(int time) {
		this.time = ((short) time);
	}

	@Override
	public String toString() {
		return "GroundItem [item=" + item + ", owner=" + owner + "] Global: " + global + "";
	}
}
