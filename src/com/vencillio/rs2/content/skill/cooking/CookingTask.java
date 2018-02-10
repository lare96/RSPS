package com.vencillio.rs2.content.skill.cooking;

import com.vencillio.core.cache.map.ObjectDef;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendEnterXInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendItemOnInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class CookingTask extends Task {

	public static final String COOKING_OBJECT_KEY = "cookingobject";
	public static final String COOKING_ITEM_KEY = "cookingitem";
	public static final int COOKING_GAUNTLETS = 775;

	public static void attemptCooking(Player player, int cook, int object, int amount) {
		CookingData data = CookingData.forId(cook);
		if (data == null) {
			return;
		}
		if (!meetsRequirements(player, data, cook, object)) {
			return;
		}

		TaskQueue.queue(new CookingTask(player, data, cook, object, amount));
		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
	}

	public static boolean handleCookingByAmount(Player player, int buttonId) {
		int amount = 0;
		for (int i = 0; i < BUTTON_IDS.length; i++) {
			if (BUTTON_IDS[i][0] == buttonId) {
				amount = BUTTON_IDS[i][1];
				break;
			}
		}
		if (amount == 0) {
			return false;
		}
		if (amount != 100)
			attemptCooking(player, ((Integer) player.getAttributes().get("cookingitem")).intValue(), ((Integer) player.getAttributes().get("cookingobject")).intValue(), amount);
		else {
			player.getClient().queueOutgoingPacket(new SendEnterXInterface(1743, ((Integer) player.getAttributes().get("cookingitem")).intValue()));
		}
		return true;
	}

	public static boolean isCookableObject(GameObject object) {
		if (object == null) {
			return false;
		}

		ObjectDef def = ObjectDef.getObjectDef(object.getId());

		if (def == null || def.name == null) {
			return false;
		}

		String name = def.name.toLowerCase();

		return name.equals("range") || name.equals("fire") || name.contains("oven") || name.contains("stove") || name.contains("cooking range") || name.contains("fireplace");
	}

	private static boolean meetsRequirements(Player player, CookingData data, int used, int usedOn) {
		int cookingLevel = player.getSkill().getLevels()[7];
		if (cookingLevel < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a cooking level of " + data.getLevelRequired() + " to cook " + Item.getDefinition(used).getName() + "."));
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			return false;
		}
		if (!player.getInventory().hasItemId(used)) {
			return false;
		}
		return true;
	}

	public static void showInterface(Player player, GameObject usedOn, Item used) {
		if (used == null || CookingData.forId(used.getId()) == null) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot cook this!"));
			return;
		}

		player.getClient().queueOutgoingPacket(new SendChatBoxInterface(1743));
		player.getClient().queueOutgoingPacket(new SendItemOnInterface(13716, 250, used.getId()));
		player.getClient().queueOutgoingPacket(new SendString("\\n\\n\\n\\n\\n" + used.getDefinition().getName(), 13717));

		player.getAttributes().set("cookingobject", Integer.valueOf(usedOn.getId()));
		player.getAttributes().set("cookingitem", Integer.valueOf(used.getId()));
	}

	private CookingData cookingData;

	private Player player;

	private int used;

	private int usedOn;

	private int amountToCook;

	private static final int[][] BUTTON_IDS = { { 53152, 1 }, { 53151, 5 }, { 53149, 28 }, { 53150, 100 } };

	public CookingTask(Player player, CookingData data, int used, int usedOn, int amount) {
		super(player, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		cookingData = data;
		this.used = used;
		this.usedOn = usedOn;
		amountToCook = amount;
	}

	private void burnFood() {
		player.getInventory().add(new Item(cookingData.getBurnt(), 1));
		player.getClient().queueOutgoingPacket(new SendMessage("You accidentally burn the " + Item.getDefinition(used).getName() + "."));
	}

	private void cookFood() {
		player.getInventory().add(new Item(cookingData.getReplacement(), 1));
		double experience = cookingData.getExperience();
		player.getSkill().addExperience(7, experience);
		player.getClient().queueOutgoingPacket(new SendMessage("You successfully cook the " + Item.getDefinition(used).getName() + "."));
		AchievementHandler.activateAchievement(player, AchievementList.COOK_250_FOODS, 1);
		AchievementHandler.activateAchievement(player, AchievementList.COOK_1000_FOODS, 1);
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, cookingData, used, usedOn)) {
			stop();
			return;
		}

		player.getUpdateFlags().sendAnimation(883, 0);
		player.getInventory().remove(used);

		if (successfulAttempt())
			cookFood();
		else {
			burnFood();
		}
		amountToCook -= 1;
		if (amountToCook == 0)
			stop();
	}

	private int getCookingLevelBoost() {
		Item gloves = player.getEquipment().getItems()[9];

		if ((gloves != null) && (gloves.getId() == 775)) {
			return 3;
		}

		return 0;
	}

	@Override
	public void onStop() {
	}

	private boolean successfulAttempt() {
		if (player.getSkill().getLevels()[7] > cookingData.getNoBurnLevel()) {
			return true;
		}

		return Skills.isSuccess(player.getMaxLevels()[7] + getCookingLevelBoost(), cookingData.getLevelRequired() / 2 == 0 ? 1 : cookingData.getLevelRequired() / 2);
	}
}
