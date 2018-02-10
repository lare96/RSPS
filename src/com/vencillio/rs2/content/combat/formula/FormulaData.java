package com.vencillio.rs2.content.combat.formula;

import java.util.Random;

public class FormulaData {

	public static final Random r = new Random(System.currentTimeMillis());

	public static boolean isAccurateHit(double chance) {
		return r.nextDouble() <= chance;
	}

	public static double getChance(double attack, double defence) {
		double A = Math.floor(attack);
		double D = Math.floor(defence);
		double chance = A < D ? (A - 1.0) / (2.0 * D) : 1.0 - (D + 1.0) / (2.0 * A);
		chance = chance > 0.9999 ? 0.9999 : chance < 0.0001 ? 0.0001 : chance;
		return chance;
	}
}
