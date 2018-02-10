package com.vencillio.core.definitions;

public class EquipmentDefinition {

	private short id;
	private byte slot;
	private byte[] requirements;

	public int getId() {
		return id;
	}

	public byte[] getRequirements() {
		return requirements;
	}

	public int getSlot() {
		return slot;
	}

	public void setRequirements(byte[] requirements) {
		this.requirements = requirements;
	}
}
