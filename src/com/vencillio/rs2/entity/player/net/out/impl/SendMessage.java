package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendMessage extends OutgoingPacket {

	private final String message;

	public SendMessage(Object message) {
		this.message = String.valueOf(message);
	}

	@Override
	public void execute(Client client) {
		if (message == null || message.length() == 0) {
			return;
		}
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(message.length() + 3);
		out.writeVariablePacketHeader(client.getEncryptor(), getOpcode());
		out.writeString(message);
		out.finishVariablePacketHeader();
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 253;
	}

}
