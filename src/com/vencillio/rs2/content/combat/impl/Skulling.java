package com.vencillio.rs2.content.combat.impl;

import java.util.LinkedList;
import java.util.List;

import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.rs2.entity.player.Player;

public class Skulling {
	
	public static final short SKULL_TIME = 500;

	private int icon = -1;
	private short left = 0;

	private List<Player> attacked = new LinkedList<Player>();

	public void checkForSkulling(Player player, Player attack) {
		if (isSkull(player, attack))
			skull(player, attack);
	}

	public long getLeft() {
		return left;
	}

	public int getSkullIcon() {
		return icon;
	}

	public boolean hasAttacked(Player p) {
		return attacked.contains(p);
	}

	public boolean isSkull(Player player, Player attacking) {
		return (!attacking.isNpc()) && (player.inWilderness()) && (!attacking.getSkulling().hasAttacked(player));
	}

	public boolean isSkulled() {
		return left > 0;
	}

	public void setLeft(long left) {
		if (left < 0) {
			return;
		}

		if (left > Short.MAX_VALUE) {
			left = SKULL_TIME;
		}

		this.left = (short) left;
	}

	public void setSkullIcon(Player player, int skullIcon) {
		this.icon = skullIcon;
		player.setAppearanceUpdateRequired(true);
	}

	public void skull(Player player, Player attacking) {
		if (attacking != null) {
			attacked.add(attacking);
		}

		if (left <= 0) {
			left = SKULL_TIME;
			player.setAppearanceUpdateRequired(true);
			icon = 0;
		}
	}

	public void tick(final Player player) {
		TaskQueue.queue(new Task(player, 25) {
			@Override
			public void execute() {
				if (!isSkulled()) {
					return;
				}

				if ((left -= 25) <= 0) {
					unskull(player);
				}
			}

			@Override
			public void onStop() {
			}
		});
	}

	public void unskull(Player player) {
		attacked.clear();
		left = 0;
		icon = -1;
		player.setAppearanceUpdateRequired(true);
	}
}
