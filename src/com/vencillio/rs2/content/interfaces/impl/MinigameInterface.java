package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the minigame teleport interface
 * @author Daniel
 *
 */
public class MinigameInterface extends InterfaceHandler {
	
	public MinigameInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"Barrows",
			"Warriors Guild",
			"Duel Arena",
			"Pest Control",
			"Fight Caves",
			"Weapon Game",
			"Clan Wars",
			
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 65051;
	}

}

