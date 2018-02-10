package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendUpdateItems extends OutgoingPacket {

	private final int id;

	private final Item[] items;
	
	private final int[] amounts;

	public SendUpdateItems(int id, Item[] items, int[] amounts) {
		this.id = id;
		this.items = items;
		this.amounts = amounts;
	}
	
	public SendUpdateItems(int id, Item[] items) {
		this(id, items, null);
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(4096);
		out.writeVariableShortPacketHeader(client.getEncryptor(), 53);
		out.writeShort(id);
		if (items == null) {
			out.writeShort(0);
			out.writeByte(0);
			out.writeShort(0, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			out.finishVariableShortPacketHeader();
			client.send(out.getBuffer());
			return;
		}
		out.writeShort(items.length);
		for (Item item : items) {
			if (item != null) {
				if (item.getAmount() > 254) {
					out.writeByte(255);
					out.writeInt(item.getAmount(), StreamBuffer.ByteOrder.INVERSE_MIDDLE);
				} else {
					out.writeByte(item.getAmount());
				}
				out.writeShort(item.getId() + 1, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			} else {
				out.writeByte(0);
				out.writeShort(0, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			}
		}
		
		if (amounts != null) {
			for (int index = 0; index < amounts.length; index++) {
	        	int amount = amounts[index];
	        	out.writeByte(amount >> 8);
	        	out.writeShort(amount & 0xFF);
	        }
		}
		out.finishVariableShortPacketHeader();
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 53;
	}

}
