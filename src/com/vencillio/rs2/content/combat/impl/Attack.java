package com.vencillio.rs2.content.combat.impl;

public class Attack {

	private int hitDelay;
	private int attackDelay;

	public Attack(int hitDelay, int attackDelay) {
		this.hitDelay = hitDelay;
		this.attackDelay = attackDelay;
	}

	public int getAttackDelay() {
		return attackDelay;
	}

	public int getHitDelay() {
		return hitDelay;
	}

	public void setAttackDelay(int attackDelay) {
		this.attackDelay = attackDelay;
	}

	public void setHitDelay(int hitDelay) {
		this.hitDelay = hitDelay;
	}
}
