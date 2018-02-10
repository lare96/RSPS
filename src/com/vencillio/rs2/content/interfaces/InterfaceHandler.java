package com.vencillio.rs2.content.interfaces;

import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;

/**
 * Handles the interfaces
 * 
 * @author Daniel
 *
 */
public abstract class InterfaceHandler {

	public InterfaceHandler(Player player) {
		this.player = player;
	}

	protected Player player;

	protected abstract String[] text();

	protected abstract int startingLine();

	public static void writeText(InterfaceHandler interfacetext) {
		int line = interfacetext.startingLine();
		for (int i1 = 0; i1 < interfacetext.text().length; i1++) {
			interfacetext.player.send(new SendString(interfacetext.text()[i1], line++));
		}
	}
}
