package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendDetails extends OutgoingPacket {

	private final int slot;

	public SendDetails(int slot) {
		super();
		this.slot = slot;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(4);
		out.writeHeader(client.getEncryptor(), 249);
		out.writeByte(1, StreamBuffer.ValueType.A);
		out.writeShort(slot, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 249;
	}

}
