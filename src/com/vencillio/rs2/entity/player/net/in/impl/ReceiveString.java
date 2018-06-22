package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.Server;
import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.clanchat.Clan;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class ReceiveString extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		String text = in.readString();
		int index = text.indexOf(",");
		int id = Integer.parseInt(text.substring(0, index));
		String string = text.substring(index + 1);
		System.out.println("ID: " + id);
		switch (id) {
		case 0: //Leave clan chat

			if (player.clan != null) {
				player.clan.removeMember(player);
				player.lastClanChat = "";
			} else {
				player.setEnterXInterfaceId(551);
			}
			break;
		case 1: //Joining clan chat
			if (string.length() == 0) {
				break;
			} else if (string.length() > 15) {
				string = string.substring(0, 15);
			}
			Clan clan = player.getClan();
			if (clan == null) {
				Server.clanManager.create(player);
				clan = player.getClan();
			}
			if (clan != null) {
				clan.setTitle(string);
				player.getClient().queueOutgoingPacket(new SendString(clan.getTitle(), 43706));
				clan.save();
			}
			break;
		case 2: //Promoting clan chat members
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			if (string.equalsIgnoreCase(player.getUsername())) {
				break;
			}
			if (string.equalsIgnoreCase("tanner")) {
				player.getClient().queueOutgoingPacket(new SendMessage("You don't have permission to do this"));
				break;
			}

			clan = player.getClan();
			if (clan.isBanned(string)) {
				player.getClient().queueOutgoingPacket(new SendMessage("You cannot promote a banned member."));
				break;
			}
			if (clan != null) {
				clan.setRank(Utility.formatPlayerName(string), 1);
				player.setClanData();
				clan.save();
			}
			break;
		case 3: //Banning clan members
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			if (string.equalsIgnoreCase(player.getUsername())) {
				break;
			}
			clan = player.getClan();
			if (clan.isRanked(string)) {
				player.getClient().queueOutgoingPacket(new SendMessage("You can't ban a ranked member of this clan chat channel."));
				break;
			}
			if (clan != null) {
				clan.banMember(Utility.formatPlayerName(string));
				player.setClanData();
				clan.save();
			}
			break;
		default:
			System.out.println("Received string: identifier=" + id + ", string=" + string);
			break;
		}
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}