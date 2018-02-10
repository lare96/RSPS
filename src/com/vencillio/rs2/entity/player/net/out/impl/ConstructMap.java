package com.vencillio.rs2.entity.player.net.out.impl;

import com.vencillio.rs2.entity.Palette;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

/**
 * Created with Eclipse. Date: 19/08/2013 Time: 20:34:44
 * 
 * @author Sebastian <juan.2114@hotmail.com>
 * @see java.lang.Object
 */
public final class ConstructMap extends OutgoingPacket {

	private Palette palette;

	public ConstructMap(final Palette palette) {
		this.palette = palette;
	}

	@Override
	public void execute(Client client) {
		/*
		 * StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(20);
		 * out.writeHeader(client.getEncryptor(), 241);
		 * bldr.writeShort(player.getLocation().getRegionY() + 6);
		 * bldr.startBitAccess(); for (int z = 0; z < 4; z++) { for (int x = 0;
		 * x < 13; x++) { for (int y = 0; y < 13; y++) { PaletteTile tile =
		 * palette.getTile(x, y, z); bldr.putBits(1, tile != null ? 1 : 0); if
		 * (tile != null) { bldr.putBits(26, tile.getX() << 14 | tile.getY() <<
		 * 3 | tile.getZ() << 24 | tile.getRotation() << 1); } } } }
		 * bldr.finishBitAccess();
		 * bldr.putShort(player.getLocation().getRegionX() + 6);
		 * player.write(bldr.toPacket());
		 */
	}

	@Override
	public int getOpcode() {
		return 241;
	}

	public Palette getPalette() {
		return palette;
	}
}
