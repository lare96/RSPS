package com.vencillio.rs2.content;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class Yelling {

	public static final String YELL_COOLDOWN_KEY = "yellcooldown";

	public static String send;

	public static void yell(Player player, String message, boolean staffYell) {

		message = Utility.capitalizeFirstLetter(message);

		int rights = player.getRights();

		if (rights == 1) {
			send = "[@blu@Moderator</col>] <img=0>@blu@" + player.getUsername() + "</col>: " + message;
		} else if (rights == 2) {
			send = "[<col=D17417>@Administrator</col>]  <img=1><col=D17417>" + player.getUsername() + "</col>: " + message;
		} else if (rights == 3) {
			send = "[@red@CEO</col>] <img=2>@red@" + player.getUsername() + "</col>: " + message;
		} else if (rights == 4) {
			send = "[@dre@Developer</col>] <img=3>@dre@" + player.getUsername() + "</col>: " + message;
		} else if (rights == 5) {
			send = "[<col=D11717>Member</col>] <img=4><col=D11717>" + player.getUsername() + "</col>: " + message;
		} else if (rights == 6) {
			send = "[<col=0956AD>" + Utility.capitalize(player.getYellTitle()) + "</col>] <img=5><col=0956AD>" + player.getUsername() + "</col>: " + message;
		} else if (rights == 7) {
			send = "[<col=4D8528>" + Utility.capitalize(player.getYellTitle()) + "</col>] <img=6><col=4D8528>" + player.getUsername() + "</col>: " + message;
		} else if (rights == 8) {
			send = "[<col=971FF2>" + Utility.capitalize(player.getYellTitle()) + "</col>] <img=7><col=971FF2>" + player.getUsername() + "</col>: " + message;
		} else if (staffYell) {
			send = "[<shad=0><col=FF0202>Staff Yell</shad></col>]  <img=11> " + player.getUsername() + "</col>:<shad=0><col=FF0202> " + message;
		} else {
			if (player.getRights() == 0) {
				if (player.getAttributes().get("yellcooldown") == null) {
					player.getAttributes().set("yellcooldown", Long.valueOf(System.currentTimeMillis()));
				} else if (System.currentTimeMillis() - ((Long) player.getAttributes().get("yellcooldown")).longValue() < 3000L) {
					player.getClient().queueOutgoingPacket(new SendMessage("You must wait a few seconds before yelling again."));
					return;
				}

				player.getAttributes().set("yellcooldown", Long.valueOf(System.currentTimeMillis()));
			}
			return;
		}

		if (player.isMuted()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You are muted and cannot yell."));
			return;
		}

		if (player.isYellMuted()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You are muted are not allowed to yell."));
			return;
		}

		if (message.contains("<") && !PlayerConstants.isStaff(player)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot use text arguments when yelling."));
			return;
		}

		for (Player i : World.getPlayers()) {
			if (i != null && send != null && (i.getRights() >= 1 && i.getRights() <= 4) && staffYell) {
				i.send(new SendMessage(send));
			} else if (i != null && send != null && !staffYell) {
				i.send(new SendMessage(send));
			}
		}
	}
}
