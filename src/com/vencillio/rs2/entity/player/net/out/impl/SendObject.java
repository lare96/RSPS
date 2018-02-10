package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class SendObject extends OutgoingPacket {

	private final GameObject o;
	private final Location base;

	public SendObject(Player p, GameObject o) {
		super();
		this.o = o;
		this.base = new Location(p.getCurrentRegion());
	}

	@Override
	public void execute(Client client) {
		new SendCoordinates(o.getLocation(), base).execute(client);
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
		out.writeHeader(client.getEncryptor(), getOpcode());
		out.writeByte(0, StreamBuffer.ValueType.S);
		out.writeShort(o.getId(), StreamBuffer.ByteOrder.LITTLE);
		out.writeByte(((o.getType() << 2) + (o.getFace() & 3)),
				StreamBuffer.ValueType.S);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 151;
	}

}
