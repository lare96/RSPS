package com.vencillio.rs2.content.consumables;

import com.vencillio.core.definitions.FoodDefinition;
import com.vencillio.core.definitions.PotionDefinition;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.AntifireTask;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

/**
 * Handles consumables (Potions & Foods)
 *
 */
public final class Consumables {

	/**
	 * Player
	 */
	private final Player player;

	/**
	 * Consumables
	 * @param player
	 */
	public Consumables(Player player) {
		this.player = player;
	}
	
	/**
	 * Checks if item is potion
	 * @param i
	 * @return
	 */
	public static boolean isPotion(Item i) {
		return i != null && GameDefinitionLoader.getPotionDefinition(i.getId()) != null;
	}

	/**
	 * Check if player can eat
	 */
	private boolean canEat = true;

	/**
	 * Check if player can drink
	 */
	private boolean canDrink = true;

	/**
	 * Consumes the item
	 * @param id
	 * @param slot
	 * @param type
	 * @return
	 */
	public final boolean consume(int id, int slot, ConsumableType type) {
		Item consumable = player.getInventory().get(slot);

		if (consumable == null || player.getMagic().isTeleporting() || player.isStunned()) {
			return false;
		}

		PotionDefinition potions = Item.getPotionDefinition(id);
		FoodDefinition food = Item.getFoodDefinition(id);
		switch (type) {
		
		case FOOD:
			SpecialConsumables.specialFood(player, consumable);

			if (food == null) {
				return false;
			}

			if ((!canEat) || (!player.getController().canEat(player))) {
				return true;
			}

			int foodHealth = food.getHeal();

			if (id == 15272) {
				foodHealth = (int) Math.round(player.getMaxLevels()[3] * 0.23D);
			}

			int heal = player.getSkill().getLevels()[3] + foodHealth;

			if (heal > player.getMaxLevels()[3]) {
				if (id != 15272)
					heal = player.getMaxLevels()[3];
				else {
					heal = player.getMaxLevels()[3] + 10;
				}
			}
			if ((food.getReplaceId() == -1) && (consumable.getAmount() <= 1)) {
				player.getInventory().clear(slot);
			} else if ((food.getReplaceId() == -1) && (consumable.getAmount() > 1)) {
				consumable.remove(1);
				player.getInventory().update();
			} else {
				player.getInventory().setId(slot, food.getReplaceId());
			}
			player.getClient().queueOutgoingPacket(new SendSound(317, 1, 2));
			player.getUpdateFlags().sendAnimation(829, 0);

			if (player.getSkill().getLevels()[3] < heal) {
				player.getSkill().setLevel(3, heal);
			}

			player.getClient().queueOutgoingPacket(new SendMessage(food.getMessage()));
			
			AchievementHandler.activateAchievement(player, AchievementList.EAT_100_FOODS, 1);

			player.getCombat().reset();

			if (player.getCombat().getAttackTimer() > 0) {
				player.getCombat().increaseAttackTimer(food.getDelay());
			}

			if (id != 3144) {
				canEat = false;
			}

			TaskQueue.queue(new Task(player, food.getDelay(), false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					canEat = true;
					stop();
				}

				@Override
				public void onStop() {
				}
			});
			break;
			
		case POTION:
			if (potions == null) {
				return false;
			}

			if ((!player.getController().canDrink(player)) || (!canDrink)) {
				return true;
			}

			canDrink = false;
			PotionDefinition.SkillData[] skillData = potions.getSkillData();
			String name = potions.getName();
			player.getUpdateFlags().sendAnimation(829, 0);
			player.getClient().queueOutgoingPacket(new SendSound(334, 1, 0));
			String message = "You drink a dose of your " + name + ".";
			player.getClient().queueOutgoingPacket(new SendMessage(message));
			AchievementHandler.activateAchievement(player, AchievementList.DRINK_100_POTIONS, 1);
			player.getInventory().setId(slot, potions.getReplaceId() == 0 ? 229 : potions.getReplaceId());
			player.getCombat().reset();

			useSpecialCasePotion(id);

			if ((skillData != null) && (skillData.length > 0)) {
				for (int i = 0; i < skillData.length; i++) {
					int skillId = skillData[i].getSkillId();
					int add = skillData[i].getAdd();
					double modifier = skillData[i].getModifier();

					int level = player.getSkill().getLevels()[skillId];
					int levelForExp = player.getMaxLevels()[skillId];

					if (modifier < 0.0D) {
						int affectedLevel = level + (int) (levelForExp * modifier) + add;

						if (affectedLevel < 1) {
							affectedLevel = 1;
						}

						player.getSkill().setLevel(skillId, affectedLevel);
					} else {
						int maxLvl = potions.getPotionType() == PotionDefinition.PotionTypes.RESTORE ? levelForExp : levelForExp + (int) (levelForExp * modifier) + add;
						int affectedLevel = level + (int) (levelForExp * modifier) + add;

						if ((skillId == 3) && (potions.getName().contains("Saradomin brew"))) {
							maxLvl = 111;
						}

						if ((skillId == 5) && (potions.getName().contains("Zamorak brew"))) {
							maxLvl = 99;
						}

						if (maxLvl > level) {
							if (affectedLevel > maxLvl) {
								affectedLevel = maxLvl;
							}
							player.getSkill().setLevel(skillId, affectedLevel);
						}
					}
				}
			}

			TaskQueue.queue(new Task(player, 3, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
				@Override
				public void execute() {
					canDrink = true;
					stop();
				}

				@Override
				public void onStop() {
				}
			});
			break;
		default:
			System.out.print("[ERROR] - CONSUMABLES");
		}

		return true;
	}

	/**
	 * Special potion case
	 * @param id
	 * @return
	 */
	public boolean useSpecialCasePotion(int id) {
		switch (id) {
		case 2452:
		case 2454:
		case 2456:
		case 2458:
		case 2488:
			TaskQueue.queue(new AntifireTask(player, false));
			break;
		case 15304:
		case 15305:
		case 15306:
		case 15307:
			TaskQueue.queue(new AntifireTask(player, true));
			break;
		case 3008:
		case 3010:
		case 3012:
		case 3014:
			player.getRunEnergy().add(20);
			return true;
		case 175:
		case 177:
		case 179:
		case 2446:
			player.curePoison(100);
			return true;
			
		case 12695:
		case 12697:
		case 12699:
		case 12701:
			this.superCombatEffect(player);
			return true;
			
		}
		return false;
	}

	/**
	 * Super combat potion effect
	 * @param player2
	 */
	private void superCombatEffect(Player player2) {
		int amount = 0;
		for (int i = 0; i < 3; i++) {
			if (player.getMaxLevels()[i] + 5 + (int) (player.getMaxLevels()[i] * 0.15) > player.getLevels()[i]) {
				if (player.getMaxLevels()[i] + 5 + (int) (player.getMaxLevels()[i] * 0.15) < player.getLevels()[i] + 5 + (int) (player.getLevels()[i] * 0.15)) {
					amount = player.getMaxLevels()[i] + 5 + (int) (player.getMaxLevels()[i] * 0.15);
				} else {
					amount = player.getLevels()[i] + 5 + (int) (player.getLevels()[i] * 0.15);
				}
				player.getSkill().setLevel(i, amount);
			}
		}
	}
}
