package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue for Makeover Mage
 * @author Daniel
 *
 */
public class MakeoverMage extends Dialogue {
	
	public MakeoverMage(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch(id) {
		case 9157:
			if (!player.getInventory().hasItemAmount(new Item(995, 10000))) {
				DialogueManager.sendNpcChat(player, 1306, Emotion.ANNOYED, "You don't have 10,000 coins!");
				return false;
			}
			player.getInventory().remove(new Item(995, 10000));
			player.send(new SendInterface(3559));
			break;
		case 9158:
			player.send(new SendRemoveInterfaces());
			return false;
		}
		return false;
	}

	@Override
	public void execute() {
		switch(next) {
		case 0:
			DialogueManager.sendNpcChat(player, 1306, Emotion.HAPPY, "Hello "+player.getUsername()+".", "Would you care for a make over?", "Only 10,000 coins!");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, "Yes, take my money!", "10,000!? Scam much? Bye.");
			break;
		}
	}


}
