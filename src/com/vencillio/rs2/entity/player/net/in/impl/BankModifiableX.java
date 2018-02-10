package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.network.StreamBuffer.ByteOrder;
import com.vencillio.core.network.StreamBuffer.ValueType;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;

public class BankModifiableX extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		in.readShort(ValueType.A, ByteOrder.BIG);
		in.readShort();
		int item = in.readShort(ValueType.A, ByteOrder.BIG);
		int amount = in.readInt();
		player.getBank().withdraw(item, amount);
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}