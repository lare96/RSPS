package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

public class MiscInterfaces {
	
	public static void startUp(Player player) {
	
		//Magic Tab - Normal
		player.send(new SendString("Home Teleport", 19220));
		player.send(new SendString("Teleport home", 19222));	
		player.send(new SendString("Training Teleport", 19641));
		player.send(new SendString("Opens training interface", 19642));	
		player.send(new SendString("Skilling Teleport", 19722));
		player.send(new SendString("Opens skilling interface", 19723));	
		player.send(new SendString("PvP Teleport", 19803));
		player.send(new SendString("Opens pvp interface", 19804));
		player.send(new SendString("PvM Teleport", 19960));
		player.send(new SendString("Opens pvm interface", 19961));
		player.send(new SendString("Minigame Teleport", 20195));
		player.send(new SendString("Opens minigame interface", 20196));	
		player.send(new SendString("Other Teleport", 20354));
		player.send(new SendString("Opens other interface", 20355));	
		
		//Magic Tab - Ancients
		player.send(new SendString("Home Teleport", 21756));
		player.send(new SendString("Teleport home", 21757));	
		player.send(new SendString("Training Teleport", 21833));
		player.send(new SendString("Opens training interface", 21834));	
		player.send(new SendString("Skilling Teleport", 21933));
		player.send(new SendString("Opens skilling interface", 21934));	
		player.send(new SendString("PvP Teleport", 22052));
		player.send(new SendString("Opens pvp interface", 22053));
		player.send(new SendString("PvM Teleport", 22123));
		player.send(new SendString("Opens pvm interface", 22124));
		player.send(new SendString("Minigame Teleport", 22232));
		player.send(new SendString("Opens minigame interface", 22233));	
		player.send(new SendString("Other Teleport", 22307));
		player.send(new SendString("Opens other interface", 22308));
		

	
	}

}
