package com.vencillio.rs2.content.minigames.pestcontrol.monsters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vencillio.core.cache.map.Region;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.GameConstants;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControlConstants;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControlGame;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.mob.Mob;

public class Portal extends Mob {

	private final List<Mob> pests = new ArrayList<Mob>();

	private final List<Mob> shifters = new ArrayList<Mob>();

	private final PestControlGame game;

	public Portal(PestControlGame game, int id, Location p, int z) {
		super(game.getVirtualRegion(), id, false, false, new Location(p, z));
		setRetaliate(false);
		init();

		getLevels()[Skills.HITPOINTS] = 250;
		getLevels()[Skills.DEFENCE] = 350;
		getLevels()[Skills.MAGIC] = 250;

		getMaxLevels()[Skills.HITPOINTS] = 250;
		getMaxLevels()[Skills.DEFENCE] = 250;
		getMaxLevels()[Skills.MAGIC] = 250;

		setRetaliate(false);
		setRespawnable(false);

		this.game = game;

		getAttributes().set(PestControlGame.PEST_GAME_KEY, game);
	}

	public void cleanup() {
		if (pests.size() > 0) {
			for (Mob i : pests) {
				i.remove();
			}
		}

		if (shifters.size() > 0) {
			for (Mob i : shifters) {
				i.remove();
			}
		}
	}

	@Override
	public Animation getDeathAnimation() {
		return new Animation(65535, 0);
	}

	public List<Mob> getPests() {
		return pests;
	}

	public void heal(int amount) {
		getLevels()[3] += amount;

		if (getLevels()[3] > getMaxLevels()[3])
			getLevels()[3] = getMaxLevels()[3];
	}

	public void init() {
	}

	public boolean isDamaged() {
		return getLevels()[3] < getMaxLevels()[3];
	}

	@Override
	public void onDeath() {
		remove();
	}

	@Override
	public void process() {
		if (isDead()) {
			return;
		}

		for (Iterator<Mob> i = pests.iterator(); i.hasNext();) {
			if (i.next().isDead()) {
				i.remove();
			}
		}

		for (Iterator<Mob> i = shifters.iterator(); i.hasNext();) {
			if (i.next().isDead()) {
				i.remove();
			}
		}

		if (Utility.randomNumber(35) == 0 && pests.size() < 3) {
			Location l = GameConstants.getClearAdjacentLocation(getLocation(), getSize(), game.getVirtualRegion());

			if (l != null) {
				pests.add(PestControlConstants.getRandomPest(l, game, this));
			}
		}

		if (game.getPlayers().size() > 3) {
			if (Utility.randomNumber(20) == 0 && shifters.size() < 1) {
				int baseX = 2656;
				int baseY = 2592;

				int x = baseX + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));
				int y = baseY + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));

				while (Region.getRegion(x, y).getClip(x, y, 0) == 256) {
					x = baseX + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));
					y = baseY + (Utility.randomNumber(2) == 0 ? Utility.randomNumber(7) : -Utility.randomNumber(7));
				}

				shifters.add(new Shifter(new Location(x, y, game.getZ()), game));
			}
		}
	}
}
