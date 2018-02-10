package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;

public class CloseInterfacePacket extends IncomingPacket {
	@Override
	public int getMaxDuplicates() {
		return 1;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {

		if (player.getInterfaceManager().getMain() == 48500) {
			player.getPriceChecker().withdrawAll();
		}

		player.getInterfaceManager().reset();

		if (player.getTrade().trading()) {
			player.getTrade().end(false);
		}

		if (player.getDueling().isStaking()) {
			player.getDueling().decline();
		}

		player.getShopping().reset();
	}
}
