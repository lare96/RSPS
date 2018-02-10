package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendItemOnInterface extends OutgoingPacket {

	private final int id;

	private final int zoom;

	private final int model;

	public SendItemOnInterface(int id, int zoom, int model) {
		this.id = id;
		this.zoom = zoom;
		this.model = model;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
		out.writeHeader(client.getEncryptor(), 246);
		out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
		out.writeShort(zoom);
		out.writeShort(model);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 246;
	}

}
