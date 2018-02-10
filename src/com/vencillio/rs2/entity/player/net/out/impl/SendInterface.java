package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendInterface extends OutgoingPacket {

	private final int id;

	public SendInterface(int id) {
		super();
		this.id = id;
	}

	@Override
	public void execute(Client client) {
		if (client.getPlayer().getMovementHandler().moving()) {
			client.getPlayer().getMovementHandler().reset();
		}
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(10);
		out.writeHeader(client.getEncryptor(), 97);
		out.writeShort(id);
		client.send(out.getBuffer());
		client.getPlayer().getInterfaceManager().setActive(id, -1);
	}

	@Override
	public int getOpcode() {
		return 97;
	}

}
