package com.vencillio.rs2.content.skill.melee;

import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.GraphicTask;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class BarrowsSpecials {
	
	public static final int BARROWS_SPECIAL_CHANCE = 42;
	public static final Graphic GUTHAN_SPECIAL_GRAPHIC = Graphic.highGraphic(398, 0);
	public static final Graphic TORAG_SPECIAL_GRAPHIC = Graphic.highGraphic(399, 0);
	public static final Graphic KARIL_SPECIAL_GRAPHIC = Graphic.highGraphic(401, 0);
	public static final Graphic AHRIM_SPECIAL_GRAPHIC = Graphic.highGraphic(400, 0);

	public static void checkForBarrowsSpecial(Player player) {
		if (Utility.randomNumber(100) > 47) {
			return;
		}

		if (player.getSpecialAttack().isInitialized()) {
			return;
		}

		if (ItemCheck.wearingFullBarrows(player, "Dharok"))
			player.getSpecialAttack().toggleSpecial();
		else if (ItemCheck.wearingFullBarrows(player, "Guthan"))
			player.getMelee().setGuthanEffectActive(true);
		else if (ItemCheck.wearingFullBarrows(player, "Torag"))
			player.getMelee().setToragEffectActive(true);
		else if ((player.getCombat().getCombatType() == CombatTypes.MAGIC) && (ItemCheck.wearingFullBarrows(player, "Ahrim")))
			player.getMagic().setAhrimEffectActive(true);
		else if (ItemCheck.wearingFullBarrows(player, "Verac"))
			player.getMelee().setVeracEffectActive(true);
		else if (ItemCheck.wearingFullBarrows(player, "Karil"))
			player.getRanged().setKarilEffectActive(true);
	}

	public static void doAhrimEffect(Player player, Entity attack, int damage) {
		TaskQueue.queue(new GraphicTask(4, false, AHRIM_SPECIAL_GRAPHIC, attack));
		player.getMagic().setAhrimEffectActive(false);

		if ((damage > 0) && (Utility.randomNumber(4) == 0)) {
			int newLvl = attack.getLevels()[2] - 5;

			attack.getLevels()[2] = ((byte) (newLvl > 0 ? newLvl : 0));

			if (!attack.isNpc()) {
				Player p2 = com.vencillio.rs2.entity.World.getPlayers()[attack.getIndex()];

				if (p2 == null) {
					return;
				}

				p2.getSkill().update(2);
			}

			player.getClient().queueOutgoingPacket(new SendMessage("You drain 5 levels from your opponent's Strength level."));
		}
	}

	public static void doGuthanEffect(Player player, Entity attack, Hit hit) {
		int newLvl = player.getLevels()[3] + hit.getDamage();
		int maxLvl = player.getMaxLevels()[3];

		player.getLevels()[3] = ((short) (newLvl > maxLvl ? maxLvl : newLvl));
		player.getSkill().update(3);

		attack.getUpdateFlags().sendGraphic(GUTHAN_SPECIAL_GRAPHIC);
		player.getMelee().setGuthanEffectActive(false);
		player.getClient().queueOutgoingPacket(new SendMessage("You absorb some of your opponent's hitpoints."));
	}

	public static void doKarilEffect(Player player, Entity attack) {
		attack.getUpdateFlags().sendGraphic(KARIL_SPECIAL_GRAPHIC);
		player.getRanged().setKarilEffectActive(false);
		int newLvl = attack.getLevels()[6] - 5;

		attack.getLevels()[6] = ((byte) (newLvl > 0 ? newLvl : 0));

		if (!attack.isNpc()) {
			Player p2 = com.vencillio.rs2.entity.World.getPlayers()[attack.getIndex()];

			if (p2 == null) {
				return;
			}

			p2.getSkill().update(6);
		}

		player.getClient().queueOutgoingPacket(new SendMessage("You drain 5 levels from your opponent's Magic level."));
	}

	public static void doToragEffect(Player player, Entity attack) {
		attack.getUpdateFlags().sendGraphic(TORAG_SPECIAL_GRAPHIC);
		player.getMelee().setToragEffectActive(false);

		if (!attack.isNpc()) {
			Player p = com.vencillio.rs2.entity.World.getPlayers()[attack.getIndex()];

			if (p == null) {
				return;
			}

			p.getRunEnergy().deduct(0.2D);
			p.getRunEnergy().update();
			player.getClient().queueOutgoingPacket(new SendMessage("You have drained 20% of your opponent's energy."));
		}
	}
}
