package com.vencillio.rs2.content.minigames.godwars;

import com.vencillio.rs2.content.minigames.godwars.GodWarsData.Allegiance;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.DefaultController;
import com.vencillio.rs2.entity.player.net.out.impl.SendWalkableInterface;

public class GodWarsController extends DefaultController {

	@Override
	public void onControllerInit(Player player) {
		player.send(new SendWalkableInterface(16210));

		for (Allegiance allegiance : Allegiance.values()) {
			player.getMinigames().updateGWKC(allegiance);
		}
	}
}