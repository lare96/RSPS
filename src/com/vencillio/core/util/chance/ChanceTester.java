package com.vencillio.core.util.chance;
import java.util.Arrays;

public class ChanceTester {

	public static void main(String[] args) {
		Chance<String> chance = new Chance<String>(Arrays.asList(
				new WeightedChance<String>(1, "Kiwi"),
				new WeightedChance<String>(25, "Mango"),
				new WeightedChance<String>(50, "Orange"),
				new WeightedChance<String>(60, "Lemmon")
		));
		
//		Chance<String> chance = new Chance<String>(Arrays.asList(
//				new WeightedChance<String>(1, "Lemmon"),
//				new WeightedChance<String>(2, "Mango"),
//				new WeightedChance<String>(4, "Kiwi"),
//				new WeightedChance<String>(9, "Orange")
//		));

		double iterations = 10_000_000;

		int lemmon = 0;
		int mango = 0;
		int kiwi = 0;
		int orange = 0;

		for (int i = 0; i < iterations; i++) {
			String fruit = chance.nextObject().get();

			if (fruit.equals("Lemmon")) {
				lemmon++;
			} else if (fruit.equals("Mango")) {
				mango++;
			} else if (fruit.equals("Kiwi")) {
				kiwi++;
			} else if (fruit.equals("Orange")) {
				orange++;
			}

//			System.out.println(fruit);
		}

		System.out.println("The lemmons are chosen " + (lemmon * 100 / iterations) + "% of the time.");
		System.out.println("The mangos are chosen " + (mango * 100 / iterations) + "% of the time.");
		System.out.println("The kiwis are chosen " + (kiwi * 100 / iterations) + "% of the time.");
		System.out.println("The oranges are chosen " + (orange * 100 / iterations) + "% of the time.");
	}
}