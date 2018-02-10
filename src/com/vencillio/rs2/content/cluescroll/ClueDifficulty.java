package com.vencillio.rs2.content.cluescroll;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.cluescroll.ClueScrollManager.ClueReward;
import com.vencillio.rs2.entity.item.Item;

/**
 * A list of difficulties for a clue scroll. Each difficulty has specific
 * rewards.
 *
 * @author Michael | Chex
 */
public enum ClueDifficulty {

	/**
	 * Easy clue scroll rewards.
	 */
	EASY(() -> {
		Item reward = null;
		int amount;
		
		if (Math.random() > (1 - (1 / 5.0))) {
			reward = crossTrialItems();
			amount = reward.getAmount();
		} else {
			reward = ClueScrollManager.EASY.nextObject().get();
			amount = reward.getAmount();
			
			if (amount > 1) {
				amount = Utility.randomNumber(amount) + 1;
			}
		}

		return new Item(reward.getId(), amount);
	}),

	/**
	 * Medium clue scroll rewards.
	 */
	MEDIUM(() -> {
		Item reward = null;
		int amount;
		
		if (Math.random() > (1 - (1 / 5.0))) {
			reward = EASY.getRewards().getReward();
			amount = reward.getAmount();
		} else {
			reward = ClueScrollManager.MEDIUM.nextObject().get();
			amount = reward.getAmount();
			
			if (amount > 1) {
				amount = Utility.randomNumber(amount) + 1;
			}
		}

		return new Item(reward.getId(), amount);
	}),

	/**
	 * Hard clue scroll rewards.
	 */
	HARD(() -> {
		Item reward = null;
		int amount;

		if (Math.random() > (1 - (1 / 5.0))) {
			reward = MEDIUM.getRewards().getReward();
			amount = reward.getAmount();
		} else {
			reward = ClueScrollManager.HARD.nextObject().get();
			amount = reward.getAmount();
			
			if (amount > 1) {
				amount = Utility.randomNumber(amount) + 1;
			}
		}

		return new Item(reward.getId(), amount);
	});

	/**
	 * All possible rewards for the difficulty.
	 */
	private ClueReward reward;

	/**
	 * Constructs a new clue scroll difficulty.
	 *
	 * @param rewardsTable
	 *            - All possible rewards for the difficulty.
	 */
	private ClueDifficulty(ClueReward reward) {
		this.reward = reward;
	}

	/**
	 * Generates a random item that can be rewarded by any level clue.
	 *
	 * @return The reward item.
	 */
	private static Item crossTrialItems() {
		return new ClueReward() {

			@Override
			public Item getReward() {
				Item reward = ClueScrollManager.CROSS_TRAILS.nextObject().get();
				int amount = reward.getAmount();
				
				if (amount > 1) {
					amount = Utility.randomNumber(amount) + 1;
				}
				return new Item(reward.getId(), amount);
			}
		}.getReward();
	}

	/**
	 * Gets the rewards table. Each reward is formated:
	 *
	 * @return The list of rewards.
	 */
	public ClueReward getRewards() {
		return reward;
	}
}