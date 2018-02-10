package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.content.skill.Skill;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public class RegenerateSkillTask extends Task {

	private final Entity entity;
	private Skill skill = null;
//	private int iterations = 0;

	public static final String EXRTA_HP_REGEN_TASK = "extrahpregentask";

	public RegenerateSkillTask(Entity entity, int delay) {
		super(entity, delay, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.SKILL_RESTORE);

		if (entity == null) {
			stop();
		}

		if (!entity.isNpc()) {
			Player p = World.getPlayers()[entity.getIndex()];
			if (p != null) {
				skill = p.getSkill();
			}
		}

		this.entity = entity;
	}

	@Override
	public void execute() {
		if (entity == null) {
			stop();
			return;
		}

//		iterations++;
		
		/**
		 * Check for the Regen bracelet
		 */
		if (!entity.isNpc()) {
			final Player p = World.getPlayers()[entity.getIndex()];
			if (p != null) {
				if (p.getAttributes().get(EXRTA_HP_REGEN_TASK) == null/* && iterations % 2 == 0*/) {
					Item gl = p.getEquipment().getItems()[EquipmentConstants.GLOVES_SLOT];

					if (gl != null && gl.getId() == 11133) {
						p.getAttributes().set(EXRTA_HP_REGEN_TASK, (byte) 0);
						Task t = new Task(p, 25) {

							@Override
							public void execute() {
								Item gl = p.getEquipment().getItems()[EquipmentConstants.GLOVES_SLOT];

								if (gl == null || gl != null && gl.getId() != 11133) {
									p.getAttributes().remove(EXRTA_HP_REGEN_TASK);
									stop();
									return;
								}

								if (p.getLevels()[3] < p.getMaxLevels()[3]) {
									p.getLevels()[3] += 1;
									p.getSkill().update(3);
								}
							}

							@Override
							public void onStop() {
							}

						};

						TaskQueue.queue(t);
					}
				}
			}
		}

		for (int i = 0; i < (!entity.isNpc() ? Skills.SKILL_COUNT : 7); i++) {
			if (i > 7 && entity.isNpc()) {
				break;
			}

			if (i == Skills.PRAYER || i == Skills.HITPOINTS && entity.getLevels()[Skills.HITPOINTS] > entity.getMaxLevels()[Skills.HITPOINTS]) {
				continue;
			}

			int lvl = entity.getLevels()[i];
			int max = entity.getMaxLevels()[i];
			
//			boolean restore = iterations % 2 == 0;
//
//			if (!entity.isNpc()) {
//				if (entity.getPlayer().getPrayer().active(8)) {
//					if (i == 3) {
//						continue;
//					}
//					
//					entity.getPlayer().send(new SendMessage("Rapid Restore"));
//					
//					restore = true;
//				}
//				
//				if (entity.getPlayer().getPrayer().active(9)) {
//					if (i != 3) {
//						continue;
//					}
//					entity.getPlayer().send(new SendMessage("Rapid Heal"));
//					
//					restore = true;
//				}
//			}
			
//			if (!restore) {
//				return;
//			}
//			
//			if (lvl > max && restore) {
//				continue;
//			}

			if (lvl != max) {
				entity.getLevels()[i] += (lvl < max ? 1 : -1);
				if (skill != null) {
					skill.update(i);
				}
			}
		}
	}

	@Override
	public void onStop() {
	}
}
