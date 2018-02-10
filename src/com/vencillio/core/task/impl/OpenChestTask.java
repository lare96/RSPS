package com.vencillio.core.task.impl;

import com.vencillio.core.cache.map.RSObject;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.task.Task;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public abstract class OpenChestTask extends Task {

	private final int x;
	private final int y;
	private final int z;
	private final int type;
	private final int face;
	private final int replace;
	private byte stage = 0;

	public OpenChestTask(Player p, int x, int y, int z, int replace, int type, int face) {
		super(p, 1);
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.face = face;
		this.replace = replace;

		p.getClient().queueOutgoingPacket(new SendMessage("You open the chest.."));
		p.getUpdateFlags().sendAnimation(832, 0);
	}

	@Override
	public void execute() {
		if (stage == 0) {
			GameObject o = new GameObject(replace, x, y, z, type, face);
			ObjectManager.removeFromList(o);
			ObjectManager.register(o);
			Region.getRegion(x, y).addObject(new RSObject(x, y, z, replace, type, face));
		} else if (stage == 2) {
			stop();
		}

		stage++;
	}
}
