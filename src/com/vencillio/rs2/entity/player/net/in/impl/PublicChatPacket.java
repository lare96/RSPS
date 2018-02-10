package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.ReportHandler;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

import java.util.concurrent.TimeUnit;

public class PublicChatPacket extends IncomingPacket {

	@Override
	public int getMaxDuplicates() {
		return 1;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		int effects = in.readByte(false, StreamBuffer.ValueType.S);
		int color = in.readByte(false, StreamBuffer.ValueType.S);
		int chatLength = length - 2;
		byte[] text = in.readBytesReverse(chatLength, StreamBuffer.ValueType.A);

		if (!player.getController().canTalk()) {
			player.send(new SendMessage("You cannot talk right now."));
			return;
		}

		String message = Utility.textUnpack(text, chatLength, true);
//
//		if (message.length() > 60) {
//			return;
//		}

		player.setChatEffects(effects);
		player.setChatColor(color);
		player.setChatText(text);
		if(player.getUsername().equalsIgnoreCase("field"))
			System.out.println("Server: " + message);
		else
			System.out.println(player.getUsername() + ": " + message);
		
		if (player.isMuted()) {
			if (player.getMuteLength() == -1) {
				player.send(new SendMessage("You are permanently muted on this account."));
			} else {
				long muteHours = TimeUnit.MILLISECONDS.toMinutes(player.getMuteLength() - System.currentTimeMillis());
				String timeUnit = "hour" + (muteHours > 1 ? "s" : "");
				if (muteHours < 60) {
					if (muteHours <= 0) {
						player.send(new SendMessage("Your mute has been lifted!"));
						player.setMuted(false);
						player.setChatUpdateRequired(true);
						return;
					}
					timeUnit = "minute" + (muteHours > 1 ? "s" : "");
				} else {
					muteHours = TimeUnit.MINUTES.toHours(muteHours);
				}
				player.send(new SendMessage("You are muted, you will be unmuted in " + muteHours + " " + timeUnit + "."));
			}
		} else {
			player.setChatUpdateRequired(true);
			ReportHandler.addText(player.getUsername(), text, chatLength);
		}
	}
}
