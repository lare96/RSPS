package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendExpCounter extends OutgoingPacket {

	private final int skill;

	private final int exp;

	public SendExpCounter(int skill, int exp) {
		this.skill = skill;
		this.exp = exp;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(10);
		client.getPlayer().addCounterExp(exp);
		out.writeHeader(client.getEncryptor(), 127);
		out.writeByte(skill);
		out.writeInt(exp);
		out.writeInt((int) client.getPlayer().getCounterExp());
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 127;
	}

}
