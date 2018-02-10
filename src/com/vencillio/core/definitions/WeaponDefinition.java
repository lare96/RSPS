package com.vencillio.core.definitions;

import com.vencillio.rs2.content.combat.Combat.CombatTypes;

public class WeaponDefinition {

	private short id;
	private boolean twoHanded;
	private CombatTypes type;
	private short[] attackAnimations;
	private byte[] attackSpeeds;
	private short block;
	private short stand;
	private short walk;
	private short run;
	private int sidebarId;
	private int standTurn;
	private int turn180;
	private int turn90CW;
	private int turn90CCW;

	public short[] getAttackAnimations() {
		return attackAnimations;
	}

	public byte[] getAttackSpeeds() {
		return attackSpeeds;
	}

	public int getBlock() {
		return block;
	}

	public int getId() {
		return id;
	}

	public int getRun() {
		return run;
	}

	public int getSidebarId() {
		return sidebarId;
	}

	public int getStand() {
		return stand;
	}

	public int getStandTurn() {
		return standTurn;
	}

	public int getTurn180() {
		return turn180;
	}

	public int getTurn90CCW() {
		return turn90CCW;
	}

	public int getTurn90CW() {
		return turn90CW;
	}

	public CombatTypes getType() {
		return type;
	}

	public int getWalk() {
		return walk;
	}
	
	public boolean isTwoHanded() {
		return twoHanded;
	}
}
