package com.vencillio.rs2.content.dialogue.impl;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.Dialogue;
import com.vencillio.rs2.content.dialogue.DialogueConstants;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Otto Godblessed dialogue (creates & reverts Zamorakian hasta)
 * @author Daniel
 *
 */
public class OttoGodblessed extends Dialogue {
	
	/**
	 * Otto Godblessed
	 * @param player
	 */
	public OttoGodblessed(Player player) {
		this.player = player;
	}
	
	/**
	 * Zamorakian spear identification
	 */
	private final int ZAMORAKIAN_SPEAR = 11824;
	
	/**
	 * Zamorakian hasta identification
	 */
	private final int ZAMORAKIAN_HASTA = 11889;
	
	/**
	 * The cost of creating Zamorakian hasta
	 */
	private final int CREATION_COST = 3_000_000;

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		
		//Creates hasta
		case DialogueConstants.OPTIONS_3_1:
			if (player.getInventory().hasItemId(ZAMORAKIAN_SPEAR)) {
				if (player.getInventory().hasItemAmount(995, CREATION_COST)) {
					player.getInventory().remove(995, CREATION_COST);
					player.getInventory().remove(ZAMORAKIAN_SPEAR, 1);
					player.getInventory().add(ZAMORAKIAN_HASTA, 1);
					DialogueManager.sendItem1(player, "Otto has given you a @dre@Zamorakian hasta</col>!", ZAMORAKIAN_HASTA);
					setNext(-1);
				} else {
					DialogueManager.sendNpcChat(player, 2914, Emotion.ANNOYED, "You need " + Utility.format(CREATION_COST) + " coins to do this!");
					setNext(-1);
				}
			} else {
				DialogueManager.sendNpcChat(player, 2914, Emotion.ANNOYED, "You need a Zamorakian spear to do this!");
				setNext(-1);
			}
			break;
			
		//Reverts hasta
		case DialogueConstants.OPTIONS_3_2:
			if (player.getInventory().hasItemId(ZAMORAKIAN_HASTA)) {
				player.getInventory().remove(ZAMORAKIAN_HASTA, 1);
				player.getInventory().add(ZAMORAKIAN_SPEAR, 1);
				DialogueManager.sendItem1(player, "Otto has given you a @dre@Zamorakian spear</col>!", ZAMORAKIAN_SPEAR);
				setNext(-1);

			} else {
				DialogueManager.sendNpcChat(player, 2914, Emotion.ANNOYED, "You need a Zamorakian hasta to do this!");
				setNext(-1);
			}
			break;
			
		//Nothing
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
			DialogueManager.sendNpcChat(player, 2914, Emotion.HAPPY_TALK, "Hello young warrior!", "You have quite the body on you I may say.", "I can offer you some services.");
			next++;
			break;
			
		case 1:
			DialogueManager.sendOption(player, "Make Zamorakian hasta", "Revert Zamorakian spear", "Nothing");
			break;
		
		}
	}

}
