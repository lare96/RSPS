package com.vencillio.core.task.impl;

import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles ticking down the antifire potion effect
 * 
 * @author Arithium
 * 
 */
public class AntifireTask extends Task {

	/**
	 * The cycles before it ends
	 */
	private int cycles;

	/**
	 * The player who drank the antifire potion
	 */
	private final Player player;

	/**
	 * The potion is a super potion
	 */
	private final boolean isSuper;

	/**
	 * The potion is successfull
	 */
	private boolean success;

	/**
	 * Constructs a new AntiFireTask for the player
	 * 
	 * @param player
	 *            The player who drank the antifire potion
	 * @param isSuper
	 *            If the potion is a super potion or not
	 */
	public AntifireTask(Player player, boolean isSuper) {
		super(player, 1, false, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.cycles = 600;
		this.player = player;
		this.isSuper = isSuper;
		this.success = true;

		if (player.getAttributes().get("fire_potion_task") != null) {
			// cancels this task when starting another one
			((AntifireTask) player.getAttributes().get("fire_potion_task")).cycles = 600;
			success = false;
			return;
		}
		
		/**
		 * To cancel the previous task
		 */
		player.getAttributes().set("fire_resist", Boolean.FALSE);
		player.getAttributes().set("super_fire_resist", Boolean.FALSE);
		player.getAttributes().set("fire_potion_task", this);

		player.getAttributes().set(isSuper ? "super_fire_resist" : "fire_resist", Boolean.TRUE);
	}

	@Override
	public void execute() {
		if (player.isDead() || !success) {
			this.stop();
			return;
		}

		if ((!isSuper && !player.getAttributes().is("fire_resist")) || (isSuper && !player.getAttributes().is("super_fire_resist"))) {
			// cancels this task when starting another one
			this.stop();
			return;
		}
		if (cycles > 0) {
			cycles--;

			if (cycles == 100) {
				player.getClient().queueOutgoingPacket(new SendMessage("@red@Your resistance to dragonfire is about to run out."));
			}

			if (cycles == 0) {
				player.getClient().queueOutgoingPacket(new SendMessage("@red@Your resistance to dragonfire has run out."));
				this.stop();
				return;
			}
		}
	}

	@Override
	public void onStop() {
		if (success) {
			player.getAttributes().set(isSuper ? "super_fire_resist" : "fire_resist", Boolean.FALSE);
			player.getAttributes().remove("fire_potion_task");
		}
	}

}
