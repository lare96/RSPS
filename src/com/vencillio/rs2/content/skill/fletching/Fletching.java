package com.vencillio.rs2.content.skill.fletching;

import java.util.HashMap;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.Task.BreakType;
import com.vencillio.core.task.Task.StackType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.firemaking.LogData;
import com.vencillio.rs2.content.skill.fletching.fletchable.Fletchable;
import com.vencillio.rs2.content.skill.fletching.fletchable.FletchableItem;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendItemOnInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public enum Fletching {
	SINGLETON;

	public static final String FLETCHABLE_KEY = "FLETCHABLE_KEY";

	private final HashMap<Integer, Fletchable> FLETCHABLES = new HashMap<>();

	public boolean itemOnItem(Player player, Item use, Item with) {
		if (player.getSkill().locked()) {
			return false;
		}
		
		if (LogData.getLogById(use.getId()) != null && LogData.getLogById(with.getId()) != null) {
			return false;
		}

		final Fletchable fletchable = getFletchable(use.getId(), with.getId());

		if (fletchable == null || use.getId() == 590 || with.getId() == 590) {
			return false;
		}

		String prefix = fletchable.getWith().getDefinition().getName().split(" ")[0];

		switch (fletchable.getFletchableItems().length) {
		case 1:
			player.getAttributes().set(FLETCHABLE_KEY, fletchable);
			player.send(new SendString("\\n \\n \\n \\n \\n" + fletchable.getFletchableItems()[0].getProduct().getDefinition().getName(), 2799));
			player.send(new SendItemOnInterface(1746, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
			player.send(new SendChatBoxInterface(4429));
			return true;
		case 2:
			player.getAttributes().set(FLETCHABLE_KEY, fletchable);
			player.send(new SendItemOnInterface(8869, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
			player.send(new SendItemOnInterface(8870, 170, fletchable.getFletchableItems()[1].getProduct().getId()));

			player.send(new SendString("\\n \\n \\n \\n \\n ".concat(prefix + " Short Bow"), 8874));
			player.send(new SendString("\\n \\n \\n \\n \\n ".concat(prefix + " Long Bow"), 8878));

			player.send(new SendChatBoxInterface(8866));
			return true;
		case 3:
			player.getAttributes().set(FLETCHABLE_KEY, fletchable);
			player.send(new SendItemOnInterface(8883, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
			player.send(new SendItemOnInterface(8884, 170, fletchable.getFletchableItems()[1].getProduct().getId()));
			player.send(new SendItemOnInterface(8885, 170, fletchable.getFletchableItems()[2].getProduct().getId()));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(prefix + " Short Bow"), 8889));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(prefix + " Long Bow"), 8893));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Crossbow Stock"), 8897));
			player.send(new SendChatBoxInterface(8880));
			return true;
		case 4:
			player.getAttributes().set(FLETCHABLE_KEY, fletchable);
			player.send(new SendItemOnInterface(8902, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
			player.send(new SendItemOnInterface(8903, 170, fletchable.getFletchableItems()[1].getProduct().getId()));
			player.send(new SendItemOnInterface(8904, 170, fletchable.getFletchableItems()[2].getProduct().getId()));
			player.send(new SendItemOnInterface(8905, 170, fletchable.getFletchableItems()[3].getProduct().getId()));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("15 Arrow Shafts"), 8909));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Short Bow"), 8913));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Long Bow"), 8917));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Crossbow Stock"), 8921));
			player.send(new SendChatBoxInterface(8899));
			return true;
		default:
			return false;
		}
	}

	public boolean fletch(Player player, int index, int amount) {
		if (player.getSkill().locked()) {
			return false;
		}

		Fletchable fletchable = (Fletchable) player.getAttributes().get(FLETCHABLE_KEY);

		if (fletchable == null) {
			return false;
		}

		if (start(player, fletchable, index, amount)) {
			return true;
		}

		return false;
	}

	public void addFletchable(Fletchable fletchable) {
		if (FLETCHABLES.put(fletchable.getWith().getId(), fletchable) != null) {
			System.out.println("[Fletching] Conflicting item values: " + fletchable.getWith().getId() + " Type: " + fletchable.getClass().getSimpleName());
		}
	}

	public Fletchable getFletchable(int use, int with) {
		return FLETCHABLES.get(use) == null ? FLETCHABLES.get(with) : FLETCHABLES.get(use);
	}

	public boolean clickButton(Player player, int button) {
		if (player.getAttributes().get(FLETCHABLE_KEY) == null) {
			return false;
		}

		Fletchable fletchable = (Fletchable) player.getAttributes().get(FLETCHABLE_KEY);

		switch (button) {

		/** Option 1 - Make all */
		case 6211:
			start(player, fletchable, 0, player.getInventory().getItemAmount(fletchable.getWith().getId()));
			return true;

			/** Option 1 - Make 1 */
		case 34205:
		case 34185:
		case 34170:
		case 10239:
			start(player, fletchable, 0, 1);
			return true;

			/** Option 1 - Make 5 */
		case 34204:
		case 34184:
		case 34169:
		case 10238:
			start(player, fletchable, 0, 5);
			return true;

			/** Option 1 - Make 10 */
		case 34203:
		case 34183:
		case 34168:
			start(player, fletchable, 0, 10);
			return true;

			/** Option 1 - Make X */
		case 34202:
		case 34182:
		case 34167:
		case 6212:
			start(player, fletchable, 0, 28);
			return true;

			/** Option 2 - Make 1 */
		case 34209:
		case 34189:
		case 34174:
			start(player, fletchable, 1, 1);
			return true;

			/** Option 2 - Make 5 */
		case 34208:
		case 34188:
		case 34173:
			start(player, fletchable, 1, 5);
			return true;

			/** Option 2 - Make 10 */
		case 34207:
		case 34187:
		case 34172:
			start(player, fletchable, 1, 10);
			return true;

			/** Option 2 - Make X */
		case 34206:
		case 34186:
		case 34171:
			start(player, fletchable, 1, 28);
			return true;

			/** Option 3 - Make 1 */
		case 34213:
		case 34193:
			start(player, fletchable, 2, 1);
			return true;

			/** Option 3 - Make 5 */
		case 34212:
		case 34192:
			start(player, fletchable, 2, 5);
			return true;

			/** Option 3 - Make 10 */
		case 34211:
		case 34191:
			start(player, fletchable, 2, 10);
			return true;

			/** Option 3 - Make X */
		case 34210:
		case 34190:
			start(player, fletchable, 2, 28);
			return true;

			/** Option 4 - Make 1 */
		case 34217:
			start(player, fletchable, 3, 1);
			return true;

			/** Option 4 - Make 5 */
		case 34216:
			start(player, fletchable, 3, 5);
			return true;

			/** Option 4 - Make 10 */
		case 34215:
			start(player, fletchable, 3, 10);
			return true;

			/** Option 4 - Make X */
		case 34214:
			start(player, fletchable, 3, 28);
			return true;

		default:
			return false;
		}
	}

	public boolean start(Player player, Fletchable fletchable, int index, int amount) {
		if (fletchable == null) {
			return false;
		}

		player.getAttributes().remove(FLETCHABLE_KEY);

		FletchableItem item = fletchable.getFletchableItems()[index];

		player.send(new SendRemoveInterfaces());

		if (player.getLevels()[Skills.FLETCHING] < item.getLevel()) {
			DialogueManager.sendStatement(player, "<col=369>You need a Fletching level of " + item.getLevel() + " to do that.");
			return true;
		}

		if (!(player.getInventory().hasAllItems(fletchable.getIngediants()))) {
			String firstName = fletchable.getUse().getDefinition().getName().toLowerCase();
			String secondName = fletchable.getWith().getDefinition().getName().toLowerCase();

			if (fletchable.getUse().getAmount() > 1 && !firstName.endsWith("s")) {
				firstName = firstName.concat("s");
			}

			if (fletchable.getWith().getAmount() > 1 && !secondName.endsWith("s")) {
				secondName = secondName.concat("s");
			}

			if (fletchable.getUse().getAmount() == 1 && firstName.endsWith("s")) {
				firstName = firstName.substring(0, firstName.length() - 1);
			}

			if (fletchable.getWith().getAmount() == 1 && secondName.endsWith("s")) {
				secondName = secondName.substring(0, secondName.length() - 1);
			}

			final String firstAmount;

			if (fletchable.getUse().getAmount() == 1) {
				firstAmount = Utility.getAOrAn(fletchable.getUse().getDefinition().getName());
			} else {
				firstAmount = String.valueOf(fletchable.getUse().getAmount());
			}

			final String secondAmount;

			if (fletchable.getWith().getAmount() == 1) {
				secondAmount = Utility.getAOrAn(fletchable.getWith().getDefinition().getName());
			} else {
				secondAmount = String.valueOf(fletchable.getWith().getAmount());
			}

			String firstRequirement = firstAmount + " " + firstName;
			String secondRequirement = secondAmount + " " + secondName;
			player.send(new SendMessage("You need " + firstRequirement + " and " + secondRequirement + " to do that."));
			return true;
		}

		TaskQueue.queue(new Task(player, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.SKILL_CREATING) {
			private int iterations = 0;

			@Override
			public void execute() {
				player.getSkill().lock(2);

				player.getUpdateFlags().sendAnimation(new Animation(fletchable.getAnimation()));
				player.getSkill().addExperience(Skills.FLETCHING, item.getExperience());
				player.getInventory().remove(fletchable.getIngediants(), true);
				player.getInventory().add(item.getProduct());

				if (fletchable.getProductionMessage() != null) {
					player.send(new SendMessage(fletchable.getProductionMessage()));
				}

				if (++iterations == amount) {
					stop();
					return;
				}

				if (!(player.getInventory().hasAllItems(fletchable.getIngediants()))) {
					stop();
					DialogueManager.sendStatement(player, "<col=369>You have run out of materials.");
					return;
				}
			}

			@Override
			public void onStop() {
				player.getUpdateFlags().sendAnimation(new Animation(65535));
			}
		});

		return true;
	}
}