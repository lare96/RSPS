package com.vencillio.core.definitions;

public class FoodDefinition {

	private short id;
	private String name;
	private byte heal;
	private byte delay;
	private short replaceId;
	private String message;

	public int getDelay() {
		return delay;
	}

	public int getHeal() {
		return heal;
	}

	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public String getName() {
		return name;
	}

	public int getReplaceId() {
		return replaceId;
	}
}
