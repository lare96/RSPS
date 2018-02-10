package com.vencillio.rs2.content.skill.farming;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Farming {
	/**
	 * Special plant one constructor & getter
	 */
	private SpecialPlantOne specialPlantOne;

	public SpecialPlantOne getSpecialPlantOne() {
		return specialPlantOne;
	}
	
	public void setSpecialPlantOne(SpecialPlantOne specialPlantOne) {
		this.specialPlantOne = specialPlantOne;
	}

	/**
	 * Special plant one constructor & getter
	 */
	private SpecialPlantTwo specialPlantTwo;

	public SpecialPlantTwo getSpecialPlantTwo() {
		return specialPlantTwo;
	}
	
	public void setSpecialPlantTwo(SpecialPlantTwo specialPlantTwo) {
		this.specialPlantTwo = specialPlantTwo;
	}

	/**
	 * Compost constructor & getter
	 */
	private Compost compost;

	public Compost getCompost() {
		return compost;
	}
	
	public void setCompost(Compost compost) {
		this.compost = compost;
	}

	/**
	 * Allotoments constructor & getter
	 */
	private Allotments allotment;

	public Allotments getAllotment() {
		return allotment;
	}
	
	public void setAllotment(Allotments allotment) {
		this.allotment = allotment;
	}

	/**
	 * Followers constructor & getter
	 */
	private Flowers flower;

	public Flowers getFlowers() {
		return flower;
	}
	
	public void setFlower(Flowers flower) {
		this.flower = flower;
	}

	/**
	 * Herbs constructor & getter
	 */
	private Herbs herb;

	public Herbs getHerbs() {
		return herb;
	}
	
	public void setHerb(Herbs herb) {
		this.herb = herb;
	}

	/**
	 * Hops constructor & getter
	 */
	private Hops hops;

	public Hops getHops() {
		return hops;
	}
	
	public void setHops(Hops hops) {
		this.hops = hops;
	}

	/**
	 * Bushes constructor & getter
	 */
	private Bushes bushes;

	public Bushes getBushes() {
		return bushes;
	}
	
	public void setBushes(Bushes bushes) {
		this.bushes = bushes;
	}

	/**
	 * Seedling constructor & getter
	 */
	private Seedling seedling;

	public Seedling getSeedling() {
		return seedling;
	}
	
	public void setSeedling(Seedling seedling) {
		this.seedling = seedling;
	}

	/**
	 * Wood trees constructor & getter
	 */
	private WoodTrees trees;

	public WoodTrees getTrees() {
		return trees;
	}
	
	public void setTrees(WoodTrees trees) {
		this.trees = trees;
	}

	/**
	 * Fruit tree constructor & getter
	 */
	private FruitTree fruitTrees;

	public FruitTree getFruitTrees() {
		return fruitTrees;
	}
	
	public void setFruitTrees(FruitTree fruitTrees) {
		this.fruitTrees = fruitTrees;
	}
	
	private long farmingTimer = 0;
	
	public long getFarmingTimer() {
		return farmingTimer;
	}
	
	public void setFarmingTimer(long farmingTimer) {
		this.farmingTimer = farmingTimer;
	}
	
	public Farming(Player player) {
		allotment = new Allotments(player);
		fruitTrees = new FruitTree(player);
		trees = new WoodTrees(player);
		seedling = new Seedling(player);
		bushes = new Bushes(player);
		hops = new Hops(player);
		herb = new Herbs(player);
		flower = new Flowers(player);
		compost = new Compost(player);
		specialPlantOne = new SpecialPlantOne(player);
		specialPlantTwo	 = new SpecialPlantTwo(player);
	}
	
	public void doCalculations() {
		allotment.doCalculations();
//		fruitTrees.doCalculations();
//		trees.doCalculations();
//		bushes.doCalculations();
//		hops.doCalculations();
		herb.doCalculations();
//		flower.doCalculations();
//		specialPlantOne.doCalculations();
//		specialPlantTwo.doCalculations();
	}
	
	public static boolean prepareCrop(Player player, int item, int id, int x, int y) {
		// plant pot
		if (player.getFarming().getSeedling().fillPotWithSoil(item, x, y)) {
			return true;
		}
		// allotments
		if (player.getFarming().getAllotment().curePlant(x, y, item)) {
			return true;
		}
		if (player.getFarming().getAllotment().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getAllotment().clearPatch(x, y, item)) {
			return true;
		}
		if (item >= 3422 && item <= 3428 && id == 4090) {
			player.getInventory().remove(item, 1);
			player.getInventory().add(item + 8, 1);
			player.getUpdateFlags().sendAnimation(new Animation(832));
			player.send(new SendMessage("You put the olive oil on the fire, and turn it into sacred oil."));
			return true;
		}
		if (item <= 5340 && item > 5332) {
			if (player.getFarming().getAllotment().waterPatch(x, y, item)) {
				return true;
			}
		}
		if (player.getFarming().getAllotment().plantSeed(x, y, item)) {
			return true;
		}
		// flowers
		if (player.getFarming().getFlowers().plantScareCrow(x, y, item)) {
			return true;
		}
		if (player.getFarming().getFlowers().curePlant(x, y, item)) {
			return true;
		}
		if (player.getFarming().getFlowers().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getFlowers().clearPatch(x, y, item)) {
			return true;
		}
		if (item <= 5340 && item > 5332) {
			if (player.getFarming().getFlowers().waterPatch(x, y, item)) {
				return true;
			}
		}
		if (player.getFarming().getFlowers().plantSeed(x, y, item)) {
			return true;
		}
//		if (player.getFarming().getCompost().handleItemOnObject(item, id, x, y)) {
//			return true;
//		}
		// herbs
		if (player.getFarming().getHerbs().curePlant(x, y, item)) {
			return true;
		}
		if (player.getFarming().getHerbs().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getHerbs().clearPatch(x, y, item)) {
			return true;
		}
		if (player.getFarming().getHerbs().plantSeed(x, y, item)) {
			return true;
		}
		// hops
		if (player.getFarming().getHops().curePlant(x, y, item)) {
			return true;
		}
		if (player.getFarming().getHops().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getHops().clearPatch(x, y, item)) {
			return true;
		}
		if (item <= 5340 && item > 5332)
			if (player.getFarming().getHops().waterPatch(x, y, item)) {
				return true;
			}
		if (player.getFarming().getHops().plantSeed(x, y, item)) {
			return true;
		}
		// bushes
		if (player.getFarming().getBushes().curePlant(x, y, item)) {
			return true;
		}
		if (player.getFarming().getBushes().putCompost(x, y, item)) {
			return true;
		}

		if (player.getFarming().getBushes().clearPatch(x, y, item)) {
			return true;
		}
		if (player.getFarming().getBushes().plantSeed(x, y, item)) {
			return true;
		}
		// trees
		if (player.getFarming().getTrees().pruneArea(x, y, item)) {
			return true;
		}
		if (player.getFarming().getTrees().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getTrees().plantSapling(x, y, item)) {
			return true;
		}
		if (player.getFarming().getTrees().clearPatch(x, y, item)) {
			return true;
		}
		// fruit trees
		if (player.getFarming().getFruitTrees().pruneArea(x, y, item)) {
			return true;
		}
		if (player.getFarming().getFruitTrees().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getFruitTrees().clearPatch(x, y, item)) {
			return true;
		}
		if (player.getFarming().getFruitTrees().plantSapling(x, y, item)) {
			return true;
		}
		// special plant one
		if (player.getFarming().getSpecialPlantOne().curePlant(x, y, item)) {
			return true;
		}
		if (player.getFarming().getSpecialPlantOne().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getSpecialPlantOne().clearPatch(x, y, item)) {
			return true;
		}
		if (player.getFarming().getSpecialPlantOne().plantSapling(x, y, item)) {
			return true;
		}
		// Special plant two
		if (player.getFarming().getSpecialPlantTwo().curePlant(x, y, item)) {
			return true;
		}
		if (player.getFarming().getSpecialPlantTwo().putCompost(x, y, item)) {
			return true;
		}
		if (player.getFarming().getSpecialPlantTwo().clearPatch(x, y, item)) {
			return true;
		}
		if (player.getFarming().getSpecialPlantTwo().plantSeeds(x, y, item)) {
			return true;
		}
		// player.sendMessage("Farming disabled - coming soon");
		return false;
	}

	public static boolean inspectObject(Player player, int x, int y) {
		// allotments
		if (player.getFarming().getAllotment().inspect(x, y)) {
			return true;
		} // flowers
		if (player.getFarming().getFlowers().inspect(x, y)) {
			return true;
		}
		// herbs
		if (player.getFarming().getHerbs().inspect(x, y)) {
			return true;
		}
		// hops
		if (player.getFarming().getHops().inspect(x, y)) {
			return true;
		}
		// bushes
		if (player.getFarming().getBushes().inspect(x, y)) {
			return true;
		}
		// trees
		if (player.getFarming().getTrees().inspect(x, y)) {
			return true;
		}
		// fruit trees
		if (player.getFarming().getFruitTrees().inspect(x, y)) {
			return true;
		}
		// special plant one
		if (player.getFarming().getSpecialPlantOne().inspect(x, y)) {
			return true;
		}
		// special plant two
		if (player.getFarming().getSpecialPlantTwo().inspect(x, y)) {
			return true;
		}
		return false;
	}

	public static boolean harvest(Player player, int x, int y) {
		// allotments

		if (player.getFarming().getAllotment().harvest(x, y)) {
			return true;
		}
		// flowers
		if (player.getFarming().getFlowers().harvest(x, y)) {
			return true;
		}
		// herbs
		if (player.getFarming().getHerbs().harvest(x, y)) {
			return true;
		}
		// hops
		if (player.getFarming().getHops().harvest(x, y)) {
			return true;
		}
		// bushes
		if (player.getFarming().getBushes().harvestOrCheckHealth(x, y)) {
			return true;
		}
		// trees
		if (player.getFarming().getTrees().checkHealth(x, y)) {
			return true;
		}
		if (player.getFarming().getTrees().cut(x, y)) {
			return true;
		}
		// fruit trees
		if (player.getFarming().getFruitTrees().harvestOrCheckHealth(x, y)) {
			return true;
		}
		// special plant one
		if (player.getFarming().getSpecialPlantOne().harvestOrCheckHealth(x, y)) {
			return true;
		}
		// special plant two
		if (player.getFarming().getSpecialPlantTwo().harvestOrCheckHealth(x, y)) {
			return true;
		}
		return false;
	}
	
	public static void declare() {
		TaskQueue.queue(new Task(100, true) {
			@Override
			public void execute() {
				for (Player player : World.getPlayers()) {
					if (player == null || player.getFarming() == null || !player.isActive()) {
						continue;
					}

					player.getFarming().farmingTimer++;
					player.getFarming().doCalculations();
				}
			}

			@Override
			public void onStop() {
			}
		});
	}

	public static long getMinutesCounter(Player player) {
		return player.getFarming().farmingTimer;
	}
}