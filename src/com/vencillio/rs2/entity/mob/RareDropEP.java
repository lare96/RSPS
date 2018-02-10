package com.vencillio.rs2.entity.mob;

import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;

public class RareDropEP {

	private double ep = 0;
	private byte received = 0;

	public RareDropEP() {
	}

	public void addReceived() {
		received++;
	}

	public void forHitOnMob(Player player, Mob mob, Hit hit) {
		if (hit.getDamage() > 0) {
			if (player.getController().equals(ControllerManager.DEFAULT_CONTROLLER) || player.getController().equals(ControllerManager.GOD_WARS_CONTROLLER) || player.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)) {

				ep += ((hit.getDamage()) / 5000.0) + ((mob.getLevels()[Skills.DEFENCE]) / 4000.0);
			}
		}
	}

	public double getEp() {
		return ep;
	}

	public int getEpAddon() {
		return (int) ep;
	}

	public int getReceived() {
		return received;
	}

	public void reset() {
		ep = 0;
	}

	public void setEp(double ep) {
		this.ep = ep;
	}

	public void setReceived(int received) {
		this.received = (byte) received;
	}

}
