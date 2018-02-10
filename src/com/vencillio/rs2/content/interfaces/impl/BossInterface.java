package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.player.Player;

/**
 * Handles the boss teleport interface
 * @author Daniel
 *
 */
public class BossInterface extends InterfaceHandler {
	
	public BossInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"King Black Dragon",
			"Sea Troll Queen",
			"Barrelchest",
			"Corporeal Beast",
			"Daggonoths Kings",
			"Godwars",
			"Zulrah",
			"Kraken",
			"Giant Mole",
			"Chaos Element",
			"Callisto",
			"Scorpia",
			"Vet'ion",
			"Venenatis (N/A)",
			"Chaos Fanatic",
			"Crazy archaeologist",
			"Kalphite Queen (N/A)",
			
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 64051;
	}

}

