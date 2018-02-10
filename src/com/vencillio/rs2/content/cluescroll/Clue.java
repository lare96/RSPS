package com.vencillio.rs2.content.cluescroll;

import java.util.Objects;

import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the clue the player reads after clicking a clue scroll.
 * 
 * @author Michael | Chex
 *
 */
public final class Clue {

	/**
	 * A representation of the type of clue a scroll is.
	 * 
	 * @author Michael | Chex
	 *
	 */
	public static enum ClueType {

		/**
		 * A map clue.
		 */
		MAP,

		/**
		 * A cryptic clue.
		 */
		CRYPTIC,

		/**
		 * An emote clue.
		 */
		EMOTE,

		/**
		 * A coordinate clue.
		 */
		COORDINATE
	}

	/**
	 * The clue type for this clue.
	 */
	private final ClueType clueType;

	/**
	 * The data required to display the clue.
	 */
	private final Object[] data;

	/**
	 * Constructs a new Clue.
	 * 
	 * @param clueType
	 *            - The type of clue.
	 * @param data
	 *            - The data required to display the clue.
	 */
	public Clue(ClueType clueType, Object... data) {
		this.clueType = clueType;
		this.data = Objects.requireNonNull(data);
	}

	/**
	 * Displays the clue for a player.
	 * 
	 * @param player
	 *            - The player to display the clue for.
	 */
	public void display(Player player) {
		switch (clueType) {
		
		case MAP:
			player.send(new SendInterface(Integer.parseInt(String.valueOf(data[0]))));
			break;
			
		case COORDINATE:
		case CRYPTIC:
			player.send(new SendRemoveInterfaces());
			player.send(new SendInterface(6965));
			
			for (int i = 6968; i <= 6975; i++) {
				player.send(new SendString(String.valueOf(data[i - 6968]), i));
			}
			break;
			
		case EMOTE:
			player.send(new SendRemoveInterfaces());
			player.send(new SendInterface(6965));

			for (int i = 6968; i <= 6975; i++) {
				player.send(new SendString(String.valueOf(data[1 + i - 6968]), i));
			}
			break;

		}
	}

	public ClueType getClueType() {
		return clueType;
	}

}