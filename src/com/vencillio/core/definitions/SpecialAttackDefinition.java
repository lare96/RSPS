package com.vencillio.core.definitions;

public class SpecialAttackDefinition {

	private short id;
	private int barId1;
	private int barId2;
	private int button;
	private double amount;

	public double getAmount() {
		return amount;
	}

	public int getBarId1() {
		return barId1;
	}

	public int getBarId2() {
		return barId2;
	}

	public int getButton() {
		return button;
	}

	public int getId() {
		return id;
	}
}
