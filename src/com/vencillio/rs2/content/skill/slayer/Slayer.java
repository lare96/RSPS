package com.vencillio.rs2.content.skill.slayer;

import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Slayer {

	public static enum SlayerDifficulty {
		LOW,
		MEDIUM,
		HIGH,
		BOSS;
	}

	public static boolean isSlayerTask(Player p, Mob mob) {
		return (p.getSlayer().getTask() != null) && (mob.getDefinition().getName().toLowerCase().contains(p.getSlayer().getTask().toLowerCase()));
	}

	public static boolean isSlayerTask(Player p, String other) {
		return (p.getSlayer().getTask() != null) && (other.toLowerCase().contains(p.getSlayer().getTask().toLowerCase()));
	}

	private final Player p;
	private String task = null;

	private byte amount = 0;

	private SlayerDifficulty current = null;

	private Player partner = null;

	public Slayer(Player p) {
		this.p = p;
	}

	public void addSlayerExperience(double am) {
		p.getSkill().addExperience(18, am);
	}

	public void assign(SlayerDifficulty diff) {
		switch (diff) {
		case LOW:
			SlayerTasks.LowLevel[] lval = SlayerTasks.LowLevel.values();

			SlayerTasks.LowLevel set = lval[Utility.randomNumber(lval.length)];

			while (p.getMaxLevels()[18] < set.lvl) {
				set = lval[Utility.randomNumber(lval.length)];
			}

			task = set.name;

			amount = ((byte) (30 + Utility.randomNumber(25)));
			current = SlayerDifficulty.LOW;
			break;
		case MEDIUM:
			SlayerTasks.MediumLevel[] mval = SlayerTasks.MediumLevel.values();

			SlayerTasks.MediumLevel set2 = mval[Utility.randomNumber(mval.length)];

			while (p.getMaxLevels()[18] < set2.lvl) {
				set2 = mval[Utility.randomNumber(mval.length)];
			}

			task = set2.name;

			amount = ((byte) (30 + Utility.randomNumber(25)));
			current = SlayerDifficulty.MEDIUM;
			break;
		case HIGH:
			SlayerTasks.HighLevel[] hval = SlayerTasks.HighLevel.values();

			SlayerTasks.HighLevel set3 = hval[Utility.randomNumber(hval.length)];

			while (p.getMaxLevels()[18] < set3.lvl) {
				set3 = hval[Utility.randomNumber(hval.length)];
			}

			task = set3.name;

			amount = ((byte) (20 + Utility.randomNumber(25)));
			current = SlayerDifficulty.HIGH;
			break;
		case BOSS:
			SlayerTasks.BossLevel[] bval = SlayerTasks.BossLevel.values();

			SlayerTasks.BossLevel set4 = bval[Utility.randomNumber(bval.length)];

			while (p.getMaxLevels()[18] < set4.lvl) {
				set4 = bval[Utility.randomNumber(bval.length)];
			}

			task = set4.name;

			amount = ((byte) (20 + Utility.randomNumber(25)));
			current = SlayerDifficulty.BOSS;
			break;
		default:
			throw new IllegalArgumentException("(Slayer.java) The world is going to end");
		}
	}

	public void checkForSlayer(Mob killed) {
		if ((partner != null) && ((task == null) || ((partner.getSlayer().hasTask()) && (!isSlayerTask(partner, task)))) && (partner.getLocation().isViewableFrom(p.getLocation())) && (partner.getSlayer().hasTask()) && (isSlayerTask(partner, killed.getDefinition().getName()))) {
			partner.getSkill().addExperience(18, killed.getDefinition().getLevel() * 2 / 4);
		}

		if (task == null) {
			return;
		}

		NpcDefinition def = killed.getDefinition();

		if (isSlayerTask(p, killed)) {
			amount = ((byte) (amount - 1));
			double exp = def.getLevel() * 2;

			addSlayerExperience(exp);
			doSocialSlayerExperience(killed, exp);

			if (amount == 0) {

				task = null;
				addSlayerExperience(def.getLevel() * 35);
				p.getClient().queueOutgoingPacket(new SendMessage("<col=075D78>You have completed your Slayer task; return to Vannaka for another."));
				p.addSlayerPoints(amount);
				AchievementHandler.activateAchievement(p, AchievementList.COMPLETE_10_SLAYER_TASKS, 1);
				AchievementHandler.activateAchievement(p, AchievementList.COMPLETE_100_SLAYER_TASKS, 1);
				if (current != null) {
					p.addSlayerPoints(current == SlayerDifficulty.BOSS ? 20 + 0 : current == SlayerDifficulty.LOW ? 5 + 0 : current == SlayerDifficulty.MEDIUM ? 8 + 0 : current == SlayerDifficulty.HIGH ? 10 + 0 : 0);
				}

			} else {
			}
		}
	}

	public void doSocialSlayerExperience(Mob killed, double am) {
		if (partner != null) {
			Player other = partner;

			if ((other.getSlayer().getPartner() == null) || (!other.getSlayer().getPartner().equals(p))) {
				return;
			}

			if (!other.isActive()) {
				DialogueManager.sendStatement(p, "Your social slayer partner is not online.");
				partner = null;
			} else if ((other.getLocation().isViewableFrom(p.getLocation())) && ((other.getSlayer().hasTask()) || (isSlayerTask(other, task)))) {
				other.getSkill().addExperience(18, am / 2.0D);
			}
		}
	}

	public byte getAmount() {
		return amount;
	}

	public SlayerDifficulty getCurrent() {
		return current;
	}

	public Player getPartner() {
		return partner;
	}

	public String getPartnerName() {
		return partner != null ? partner.getUsername() : null;
	}

	public String getTask() {
		return task;
	}

	public boolean hasSlayerPartner() {
		return partner != null;
	}

	public boolean hasTask() {
		return (amount > 0) && (task != null);
	}

	public void reset() {
		task = null;
		amount = 0;
		current = null;
	}

	public void setAmount(byte amount) {
		this.amount = amount;
	}

	public void setCurrent(SlayerDifficulty current) {
		this.current = current;
	}

	public void setSocialSlayerPartner(String name) {
		if (name.equalsIgnoreCase(p.getUsername())) {
			DialogueManager.sendStatement(p, "You may not set your partner as yourself!");
			return;
		}

		Player other = World.getPlayerByName(name);

		if (other == null) {
			DialogueManager.sendStatement(p, "It seems '" + name + "' doesn't exist or isn't online.");
			return;
		}

		if (other.getSlayer().hasSlayerPartner()) {
			DialogueManager.sendStatement(p,  name + " already has a slayer partner!");
			return;
		}
		
		DialogueManager.sendStatement(p, "You have successfully set " + name + " as your partner.");
		partner = other;
		DialogueManager.sendStatement(other, "You have been set as " + p.getUsername() + "'s slayer partner.");
	}

	public void setTask(String task) {
		this.task = task;
	}
}
