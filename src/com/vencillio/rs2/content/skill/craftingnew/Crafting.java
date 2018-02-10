package com.vencillio.rs2.content.skill.craftingnew;

import java.util.HashMap;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.Task.BreakType;
import com.vencillio.core.task.Task.StackType;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.craftingnew.craftable.Craftable;
import com.vencillio.rs2.content.skill.craftingnew.craftable.CraftableItem;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendChatBoxInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendItemOnInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public enum Crafting {
	SINGLETON;

	public static final String CRAFTABLE_KEY = "CRAFTABLE_KEY";

	private final HashMap<Integer, Craftable> CRAFTABLES = new HashMap<>();

	public boolean itemOnItem(Player player, Item use, Item with) {
		if (player.getSkill().locked()) {
			return false;
		}

		if ((use.getId() == 1733 && with.getId() == 1741) || (use.getId() == 1741 && with.getId() == 1733)) {
			player.getClient().queueOutgoingPacket(new SendInterface(2311));
			return true;
		}

		final Craftable craftable = getCraftable(use.getId(), with.getId());

		if (craftable == null) {
			return false;
		}

		switch (craftable.getCraftableItems().length) {
		case 1:
			player.getAttributes().set(CRAFTABLE_KEY, craftable);
			player.send(new SendString("\\n \\n \\n \\n \\n" + craftable.getCraftableItems()[0].getProduct().getDefinition().getName(), 2799));
			player.send(new SendItemOnInterface(1746, 170, craftable.getCraftableItems()[0].getProduct().getId()));
			player.send(new SendChatBoxInterface(4429));
			return true;
			
		case 2:
			player.getAttributes().set(CRAFTABLE_KEY, craftable);
			player.send(new SendItemOnInterface(8869, 170, craftable.getCraftableItems()[0].getProduct().getId()));
			player.send(new SendItemOnInterface(8870, 170, craftable.getCraftableItems()[1].getProduct().getId()));

			player.send(new SendString("\\n \\n \\n \\n \\n ".concat(craftable.getCraftableItems()[0].getProduct().getDefinition().getName().replace("d'hide ", "")), 8874));
			player.send(new SendString("\\n \\n \\n \\n \\n ".concat(craftable.getCraftableItems()[1].getProduct().getDefinition().getName().replace("d'hide ", "")), 8878));

			player.send(new SendChatBoxInterface(8866));
			return true;
			
		case 3:
			player.getAttributes().set(CRAFTABLE_KEY, craftable);
			player.send(new SendItemOnInterface(8883, 170, craftable.getCraftableItems()[0].getProduct().getId()));
			player.send(new SendItemOnInterface(8884, 170, craftable.getCraftableItems()[1].getProduct().getId()));
			player.send(new SendItemOnInterface(8885, 170, craftable.getCraftableItems()[2].getProduct().getId()));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(craftable.getCraftableItems()[0].getProduct().getDefinition().getName().replace("d'hide ", "")), 8889));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(craftable.getCraftableItems()[1].getProduct().getDefinition().getName().replace("d'hide ", "")), 8893));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(craftable.getCraftableItems()[2].getProduct().getDefinition().getName().replace("d'hide ", "")), 8897));
			player.send(new SendChatBoxInterface(8880));
			return true;
			
		case 4:
			player.getAttributes().set(CRAFTABLE_KEY, craftable);
			player.send(new SendItemOnInterface(8902, 170, craftable.getCraftableItems()[0].getProduct().getId()));
			player.send(new SendItemOnInterface(8903, 170, craftable.getCraftableItems()[1].getProduct().getId()));
			player.send(new SendItemOnInterface(8904, 170, craftable.getCraftableItems()[2].getProduct().getId()));
			player.send(new SendItemOnInterface(8905, 170, craftable.getCraftableItems()[3].getProduct().getId()));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(craftable.getCraftableItems()[0].getProduct().getDefinition().getName().replace("d'hide ", "")), 8909));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(craftable.getCraftableItems()[1].getProduct().getDefinition().getName().replace("d'hide ", "")), 8913));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(craftable.getCraftableItems()[2].getProduct().getDefinition().getName().replace("d'hide ", "")), 8917));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat(craftable.getCraftableItems()[3].getProduct().getDefinition().getName().replace("d'hide ", "")), 8921));
			player.send(new SendChatBoxInterface(8899));
			return true;
			
		case 5:
			player.getAttributes().set(CRAFTABLE_KEY, craftable);
			player.send(new SendItemOnInterface(8941, 170, craftable.getCraftableItems()[0].getProduct().getId()));
			player.send(new SendItemOnInterface(8942, 170, craftable.getCraftableItems()[1].getProduct().getId()));
			player.send(new SendItemOnInterface(8943, 170, craftable.getCraftableItems()[2].getProduct().getId()));
			player.send(new SendItemOnInterface(8944, 170, craftable.getCraftableItems()[3].getProduct().getId()));
			player.send(new SendItemOnInterface(8945, 170, craftable.getCraftableItems()[4].getProduct().getId()));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Body"), 8949));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Chaps"), 8953));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Vambraces"), 8957));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Bandana"), 8961));
			player.send(new SendString("\\n \\n \\n \\n \\n".concat("Boots"), 8965));
			player.send(new SendChatBoxInterface(8938));
			return true;
			
		default:
			return false;
		}
	}

	public boolean craft(Player player, int index, int amount) {
		if (player.getSkill().locked()) {
			return false;
		}

		Craftable craftable = (Craftable) player.getAttributes().get(CRAFTABLE_KEY);

		if (craftable == null) {
			return false;
		}

		if (start(player, craftable, index, amount)) {
			return true;
		}

		return false;
	}

	public void addCraftable(Craftable craftable) {
		if (CRAFTABLES.put(craftable.getWith().getId(), craftable) != null) {
			System.out.println("[Crafting] Conflicting item values: " + craftable.getWith().getId() + " Type: " + craftable.getName());
		}
	}

	public Craftable getCraftable(int use, int with) {
		return CRAFTABLES.get(use) == null ? CRAFTABLES.get(with) : CRAFTABLES.get(use);
	}

	public boolean clickButton(Player player, int button) {
		if (player.getAttributes().get(CRAFTABLE_KEY) == null) {
			return false;
		}

		Craftable craftable = (Craftable) player.getAttributes().get(CRAFTABLE_KEY);

		switch (button) {

		/** Option 1 - Make all */
		case 6211:
			start(player, craftable, 0, player.getInventory().getItemAmount(craftable.getWith().getId()));
			return true;

			/** Option 1 - Make 1 */
		case 34205:
		case 34185:
		case 34170:
		case 10239:
		case 34245:
			start(player, craftable, 0, 1);
			return true;

			/** Option 1 - Make 5 */
		case 34204:
		case 34184:
		case 34169:
		case 10238:
		case 34244:
			start(player, craftable, 0, 5);
			return true;

			/** Option 1 - Make 10 */
		case 34203:
		case 34183:
		case 34168:
		case 34243:
			start(player, craftable, 0, 10);
			return true;

			/** Option 1 - Make X */
		case 34202:
		case 34182:
		case 34167:
		case 6212:
		case 34242:
			start(player, craftable, 0, 28);
			return true;

			/** Option 2 - Make 1 */
		case 34209:
		case 34189:
		case 34174:
		case 34249:
			start(player, craftable, 1, 1);
			return true;

			/** Option 2 - Make 5 */
		case 34208:
		case 34188:
		case 34173:
		case 34248:
			start(player, craftable, 1, 5);
			return true;

			/** Option 2 - Make 10 */
		case 34207:
		case 34187:
		case 34172:
		case 34247:
			start(player, craftable, 1, 10);
			return true;

			/** Option 2 - Make X */
		case 34206:
		case 34186:
		case 34171:
		case 34246:
			start(player, craftable, 1, 28);
			return true;

			/** Option 3 - Make 1 */
		case 34213:
		case 34193:
		case 34253:
			start(player, craftable, 2, 1);
			return true;

			/** Option 3 - Make 5 */
		case 34212:
		case 34192:
		case 34252:
			start(player, craftable, 2, 5);
			return true;

			/** Option 3 - Make 10 */
		case 34211:
		case 34191:
		case 34251:
			start(player, craftable, 2, 10);
			return true;

			/** Option 3 - Make X */
		case 34210:
		case 34190:
		case 34250:
			start(player, craftable, 2, 28);
			return true;

			/** Option 4 - Make 1 */
		case 34217:
		case 35001:
			start(player, craftable, 3, 1);
			return true;

			/** Option 4 - Make 5 */
		case 34216:
		case 35000:
			start(player, craftable, 3, 5);
			return true;

			/** Option 4 - Make 10 */
		case 34215:
		case 34255:
			start(player, craftable, 3, 10);
			return true;

			/** Option 4 - Make X */
		case 34214:
		case 34254:
			start(player, craftable, 3, 28);
			return true;

			/** Option 5 - Make 1 */
		case 35005:
			start(player, craftable, 4, 1);
			return true;

			/** Option 5 - Make 5 */
		case 35004:
			start(player, craftable, 4, 5);
			return true;

			/** Option 5 - Make 10 */
		case 35003:
			start(player, craftable, 4, 10);
			return true;

			/** Option 5 - Make X */
		case 35002:
			start(player, craftable, 4, 28);
			return true;

		default:
			return false;
		}
	}

	public boolean start(Player player, Craftable craftable, int index, int amount) {
		if (craftable == null) {
			return false;
		}

		player.getAttributes().remove(CRAFTABLE_KEY);

		CraftableItem item = craftable.getCraftableItems()[index];

		player.send(new SendRemoveInterfaces());

		if (player.getLevels()[Skills.CRAFTING] < item.getLevel()) {
			DialogueManager.sendStatement(player, "<col=369>You need a Crafting level of " + item.getLevel() + " to do that.");
			return true;
		}

		if (!player.getInventory().hasAllItems(craftable.getIngediants(index))) {
			Item requiredItem = craftable.getCraftableItems()[index].getRequiredItem();
			Item product = craftable.getCraftableItems()[index].getProduct();
			String productAmount = "";
			
			if (product.getDefinition().getName().contains("vamb")) {
				productAmount = " pair of";
			} else if (!product.getDefinition().getName().endsWith("s")) {
				productAmount = " " + Utility.getAOrAn(product.getDefinition().getName());
			}
			
			player.send(new SendMessage("You need " + requiredItem.getAmount() + " piece" + (requiredItem.getAmount() > 1 ? "s" : "") + " of " + requiredItem.getDefinition().getName().toLowerCase() + " to make" + productAmount + " " + product.getDefinition().getName().toLowerCase() + "."));
			return true;
		}

		TaskQueue.queue(new Task(player, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.SKILL_CREATING) {
			private int iterations = 0;

			@Override
			public void execute() {
				player.getSkill().lock(2);

				player.getUpdateFlags().sendAnimation(new Animation(craftable.getAnimation()));
				player.getSkill().addExperience(Skills.CRAFTING, item.getExperience());
				player.getInventory().remove(craftable.getIngediants(index), true);
				player.getInventory().add(item.getProduct());

				if (craftable.getProductionMessage() != null) {
					player.send(new SendMessage(craftable.getProductionMessage()));
				}
				
				if (craftable.getName() == "Gem") {
					AchievementHandler.activateAchievement(player, AchievementList.CUT_250_GEMS, 1);
				}

				if (++iterations == amount) {
					stop();
					return;
				}

				if (!player.getInventory().hasAllItems(craftable.getIngediants(index))) {
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