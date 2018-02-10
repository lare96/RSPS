package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.player.Player;

public class ModCommandsInterface extends InterfaceHandler {

	public ModCommandsInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"::jailarea - teleports to jail area",
			"::jail - jail a player",
			"::unjail - unjails a player",
			"::ban - bans a player",
			"::unban - unbans a player",
			"::mute - mutes a player",
			"::unmute - unmutes player",
			"::checkbank checks player bank",
			"::teleto - teleports to player",
			"::teletome - telepors player to me",
			"::staffzone - teleports to staffzone",
			"::ecosearch - searches for amount of itemId in eco",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",			

	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 8145;
	}
	
}
