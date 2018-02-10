package com.vencillio.rs2.content.minigames.clanwars;

import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;

/**
 * Handles Clan Wars FFA
 * @author Daniel
 *
 */
public class ClanWarsFFA {
	
	public static boolean clickObject(Player player, int object) {
		switch (object) {
	
		case 26645://Enter
			enterGame(player);		
			break;
			
		case 26646://Leave
			leaveGame(player);
			break;
			
		}
		return false;
	}
	
	
	public static void enterGame(Player player) {
		if (player.getBossPet() != null) {
			DialogueManager.sendStatement(player, "You can't bring a pet into this game!");
			return;
		}
		player.getMagic().teleport(ClanWarsConstants.FFA_PORTAL, TeleportTypes.SPELL_BOOK);
		player.setController(ControllerManager.CLAN_WARS_FFA_CONTROLLER);
	}
	
	public static void leaveGame(Player player) {
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		player.getMagic().teleport(new Location(3352, 3164, 0), TeleportTypes.SPELL_BOOK);
	}

}
