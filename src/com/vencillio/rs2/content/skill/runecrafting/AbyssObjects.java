package com.vencillio.rs2.content.skill.runecrafting;

import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;

public class AbyssObjects {
	
	public static boolean clickObject(Player player, int id) {
		switch (id) {
		
		case 25376:// Water
			return true;
			
		case 24976: // Chaos
			player.teleport(new Location(2281, 4837));
			return true;
			
		case 25034: // Law
			player.teleport(new Location(2464, 4818));
			return true;
			
		case 25035: // Death
			player.teleport(new Location(2208, 4830));
			return true;
			
		case 25377: // Soul
			player.teleport(new Location(3494, 4832));
			return true;
			
		case 25378: // Air
			player.teleport(new Location(2841, 4829));
			return true;
			
		case 25379: // Mind
			player.teleport(new Location(2793, 4828));
			return true;
			
		case 24972: // Earth
			player.teleport(new Location(2655, 4830));
			return true;
			
		case 24971: // Fire
			player.teleport(new Location(2577, 4846));
			return true;
			
		case 25380: // Blood
			if (player.getRights() == 0) {
				DialogueManager.sendItem1(player, "Only members may do this!", 565);
				return false;
			}
			player.teleport(new Location(2800, 3320, 0));
			return true;
			
		case 24974: // Cosmic
			player.teleport(new Location(2162, 4833));
			return true;
			
		case 24975: // Nature
			player.teleport(new Location(2400, 4835));
			return true;
			
		case 24973: // Body
			player.teleport(new Location(2521, 4834));
			return true;
		}
		return false;
	}
}
