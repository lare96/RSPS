package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendLoginResponse extends OutgoingPacket {

	private final int response;
	private final int rights;

	public SendLoginResponse(int response, int rights) {
		super();
		this.response = response;
		this.rights = rights;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
		resp.writeByte(response);
		resp.writeByte(rights);
		resp.writeByte(0);
		client.send(resp.getBuffer());
		new SendMapRegion(client.getPlayer()).execute(client);
		new SendDetails(client.getPlayer().getIndex()).execute(client);
	}

	@Override
	public int getOpcode() {
		return -1;
	}

}
