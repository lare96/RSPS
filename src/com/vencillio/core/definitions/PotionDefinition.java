package com.vencillio.core.definitions;

public class PotionDefinition {

	public enum PotionTypes {

		NORMAL, RESTORE, ANTIFIRE, SUPER_ANTIFIRE
	}

	public class SkillData {

		private byte skillId;
		private byte add;
		private double modifier;

		public int getAdd() {
			return add;
		}

		public double getModifier() {
			return modifier;
		}

		public int getSkillId() {
			return skillId;
		}
	}

	private short id;
	private String name;
	private short replaceId;

	private PotionTypes potionType;

	private SkillData[] skillData;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public PotionTypes getPotionType() {
		return potionType;
	}

	public int getReplaceId() {
		return replaceId;
	}

	public SkillData[] getSkillData() {
		return skillData;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
