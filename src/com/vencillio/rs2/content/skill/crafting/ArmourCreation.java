package com.vencillio.rs2.content.skill.crafting;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.impl.ProductionTask;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;

public class ArmourCreation extends ProductionTask {
	short creationAmount;
	Craftable craftable;

	public ArmourCreation(Player entity, short creationAmount, Craftable craft) {
		super(entity, 0, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.creationAmount = creationAmount;
		craftable = craft;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return new Animation(1249);
	}

	@Override
	public Item[] getConsumedItems() {
		if (craftable.getItemId() == 1741) {
			if (player.getInventory().hasItemId(1741)) {
				return new Item[] { new Item(1734, 1), new Item(1741, 1) };
			}
			return new Item[] { new Item(1734, 1), new Item(craftable.getItemId(), 1) };
		}

		return new Item[] { new Item(1734, 1), new Item(craftable.getItemId(), 1) };
	}

	@Override
	public int getCycleCount() {
		return 2;
	}

	@Override
	public double getExperience() {
		return craftable.getExperience() * (getConsumedItems()[0].getId() == 1743 ? 2 : 1);
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getInsufficentLevelMessage() {
		return "You need a " + com.vencillio.rs2.content.skill.Skills.SKILL_NAMES[getSkill()] + " level of " + getRequiredLevel() + " to create " + GameDefinitionLoader.getItemDef(getRewards()[0].getId()).getName() + ".";
	}

	@Override
	public int getProductionCount() {
		return creationAmount;
	}

	@Override
	public int getRequiredLevel() {
		return craftable.getRequiredLevel();
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(craftable.getOutcome(), 1) };
	}

	@Override
	public int getSkill() {
		return 12;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		String prefix = "a";
		String itemName = GameDefinitionLoader.getItemDef(getRewards()[0].getId()).getName().toLowerCase();
		if ((itemName.contains("glove")) || (itemName.contains("boot")) || (itemName.contains("vamb")) || (itemName.contains("chap")))
			prefix = "a pair of";
		else if (itemName.endsWith("s"))
			prefix = "some";
		else if (Utility.startsWithVowel(itemName)) {
			prefix = "an";
		}
		return "You make " + prefix + " " + itemName + ".";
	}

	@Override
	public String noIngredients(Item item) {
		return "You don't have enough " + GameDefinitionLoader.getItemDef(item.getId()).getName() + " to craft this item.";
	}

	@Override
	public void onStop() {
	}
}
