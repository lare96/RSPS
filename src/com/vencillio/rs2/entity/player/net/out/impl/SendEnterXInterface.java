package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.network.StreamBuffer.OutBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendEnterXInterface extends OutgoingPacket {

	private final int interfaceId;
	private final int itemId;

	public SendEnterXInterface() {
		interfaceId = 0;
		itemId = 0;
	}

	public SendEnterXInterface(int interfaceId, int itemId) {
		this.interfaceId = interfaceId;
		this.itemId = itemId;
	}

	@Override
	public void execute(Client client) {
		if (itemId > 0 || interfaceId > 0) {
			client.getPlayer().setEnterXInterfaceId(interfaceId);
			client.getPlayer().setEnterXItemId(itemId);
		}
		OutBuffer outBuffer = StreamBuffer.newOutBuffer(5);
		outBuffer.writeHeader(client.getEncryptor(), getOpcode());
		client.send(outBuffer.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 27;
	}

}
