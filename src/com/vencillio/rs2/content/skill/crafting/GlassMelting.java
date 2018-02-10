package com.vencillio.rs2.content.skill.crafting;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.impl.ProductionTask;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public class GlassMelting extends ProductionTask {
	private short productionCount;
	private Glass glass;

	public GlassMelting(Player entity, short productionCount, Glass glass) {
		super(entity, 0, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.productionCount = productionCount;
		this.glass = glass;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return new Animation(899);
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(1783), new Item(glass.getMaterialId()) };
	}

	@Override
	public int getCycleCount() {
		return 2;
	}

	@Override
	public double getExperience() {
		return glass.getExperience();
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getInsufficentLevelMessage() {
		return "You need a " + com.vencillio.rs2.content.skill.Skills.SKILL_NAMES[getSkill()] + " level of " + getRequiredLevel() + " to melt glass.";
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public int getRequiredLevel() {
		return glass.getRequiredLevel();
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(glass.getRewardId()) };
	}

	@Override
	public int getSkill() {
		return 12;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "you heat the sand and soda ash in the furnace to make glass.";
	}

	@Override
	public String noIngredients(Item item) {
		return null;
	}

	@Override
	public void onStop() {
	}
}
