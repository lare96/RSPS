package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public final class SendNpcDisplay extends OutgoingPacket {

	private int npc;
	private int size;

	public SendNpcDisplay(int npc, int size) {
		this.npc = npc;
		this.size = size;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
		out.writeHeader(client.getEncryptor(), 124);
		out.writeByte(npc);
		out.writeByte(size);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 124;
	}
}
