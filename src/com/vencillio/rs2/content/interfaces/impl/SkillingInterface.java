package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the skilling teleport interface
 * @author Daniel
 *
 */
public class SkillingInterface extends InterfaceHandler {
	
	public SkillingInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"Wilderness Resource",
			"Thieving",
			"Crafting",
			"Agility",
			"Mining",
			"Smithing",
			"Fishing",
			"Woodcutting",
			"Farming",
			"",
			"",
			"",
			"",
			
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 62051;
	}

}

