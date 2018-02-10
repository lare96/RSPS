package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendMapRegion extends OutgoingPacket {

	private final Location p;

	public SendMapRegion(Player player) {
		player.getCurrentRegion().setAs(player.getLocation());
		p = player.getLocation();
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
		out.writeHeader(client.getEncryptor(), 73);
		out.writeShort(p.getRegionX() + 6, StreamBuffer.ValueType.A);
		out.writeShort(p.getRegionY() + 6);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 73;
	}

}
