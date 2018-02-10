package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendStillGraphic extends OutgoingPacket {

	private final int id;

	private final Location p;

	private final int delay;

	public SendStillGraphic(int id, Location p, int delay) {
		this.id = id;
		this.p = p;
		this.delay = delay;
	}

	@Override
	public void execute(Client client) {
		new SendCoordinates(p, client.getPlayer()).execute(client);
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
		out.writeHeader(client.getEncryptor(), 4);
		out.writeByte(0);
		out.writeShort(id);
		out.writeByte(p.getZ());
		out.writeShort(delay);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 4;
	}

}
