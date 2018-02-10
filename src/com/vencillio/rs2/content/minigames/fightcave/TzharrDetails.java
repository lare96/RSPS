package com.vencillio.rs2.content.minigames.fightcave;

import java.util.ArrayList;
import java.util.List;

import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.player.Player;

public final class TzharrDetails {
	
	private int stage = 0;
	private List<Mob> mobs = new ArrayList<Mob>();
	private int z;

	public void addNpc(Mob mob) {
		mobs.add(mob);
	}

	public int getKillAmount() {
		return mobs.size();
	}

	public List<Mob> getMobs() {
		return mobs;
	}

	public int getStage() {
		return stage;
	}

	public int getZ() {
		return z;
	}

	public void increaseStage() {
		stage += 1;
	}

	public boolean removeNpc(Mob mob) {
		int index = mobs.indexOf(mob);

		if (index == -1) {
			return false;
		}

		mobs.remove(mob);
		return true;
	}

	public void reset() {
		stage = 0;
	}


	public void setStage(int stage) {
		this.stage = stage;
	}

	public void setZ(Player p) {
		z = (p.getIndex() * 4);
	}

}
