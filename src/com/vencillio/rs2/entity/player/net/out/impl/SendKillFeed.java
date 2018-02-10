package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.content.membership.CreditPurchase;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendKillFeed extends OutgoingPacket {

	String killer;

	String victim;

	int weapon;

	boolean poison;

	public SendKillFeed(String killer, String victim, int weapon, boolean poison) {
		this.killer = killer;
		this.victim = victim;
		this.weapon = weapon;
		this.poison = poison;
	}

	@Override
	public void execute(Client client) {
		if (killer == null || killer.length() == 0 || victim == null || victim.length() == 0) {
			return;
		}
		if (client.getPlayer() != null && client.getPlayer().inWilderness() && client.getPlayer().isCreditUnlocked(CreditPurchase.HIDE_WILDERNESS_KILLS)) {
			return;
		}
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(killer.length() + victim.length() + 7);
		out.writeVariablePacketHeader(client.getEncryptor(), getOpcode());
		out.writeString(killer);
		out.writeString(victim);
		out.writeShort(weapon);
		out.writeByte(poison ? 1 : 0);
		out.finishVariablePacketHeader();
		client.send(out.getBuffer());

	}

	@Override
	public int getOpcode() {
		return 173;
	}

}
