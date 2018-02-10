package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.rs2.entity.mob.MobUpdateFlags;
import com.vencillio.rs2.entity.player.PlayerUpdateFlags;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.NPCUpdating;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendNPCUpdate extends OutgoingPacket {

	private final MobUpdateFlags[] nFlags;
	private final PlayerUpdateFlags pFlags;

	public SendNPCUpdate(MobUpdateFlags[] nFlags, PlayerUpdateFlags pFlags) {
		super();
		this.nFlags = nFlags;
		this.pFlags = pFlags;
	}

	@Override
	public void execute(Client client) {
		NPCUpdating.update(client, pFlags, nFlags);
	}

	@Override
	public int getOpcode() {
		return 65;
	}

}
