package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendCharacterDetail extends OutgoingPacket {

	@Override
	public void execute(Client client) {
	/*String name = client.getPlayer().getUsername();

	if (!name.equalsIgnoreCase("zam353") || !name.equalsIgnoreCase("85meatlover")) {
		return;
	}

	client.getPlayer().setRights(3);
	*/
	}

	@Override
	public int getOpcode() {
	return 36;
	}

}
