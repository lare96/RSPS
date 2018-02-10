package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendPlayerProfilerIndex extends OutgoingPacket {

	private final int id;

	public SendPlayerProfilerIndex(int id) {
		this.id = id;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
		out.writeHeader(client.getEncryptor(), 201);
		out.writeShort(id, StreamBuffer.ByteOrder.BIG);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 201;
	}
}
