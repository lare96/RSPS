package com.vencillio.core.util.chance;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles a random chance.
 * 
 * @param <T>
 *            - The representation type.
 * @author Michael | Chex
 */
public class Chance<T> {

	/**
	 * The list of weighted objects.
	 */
	private final List<WeightedObject<T>> objects;

	/**
	 * The sum of the weights.
	 */
	private double sum;

	/**
	 * Creates a new instance of the class.
	 * 
	 * @return A new {@link Chance} object.
	 */
	public Chance(List<WeightedObject<T>> objects) {
		this.objects = objects;
		sum = objects.stream().mapToDouble(WeightedObject::getWeight).sum();
		sort();
	}

	/**
	 * Creates a new instance of the class.
	 * 
	 * @return A new {@link Chance} object.
	 */
	public Chance() {
		this.objects = new ArrayList<>();
		sum = 0;
	}

	/**
	 * Adds a new {@link WeightedObject} to the {@link #objects} list.
	 * 
	 * @param weight
	 *            - The weight of the object.
	 * @param t
	 *            - The represented object to add.
	 */
	public final void add(double weight, T t) {
		objects.add(new WeightedChance<T>(weight, t));
		sum += weight;
	}

	/**
	 * Sorts the {@link #objects} list by the weighted object comparator.
	 */
	public final void sort() {
		objects.sort(WeightedObject::compareTo);
	}

	/**
	 * Generates a {@link WeightedObject}.
	 * 
	 * @return The {@link WeightedObject}.
	 */
	public WeightedObject<T> nextObject() {
		double rnd = Math.random() * sum;
		double hit = 0;

		for (WeightedObject<T> obj : objects) {
			hit += obj.getWeight();

			if (hit > rnd) {
				return obj;
			}
		}

		throw new AssertionError("The random number [" + rnd + "] is too large!");
	}
}