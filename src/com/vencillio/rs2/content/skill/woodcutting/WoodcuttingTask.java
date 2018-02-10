package com.vencillio.rs2.content.skill.woodcutting;

import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.TaskIdentifier;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSound;

/**
 * Handles the woodcutting skill
 * 
 * @author Arithium
 * 
 */
public class WoodcuttingTask extends Task {

	/**
	 * Attempts to chop a tree down
	 * 
	 * @param player
	 *            The player woodcutting
	 * @param objectId
	 *            The id of the object
	 * @param x
	 *            The x coordinate of the object
	 * @param y
	 *            The y coordinate of the object
	 */
	public static void attemptWoodcutting(Player player, int objectId, int x, int y) {
		GameObject object = new GameObject(objectId, x, y, player.getLocation().getZ(), 10, 0);
		WoodcuttingTreeData tree = WoodcuttingTreeData.forId(object.getId());
		if (tree == null) {
			return;
		}

		if (!meetsRequirements(player, tree, object)) {
			return;
		}

		WoodcuttingAxeData[] axes = new WoodcuttingAxeData[15];

		int d = 0;

		for (int s : AXES) {
			if (player.getEquipment().getItems()[3] != null && player.getEquipment().getItems()[3].getId() == s) {
				axes[d] = WoodcuttingAxeData.forId(s);
				d++;
				break;
			}
		}

		// if (axes == null) {
		if (d == 0) {
			for (Item i : player.getInventory().getItems()) {
				if (i != null) {
					for (int c : AXES) {
						if (i.getId() == c) {
							axes[d] = WoodcuttingAxeData.forId(c);
							d++;
							break;
						}
					}
				}
			}
		}
		// }

		int skillLevel = 0;
		int anyLevelAxe = 0;
		int index = -1;
		int indexb = 0;

		for (WoodcuttingAxeData i : axes) {
			if (i != null) {
				if (meetsAxeRequirements(player, i)) {
					if (index == -1 || i.getLevelRequired() > skillLevel) {
						index = indexb;
						skillLevel = i.getLevelRequired();
					}
				}

				anyLevelAxe = i.getLevelRequired();
			}

			indexb++;
		}

		if (index == -1) {
			if (anyLevelAxe != 0) {
				player.getClient().queueOutgoingPacket(new SendMessage("You need a woodcutting level of " + anyLevelAxe + " to use this axe."));
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have an axe."));
			}
			return;
		}

		WoodcuttingAxeData axe = axes[index];

		player.getClient().queueOutgoingPacket(new SendMessage("You swing your axe at the tree."));
		player.getUpdateFlags().sendAnimation(axe.getAnimation());
		player.getUpdateFlags().sendFaceToDirection(object.getLocation());
		TaskQueue.queue(new WoodcuttingTask(player, objectId, tree, object, axe));
	}

	/**
	 * Gets if the player meets the requirements to chop the tree with the axe
	 * 
	 * @param player
	 *            The player chopping the tree
	 * @param data
	 *            The data for the axe the player is wielding
	 * @return
	 */
	private static boolean meetsAxeRequirements(Player player, WoodcuttingAxeData data) {
		if (data == null) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have a hatchet."));
			return false;
		}
		if (player.getSkill().getLevels()[8] < data.getLevelRequired()) {
			return false;
		}
		return true;
	}

	/**
	 * Gets if the player meets the requirements to chop the tree
	 * 
	 * @param player
	 *            The player chopping the tree
	 * @param data
	 *            The tree data
	 * @param object
	 *            The tree object
	 * @return
	 */
	private static boolean meetsRequirements(Player player, WoodcuttingTreeData data, GameObject object) {
		if (player.getSkill().getLevels()[Skills.WOODCUTTING] < data.getLevelRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You need a woodcutting level of " + data.getLevelRequired() + " to cut this tree."));
			return false;
		}
		if (!Region.objectExists(object.getId(), object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ())) {
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getUpdateFlags().sendAnimation(-1, 0);
			player.getClient().queueOutgoingPacket(new SendMessage("You don't have enough inventory space to cut this."));
			return false;
		}
		return true;
	}

	/**
	 * Constructs a new player instance
	 */
	private Player player;

	/**
	 * The tree the player is chopping
	 */
	private GameObject object;

	/**
	 * The woodcutting axe data for the axe the player is using
	 */
	private WoodcuttingAxeData axe;
	/**
	 * The woodcutting tree data for the tree the player is chopping
	 */
	private WoodcuttingTreeData tree;
	/**
	 * The id of the tree the player is chopping
	 */
	private final int treeId;

	/**
	 * The animation cycle for the chopping animation
	 */
	private int animationCycle;

	private int pos;

	/**
	 * An array of normal tree ids
	 */
	private final int[] NORMAL_TREES = { 1278, 1276 };

	/**
	 * An array of axes starting from the best to the worst
	 */
	private static final int[] AXES = { 13661, 6739, 1359, 1357, 1355, 1361, 1353, 1349, 1351 };

	/**
	 * Constructs a new woodcutting task
	 * 
	 * @param player
	 * @param treeId
	 * @param tree
	 * @param object
	 * @param axe
	 */
	public WoodcuttingTask(Player player, int treeId, WoodcuttingTreeData tree, GameObject object, WoodcuttingAxeData axe) {
		super(player, 1, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.player = player;
		this.object = object;
		this.tree = tree;
		this.axe = axe;
		this.treeId = treeId;
	}

	/**
	 * Sends the animation to swing the axe
	 */
	private void animate() {
		player.getClient().queueOutgoingPacket(new SendSound(472, 0, 0));

		if (++animationCycle == 1) {
			player.getUpdateFlags().sendAnimation(axe.getAnimation());
			animationCycle = 0;
		}
	}

	@Override
	public void execute() {
		if (!meetsRequirements(player, tree, object)) {
			stop();
			return;
		}

		if (pos == 3) {
			if ((successfulAttemptChance()) && (handleTreeChopping())) {
				stop();
				return;
			}

			pos = 0;
		} else {
			pos += 1;
		}

		animate();
	}

	/**
	 * Handles giving a log after cutting a tree
	 */
	private void handleGivingLogs() {
		player.getInventory().add(new Item(tree.getReward(), 1));
		player.getSkill().addExperience(Skills.WOODCUTTING, tree.getExperience());

	}

	/**
	 * Handles chopping a tree down
	 * 
	 * @return
	 */
	private boolean handleTreeChopping() {
		if (isNormalTree()) {
			successfulAttempt();
			return true;
		}

		if (Utility.randomNumber(9 + (World.getActivePlayers() / 50)) == 1) {
			successfulAttempt();
			return true;
		}

		handleGivingLogs();

		return false;
	}

	/**
	 * Gets if the tree is a normal tree or not
	 * 
	 * @return
	 */
	private boolean isNormalTree() {
		for (int i : NORMAL_TREES) {
			if (i == treeId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onStop() {
	}

	/**
	 * Handles chopping a tree down
	 */
	private void successfulAttempt() {
		player.getClient().queueOutgoingPacket(new SendSound(1312, 5, 0));
		player.getClient().queueOutgoingPacket(new SendMessage("You successfully chop down the tree."));
		player.getInventory().add(new Item(tree.getReward(), 1));
		player.getSkill().addExperience(Skills.WOODCUTTING, tree.getExperience());
		player.getUpdateFlags().sendAnimation(new Animation(65535));
		AchievementHandler.activateAchievement(player, AchievementList.CHOP_DOWN_150_TREES, 1);
		AchievementHandler.activateAchievement(player, AchievementList.CHOP_DOWN_750_TREES, 1);

		GameObject replacement = new GameObject(tree.getReplacement(), object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), 10, 0);
		RSObject rsObject = new RSObject(replacement.getLocation().getX(), replacement.getLocation().getY(), replacement.getLocation().getZ(), object.getId(), 10, 0);

		if (rsObject != null) {
			ObjectManager.register(replacement);
			Region.getRegion(rsObject.getX(), rsObject.getY()).removeObject(rsObject);
			TaskQueue.queue(new StumpTask(object, treeId, tree.getRespawnTimer()));
		}
	}

	/**
	 * Gets if the chop was a successful attempt
	 * 
	 * @return
	 */
	private boolean successfulAttemptChance() {
		return Skills.isSuccess(player, Skills.WOODCUTTING, tree.getLevelRequired(), axe.getLevelRequired());
	}
}
