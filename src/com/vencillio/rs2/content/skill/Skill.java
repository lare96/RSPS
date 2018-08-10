package com.vencillio.rs2.content.skill;

import com.vencillio.VencillioConstants;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.Prestige;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.*;

import static com.vencillio.rs2.content.skill.Skills.DUNGEONEERING;

public class Skill {

	public static final int[] EXP_FOR_LEVEL = { 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358,
		1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842,
		8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408,
		33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333,
		111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288,
		333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257,
		992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087,
		2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831,
		6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431 };

	private double[] experience = new double[Skills.SKILL_COUNT];
	private int combatLevel = 0;
	private int totalLevel = 0;
	private final Player player;

	private boolean expLock = false;

	private long lock = 0L;
	
	/**
	 * Melee experience
	 */
	private int meleeExp = 40;
	
	/**
	 * Range experience
	 */
	private int rangeExp = 40;
	
	/**
	 * Magic experience
	 */
	private int magicExp = 40;

	/**
	 * Constructs a new skill instance
	 * 
	 * @param player
	 *            The player creating the skill instance
	 */
	public Skill(Player player) {
		this.player = player;
		for (int i = 0; i < Skills.SKILL_COUNT; i++)
			if (i == 3) {
				getLevels()[i] = 10;
				experience[i] = 1154.0D;
			} else {
				getLevels()[i] = 1;
				experience[i] = 0.0D;
			}
	}

	/**
	 * Adds combat experience after dealing damage
	 * 
	 * @param type
	 *            The type of combat the player dealt damage with
	 * @param hit
	 *            The amount of damage dealt
	 */
	public void addCombatExperience(CombatTypes type, int hit) {
		if ((expLock) || (player.getMagic().isDFireShieldEffect())) {
			return;
		}

		if (type == CombatTypes.MAGIC) {
			player.getMagic().getSpellCasting().addSpellExperience();
		}

		if (hit <= 0) {
			return;
		}

		//325k hits for 13m xp (ultimate) | 54,300 hit for easy
		//easy = 60x RS medium = 50x hard = 30x ultimate = 10x
		//Kept hp ratio same for all modes (3:1)
		int[] modes = {6,5,3,1};
		double[] modes2 = {1,4/3,2,4};
		double exp = hit * modes[player.mode];
		switch (type) {
		case NONE:
			break;
		case MELEE:
			switch (player.getEquipment().getAttackStyle()) {
			case ACCURATE:
				addExperience(0, exp * meleeExp);
				break;
			case AGGRESSIVE:
				addExperience(2, exp * meleeExp);
				break;
			case CONTROLLED:
				addExperience(0, (exp / 3.0D) * meleeExp);
				addExperience(2, (exp / 3.0D) * meleeExp);
				addExperience(1, (exp / 3.0D) * meleeExp);
				break;
			case DEFENSIVE:
				addExperience(1, exp * meleeExp);
				break;
			}

			break;
		case MAGIC:
			addExperience(6, exp * magicExp);
			break;
		case RANGED:
			addExperience(4, exp * rangeExp);
			switch (player.getEquipment().getAttackStyle()) {
			case ACCURATE:
				addExperience(4, exp * rangeExp);
				break;
			case AGGRESSIVE:
				addExperience(4, exp * rangeExp);
				break;
			case CONTROLLED:
				addExperience(4, exp * rangeExp);
				break;
			case DEFENSIVE:
				addExperience(4, exp / 2.0D * rangeExp);
				addExperience(1, exp / 2.0D * rangeExp);
				break;
			}
			break;
		}

		addExperience(3, hit * meleeExp * 1.33D / modes2[player.mode]);
	}

	/**
	 * Adds experience to the experience array
	 * 
	 * @param id
	 *            The id of the skill to add experience too
	 * @param experience
	 *            The amount of experience too add
	 * @return
	 */
	public double addExperience(int id, double experience) {
		if ((expLock) && (id <= 6)) {
			return 0;
		}

		int doubleXP = VencillioConstants.doubleExperience ? 2 : 1;

		double expBefore = experience;

		experience = experience * Skills.EXPERIENCE_RATES[id] * 1.0D * doubleXP;

		this.experience[id] += experience;

		if (player.getMaxLevels()[id] == 99 && id != DUNGEONEERING || player.getMaxLevels()[id] == 120 && id == DUNGEONEERING) {
			if (this.experience[id] < 200_000_000) {
				player.send(new SendExpCounter(id, (int) experience));
			}

			update(id);
			return experience;
		}

		int newLevel = getLevelForExperience(id, this.experience[id]);

		if (newLevel > 99 && id != DUNGEONEERING) {
			newLevel = 99;
		}

		if (newLevel > 120 && id == DUNGEONEERING) {
			newLevel = 120;
		}

		if (player.getMaxLevels()[id] < newLevel) {
			getLevels()[id] = ((short) (newLevel - (player.getMaxLevels()[id] - getLevels()[id])));
			player.getMaxLevels()[id] = ((short) (newLevel));

			updateTotalLevel();

			onLevelup(newLevel, id);
			
			player.setAppearanceUpdateRequired(true);

			if (id == DUNGEONEERING ? newLevel == 120 : newLevel == 99) {
				World.sendGlobalMessage("<col=855907><img=12> "+player.getUsername() + " has achieved level " + 99 + " in " + Skills.SKILL_NAMES[id] + "! Prestige level: " + player.getSkillPrestiges()[id]);
			}
		}

		if (this.experience[id] >= 200000000 && expBefore < 200_000_000) {
			this.experience[id] = 200000000;
			World.sendGlobalMessage("<col=855907><img=12> "+player.getUsername() + " has achieved 200m xp in " + Skills.SKILL_NAMES[id] + "! Prestige level: " + player.getSkillPrestiges()[id]);
		} else {
			player.send(new SendExpCounter(id, (int) experience));
		}

		update(id);
		return experience;
	}

	/**
	 * Deducts an amount from a skill
	 * 
	 * @param id
	 *            The id of the skill
	 * @param amount
	 *            The amount to remove from the skill
	 */
	public void deductFromLevel(int id, int amount) {
		getLevels()[id] = ((short) (getLevels()[id] - amount));

		if (getLevels()[id] < 0) {
			getLevels()[id] = 0;
		}

		update(id);
	}

	/**
	 * Gets the players combat levels
	 * 
	 * @return
	 */
	public int getCombatLevel() {
		return combatLevel;
	}

	/**
	 * Gets the players combat levels
	 * 
	 * @return
	 */
	public int calcCombatLevel() {
		int magLvl = player.getMaxLevels()[Skills.MAGIC];
		int ranLvl = player.getMaxLevels()[Skills.RANGED];
		int attLvl = player.getMaxLevels()[Skills.ATTACK];
		int strLvl = player.getMaxLevels()[Skills.STRENGTH];
		int defLvl = player.getMaxLevels()[Skills.DEFENCE];
		int hitLvl = player.getMaxLevels()[Skills.HITPOINTS];
		int prayLvl = player.getMaxLevels()[Skills.PRAYER];
		double mag = magLvl * 1.5;
		double ran = ranLvl * 1.5;
		double attstr = attLvl + strLvl;

		combatLevel = 0;

		if (ran > attstr && ran > mag) { // player is ranged class
			combatLevel = (int) (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((ranLvl) * 0.4875));
		} else if (mag > attstr) { // player is mage class
			combatLevel = (int) (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((magLvl) * 0.4875));
		} else {
			combatLevel = (int) (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((attLvl) * 0.325) + ((strLvl) * 0.325));
		}
	
		return combatLevel;
	}

	/**
	 * Gets the players current experience
	 * 
	 * @return
	 */
	public double[] getExperience() {
		return experience;
	}

	/**
	 * Gets a level based on the amount of experience provided
	 * 
	 * @param id
	 *            The skill to check the level for
	 * @param experience
	 *            The amount of experience to check for a level
	 * @return The level based on the provided experience
	 */
	public byte getLevelForExperience(final int id, double experience) {
		if (experience >= EXP_FOR_LEVEL[98]) {
			return 99;
		}
		return binarySearch(experience, 0, 98);
	}

	private static byte binarySearch(double experience, int min, int max) {
		int mid = (min + max) / 2;
		double value = EXP_FOR_LEVEL[mid];

		if (value > experience) {
			return binarySearch(experience, min, mid - 1);
		} else if (value == experience || EXP_FOR_LEVEL[mid + 1] > experience) {
			return (byte) (mid + 1);
		}

		return binarySearch(experience, mid + 1, max);
	}

	/**
	 * Gets the players levels
	 * 
	 * @return
	 */
	public short[] getLevels() {
		return player.getLevels();
	}

	/**
	 * Gets the total amount of combat experience
	 * 
	 * @return The total amount of combat experience
	 */
	public long getTotalCombatExperience() {
		long total = 0L;

		for (int i = 0; i <= 6; i++) {
			total = (long) (total + experience[i]);
		}

		return total;
	}

	/**
	 * Gets the players total experience
	 * 
	 * @return The players total experience
	 */
	public long getTotalExperience() {
		long total = 0L;

		for (int ii = 0; ii < experience.length; ii++) {
			if (ii == Skills.SUMMONING || ii == Skills.CONSTRUCTION || ii == Skills.DUNGEONEERING) {
				continue;
			}
			
			double i = experience[ii];
			total = (long) (total + (i > 200_000_000.0D ? 200_000_000.0D : i));
		}

		return total;
	}

	/**
	 * Gets the players total level
	 * 
	 * @return
	 */
	public int getTotalLevel() {
		return totalLevel;
	}

	/**
	 * Gets the amount of experience for a level
	 * 
	 * @param skillId
	 *            The id of the skill
	 * @param level
	 *            The level the player is getting the experience for
	 * @return The amount of experience for a level
	 */
	public int getXPForLevel(int skillId, int level) {
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= level; lvl++) {
			points = (int) (points + Math.floor(lvl + 300.0D * Math.pow(2.0D, lvl / 7.0D)));
			if ((lvl >= level) || (lvl == 99) && skillId != DUNGEONEERING || (lvl == 120) && skillId == DUNGEONEERING)
				return output;
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	/**
	 * Gets if the player has a combat levels
	 * 
	 * @return
	 */
	public boolean hasCombatLevels() {
		for (int i = 0; i <= 6; i++) {
			if ((i == 3) && (player.getMaxLevels()[i] > 10)) {
				return true;
			}

			if (player.getMaxLevels()[i] > 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets if the player has at least 2 99 skills
	 * 
	 * @return The player has at least 2 99's
	 */
	public boolean hasTwo99s() {
		byte c = 0;
		int index = 0;

		for (int i : player.getMaxLevels()) {
			if ((i == 99) && index != Skills.DUNGEONEERING || i == 120 && index == Skills.DUNGEONEERING) {
				if (++c == 2) {
					return true;
				}
			}

			index++;
		}

		return false;
	}

	/**
	 * Gets if the experience is locked
	 * 
	 * @return The experience is locked
	 */
	public boolean isExpLocked() {
		return expLock;
	}

	/**
	 * Locks the players skill from performing another skill for a certain delay
	 * 
	 * @param delay
	 *            The delay to lock the skills
	 */
	public void lock(int delay) {
		lock = (World.getCycles() + delay);
	}

	/**
	 * Gets if the players skill is locked
	 * 
	 * @return The players skills are locked
	 */
	public boolean locked() {
		return lock > World.getCycles();
	}

	/**
	 * Actions that take place on leveling up
	 * 
	 * @param lvl
	 *            The level the player has just achieved
	 * @param skill
	 *            The skill the player achieved the level in
	 */
	public void onLevelup(int lvl, int skill) {
		player.getUpdateFlags().sendGraphic(Skills.LEVELUP_GRAPHIC);
		String line1 = "Congratulations! You've just advanced " + Utility.getAOrAn(Skills.SKILL_NAMES[skill]) + " " + Skills.SKILL_NAMES[skill] + " level!";
		String line2 = "You have reached level " + lvl + "!";

		player.getClient().queueOutgoingPacket(new SendMessage(line1));

		if (skill == Skills.HUNTER)
			player.getClient().queueOutgoingPacket(new SendMessage(line2));

		else {

			player.getClient().queueOutgoingPacket(new SendChatBoxInterface(Skills.CHAT_INTERFACES[skill][1]));


			if (skill == Skills.FARMING) {
				player.send(new SendInterfaceConfig(4888, 200, 5340));
			}

			if ((skill != 4) && (skill != 14) && (skill != 17) && (skill != 19)) {
				player.getClient().queueOutgoingPacket(new SendString("<col=369>" + line1, Skills.CHAT_INTERFACES[skill][1] + 1));
				player.getClient().queueOutgoingPacket(new SendString(line2, Skills.CHAT_INTERFACES[skill][1] + 2));
			} else {
				player.getClient().queueOutgoingPacket(new SendString("<col=369>" + line1, Skills.CHAT_INTERFACES[skill][2]));
				player.getClient().queueOutgoingPacket(new SendString(line2, Skills.CHAT_INTERFACES[skill][3]));
			}
		}
		player.getUpdateFlags().setUpdateRequired(true);
	}

	/**
	 * Updates the skills on login
	 */
	public void onLogin() {
		updateLevelsForExperience();
		updateTotalLevel();

		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			update(i);
		}
	}

	/**
	 * Resets the players skill back to default
	 * 
	 * @param id
	 *            The id of the skill to reset
	 */
	public void reset(int id) {
		if (id == 3) {
			getLevels()[id] = 10;
			experience[id] = 1154.0D;
			player.getMaxLevels()[id] = 10;
		} else {
			getLevels()[id] = 1;
			player.getMaxLevels()[id] = 1;
			experience[id] = 0.0D;
		}

		update(id);
	}

	/**
	 * Resets the players combat stats, attack through magic
	 */
	public void resetCombatStats() {
		for (int i = 0; i <= 7; i++) {
			getLevels()[i] = player.getMaxLevels()[i];
			update(i);
		}
	}

	/**
	 * Restores the players levels back to normal
	 */
	public void restore() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			getLevels()[i] = player.getMaxLevels()[i];
			update(i);
		}
	}

	/**
	 * Sets the players current experience
	 * 
	 * @param experience
	 */
	public void setExperience(double[] experience) {
		this.experience = experience;
	}

	/**
	 * Sets the experience locked or unlocked
	 * 
	 * @param locked
	 *            If he experience is locked or unlocked
	 */
	public void setExpLock(boolean locked) {
		expLock = locked;
	}

	/**
	 * Sets a level by the id
	 * 
	 * @param id
	 *            The id of the skill
	 * @param level
	 *            The level to set the skill too
	 */
	public void setLevel(int id, int level) {
		getLevels()[id] = ((byte) level);
		update(id);
	}

	/**
	 * Updates all of the players skills
	 */
	public void update() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			update(i);
		}
	}

	/**
	 * Updates a skill by the id
	 * 
	 * @param id
	 *            The id of the skill being updated
	 */
	public void update(int id) {
		player.send(new SendSkill(id, getLevels()[id], (int) experience[id]));		
		if (player.isPrestigeColors()) {
			player.send(new SendColor(Skills.REFRESH_DATA[id][1], Prestige.skillTierColor(player, id)));
			player.send(new SendColor(Skills.REFRESH_DATA[id][0], Prestige.skillTierColor(player, id)));			
		} else {
			player.send(new SendColor(Skills.REFRESH_DATA[id][1], 0xFFFF00));
			player.send(new SendColor(Skills.REFRESH_DATA[id][0], 0xFFFF00));		
		}
	}
	
	/**
	 * Resets the prestige colors
	 */
	public void resetColors() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			if (player.isPrestigeColors()) {
				player.send(new SendColor(Skills.REFRESH_DATA[i][1], Prestige.skillTierColor(player, i)));
				player.send(new SendColor(Skills.REFRESH_DATA[i][0], Prestige.skillTierColor(player, i)));	
			} else {
				player.send(new SendColor(Skills.REFRESH_DATA[i][1], 0xFFFF00));
				player.send(new SendColor(Skills.REFRESH_DATA[i][0], 0xFFFF00));					
			}
		}
	}

	/**
	 * Updates all of the levels for the players experience
	 */
	public void updateLevelsForExperience() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++)
			player.getMaxLevels()[i] = getLevelForExperience(i, experience[i]);
	}

	/**
	 * Updates the total level
	 */
	public void updateTotalLevel() {
		totalLevel = 0;

		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			if (i == Skills.CONSTRUCTION || i == Skills.SUMMONING || i == Skills.DUNGEONEERING) {
				continue;
			}
			totalLevel += player.getMaxLevels()[i];
		}

	}
}
