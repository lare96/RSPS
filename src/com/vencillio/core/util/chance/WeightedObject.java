package com.vencillio.core.util.chance;
/**
 * Represents a weighted object.
 * 
 * @author Michael | Chex
 */
public interface WeightedObject<T> extends Comparable<WeightedObject<T>> {

	/**
	 * Gets the object's weight.
	 * 
	 * @return The weight.
	 */
	public double getWeight();

	/**
	 * Gets the representation of the weighted chance.
	 * 
	 * @return The representation.
	 */
	public T get();

	/**
	 * The toString method.
	 * 
	 * @return The class variables represented in a string.
	 */
	@Override
	public String toString();
}