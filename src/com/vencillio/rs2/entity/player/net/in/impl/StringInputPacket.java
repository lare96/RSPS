package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.VencillioConstants;
import com.vencillio.Server;
import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.DropTable;
import com.vencillio.rs2.content.PlayerTitle;
import com.vencillio.rs2.content.clanchat.Clan;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.gambling.Gambling;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class StringInputPacket extends IncomingPacket {

	@Override
	public int getMaxDuplicates() {
		return 1;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		String input = Utility.longToPlayerName2(in.readLong());
		input = input.replaceAll("_", " ");
		
		if (player.getInterfaceManager().getMain() == 41750) {
			player.reportName = Utility.capitalize(input);
			return;
		}
		
		if (player.getInterfaceManager().getMain() == 59800) {
			DropTable.searchItem(player, input);
			return;
		}
		
		if (player.getEnterXInterfaceId() == 56000) {
			Gambling.play(player, Integer.parseInt(input));
			return;
		}
		
		if (player.getEnterXInterfaceId() == 56002) {
			for (int i = 0; i < VencillioConstants.BAD_STRINGS.length; i++) {
				if (input.equalsIgnoreCase(VencillioConstants.BAD_STRINGS[i])) {
					DialogueManager.sendStatement(player, "Grow up! That title can not be used.");
					return;
				}
			}
			if (input.length() >= 15) {
				DialogueManager.sendStatement(player, "Titles can not exceed 15 characters!");
				return;
			}
			player.setPlayerTitle(PlayerTitle.create(input, player.getPlayerTitle().getColor(), false));
			player.setAppearanceUpdateRequired(true);
			player.send(new SendRemoveInterfaces());
			return;
		}

		if (player.getEnterXInterfaceId() == 55776) {
			player.setCredits(player.getCredits() - 10);
			player.setShopMotto(Utility.capitalize(input));
			DialogueManager.sendInformationBox(player, "Player Owned Shops Exchange", "You have successfully changed your shop motto.", "Motto:", "@red@" + Utility.capitalize(input), "");
			return;
		}

		if (player.getEnterXInterfaceId() == 100) {
			player.getSlayer().setSocialSlayerPartner(input);
			return;
		}

		if (player.getEnterXInterfaceId() == 55777) {
			player.getShopping().open(World.getPlayerByName(input));
			return;
		}

		if (player.getEnterXInterfaceId() == 55778) {
			player.getPlayerShop().setSearch(input);
			return;
		}

		if (player.getEnterXInterfaceId() == 6969) {
			if ((input != null) && (input.length() > 0) && (player.clan == null)) {
				Clan localClan = Server.clanManager.getClan(input);
				if (localClan != null)
					localClan.addMember(player);
				else if (input.equalsIgnoreCase(player.getUsername()))
					Server.clanManager.create(player);
				else {
					player.getClient().queueOutgoingPacket(new SendMessage(Utility.formatPlayerName(input) + " has not created a clan yet."));
				}
			}
		} else {
			return;
		}
	}
}
