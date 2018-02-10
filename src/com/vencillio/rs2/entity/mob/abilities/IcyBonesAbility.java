package com.vencillio.rs2.entity.mob.abilities;

import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.HitTask;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.CombatEffect;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Graphic;

public class IcyBonesAbility implements CombatEffect {
	@Override
	public void execute(Entity e1, Entity e2) {
		if (Utility.randomNumber(5) == 0) {
			e2.getUpdateFlags().sendGraphic(new Graphic(2598, 0, 0));
			TaskQueue.queue(new HitTask(2, false,
					new Hit(Utility.randomNumber(10)), e2));
		}
	}
}
