package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.network.StreamBuffer.ByteOrder;
import com.vencillio.core.network.StreamBuffer.ValueType;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendColor extends OutgoingPacket {

	private final int id;

	private final int color;

	public SendColor(int id, int color) {
		this.id = id;
		this.color = color;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(6);
		out.writeHeader(client.getEncryptor(), 122);
		out.writeShort(id, ValueType.A, ByteOrder.LITTLE);
		out.writeByte((color >> 16) & 0xFF);
		out.writeByte((color >> 8) & 0xFF);
		out.writeByte(color & 0xFF);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 122;
	}

}
