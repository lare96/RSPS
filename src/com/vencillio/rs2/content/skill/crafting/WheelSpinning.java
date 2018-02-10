package com.vencillio.rs2.content.skill.crafting;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.impl.ProductionTask;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public class WheelSpinning extends ProductionTask {
	private Spinnable spinnable;
	private short productionCount;

	public WheelSpinning(Player entity, short productionCount, Spinnable spin) {
		super(entity, 0, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.productionCount = productionCount;
		spinnable = spin;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return new Animation(896);
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { spinnable.getItem() };
	}

	@Override
	public int getCycleCount() {
		return 2;
	}

	@Override
	public double getExperience() {
		return spinnable.getExperience();
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getInsufficentLevelMessage() {
		return "You need a " + com.vencillio.rs2.content.skill.Skills.SKILL_NAMES[getSkill()] + " level of " + getRequiredLevel() + " to spin " + GameDefinitionLoader.getItemDef(spinnable.getOutcome().getId()).getName().toLowerCase() + ".";
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public int getRequiredLevel() {
		return spinnable.getRequiredLevel();
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { spinnable.getOutcome() };
	}

	@Override
	public int getSkill() {
		return 12;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "You spin the " + GameDefinitionLoader.getItemDef(getConsumedItems()[0].getId()).getName().toLowerCase() + " into a " + GameDefinitionLoader.getItemDef(getRewards()[0].getId()).getName().toLowerCase() + ".";
	}

	@Override
	public String noIngredients(Item item) {
		return null;
	}

	@Override
	public void onStop() {
	}
}
