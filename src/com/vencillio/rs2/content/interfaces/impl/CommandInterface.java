package com.vencillio.rs2.content.interfaces.impl;

import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.entity.player.Player;

public class CommandInterface extends InterfaceHandler {

	public CommandInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"::players - shows amount of active players",
			"::vote - opens the voting link",
			"::store - opens the store link",
			"::forum - opens the forum link",
			"::answer - answers the TriviaBot",
			"::changepassword - changes password",
			"::wealth - tells you your total wealth",
			"::playtime - tell you your total playtime",
			"::mymode - shows your current mode",
			"::kc (npc)",
			"::ib or ::itemdef (item)",
			"::yell - does a global yell(<img=4>Members only)",
			"::yelltitle - changes yell title(<img=5>Super+ Members only)",
			"::empty - deletes inventory",
			"::home - teleports home (edge)",
			"::teleport - opens the teleporting menu",
			"::triviasettings - TriviaBot settings",
			"::reward (id)",
			"::claim",
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
