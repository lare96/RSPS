package com.vencillio.rs2.content.skill;

import java.util.HashMap;

import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterXInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendSkillGoal;

public enum SkillGoal {
	ATTACK(0, 94100, 94099, 33206),
	STRENGTH(2, 94106, 94105, 33209),
	DEFENCE(1, 94112, 94111, 33212),
	RANGE(4, 94118, 94117, 33215),
	PRAYER(5, 94124, 94123, 33218),
	MAGIC(6, 94130, 94129, 33221),
	RUNECRAFTING(20, 94136, 94135, 33224),
	HUNTER(22, 94142, 94141, 94085),
	HITPOINTS(3, 94102, 94101, 33207),
	AGILITY(16, 94108, 94107, 33210),
	HERBLORE(15, 94114, 94113, 33213),
	THIEVING(17, 94120, 94119, 33216),
	CRAFTING(12, 94126, 94125, 33219),
	FLETCHING(9, 94132, 94131, 33222),
	SLAYER(18, 94138, 94137, 47130),
	TOTAL_LEVEL(25, -1, 94143, 94098),
	MINING(14, 94104, 94103, 33208),
	SMITHING(13, 94110, 94109, 33211),
	FISHING(10, 94116, 94115, 33214),
	COOKING(7, 94122, 94121, 33217),
	FIREMAKING(11, 94128, 94127, 33220),
	WOODCUTTING(8, 94134, 94133, 33223),
	FARMING(19, 94140, 94139, 54104);

	public final int skill, levelId, expId, clearId;

	private SkillGoal(int skill, int levelId, int expId, int clearId) {
		this.skill = skill;
		this.levelId = levelId;
		this.expId = expId;
		this.clearId = clearId;
	}

	public static HashMap<Integer, SkillGoal> skills = new HashMap<Integer, SkillGoal>();

	static {
		for (SkillGoal skill : values()) {
			skills.put(skill.levelId, skill);
			skills.put(skill.expId, skill);
			skills.put(skill.clearId, skill);
		}
	}

	public static boolean handle(Player player, int buttonId) {
		SkillGoal skillGoal = skills.get(buttonId);
		
		if (skillGoal == null) {
			return false;
		}
		
		if (buttonId == skillGoal.clearId) {
			player.send(new SendSkillGoal(skillGoal.skill, 0, 0, 0));
			return true;
		}
		
		player.send(new SendEnterXInterface(3917, skillGoal.levelId == buttonId ? 1 : skillGoal.expId == buttonId ? 2 : 0));
		player.setEnterXSlot(skillGoal.skill);
		return true;
	}
}