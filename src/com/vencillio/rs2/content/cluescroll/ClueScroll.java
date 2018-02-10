package com.vencillio.rs2.content.cluescroll;

import com.vencillio.rs2.content.cluescroll.Clue.ClueType;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Contains all the fundamental methods to create a functioning clue scroll.
 * 
 * @author Michael | Chex
 *
 */
public interface ClueScroll {

	/**
	 * Executes the clue scroll action.
	 * 
	 * @param player
	 *            - The player executing the clue scroll.
	 * @return <code>True if the clue scroll was successfully executed; <code>False</code>
	 *         otherwise.
	 */
	public boolean execute(Player player);

	/**
	 * Checks if the player can complete the scroll.
	 * 
	 * @param player
	 *            - The player to test.
	 * @return <code>True</code> if the player can complete the clue scroll;
	 *         <code>False</code> otherwise.
	 */
	public boolean meetsRequirements(Player player);

	/**
	 * A check if the player is within the completion area of the scroll.
	 * 
	 * @param location
	 *            - The player's location to test.
	 * @return <code>True</code> if the location is within the completion area;
	 *         <code>False</code> otherwise.
	 */
	public boolean inEndArea(Location location);

	/**
	 * Gets the clue for the scroll.
	 * 
	 * @return The clue.
	 */
	public Clue getClue();

	/**
	 * Gets the clue scroll item id.
	 * 
	 * @return The clue scroll item id.
	 */
	public int getScrollId();

	/**
	 * Gets the difficulty of the clue scroll.
	 * 
	 * @return The clue difficulty.
	 */
	public ClueDifficulty getDifficulty();

	/**
	 * Displays the clue for a player.
	 * 
	 * @param player
	 *            - The player to display the clue for.
	 */
	public default void displayClue(Player player) {
		getClue().display(player);
	}

	/**
	 * Gets the clue scroll type.
	 * @return The clue scroll type.
	 */
	public default ClueType getClueType() {
		return getClue().getClueType();
	}
	/**
	 * Rewards the player with either the next clue scroll in the treasure
	 * trails or rewards with the final item; a casket.
	 * 
	 * <pre>
	 * Easy scrolls have a maximum of 5 clues per trail.
	 * Medium scrolls have a maximum of 6 clues per trail.
	 * Hard scrolls have a maximum of 8 clues per trail.
	 * Elite scrolls have a maximum of 11 clues per trail.
	 * </pre>
	 * 
	 * @param player
	 *            - The player to receive the reward.
	 * @param acquirement
	 *            - The means of which the player has acquired the reward.
	 */
	public default void reward(Player player, String acquirement) {
		Item item;
		boolean nextClue = false;

		switch (getDifficulty()) {
		case EASY:
			nextClue = player.getCluesCompleted()[0] < 5 && Math.random() > 0.5;

			if (nextClue) {
				player.setCluesCompleted(0, player.getCluesCompleted()[0] + 1);
			} else {
				player.setCluesCompleted(0, 0);
			}
			break;

		case MEDIUM:
			nextClue = player.getCluesCompleted()[1] < 6 && Math.random() > 0.5;

			if (nextClue) {
				player.setCluesCompleted(1, player.getCluesCompleted()[1] + 1);
			} else {
				player.setCluesCompleted(1, 0);
			}
			break;

		case HARD:
			nextClue = player.getCluesCompleted()[2] < 8 && Math.random() > 0.5;

			if (nextClue) {
				player.setCluesCompleted(2, player.getCluesCompleted()[2] + 1);
			} else {
				player.setCluesCompleted(2, 0);
			}
			break;
		}

		if (!nextClue) {
			DialogueManager.sendItem1(player, acquirement + " a casket!", ClueScrollManager.CASKET_HARD);
			player.send(new SendMessage("Well done, you've completed the Treasure Trail!"));
			item = new Item(getDifficulty().equals(ClueDifficulty.EASY) ? ClueScrollManager.CASKET_EASY : getDifficulty().equals(ClueDifficulty.MEDIUM) ? ClueScrollManager.CASKET_MEDIUM : ClueScrollManager.CASKET_HARD);
		} else {
			item = ClueScrollManager.getRandomClue(player, getDifficulty());
			if (item == null) {
				DialogueManager.sendItem1(player, acquirement + " a casket!", ClueScrollManager.CASKET_HARD);
				player.send(new SendMessage("Well done, you've completed the Treasure Trail!"));
				item = new Item(getDifficulty().equals(ClueDifficulty.EASY) ? ClueScrollManager.CASKET_EASY : getDifficulty().equals(ClueDifficulty.MEDIUM) ? ClueScrollManager.CASKET_MEDIUM : ClueScrollManager.CASKET_HARD);
			} else {
				DialogueManager.sendItem1(player, acquirement + " another clue!", item.getId());
			}
		}
		
		player.getInventory().remove(getScrollId());
		player.getInventory().add(item);
	}
}