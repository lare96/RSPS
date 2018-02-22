package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Created by Tanner on 2/21/2018.
 */
public class DoubleLottoGame {

	private static final int CHANCE_OF_LOSING = 55;

	public static void playGame(Player player, String input) {
		boolean won = Utility.random(100) >= CHANCE_OF_LOSING;

		if (player.isPouchPayment()) {
			if (player.getMoneyPouch() < Integer.parseInt(input)) {
				player.send(new SendMessage("You don't have enough money to bet that much"));
				return;
			}
		} else if (!player.getInventory().hasItemAmount(995, Integer.parseInt(input))) {
			player.send(new SendMessage("You don't have enough money to bet that much"));
			return;
		}

		if (won) {
			if (player.isPouchPayment())
				player.setMoneyPouch(player.getMoneyPouch() + (Integer.parseInt(input)));
			else
				player.getInventory().add(995, Integer.parseInt(input));

			DialogueManager.sendNpcChat(player, 1011, Emotion.HAPPY, "Congratulations, you have won double your bet");
		} else {
			player.getInventory().remove(new Item(995, Integer.parseInt(input)));
			DialogueManager.sendNpcChat(player, 1011, Emotion.SAD, "Sorry you have lost the money, better luck next time");
		}

	}

}