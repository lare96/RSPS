package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendEntityFeed extends OutgoingPacket {
	private String entityName;
	private int HP;
	private int maxHP;

	public SendEntityFeed(String entityName, int HP, int maxHP) {
		this.entityName = entityName;
		this.HP = HP;
		this.maxHP = maxHP;
	}

	@Override
	public void execute(Client client) {
		if (client.getPlayer() == null) {
			return;
		}

		if (entityName.length() == 0) {
			return;
		}
		
		if (!client.getPlayer().getCombat().inCombat()) {
			entityName = null;	
		}
		
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(entityName.length() + 7);
		out.writeVariablePacketHeader(client.getEncryptor(), getOpcode());
		out.writeString(entityName);
		out.writeShort(HP);
		out.writeShort(maxHP);
		out.finishVariablePacketHeader();
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 175;
	}

}