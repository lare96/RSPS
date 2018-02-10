package com.vencillio.rs2.content.cluescroll.scroll;

import com.vencillio.rs2.content.cluescroll.Clue;
import com.vencillio.rs2.content.cluescroll.Clue.ClueType;
import com.vencillio.rs2.content.cluescroll.ClueDifficulty;
import com.vencillio.rs2.content.cluescroll.ClueScroll;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public class MapScroll implements ClueScroll {
	private final int scrollId;
	private final ClueDifficulty difficulty;
	private final Location endLocation;
	private final Object[] data;
	
	public MapScroll(int scrollId, ClueDifficulty difficulty, Location endLocation, Object... data) {
		this.scrollId = scrollId;
		this.difficulty = difficulty;
		this.endLocation = endLocation;
		this.data = data;
	}

	@Override
	public boolean inEndArea(Location location) {
		return location.equals(endLocation);
	}

	@Override
	public Clue getClue() {
		return new Clue(ClueType.MAP, data);
	}

	@Override
	public ClueDifficulty getDifficulty() {
		return difficulty;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return inEndArea(player.getLocation());
	}

	@Override
	public boolean execute(Player player) {
		if (!player.getInventory().hasItemId(scrollId)) {
			return false;
		}

		reward(player, "You've found");
		return true;
	}

	@Override
	public int getScrollId() {
		return scrollId;
	}
}