package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGameConstants;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGameStore;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class WeaponGameDialogue extends Dialogue {
	
	public WeaponGameDialogue(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {

		switch (id) {
		
		case DialogueConstants.OPTIONS_3_1:
			setNext(2);
			execute();
			break;
			
		case DialogueConstants.OPTIONS_3_2:
			WeaponGameStore.open(player);
			break;
			
		case DialogueConstants.OPTIONS_3_3:
			player.send(new SendRemoveInterfaces());
			break;
		
		}
	
		return false;
	}

	@Override
	public void execute() {
	
		switch (next) {
		
		case 0:
			DialogueManager.sendNpcChat(player, 1103, Emotion.HAPPY_TALK, "Hey there pal!", "Welcome to the Weapon Game Lobby.", "What can I do you for?");
			next++;
			break;
			
		case 1:
			DialogueManager.sendOption(player, "How do I play?", "Can I see the rewards store?", "Nothing.");
			break;
			
		case 2:
			pChat("How do I play?");
			break;
			
		case 3:
			nChat("Bank all your equipment and inventory items.", "Proceed by entering the portal.", "The game requires a minimum of " + WeaponGameConstants.MINIMUM_PLAYERS + " players to start.");
			break;
		
		case 4:
			nChat("Once there are a minimum of " + WeaponGameConstants.MINIMUM_PLAYERS + " inside the lobby", "the game will begin.", "The objective is to be the first player to reach 10 kills.", "Each kill will grant you a new weapon.");
			break;
			
		case 5:
			nChat("Throughout the game there will be crates that spawn.", "Clicking on these crates could", "grant you some supplies or gear.", "However, there is a chance of something else happening...");
			break;
			
		case 6:
			nChat("Once the game is over by a player winning", "All participants will recieve 25,000 points.");
		break;
		
		case 7:
			nChat("The winning player will recieve", "35,000 points along with 500k.", "Points earned can be used in my store.");
			break;
		
		case 8:
			setNext(1);
			execute();
			break;
			
		}
	
	}
	
	public void nChat(String... chat) {
		DialogueManager.sendNpcChat(player, 1103, Emotion.HAPPY_TALK, chat);
		next += 1;
	}
	
	public void pChat(String... chat) {
		DialogueManager.sendPlayerChat(player, Emotion.HAPPY, chat);
		next += 1;
	}
	
}