package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Handles the Kamfree dialogue for Warriors Guild
 * 
 * @notes I wanted to do something different rather than the constant boring dialogues in every private sever. 
 * @notes As the kids would say, "That shit lame, homie." Obviously that sentenced would be followed by low riding pants
 * @notes And retards with their hats on sideways. But yes, you get the point.
 * 
 * @author Daniel
 *
 */
public class KamfreeDialogue extends Dialogue {

	@Override
	public boolean clickButton(int id) {
		return false;
	}

	@Override
	public void execute() {
		switch (next) {		
		case 0:
			DialogueManager.sendPlayerChat(player, Emotion.UNINTERESTED, "Hello!", "What is this place?");
			next ++;
			break;
		case 1:
			DialogueManager.sendNpcChat(player, 2461, Emotion.HAPPY_TALK, "This is the Warriors Guild of course, ", "you little chimney snatcher!");
			next ++;
			break;
		case 2:
			DialogueManager.sendPlayerChat(player, Emotion.ANGRY_1, "Okay... Whack job...");
			next ++;
			break;
		case 3:
			DialogueManager.sendPlayerChat(player, Emotion.UNINTERESTED, "What do I do here?");
			next ++;
			break;
		case 4:
			DialogueManager.sendNpcChat(player, 2461, Emotion.HAPPY_TALK, "Really? You don't know what this place is? Of course you do.", "You wouldn't be here otherwise.");
			next ++;
			break;
		case 5:
			DialogueManager.sendPlayerChat(player, Emotion.UNINTERESTED, "Huh?", "What are you talking about?");
			next ++;
			break;
		case 6:
			DialogueManager.sendNpcChat(player, 2461, Emotion.HAPPY_TALK, "This is a Runescape Private Server.", "Only reason you are here is because you have played", "Runescape. Obviously you would have done Warriors Guild.", "That's why you are here.");
			next ++;
			break;
		case 7:
			DialogueManager.sendPlayerChat(player, Emotion.UNINTERESTED, "Dude what the hell? Keep your cool.", "This is a private server we gotta act like one.", "Can we restart?", "Just do the regular Runescape dialogue please.");
			next ++;
			break;
		case 8:
			DialogueManager.sendNpcChat(player, 2461, Emotion.ANNOYED, "I CAN'T STAND IT ANYMORE " + player.getUsername().toUpperCase() + "!", "All I do is stand here like a moron.", "It's not like these damn Cyclops help!", "They are shitting constantly!!! It smells so bad!");
			next ++;
			break;
		case 9:
			DialogueManager.sendNpcChat(player, 2461, Emotion.DISTRESSED, "WHERE IS MY TIME? WHEN DO I GET TO SHINE??", "I NEED TO GET OUT OF THIS!!!");
			next ++;
			break;
		case 10:
			DialogueManager.sendPlayerChat(player, Emotion.UNINTERESTED, "I guess I will just enter the doors then...");
			next ++;
			break;
		case 11:
			DialogueManager.sendNpcChat(player, 2461, Emotion.DISTRESSED, "SFNHBHDFSDFNSHDFB NO ONE AFFBAHSH CARES UAFUASFJ", "ABOUT ZUFHASYFHASYFASDH MEEE!");
			next ++;
			break;
		case 12:
			DialogueManager.sendPlayerChat(player, Emotion.UNINTERESTED, "Whatever. Later Dink.");
			next ++;
			break;
		case 13:
			player.send(new SendRemoveInterfaces());
			break;
		}
	}

}
